package com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist;

import com.enderasz.ledmanager.desktop.data.config.LightConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class LightListViewController {
    @FXML
    private TreeTableView<LightListItem> lightList;
    @FXML
    private TreeTableColumn<LightListItem, String> lightListGroupNameColumn;
    @FXML
    private TreeTableColumn<LightListItem, String> lightListLightIdColumn;
    @FXML
    private ResourceBundle resources;

    private Runnable onChoiceChange;


    @FXML
    public void initialize() throws LightGroupAlreadyExists {
        LightListItem.setLightIdColumnCellValueFactory(lightListLightIdColumn);
        LightListItem.setGroupNameColumnCellValueFactory(lightListGroupNameColumn);

        TreeItem<LightListItem> rootItem = new TreeItem<>();
        lightList.setRoot(rootItem);
        lightList.setSortPolicy(LightListViewController::sortPolicy);

        try {
            addLightItem(1);
            addLightItem(2);
            addLightGroupItem("Przykładowa grupa");
            addLightItem(3, "Przykładowa grupa");
            addLightItem(4, "Przykładowa grupa");
            addLightItem(5, "Przykładowa grupa");
            addLightItem(6);
            addLightItem(7);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        lightList.getSelectionModel().clearSelection();

        lightList.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                lightList.setContextMenu(new LightListContextMenuHelper(resources, this).create());
            }
        });

        lightList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onChoiceChange.run());
    }

    public void registerOnChoiceChange(Runnable runnable) {
        onChoiceChange = runnable;
    }

    public LightListItem getSelectedItem() {
        TreeItem<LightListItem> treeItem = getSelectedTreeItem();
        if (treeItem == null) {
            return null;
        }
        return getSelectedTreeItem().getValue();
    }

    // TODO: DELETE ME - temporary for backward compatibility with pre-refactor code
    public TreeTableView<LightListItem> getLightListTree() {
        return lightList;
    }

    public Window getWindow() {
        return lightList.getScene().getWindow();
    }

    public Boolean doesGroupAlreadyExists(String groupName) {
        return groupTreeItemsStream().anyMatch(treeItem -> treeItem.getValue().getGroupName().equals(groupName));
    }

    public Boolean doesLightWithGivenIdExists(Integer lightId) {
        return lightTreeItemsStream().anyMatch(treeItem -> treeItem.getValue().getLightId().equals(lightId));
    }

    public Stream<LightListItem> groupItemsStream() {
        return groupTreeItemsStream().map(TreeItem::getValue);
    }

    public Stream<LightListItem> groupedLightsStream() {
        return groupedLightsStream(null);
    }

    public Stream<LightListItem> groupedLightsStream(String groupName) {
        return groupedLightTreeItemsStream(groupName).map(TreeItem::getValue);
    }

    private Stream<TreeItem<LightListItem>> groupTreeItemsStream() {
        return lightList.getRoot().getChildren().stream().filter(treeItem -> treeItem.getValue().isGroup());
    }

    private Stream<TreeItem<LightListItem>> standaloneLightTreeItemsStream() {
        return lightList.getRoot().getChildren().stream().filter(treeItem -> treeItem.getValue().isStandaloneLight());
    }

    private Stream<TreeItem<LightListItem>> groupedLightTreeItemsStream() {
        return groupedLightTreeItemsStream(null);
    }

    private Stream<TreeItem<LightListItem>> groupedLightTreeItemsStream(String groupName) {
        Stream<TreeItem<LightListItem>> groupsToInclude = groupTreeItemsStream().filter(treeItem -> !treeItem.isLeaf());
        if (groupName != null) {
            groupsToInclude = groupsToInclude.filter(treeItem -> treeItem.getValue().getGroupName().equals(groupName));
        }

        return groupsToInclude.flatMap(treeItem -> treeItem.getChildren().stream());
    }

    private Stream<TreeItem<LightListItem>> lightTreeItemsStream() {
        return Stream.concat(
                standaloneLightTreeItemsStream(),
                groupedLightTreeItemsStream()
        );
    }

    public Stream<LightListItem> lightItemsStream() {
        return lightTreeItemsStream().map(TreeItem::getValue);
    }

    public void addLightItem(Integer lightId) throws LightAlreadyExists {
        addLightItem(lightId, null, false);
    }

    public void addLightItem(Integer lightId, String groupName) throws LightAlreadyExists {
        addLightItem(lightId, groupName, false);
    }

    public void insertLightItem(Integer lightId) {
        try {
            addLightItem(lightId, null, true);
        } catch (LightAlreadyExists e) {
            throw new RuntimeException(e);
        }
    }

    public void insertLightItem(Integer lightId, String groupName) {
        try {
            addLightItem(lightId, groupName, true);
        } catch (LightAlreadyExists e) {
            throw new RuntimeException(e);
        }
    }

    public void addLightGroupItem(String groupName) throws LightGroupAlreadyExists {
        if (doesGroupAlreadyExists(groupName)) {
            throw new LightGroupAlreadyExists("Group " + groupName + " already exists");
        }

        lightList.getRoot().getChildren().add(new TreeItem<>(new LightListItem(groupName, null)));
        lightList.sort();
    }

    public Boolean removeItem() {
        TreeItem<LightListItem> selectedTreeItem = getSelectedTreeItem();
        if(selectedTreeItem.getValue().isGroup() || selectedTreeItem.getValue().isStandaloneLight()) {
            return lightList.getRoot().getChildren().remove(selectedTreeItem);
        }

        assert selectedTreeItem.getValue().isGroupedLight();

        for(TreeItem<LightListItem> treeItem : groupTreeItemsStream().toList()) {
            if(treeItem.getChildren().remove(selectedTreeItem)) {
                return true;
            }
        }
        return false;
    }

    public Integer getFirstFreeLightId() {
        Integer id = 1;
        for(LightListItem item : lightItemsStream().sorted(Comparator.comparing(LightListItem::getLightId)).toList()) {
            if(!item.getLightId().equals(id)) {
                return id;
            }
            id += 1;
        }
        return id;
    }

    private void addLightItem(Integer lightId, String groupName, Boolean shiftColliding) throws LightAlreadyExists {
        if(shiftColliding) {
            shiftLightIds(lightId);
        } else if(doesLightWithGivenIdExists(lightId)) {
            throw new LightAlreadyExists("Light " + lightId + " already exists");
        }

        TreeItem<LightListItem> parentItem;
        LightConfig config;
        if(groupName != null) {
            Optional<TreeItem<LightListItem>> groupItem = groupTreeItemsStream().filter(treeItem -> treeItem.getValue().getGroupName().equals(groupName)).findFirst();
            if(groupItem.isEmpty()) {
                throw new RuntimeException("Group " + groupName + " does not exist");
            }
            parentItem = groupItem.get();
            config = parentItem.getValue().getConfig();
        } else {
            parentItem = lightList.getRoot();
            config = null;
        }

        LightListItem newLight = new LightListItem(groupName, lightId);
        newLight.setConfig(config);
        TreeItem<LightListItem> newItem = new TreeItem<>(newLight);
        parentItem.getChildren().add(newItem);

        if(newLight.isGroupedLight()) {
            lightList.getSelectionModel().select(parentItem);
        } else {
            lightList.getSelectionModel().select(newItem);
        }

        lightList.sort();
    }

    private void shiftLightIds(Integer id) {
        Integer fromId = id;
        for(LightListItem item : lightItemsStream().filter(item -> item.getLightId() >= fromId).sorted(Comparator.comparing(LightListItem::getLightId)).toList()) {
            if(item.getLightId().equals(id)) {
                item.setLightId(item.getLightId() + 1);
                id += 1;
            } else {
                break;
            }
        }
    }

    private static Boolean sortPolicy(TreeTableView<LightListItem> table) {
        ObservableList<TreeItem<LightListItem>> standaloneItems = table.getRoot().getChildren();

        FXCollections.sort(
                standaloneItems,
                Comparator.comparing(
                        (TreeItem<LightListItem> o) -> o.getValue().getGroupName(),
                        Comparator.nullsFirst(String::compareTo)
                ).thenComparing(o -> o.getValue().getLightId())
        );
        for (TreeItem<LightListItem> treeItem : standaloneItems) {
            if (!treeItem.isLeaf()) {
                FXCollections.sort(
                        treeItem.getChildren(),
                        Comparator.comparing(o -> o.getValue().getLightId())
                );
            }
        }
        return true;
    }

    private TreeItem<LightListItem> getSelectedTreeItem() {
        return lightList.getSelectionModel().getSelectedItem();
    }
}
