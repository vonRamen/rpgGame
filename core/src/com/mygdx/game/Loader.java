/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Tile;
import Persistence.GameObject;
import com.badlogic.gdx.ApplicationAdapter;

/**
 *
 * @author kristian
 */
public class Loader extends ApplicationAdapter implements Runnable {

    private static boolean isLoaded = false;

    public static boolean isLoaded() {
        return isLoaded;
    }
    
    public static void loadAll() {
        new Thread(new Loader()).start();
    }

    @Override
    public void run() {
        GameObject.loadObjects();
        Tile.load();
        System.out.println("here");
        isLoaded = true;
    }
}
