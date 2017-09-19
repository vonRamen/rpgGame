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
public class EmptySpace extends GameItem {

    public EmptySpace() {
        this.id = 0;
        this.sprite = Game.textureHandler.generateRegion("items.png", 0, 0, 32, 32);
    }
    
}
