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
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Drawable;
import com.mygdx.game.Player;
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
    private static Music currentSong;

    public Sound2D(String soundName) {
        FileHandle fileHandle = Gdx.files.internal(path + "sounds/"+soundName);
        Json json = new Json();
        this.sound = Gdx.audio.newSound(fileHandle);
    }
    
    public void play(Drawable obj1, Drawable obj2, boolean loop) {
        //Start the sound if not already started
        if(loop) {
            soundId = this.sound.loop();
        } else {
            soundId = this.sound.play();
        }
        playerPosition = new Vector2(obj1.getX(), obj1.getY());
        objectPosition = new Vector2(obj2.getX(), obj2.getY());
        //update panning according to the coordinates
        angle = playerPosition.angle(objectPosition);
        distance = playerPosition.dst2(objectPosition);
        
        float volumen = 1-(distance/300);
        System.out.println("Volumen: "+volumen);
        sound.setPan(1, 0, volumen);
    }
    
    public void stop() {
        this.sound.stop();
        this.sound.dispose();
    }

    public static void playMusic(String songName) {
        FileHandle newSong = new FileHandle(path + "music/" + songName);
        currentSong = Gdx.audio.newMusic(newSong);
        currentSong.play();
    }

    public static Sound2D getSound(String path) {
        return sounds.get(path);
    }
}
