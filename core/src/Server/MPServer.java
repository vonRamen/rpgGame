/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Persistence.GameObject;
import Persistence.NoiseGenerator;
import Utility.Grid;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.GameWorld;
import com.mygdx.game.KKryo;
import com.mygdx.game.Player;
import com.mygdx.game.ServerListener;
import com.mygdx.game.WorldGenerator;
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
public class MPServer extends Thread {

    private String log;
    private String ip;
    private int sizeX, sizeY;
    private int port;
    private GameWorld world;
    private String extraPath;
    private Server server;
    private String worldName;
    private WorldSettings worldSettings;
    private boolean ready;
    private String[] arguments;
    
    public static void main(String[] args) {
        String[] newArgs = {"Test", "7777"};
        MPServer server = new MPServer(newArgs);
        server.start();
    }

    public MPServer(String[] args) {
        this.log = "Server is starting..";
        this.worldName = args[0];
        this.port = Integer.parseInt(args[1]);
        this.arguments = args;
    }

    private void registerPackets() {
        Kryo kryo = server.getKryo();
        KKryo.registerAll(kryo);
    }

    @Override
    public void run() {
        super.run();
        if (this.arguments.length == 4) {
            this.setLog("Beginning world generation");
            //name.getText(), "7777", width.getText(), height.getText()
            this.sizeX = Integer.parseInt(this.arguments[2]);
            this.sizeY = Integer.parseInt(this.arguments[3]);
            WorldGenerator wGen = WorldGenerator.generateWorld(this.worldName, this.sizeX, this.sizeY);
            wGen.generate(this);
            wGen.saveWorld("worlds/"+this.worldName+ "/");
            wGen = null;
            this.setLog("Done generating world!");
        }
        extraPath = "worlds/" + worldName + "/";
        Player.generate(extraPath + "players/", "Host", "");
        this.world = new GameWorld(true, extraPath, null);
        this.port = port;
        server = new Server(36384, 6048);
        server.addListener(new ServerListener(this));
        registerPackets();
        try {
            server.bind(port);
        } catch (IOException ex) {
            Logger.getLogger(MPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setLog("Starting server..");
        server.start();
        this.ready = true;
    }
    
    public void setLog(String log) {
        this.log = log;
    }

    public boolean isReady() {
        return this.ready;
    }

    public String getLog() {
        String returnLog = this.log;
        this.log = null;
        return returnLog;
    }

    public Server getServer() {
        return this.server;
    }

    public String getPath() {
        return this.extraPath;
    }

    public GameWorld getWorld() {
        return this.world;
    }

    public int getPort() {
        return this.port;
    }
}
