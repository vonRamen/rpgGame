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
public class MPServer {

    private static String log;
    private String ip;
    private int port;
    private Server server;
    private GameWorld world;
    private String extraPath;
    private String worldName;
    private WorldSettings worldSettings;

    public static void main(String[] args) {
        log = "";
        if(args.length == 4) {
            WorldGenerator wGen = WorldGenerator.generateWorld("worlds/"+args[0]+"/", Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            while(!wGen.isFinished()) {
                
            }
        }
        MPServer mpServer = new MPServer(args[0], Integer.parseInt(args[1]));
    }
    
    public static String getLog() {
        return log;
    }

    public MPServer(String worldName, int port) {
        worldName = "name";
        extraPath = "assets/worlds/" + worldName + "/";
        //WorldGenerator.generateWorld("assets/", 10, 10);
        world = new GameWorld(true, extraPath, null);
        Player.generate(extraPath + "players/", "Kristian", "ubv59mve");
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
