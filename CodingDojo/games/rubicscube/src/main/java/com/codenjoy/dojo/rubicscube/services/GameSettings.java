package com.codenjoy.dojo.rubicscube.services;

import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.codenjoy.dojo.services.settings.SettingsReader;

import static com.codenjoy.dojo.rubicscube.services.GameSettings.Keys.FAIL_PENALTY;
import static com.codenjoy.dojo.rubicscube.services.GameSettings.Keys.SUCCESS_SCORE;

public class GameSettings extends SettingsImpl implements SettingsReader<GameSettings> {

    public enum Keys implements Key {

        SUCCESS_SCORE("Success score"),
        FAIL_PENALTY("Fail penalty");

        private String key;

        Keys(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    public GameSettings() {
        addEditBox(SUCCESS_SCORE.key()).type(Integer.class).def(1000);
        addEditBox(FAIL_PENALTY.key()).type(Integer.class).def(500);
    }
}