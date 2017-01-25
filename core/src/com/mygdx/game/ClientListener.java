/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Server.ExtraCommand;
import Server.WorldSettings;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Client;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author kristian
 */
public class ClientListener extends Listener {

    private GameWorld world;
    private String userName;
    private Client client;
    private HashMap<String, Entity> entitiesMap;
    private HashMap<String, DroppedItem> droppedItems;

    public ClientListener(Client client, GameWorld world, String userName) {
        this.client = client;
        this.userName = userName;
        this.world = world;
        this.entitiesMap = new HashMap();
        this.droppedItems = new HashMap();
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof Packets.Message) {
            Packets.Message m = (Packets.Message) object;
        }
        if (object instanceof Chunk) {
            Chunk chunk = (Chunk) object;
            //check if already existing.
            if (world.hasChunk(chunk)) {
                Chunk chunkToModify = world.getChunk(chunk.getX(), chunk.getY());
                chunkToModify.setClientControlling(chunk.getClientControlling());
                return;
            }
            //if not existing, initialize and add to lists.
            world.addObjectToBeAdded(chunk);
        }
        if (object instanceof Entity) {
            if (object instanceof Player) {
                Player player = (Player) object;
                if (player.getUsername().equals(userName)) {
                    if (world.getPlayer() == null) {
                        player.initialize();
                        world.setPlayer(player);
                        entitiesMap.put(player.getUId(), player);
                    }
                } else if (entitiesMap.containsKey(player.getUId())) {
                    Player playerToRemove = (Player) entitiesMap.get(player.getUId());
                    world.removeEntity(playerToRemove);
                    entitiesMap.put(player.getUId(), null);
                    if (!player.extraCommand.equals(ExtraCommand.LOGOUT)) { //If the player name equals Logout now, it's a message from the server to remove the player.
                        System.out.println("It doesn't!");
                        world.addEntity(player);
                        entitiesMap.put(player.getUId(), player);
                        player.initialize();
                        player.isNetworkObject = true;
                    }
                } else {
                    player.initialize();
                    player.isNetworkObject = true;
                    world.addEntity(player);
                    entitiesMap.put(player.getUId(), player);
                }
            }
        }
        if (object instanceof EntitySimpleType) {
            EntitySimpleType entity = (EntitySimpleType) object;

            //get Uid
            Entity entityToUpdate = entitiesMap.get(entity.uId);
            entityToUpdate.x = entity.x;
            entityToUpdate.y = entity.y;
            entityToUpdate.changeX = entity.changeX;
            entityToUpdate.changeY = entity.changeY;
        }
        if (object instanceof WorldSettings) {
            WorldSettings settings = (WorldSettings) object;
            world.applySettings(settings);
        }
        if (object instanceof WorldObject) {
            System.out.println("Transform!");
            WorldObject worldObject = (WorldObject) object;
            world.addObjectToBeAdded(worldObject);
        }
        if (object instanceof DroppedItem) {
            DroppedItem item = (DroppedItem) object;
            world.addObjectToBeAdded(item);
        }
        if (object instanceof Town) {
            Town town = (Town) object;
            world.addObjectToBeAdded(town);
        }
    }
}
