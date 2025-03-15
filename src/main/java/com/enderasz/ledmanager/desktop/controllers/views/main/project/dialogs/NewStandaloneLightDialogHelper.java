package com.enderasz.ledmanager.desktop.controllers.views.main.project.dialogs;

import com.enderasz.ledmanager.desktop.controllers.views.common.ConfirmDialogViewController;
import com.enderasz.ledmanager.desktop.controllers.views.common.DialogHelper;
import com.enderasz.ledmanager.desktop.controllers.views.common.SingleInputDialogViewController;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist.LightAlreadyExists;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist.LightListViewController;

import javafx.stage.Window;

import java.util.ResourceBundle;


public final class NewStandaloneLightDialogHelper implements DialogHelper {
    private final ResourceBundle resources;
    private final LightListViewController lightList;

    public NewStandaloneLightDialogHelper(ResourceBundle resources, LightListViewController lightList) {
        this.resources = resources;
        this.lightList = lightList;
    }

    private void onLightCreationSubmit(SingleInputDialogViewController controller) {
        String newIdString = controller.getInputValue().strip();

        int newId;
        try {
            newId = Integer.parseInt(newIdString);
        } catch (NumberFormatException e) {
            controller.showError(resources.getString("project_new_standalone_light_dialog_invalid_format_err"));
            return;
        }
        if (newId < 0) {
            controller.showError(resources.getString("project_new_standalone_light_dialog_negative_id_err"));
            return;
        }

        try {
            lightList.addLightItem(newId);
        } catch (LightAlreadyExists e) {
            ConfirmDialogViewController.createAndShow(
                    "Dodawanie diody o już istniejącym ID",
                    "Czy napewno chcesz dodać diodę o już istniejącym ID{%d}?\nDodanie tej diody spowoduje przesunięcie ID o 1 dla już istniejącej oraz również potencjalnie wszystkich lub części po niej następujących.".formatted(newId),
                    "Tak",
                    "Nie",
                    () -> {
                        lightList.insertLightItem(newId);
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
                resources.getString("project_new_standalone_light_dialog_title"),
                resources.getString("project_new_standalone_light_dialog_prompt"),
                resources.getString("project_new_standalone_light_dialog_confirm_btn"),
                this::onLightCreationSubmit,
                null,
                lightList.getFirstFreeLightId().toString(),
                owner
        );
    }
}
