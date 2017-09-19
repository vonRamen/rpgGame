/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Items;

import Persistence.Action;
import Persistence.DropItem;
import Persistence.GameItem;
import com.mygdx.game.Game;
import java.util.ArrayList;

/**
 *
 * @author Kristian
 */
public class Logs extends GameItem {

    public Logs() {
        this.name = "logs";
        this.description = "This is a common log found throughout the world. Needs refinement.";
        this.basePrice = 15;
        this.id = 2;
        this.sprite = Game.textureHandler.generateRegion("items.png", 64, 0, 32, 32);
        
        Action fletch = new Action();
        ArrayList<DropItem> takes = new ArrayList();
        ArrayList<DropItem> gives = new ArrayList();
        takes.add(new DropItem(2, 1, 100));
        gives.add(new DropItem(1, 1, 100));
        fletch.setItemGives(gives);
        fletch.setItemTakes(takes);
        fletch.setName("fletch");
        this.actions.add(fletch);
    }
    
}
