package com.enderasz.ledmanager.desktop.controllers.views.main.project.lightlist;

import com.enderasz.ledmanager.desktop.data.config.LightConfig;
import com.enderasz.ledmanager.desktop.data.config.NoneLightConfig;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeTableColumn;

public class LightListItem {
    private final StringProperty groupName = new SimpleStringProperty(null);
    private final StringProperty lightIdColumnValue = new SimpleStringProperty(null);
    private Integer lightIdValue = null;

    private LightConfig config = null;

    public LightListItem(String groupName) {
        setGroupName(groupName);
    }

    public LightListItem(String groupName, Integer lightId) {
        this(groupName);
        setLightId(lightId);
    }

    public String getGroupName() {
        return groupName.get();
    }

    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }

    public Integer getLightId() {
        return lightIdValue;
    }

    public void setLightId(Integer lightId) {
        this.lightIdValue = lightId;
        this.lightIdColumnValue.set(lightId != null ? lightId.toString() : null);
    }

    public boolean isLight() {
        return lightIdValue != null;
    }

    public boolean isStandaloneLight() {
        return isLight() && groupName.get() == null;
    }

    public boolean isGroupedLight() {
        return isLight() && groupName.get() != null;
    }

    public boolean isGroup() {
        return groupName.get() != null && !isLight();
    }

    public LightConfig getConfig() {
        if(config == null) {
            return new NoneLightConfig();
        }
        return config;
    }

    public void setConfig(LightConfig config) {
        this.config = config;
    }

    public static void setGroupNameColumnCellValueFactory(TreeTableColumn<LightListItem, String> column) {
        column.setCellValueFactory(data -> data.getValue().getValue().groupName);
    }

    public static void setLightIdColumnCellValueFactory(TreeTableColumn<LightListItem, String> column) {
        column.setCellValueFactory(data -> data.getValue().getValue().lightIdColumnValue);
    }
}
