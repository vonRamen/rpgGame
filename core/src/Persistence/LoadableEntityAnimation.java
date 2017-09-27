/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.graphics.g2d.Animation;
import java.util.TreeMap;

/**
 *
 * @author Kristian
 */
public abstract class LoadableEntityAnimation extends PersistenceFile {

    protected TreeMap<String, Animation[]> animations;

    public LoadableEntityAnimation() {
        this.animations = new TreeMap();
    }

    public Animation getAnimation(String filename, int direction) {
        return animations.get(filename)[direction];
    }
}
