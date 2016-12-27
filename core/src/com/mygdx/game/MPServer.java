/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.GameObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kristian
 */
public class MPServer {

    private String ip;
    private int port;
    private Server server;
    private GameWorld world;
    private String extraPath;
    private String worldName;
    
    public static void main(String[] args) {
        MPServer mpServer = new MPServer(7777);
    }

    public MPServer(int port) {
        worldName = "name";
        extraPath = "assets/worlds/"+worldName+"/";
        world = new GameWorld(true, extraPath);
        Player.generate(extraPath+"players/", "Mathias", "ubv59mve");
        this.port = port;
        server = new Server();
        server.addListener(new ServerListener(server, world, extraPath));
        registerPackets();
        try {
            server.bind(port);
        } catch (IOException ex) {
            Logger.getLogger(MPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        server.start();
    }

    private void registerPackets() {
        Kryo kryo = server.getKryo();
        KKryo.registerAll(kryo);
    }

}
