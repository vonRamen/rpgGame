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

    public MPServer(String[] args) {
        this.log = "";
        this.port = Integer.parseInt(args[1]);
        this.arguments = args;
        this.start();

        //Start world generation if needed!
        //Player.generate(extraPath + "players/", "Kristian", "ubv59mve");
    }

    private void registerPackets() {
        Kryo kryo = server.getKryo();
        KKryo.registerAll(kryo);
    }

    @Override
    public void run() {
        super.run();
        if (this.arguments.length == 4) {
            //name.getText(), "7777", width.getText(), height.getText()
            this.worldName = this.arguments[0];
            this.sizeX = Integer.parseInt(this.arguments[2]);
            this.sizeY = Integer.parseInt(this.arguments[3]);
            WorldGenerator wGen = WorldGenerator.generateWorld(this.worldName, this.sizeX, this.sizeY);
            while (!wGen.isFinished()) {
                String log = wGen.getLog();
                if (log != null || log != "") {
                    this.log = log;
                }
            }
            wGen = null;
            this.log = ("Done generating world!");
        }
        extraPath = "assets/worlds/" + worldName + "/";
        //WorldGenerator.generateWorld("assets/", 10, 10);
        this.world = new GameWorld(true, extraPath, null);
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
        this.ready = true;
    }

    public boolean isReady() {
        return this.ready;
    }

    public String getLog() {
        return log;
    }
}
