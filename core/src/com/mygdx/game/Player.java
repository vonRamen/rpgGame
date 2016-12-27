/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import java.util.UUID;

/**
 *
 * @author kristian
 */
public class Player extends Human {

    private String userName;
    private String userPassword;

    public Player(GameWorld world) {
        super(world);
    }

    public Player() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();
    }
    
    
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
    }

    public static void generate(String path, String userName, String userPassword) {
        Player player = new Player();
        player.userName = userName;
        player.userPassword = userPassword;
        player.inventory = new Inventory(28);
        player.addSkills();
        FileHandle fileHandle = new FileHandle(path + userName + ".json");

        Json json = new Json();
        String string = json.toJson(player);
        fileHandle.writeString(string, false);
    }

    public static Player get(String path, String userName, String userPassword) {
        FileHandle fileHandle = new FileHandle(path + userName + ".json");
        Json json = new Json();
        Player player = json.fromJson(Player.class, fileHandle);
        if(player!=null) {
            if(player.userPassword.equals(userPassword)) {
                player.uId = UUID.randomUUID().toString();
                return player;
            }
        }
        return null;
    }
    
    public String getUsername() {
        return userName;
    }

    public static Player create(GameWorld world, int x, int y) {
        Player player = new Player(world);
        player.setX(x);
        player.setY(y);
        return player;
    }
}
