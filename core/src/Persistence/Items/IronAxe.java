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
public class IronAxe extends GameItem {

    public IronAxe() {
        this.name = "iron axe";
        this.description = "This is a commonly used axe, due to the price and the quality. It is quite the mix.";
        this.basePrice = 155;
        this.id = 4;
        this.sprite = Game.textureHandler.generateRegion("items.png", 128, 0, 32, 32);
    }
    
}
