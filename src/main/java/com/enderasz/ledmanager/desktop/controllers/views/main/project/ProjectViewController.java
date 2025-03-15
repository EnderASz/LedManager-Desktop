package com.enderasz.ledmanager.desktop.controllers.views.main.project;

import com.enderasz.ledmanager.desktop.LightMode;
import com.enderasz.ledmanager.desktop.connection.ServerConnection;
import com.enderasz.ledmanager.desktop.controllers.views.common.ConfirmDialogViewController;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.dialogs.ProjectNameChangeDialogHelper;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist.LightListItem;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist.LightListViewController;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.modes.LightConfigViewController;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.modes.LightConfigViewControllerFactory;
import com.enderasz.ledmanager.desktop.data.config.LightConfig;
import com.enderasz.ledmanager.desktop.views.LoadedView;
import com.enderasz.ledmanager.desktop.views.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.*;
import java.util.function.Supplier;

public class ProjectViewController {
    @FXML
    public GridPane rootNode;

    @FXML
    private TreeTableView<LightListItem> lightListView;

    @FXML
    private LightListViewController lightListViewController;

    @FXML
    private ResourceBundle resources;

    @FXML
    private ChoiceBox<String> lightModeChoice;

    @FXML
    public AnchorPane lightConfigAnchor;

    @FXML
    private Label projectName;

    @FXML
    private Button changeNameBtn;

    private LightConfigViewController<?> lightConfigController;

    private Boolean blockConfigViewLoad = false;

    private Supplier<ServerConnection> serverConnectionSupplier;

    private UUID projectId = null;

    @FXML
    public void initialize() {
        initializeLightModeChoice();
        lightListViewController.registerOnChoiceChange(this::handleLightListChoiceChange);
        Platform.runLater(changeNameBtn::requestFocus);
    }

    private void initializeLightModeChoice() {
        // Get all possible LightMode names
        List<String> modeNames = Arrays.stream(LightMode.values()).map((LightMode value) -> value.getDisplayNameFor(resources)).toList();

        // Add these names to choice box
        lightModeChoice.getItems().addAll(modeNames);
        lightModeChoice.setValue(LightMode.NONE.getDisplayNameFor(resources));

        // Calculate and setup preferred width of lightModeChoice
        Optional<Integer> prefInputLength = modeNames.stream().map(String::length).max(Integer::compareTo);
        assert prefInputLength.isPresent();
        Text tempText = new Text("W".repeat(prefInputLength.get() + 2));
        // For now, I'm not sure how to get font of lightModeChoice, so I will base on Text default font
        lightModeChoice.setPrefWidth(tempText.getBoundsInLocal().getWidth());
    }

    public void setServerConnectionSupplier(Supplier<ServerConnection> serverConnectionSupplier) {
        this.serverConnectionSupplier = serverConnectionSupplier;
    }

    @FXML
    private void handleChangeProjectNameButton(ActionEvent actionEvent) {
        openProjectNameChangeDialog();
    }

    @FXML
    private void handleLightModeChange(ActionEvent event) {
        if(blockConfigViewLoad) {
            return;
        }

        String selectedValue = lightModeChoice.getValue();

        LightMode selectedMode = LightMode.getByDisplayName(resources, selectedValue);

        if (selectedMode == null) {
            throw new RuntimeException("Unsupported light mode selected");
        }

        loadLightConfigView(selectedMode);

        LightConfig config = lightConfigController != null ? lightConfigController.getLightConfig() : null;

        LightListItem lightItem = lightListViewController.getSelectedItem();
        if (lightItem == null) {
            assert selectedMode.equals(LightMode.NONE);
            return;
        }

        lightItem.setConfig(config);    // Set new config for currently chosen light
        if(lightItem.isGroup()) {
            lightListViewController.groupedLightsStream(lightItem.getGroupName()).forEach(light -> light.setConfig(config));
        }
    }

    private void handleLightListChoiceChange() {
        blockConfigViewLoad = true;

        LightListItem item = lightListViewController.getSelectedItem();
        LightConfig config = item.getConfig();

        LightMode mode = LightMode.getForConfig(config);
        lightModeChoice.setValue(mode.getDisplayNameFor(resources));    // This will also call handleLightModeChange and load new config view
        lightModeChoice.setDisable(item.isGroupedLight());
        lightConfigAnchor.setDisable(item.isGroupedLight());

        loadLightConfigView(config);

        blockConfigViewLoad = false;
    }

    private void loadLightConfigView(LightMode mode) {
        lightConfigAnchor.getChildren().clear();

        View view = mode.getView();
        if (view == null) {
            return;
        }

        LoadedView configView = view.load();
        lightConfigController = configView.getLoader().getController();
        lightConfigAnchor.getChildren().add(configView.getNode());
    }

    private void loadLightConfigView(LightConfig config) {
        LightMode mode = LightMode.getForConfig(config);

        lightConfigAnchor.getChildren().clear();

        View view = mode.getView();
        if (view == null) {
            lightConfigController = null;
            return;
        }

        LoadedView configView = view.load(cls -> {
            LightConfigViewController<?> controller = LightConfigViewControllerFactory.create(config);
            assert (controller == null && cls == null) || (controller != null && controller.getClass().equals(cls));
            return controller;
        });
        lightConfigController = configView.getLoader().getController();
        lightConfigAnchor.getChildren().add(configView.getNode());
    }

    private Runnable onProjectNameChange;

    public void registerOnProjectNameChange(Runnable onProjectNameChange) {
        this.onProjectNameChange = onProjectNameChange;
    }

    private Runnable onNewProjectInitialization;

    public void registerOnNewProjectInitialization(Runnable onNewProjectInitialization) {
        this.onNewProjectInitialization = onNewProjectInitialization;
    }

    public String getProjectName() {
        return projectName.getText();
    }

    private void openProjectNameChangeDialog() {
        new ProjectNameChangeDialogHelper(resources, projectName::getText, this::setProjectName).createAndOpenDialog(rootNode.getScene().getWindow());
    }

    private void setProjectName(String newName) {
        projectName.setText(newName);
        onProjectNameChange.run();
    }

    @FXML
    private void handleNewProjectBtn(ActionEvent actionEvent) {
        ConfirmDialogViewController.createAndShow(
                resources.getString("project_new_confirm_dialog_title"),
                resources.getString("project_new_confirm_dialog_prompt"),
                resources.getString("project_new_confirm_dialog_confirm_btn"),
                resources.getString("project_new_confirm_dialog_cancel_btn"),
                onNewProjectInitialization,
                rootNode.getScene().getWindow()
        );
    }

    private Map<Integer, LightConfig> getLightConfigs() {
        Map<Integer, LightConfig> lightConfigs = new HashMap<>();
        lightListViewController.lightItemsStream().forEach(light -> {
            if (light.isLight()) lightConfigs.put(light.getLightId(), light.getConfig());
        });

        return lightConfigs;
    }

    @FXML
    public void handleConfirmBtn(ActionEvent actionEvent) {
        ServerConnection connection = serverConnectionSupplier.get();
        connection.uploadConfig(getLightConfigs());
    }

    @FXML
    public void handleSaveBtn(ActionEvent actionEvent) {
        ServerConnection connection = serverConnectionSupplier.get();
        connection.saveConfig(getLightConfigs(), getProjectName(), getProjectId());
    }

    public UUID getProjectId() {
        if (projectId == null) {
            setProjectId(UUID.randomUUID());
        }
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
}