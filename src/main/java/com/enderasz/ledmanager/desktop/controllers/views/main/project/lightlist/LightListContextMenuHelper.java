package com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist;

import com.enderasz.ledmanager.desktop.controllers.views.common.SingleInputDialogViewController;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.dialogs.NewGroupedLightDialogHelper;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.dialogs.NewLightGroupDialogHelper;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.dialogs.NewStandaloneLightDialogHelper;

import javafx.scene.control.*;

import java.util.Objects;
import java.util.ResourceBundle;

public class LightListContextMenuHelper {
    private final ResourceBundle resources;
    private final LightListViewController lightListController;

    public LightListContextMenuHelper(ResourceBundle resources, LightListViewController lightListController) {
        this.resources = resources;
        this.lightListController = lightListController;
    }

    public ContextMenu create() {
        LightListItem selectedItem = lightListController.getSelectedItem();

        MenuItem addNewGroupItem = new MenuItem(resources.getString("light_list_context_menu_add_new_group"));
        addNewGroupItem.setOnAction(_ -> openNewLightGroupDialog());

        MenuItem addNewLightItem = new MenuItem(resources.getString("light_list_context_menu_add_new_light"));
        addNewLightItem.setOnAction(_ -> openNewStandaloneLightDialog());

        Menu groupMenu = new Menu(resources.getString("light_list_context_menu_group"));
        Menu lightMenu = new Menu(resources.getString("light_list_context_menu_light"));

        if (selectedItem == null) {
            groupMenu.setDisable(true);
            lightMenu.setDisable(true);
        } else if (selectedItem.isGroup()) {
            assert !selectedItem.isLight();
            lightMenu.setDisable(true);

            MenuItem addNewGroupedLightItem = new MenuItem(resources.getString("light_list_context_menu_group_add_new_light"));
            addNewGroupedLightItem.setOnAction(_ -> openNewGroupedLightDialog(selectedItem.getGroupName()));
            MenuItem disbandGroupItem = new MenuItem(resources.getString("light_list_context_menu_group_disband"));
            Menu mergeGroupIntoItem = new Menu(resources.getString("light_list_context_menu_group_merge_into"));
            MenuItem renameGroupItem = new MenuItem(resources.getString("light_list_context_menu_group_rename"));
            renameGroupItem.setOnAction(_ -> openRenameGroupDialog(lightListController.getSelectedItem()));

            lightListController.groupItemsStream().filter(item -> !item.getGroupName().equals(selectedItem.getGroupName())).forEach(item -> {
                MenuItem mergeTargetItem = new MenuItem(item.getGroupName());
                mergeGroupIntoItem.getItems().add(mergeTargetItem);
            });
            if (mergeGroupIntoItem.getItems().isEmpty()) {
                MenuItem noTargetsItem = new MenuItem(resources.getString("light_list_context_menu_group_merge_into_no_groups"));
                noTargetsItem.setDisable(true);
                mergeGroupIntoItem.getItems().add(noTargetsItem);
            }


            groupMenu.getItems().add(addNewGroupedLightItem);
            groupMenu.getItems().add(renameGroupItem);
            groupMenu.getItems().add(disbandGroupItem);
            groupMenu.getItems().add(mergeGroupIntoItem);
        } else {
            assert selectedItem.isLight();
            groupMenu.setDisable(true);

            Menu moveLightIntoItem = new Menu(resources.getString("light_list_context_menu_light_move_into"));
            MenuItem removeLightItem = new MenuItem(resources.getString("light_list_context_menu_light_remove"));
            removeLightItem.setOnAction(_ -> {
                Boolean result = lightListController.removeItem();
                assert result;
            });
            MenuItem ungroupLightItem = new MenuItem(resources.getString("light_list_context_menu_light_ungroup"));

            lightListController.groupItemsStream().filter(
                    item -> !Objects.equals(item.getGroupName(), selectedItem.getGroupName())
            ).forEach(item -> {
                MenuItem moveTargetItem = new MenuItem(item.getGroupName());
                moveLightIntoItem.getItems().add(moveTargetItem);
            });

            if (selectedItem.isStandaloneLight()) {
                ungroupLightItem.setDisable(true);
            }

            lightMenu.getItems().add(removeLightItem);
            lightMenu.getItems().add(ungroupLightItem);
            lightMenu.getItems().add(moveLightIntoItem);
        }

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(addNewGroupItem);
        contextMenu.getItems().add(addNewLightItem);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().add(groupMenu);
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().add(lightMenu);

        return contextMenu;
    }

    private void openNewGroupedLightDialog(String groupName) {
        new NewGroupedLightDialogHelper(resources, lightListController, groupName).createAndOpenDialog(lightListController.getWindow());
    }

    private void openNewLightGroupDialog() {
        new NewLightGroupDialogHelper(resources, lightListController).createAndOpenDialog(lightListController.getWindow());
    }

    private void openNewStandaloneLightDialog() {
        new NewStandaloneLightDialogHelper(resources, lightListController).createAndOpenDialog(lightListController.getWindow());
    }

    private void openRenameGroupDialog(LightListItem groupItem) {
        assert groupItem.isGroup();
        SingleInputDialogViewController.createAndShow(
                resources.getString("project_rename_group_dialog_title_format").formatted(groupItem.getGroupName()),
                resources.getString("project_rename_group_dialog_prompt_format").formatted(groupItem.getGroupName()),
                resources.getString("project_rename_group_dialog_confirm_btn"),
                (controller) -> {
                    String newGroupName = controller.getInputValue().strip();

                    if (newGroupName.isEmpty()) {
                        controller.showError(resources.getString("project_rename_group_dialog_invalid_name_err"));
                        return;
                    }
                    if (!newGroupName.equals(groupItem.getGroupName()) && lightListController.doesGroupAlreadyExists(newGroupName)) {
                        controller.showError(resources.getString("project_rename_group_dialog_duplicate_err"));
                        return;
                    }

                    lightListController.groupedLightsStream(groupItem.getGroupName()).forEach(light -> light.setGroupName(newGroupName));
                    groupItem.setGroupName(newGroupName);

                    controller.close();
                },
                null,
                groupItem.getGroupName(),
                lightListController.getWindow()
        );
    }
}
