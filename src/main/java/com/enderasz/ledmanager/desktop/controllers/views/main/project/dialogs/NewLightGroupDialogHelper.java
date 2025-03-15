package com.enderasz.ledmanager.desktop.controllers.views.main.project.dialogs;

import com.enderasz.ledmanager.desktop.controllers.views.common.DialogHelper;
import com.enderasz.ledmanager.desktop.controllers.views.common.SingleInputDialogViewController;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist.LightGroupAlreadyExists;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist.LightListViewController;
import javafx.stage.Window;

import java.util.ResourceBundle;

public final class NewLightGroupDialogHelper implements DialogHelper {
    private final ResourceBundle resources;
    private final LightListViewController lightList;

    public NewLightGroupDialogHelper(ResourceBundle resources, LightListViewController lightList) {
        this.resources = resources;
        this.lightList = lightList;
    }

    @Override
    public void createAndOpenDialog(Window owner) {
        SingleInputDialogViewController.createAndShow(
                resources.getString("project_new_light_group_dialog_title"),
                resources.getString("project_new_light_group_dialog_prompt"),
                resources.getString("project_new_light_group_dialog_confirm_btn"),
                this::onSubmit,
                null,
                null,
                owner
        );
    }

    private void onSubmit(SingleInputDialogViewController controller) {
        String newGroupName = controller.getInputValue().strip();

        if (newGroupName.isEmpty()) {
            controller.showError(resources.getString("project_new_light_group_dialog_invalid_name_err"));
            return;
        }
        try {
            lightList.addLightGroupItem(newGroupName);
        } catch (LightGroupAlreadyExists e) {
            controller.showError(resources.getString("project_new_light_group_dialog_duplicate_err"));
            return;
        }

        controller.close();
    }
}
