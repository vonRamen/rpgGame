/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Drawable;
import com.mygdx.game.Player;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kristian
 */
public class Sound2D {

    private Sound sound;
    private Vector2 playerPosition, objectPosition;
    private boolean isPlaying;
    private float angle;
    private long soundId;
    private float distance;

    public static String path = "audio/";
    public static HashMap<String, Sound2D> sounds;
    public static HashMap<String, Music> music;
    private static ArrayList<String> songQueue;
    private static Music currentSong;
    private static boolean isMusicPlaying;
    private static float timer;

    public Sound2D(String soundName) {
        FileHandle fileHandle = Gdx.files.internal(path + "sounds/" + soundName);
        Json json = new Json();
        this.sound = Gdx.audio.newSound(fileHandle);
    }

    public void play(Drawable obj1, Drawable obj2, boolean loop) {
        //Start the sound if not already started
        if (loop) {
            soundId = this.sound.loop();
        } else {
            soundId = this.sound.play();
        }
        playerPosition = new Vector2(obj1.getX(), obj1.getY());
        objectPosition = new Vector2(obj2.getX(), obj2.getY());
        //update panning according to the coordinates
        angle = playerPosition.angle(objectPosition);
        distance = playerPosition.dst2(objectPosition);

        float volumen = 1 - (distance / 300);
        System.out.println("Volumen: " + volumen);
        sound.setPan(1, 0, volumen);
    }

    public void stop() {
        this.sound.stop();
        this.sound.dispose();
    }

    public static void playMusic(String songName) {
        if (songQueue != null) {
        } else {
            songQueue = new ArrayList();
        }
        songQueue.add(songName);
    }

    public static void updateMusic(double deltaTime) {
        if (songQueue != null) {
            if (songQueue.size() == 1) {
                if (!isMusicPlaying && timer <= 0) {
                    isMusicPlaying = true;
                    currentSong = Gdx.audio.newMusic(new FileHandle(path + "music/" + songQueue.get(0)));
                    currentSong.setVolume(1.0f);
                    try {
                        currentSong.play();
                        currentSong.setLooping(true);
                    } catch(GdxRuntimeException exception) {
                        currentSong.setVolume(1.0f);
                        isMusicPlaying = false;
                    }
                }
            } else if (songQueue.size() > 1) {
                currentSong.setVolume(currentSong.getVolume() - (float) deltaTime);
                if(currentSong.getVolume() <= 0) {
                    songQueue.remove(0);
                    isMusicPlaying = false;
                    currentSong.setVolume(1);
                    currentSong.stop();
                }
            }
        }
        timer -= deltaTime;
    }

    public static Sound2D getSound(String path) {
        return sounds.get(path);
    }
}
