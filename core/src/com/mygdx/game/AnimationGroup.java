/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.ReportCreatable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author kristian
 */
public class AnimationGroup implements ReportCreatable {

    private String name;
    //type: 0 = entity, 1 = hair
    private int type;
    private String fileName;

    //0 = walk, 1 = idle, 2 = swimming
    private String[] sheets;

    private ArrayList<Animation[]> groupAnimations;

    private static final String path = "animations/";
    private static HashMap<String, AnimationGroup> animations;
    private static FileHandle dirHandle;
    private static Json json;

    public AnimationGroup() {
        groupAnimations = new ArrayList();
    }

    public static void load() {
        json = new Json();
        animations = new HashMap();
        loadAll();
    }

    private static void loadAll() {
        dirHandle = Gdx.files.internal(path + "data/");
        for (FileHandle f : dirHandle.list()) {
            AnimationGroup object = json.fromJson(AnimationGroup.class, f);
            object.loadSprites();
            object.fileName = f.name();
            animations.put(object.name, object);
        }
    }

    private void loadSprites() {
        TextureRegion[][] dirAnims = new TextureRegion[4][3];
        TextureRegion[][] underWaterFrames = new TextureRegion[4][3];
        Texture texture = new Texture(path + "sprites/" + sheets[0]);
        TextureRegion[][] textureRegion = TextureRegion.split(texture, texture.getWidth() / 3, texture.getHeight() / 4);
        TextureRegion[] frames = new TextureRegion[3 * 4];

        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                frames[index++] = textureRegion[i][j];
                underWaterFrames[i][j] = new TextureRegion(textureRegion[i][j], 0, 0, 32, 16);
                dirAnims[i][j] = textureRegion[i][j];
            }
        }
        this.name = name;
        animations.put(this.name, this);
        this.groupAnimations.add(new Animation[4]);
        this.groupAnimations.add(new Animation[4]);
        this.groupAnimations.add(new Animation[4]);
        for (int i = 0; i < 4; i++) {
            Animation dirAnim = new Animation(0.15f, dirAnims[i]);
            Animation idleAnim = new Animation(0f, dirAnims[i]);
            Animation swimAnimation = new Animation(0.15f, underWaterFrames[i]);
            dirAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
            this.groupAnimations.get(0)[i] = dirAnim;
            this.groupAnimations.get(1)[i] = idleAnim;
            this.groupAnimations.get(2)[i] = swimAnimation;
        }
        checkExtraAnimations();
    }

    private void checkExtraAnimations() {
        if (sheets.length > 1) {
            for (int i = 1; i < sheets.length; i++) {
                if (!sheets[i].equals("null")) {
                    dirHandle = Gdx.files.internal(path + "sprites/");
                    Texture texture = new Texture(path + "sprites/" + sheets[i]);
                    TextureRegion[][] textureRegion = TextureRegion.split(texture, texture.getWidth() / 3, texture.getHeight() / 4);
                    TextureRegion[][] frames = new TextureRegion[4][3];
                    int index = 0;
                    for (int iy = 0; iy < 4; iy++) {
                        for (int ix = 0; ix < 3; ix++) {
                            frames[iy][ix] = textureRegion[iy][ix];
                        }
                    }

                    for (int xx = 0; xx < 4; xx++) {
                        Animation newAnimation = new Animation(0.15f, frames[xx]);
                        newAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
                            if(i >= groupAnimations.size()) {
                                this.groupAnimations.add(new Animation[4]);
                            }
                        this.groupAnimations.get(i)[xx] = newAnimation;
                    }
                }
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public static AnimationGroup getAnimation(int id, int type) {
        Iterator it = animations.entrySet().iterator();
        int count = 1;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            AnimationGroup animationGroup = (AnimationGroup) pair.getValue();
            if (animationGroup.type == type) {
                if (count == id) {
                    return animationGroup;
                } else {
                    count++;
                }
            }
        }
        return null;
    }

    public static AnimationGroup getAnimation(String animationName) {
        return animations.get(animationName);
    }
    
    public Animation get(int index, int dir) {
        return this.groupAnimations.get(index)[dir];
    }

    public Animation getDirectionalAnimation(int index) {
        return this.groupAnimations.get(0)[index];
    }

    public Animation getIdleAnimation(int index) {
        return this.groupAnimations.get(1)[index];
    }

    public Animation getUnderWaterAnimation(int index) {
        return this.groupAnimations.get(2)[index];
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public ArrayList<? extends ReportCreatable> getAll() {
        return new Utility.KUtility<AnimationGroup>().getArrayListOfMap(animations);
    }
}
