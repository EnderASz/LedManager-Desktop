package com.enderasz.ledmanager.desktop;

import java.util.Locale;
import java.util.ResourceBundle;

public class ApplicationPreferences {
    Locale locale = Locale.forLanguageTag("pl-PL");
    private volatile ResourceBundle languageBundle;

    private static volatile ApplicationPreferences instance;

    private ApplicationPreferences() {

    }

    public static ApplicationPreferences getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ApplicationPreferences.class) {
            if (instance == null) {
                instance = new ApplicationPreferences();
            }
            return instance;
        }
    }

    public Locale getLocale() {
        return locale;
    }

    private synchronized void loadLanguageBundle() {
        languageBundle = ResourceBundle.getBundle("/com/enderasz/ledmanager/desktop/language", locale);
    }

    public ResourceBundle getLanguageBundle() {
        if (languageBundle != null) {
            return languageBundle;
        }
        loadLanguageBundle();
        return languageBundle;
    }
}
