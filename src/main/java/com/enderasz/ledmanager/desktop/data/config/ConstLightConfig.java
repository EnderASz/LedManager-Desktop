package com.enderasz.ledmanager.desktop.data.config;

import javafx.scene.paint.Color;

public class ConstLightConfig implements LightConfig {
    private Color lightColor;

    public ConstLightConfig() {
        this(Color.WHITE);
    }

    public ConstLightConfig(Color lightColor) {
        this.lightColor = lightColor;
    }

    public Color getLightColor() {
        return lightColor;
    }

    public void setLightColor(Color lightColor) {
        this.lightColor = lightColor;
    }

    @Override
    public byte[] toBytes() {
        StringBuilder builder = new StringBuilder();
        builder.append("CONST");
        builder.append((char) 0);
        builder.append((int) Math.round(lightColor.getRed() * 255));
        builder.append((char) 0);
        builder.append((int) Math.round(lightColor.getGreen() * 255));
        builder.append((char) 0);
        builder.append((int) Math.round(lightColor.getBlue() * 255));
        builder.append((char) 0);

        return builder.toString().getBytes();
    }
}
