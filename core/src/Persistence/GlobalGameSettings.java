/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 *
 * @author kristian
 */
public class GlobalGameSettings {

    private static GlobalGameSettings entity;

    public static GlobalGameSettings getInstance() {
        if (entity == null) {
            Json json = new Json();
            FileHandle settingsFile = Gdx.files.internal("settings.json");
            GlobalGameSettings settings = null;
            if (settingsFile.exists()) {
                settings = json.fromJson(GlobalGameSettings.class, settingsFile);
            }
            if (settings == null) {
                entity = new GlobalGameSettings();
                entity.generateSettingsFile();
            } else {
                entity = settings;
            }
        }
        return entity;
    }

    private boolean isFullScreen = false;
    private boolean lockZoom;
    private float zoom = 1f;
    private float masterSoundVolume = 0.7f;
    private float masterMusicVolume = 1f;
    private int maxItemsDropped = 50;

    /**
     * @return the keybindings
     */
    public Keybindings getKeybindings() {
        return keybindings;
    }

    public enum Difficulty {
        PEACEFUL, EASY, NORMAL, HARD;
    }
    private Difficulty difficulty = Difficulty.NORMAL;
    private Keybindings keybindings = new Keybindings();

    private GlobalGameSettings() {

    }

    private void generateSettingsFile() {
        Json json = new Json();
        String output = json.toJson(this);
        FileHandle file = new FileHandle("settings.json");
        file.writeString(output, false);
        System.out.println("Generated a settings file!");
    }
}
