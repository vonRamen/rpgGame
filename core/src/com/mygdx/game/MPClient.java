/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kristian
 */
public class MPClient {

    private GameWorld world;
    private Client client;
    private String playerName;
    private String playerPassword;
    
    public static void main(String[] args) {
        //MPClient client = new MPClient(7777);
    }
    
    public MPClient(int port, GameWorld world, String playerName, String playerPassword) {
        this.playerName = playerName;
        this.playerPassword = playerPassword;
        client = new Client(10000, 10000);
        registerPackets();
        client.addListener(new ClientListener(getClient(), world, playerName));
        client.start();
        try {
            client.connect(5000, "127.0.0.1", port);
            Packets.RequestAccess request = new Packets.RequestAccess();
            request.name = playerName;
            request.password = playerPassword;
            client.sendTCP(request);
        } catch (IOException ex) {
            System.out.println("Connection is not possible.."+ex);
        }
    }
    
    private void registerPackets() {
        Kryo kryo = getClient().getKryo();
        KKryo.registerAll(kryo);
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }
}
