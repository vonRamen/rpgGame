/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class KAnimation extends Animation {

    private static ArrayList<KAnimation> animations;
    private Animation[] directionalAnimation;
    private Animation[] idleAnimation;
    private static final String path = "animations/";
    private String name;
    private static FileHandle dirHandle;

    public KAnimation(float frameDuration, TextureRegion... keyFrames) {
        super(frameDuration, keyFrames);
        directionalAnimation = new Animation[4];
        idleAnimation = new Animation[4];
    }

    public static void load() {
        animations = new ArrayList();
        TextureRegion[][] dirAnims = new TextureRegion[4][3];
        dirHandle = Gdx.files.internal(path);
        for (FileHandle f : dirHandle.list()) {
            String fileName = f.name();
            Texture texture = new Texture(path + f.name());
            TextureRegion[][] textureRegion = TextureRegion.split(texture, texture.getWidth() / 3, texture.getHeight() / 4);
            TextureRegion[] frames = new TextureRegion[3 * 4];
            int index = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    frames[index++] = textureRegion[i][j];
                    dirAnims[i][j] = textureRegion[i][j];
                }
            }
            KAnimation newAnimation = new KAnimation(0.15f, frames);
            newAnimation.name = fileName;
            System.out.println(newAnimation.name);
            animations.add(newAnimation);
            for (int i = 0; i < 4; i++) {
                KAnimation dirAnim = new KAnimation(0.15f, dirAnims[i]);
                KAnimation idleAnim = new KAnimation(0f, dirAnims[i]);
                dirAnim.setPlayMode(PlayMode.LOOP_PINGPONG);
                newAnimation.directionalAnimation[i] = dirAnim;
                newAnimation.idleAnimation[i] = idleAnim;
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public static KAnimation getAnimation(int id) {
        return animations.get(id);
    }

    public Animation getDirectionalAnimation(int index) {
        return directionalAnimation[index];
    }

    public Animation getIdleAnimation(int index) {
        return idleAnimation[index];
    }

    public void directionalAnimation(Animation animation) {

    }

    public static KAnimation getAnimation(String name) {
        for (KAnimation k : animations) {
            if (k.name.equals(name)) {
                return k;
            }
        }
        return null;
    }
}
