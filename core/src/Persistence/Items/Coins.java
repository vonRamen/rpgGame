/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Items;

import Persistence.GameItem;
import com.mygdx.game.Game;

/**
 *
 * @author Kristian
 */
public class Coins extends GameItem {

    public Coins() {
        this.name = "coins";
        this.description = "The currency of the world!";
        this.basePrice = 1;
        this.id = 3;
        this.stackSize = 1000000;
        this.sprite = Game.textureHandler.generateRegion("items.png", 96, 0, 32, 32);
    }
}
