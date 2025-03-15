package com.enderasz.ledmanager.desktop.controllers.views.main.project.dialogs;

import com.enderasz.ledmanager.desktop.controllers.views.common.ConfirmDialogViewController;
import com.enderasz.ledmanager.desktop.controllers.views.common.DialogHelper;
import com.enderasz.ledmanager.desktop.controllers.views.common.SingleInputDialogViewController;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist.LightAlreadyExists;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist.LightListViewController;
import javafx.stage.Window;

import java.util.ResourceBundle;

public class NewGroupedLightDialogHelper implements DialogHelper {
    private final ResourceBundle resources;
    private final LightListViewController lightList;
    private final String groupName;

    public NewGroupedLightDialogHelper(ResourceBundle resources, LightListViewController lightList, String groupName) {
        this.resources = resources;
        this.lightList = lightList;
        this.groupName = groupName;
    }

    private void onLightCreationSubmit(SingleInputDialogViewController controller) {
        String newIdString = controller.getInputValue().strip();

        int newId;
        try {
            newId = Integer.parseInt(newIdString);
        } catch (NumberFormatException e) {
            controller.showError(resources.getString("project_new_grouped_light_dialog_invalid_format_err"));
            return;
        }
        if (newId < 0) {
            controller.showError(resources.getString("project_new_grouped_light_dialog_negative_id_err"));
            return;
        }

        try {
            lightList.addLightItem(newId, groupName);
        } catch (LightAlreadyExists e) {
            ConfirmDialogViewController.createAndShow(
                    resources.getString("project_new_grouped_light_dialog_confirm_title"),
                    resources.getString("project_new_grouped_light_dialog_confirm_prompt_format"),
                    resources.getString("project_new_grouped_light_dialog_confirm_confirm_btn"),
                    resources.getString("project_new_grouped_light_dialog_confirm_cancel_btn"),
                    () -> {
                        lightList.insertLightItem(newId, groupName);
                        controller.close();
                        // Confirm dialog is closed automatically on confirmation
                    },
                    controller.getWindow()
            );
            return;
        }

        controller.close();
    }

    public void createAndOpenDialog(Window owner) {
        SingleInputDialogViewController.createAndShow(
                resources.getString("project_new_grouped_light_dialog_title_format").formatted(groupName),
                resources.getString("project_new_grouped_light_dialog_prompt_format").formatted(groupName),
                resources.getString("project_new_grouped_light_dialog_submit_btn"),
                this::onLightCreationSubmit,
                null,
                lightList.getFirstFreeLightId().toString(),
                owner
        );
    }
}
