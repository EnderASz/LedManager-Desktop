package com.enderasz.ledmanager.desktop.controllers.views.main.project.dialogs;

import com.enderasz.ledmanager.desktop.controllers.views.common.DialogHelper;
import com.enderasz.ledmanager.desktop.controllers.views.common.SingleInputDialogViewController;
import com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist.LightListItem;
import javafx.scene.control.TreeTableView;
import javafx.stage.Window;

import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ProjectNameChangeDialogHelper implements DialogHelper {
    private final ResourceBundle resources;
    private final Supplier<String> defaultNameSupplier;
    private final Consumer<String> newNameConsumer;

    public ProjectNameChangeDialogHelper(ResourceBundle resources, Supplier<String> defaultNameSource, Consumer<String> newNameConsumer) {
        this.resources = resources;
        this.defaultNameSupplier = defaultNameSource;
        this.newNameConsumer = newNameConsumer;
    }

    @Override
    public void createAndOpenDialog(Window owner) {
        SingleInputDialogViewController.createAndShow(
                resources.getString("project_change_name_dialog_title"),
                resources.getString("project_change_name_dialog_prompt"),
                resources.getString("project_change_name_dialog_save_btn"),
                this::onSubmit,
                null,
                defaultNameSupplier.get(),
                owner
        );
    }

    private void onSubmit(SingleInputDialogViewController controller) {
        String newName = controller.getInputValue().strip();

        if (newName.isEmpty()) {
            controller.showError(resources.getString("project_change_name_dialog_err"));
            return;
        }

        newNameConsumer.accept(newName);
        controller.close();
    }
}
