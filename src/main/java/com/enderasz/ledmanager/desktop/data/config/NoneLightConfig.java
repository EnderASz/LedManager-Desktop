package com.enderasz.ledmanager.desktop.data.config;

public class NoneLightConfig implements LightConfig {
    @Override
    public byte[] toBytes() {
        String builder = "NONE" + (char) 0;
        return builder.getBytes();
    }
}
