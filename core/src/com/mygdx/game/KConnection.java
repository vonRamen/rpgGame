/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.esotericsoftware.kryonet.Connection;

/**
 *
 * @author kristian
 */
public class KConnection extends Connection {
    private Player player;
    
    public KConnection() {

    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return player;
    }
}
