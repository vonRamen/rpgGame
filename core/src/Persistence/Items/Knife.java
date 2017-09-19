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
public class Knife extends GameItem {

    public Knife() {
        this.name = "knife";
        this.description = "This is a common knife used for many different things.";
        this.basePrice = 100;
        this.id = 1;
        this.sprite = Game.textureHandler.generateRegion("items.png", 32, 0, 32, 32);
    }
    
}
