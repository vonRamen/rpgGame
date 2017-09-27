/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Objects;

import Persistence.Action;
import Persistence.DropItem;
import Persistence.GameObject;
import Persistence.TextureHandler.Region;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Entity;
import com.mygdx.game.Game;
import java.util.ArrayList;

/**
 *
 * @author Kristian
 */
public class Tree extends GameObject {

    public Tree() {
        this.id = 0;
        this.sprite = Game.textureHandler.generateRegion("world.png", new Region(5 * 32, 1 * 32, 32 * 3, 5 * 32));
        this.offsetX = -32;
        this.rectangle = new Rectangle(0, 0, 32, 32);
        this.name = "tree";
        
        Action action = new Action();
        action.setName("chop");
        action.setTransformIntoId(3);
        action.setSoundEffect("wood_chop.ogg");
        action.setSoundIsLooping(true);
        action.setMaxDistance(10000);
        action.setRequiredEquipmentId(4);
        action.setBaseTime(10);
        action.setRequiredSkill("woodcutting");
        action.setRequiredLevel(1);
        action.setExpGain(35);
        ArrayList<DropItem> drops = new ArrayList();
        drops.add(new DropItem(2, 1, 100));
        action.setItemDrops(drops);
        
        actions.add(action);
    }
}
