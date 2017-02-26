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
                entity.save();
            } else {
                entity = settings;
            }
        }
        return entity;
    }
    
    public static GlobalGameSettings setDefaultValue() {
        entity = new GlobalGameSettings();
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

    /**
     * @return the isFullScreen
     */
    public boolean isIsFullScreen() {
        return isFullScreen;
    }

    /**
     * @param isFullScreen the isFullScreen to set
     */
    public void setIsFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    /**
     * @return the lockZoom
     */
    public boolean isLockZoom() {
        return lockZoom;
    }

    /**
     * @param lockZoom the lockZoom to set
     */
    public void setLockZoom(boolean lockZoom) {
        this.lockZoom = lockZoom;
    }

    /**
     * @return the zoom
     */
    public float getZoom() {
        return zoom;
    }

    /**
     * @param zoom the zoom to set
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    /**
     * @return the masterSoundVolume
     */
    public float getMasterSoundVolume() {
        return masterSoundVolume;
    }

    /**
     * @param masterSoundVolume the masterSoundVolume to set
     */
    public void setMasterSoundVolume(float masterSoundVolume) {
        this.masterSoundVolume = masterSoundVolume;
    }

    /**
     * @return the masterMusicVolume
     */
    public float getMasterMusicVolume() {
        return masterMusicVolume;
    }

    /**
     * @param masterMusicVolume the masterMusicVolume to set
     */
    public void setMasterMusicVolume(float masterMusicVolume) {
        this.masterMusicVolume = masterMusicVolume;
    }

    /**
     * @return the maxItemsDropped
     */
    public int getMaxItemsDropped() {
        return maxItemsDropped;
    }

    /**
     * @param maxItemsDropped the maxItemsDropped to set
     */
    public void setMaxItemsDropped(int maxItemsDropped) {
        this.maxItemsDropped = maxItemsDropped;
    }

    public enum Difficulty {
        PEACEFUL, EASY, NORMAL, HARD;
    }
    private Difficulty difficulty = Difficulty.NORMAL;
    private Keybindings keybindings = new Keybindings();

    private GlobalGameSettings() {

    }

    public void save() {
        Json json = new Json();
        String output = json.toJson(this);
        FileHandle file = new FileHandle("settings.json");
        file.writeString(output, false);
    }
}
