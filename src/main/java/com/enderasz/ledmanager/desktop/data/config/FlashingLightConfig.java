package com.enderasz.ledmanager.desktop.data.config;

import javafx.scene.paint.Color;

public class FlashingLightConfig implements LightConfig {
    private Color lightColor;
    private Integer timeLength;
    private Integer timeInterval;

    public FlashingLightConfig() {
        this(Color.WHITE, 1000, 1000);
    }

    public FlashingLightConfig(Color lightColor, Integer timeLength, Integer timeInterval) {
        this.lightColor = lightColor;
        this.timeLength = timeLength;
        this.timeInterval = timeInterval;
    }

    public Color getLightColor() {
        return lightColor;
    }

    public void setLightColor(Color lightColor) {
        this.lightColor = lightColor;
    }

    public Integer getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(Integer timeLength) {
        this.timeLength = timeLength;
    }

    public Integer getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    public byte[] toBytes() {
        StringBuilder builder = new StringBuilder();
        builder.append("FLASH");
        builder.append((char) 0);
        builder.append((int) Math.round(lightColor.getRed() * 255));
        builder.append((char) 0);
        builder.append((int) Math.round(lightColor.getGreen() * 255));
        builder.append((char) 0);
        builder.append((int) Math.round(lightColor.getBlue() * 255));
        builder.append((char) 0);
        builder.append(timeLength);
        builder.append((char) 0);
        builder.append(timeInterval);
        builder.append((char) 0);
        return builder.toString().getBytes();
    }
}
