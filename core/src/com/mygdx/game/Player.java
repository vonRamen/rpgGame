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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author kristian
 */
public class Player extends Human {

    private String userName;
    private String userPassword;
    private String path;
    private String lastTownUId;
    private boolean hasUpdatedInventory;

    //whether or not the player wants to see begin walls.
    private boolean xray;

    //alerts is used to send alert windows to gui
    private ArrayList<Alert> alerts;

    public Player(GameWorld world) {
        super(world);
        alerts = new ArrayList();
    }

    public Player() {
        super();
        alerts = new ArrayList();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        //check for town "collision"
        Iterator townIterator = this.world.getTowns().entrySet().iterator();
        Town town = this.world.getTownAtPoint((int) x, (int) y);
        if (town != null) {
            if (!town.getuId().equals(this.lastTownUId)) {
                lastTownUId = town.getuId();
                this.addAlert("Arrived at " + town.getName() + "\nDescription: " + town.getDescription(), AlertType.SCREEN);
            }
        }
    }

    public static void generate(String path, String userName, String userPassword) {
        Player player = new Player();
        player.userName = userName;
        player.userPassword = userPassword;
        player.inventory = new Inventory(28);
        player.addSkills();
        player.path = path;
        player.setuId(UUID.randomUUID().toString());
        FileHandle fileHandle = new FileHandle(path + userName + ".json");

        Json json = new Json();
        String string = json.toJson(player);
        fileHandle.writeString(string, false);
    }

    public static Player get(String path, String userName, String userPassword) {
        FileHandle fileHandle = new FileHandle(path + userName + ".json");
        Json json = new Json();
        Player player = null;
        try {
            player = json.fromJson(Player.class, fileHandle);
        } catch (com.badlogic.gdx.utils.SerializationException e) {
            generate(path, userName, userPassword);
            player = json.fromJson(Player.class, fileHandle);
        }
        return player;
    }

    public Alert getAlert() {
        if (alerts.isEmpty()) {
            return null;
        }
        Alert returnAlert = alerts.get(0);
        alerts.remove(0);
        return returnAlert;
    }

    public void addAlert(String string, AlertType type) {
        alerts.add(new Alert(string, type));
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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setInventoryUpdate(boolean isUpdated) {
        hasUpdatedInventory = isUpdated;
    }

    public boolean hasUpdatedInventory() {
        return hasUpdatedInventory;
    }

    void saveProgress() {
        FileHandle fileHandle = new FileHandle(path + userName + ".json");

        Json json = new Json();
        String string = json.toJson(this);
        fileHandle.writeString(string, false);
        System.out.println("Player succesfully saved: " + userName);
    }

    /**
     * @return the lastTownUId
     */
    public String getLastTownUId() {
        return lastTownUId;
    }

    /**
     * @return the xray
     */
    public boolean isXray() {
        return xray;
    }

    /**
     * @param xray the xray to set
     */
    public void setXray(boolean xray) {
        this.xray = xray;
    }
}
