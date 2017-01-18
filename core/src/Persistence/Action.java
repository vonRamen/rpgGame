/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.mygdx.game.Drawable;
import com.mygdx.game.Entity;
import com.mygdx.game.EntityAction;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Human;
import com.mygdx.game.Player;
import com.mygdx.game.WorldObject;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author kristian
 */
public class Action {
    
    private String name;
    private int requiredLevel;
    private String requiredSkill;
    private int expGain;
    private float baseTime;
    private int requiredEquipmentId;
    private boolean doesRequireEquipment;
    private boolean doesTransform; //Does the tree transform into a stump after chop?
    private int transformIntoId;
    private boolean doesDrop;
    private ArrayList<DropItem> itemDrops;
    private Random RGN;
    
    public Action() {
        RGN = new Random();
    }
    
    public boolean executeAction(Human human, Drawable object) {

        //Gain exp:
        human.getSkill(this.requiredSkill).addExp(expGain);
        
        if (object instanceof WorldObject) {
            if (doesTransform) {
                WorldObject worldObject = (WorldObject) object;
                worldObject.setId(transformIntoId);
            }
            if (doesDrop) {
                //random number generator
                float dropPercent = RGN.nextFloat() * 100;
                GameWorld world = human.getWorld();
                for (DropItem item : itemDrops) {
                    if (dropPercent < item.getChance()) {
                        //Make sure, at least one item is dropped.
                        int itemCount = RGN.nextInt(item.getCount() + 1);
                        if (itemCount == 0) {
                            itemCount = 1;
                        }
                        System.out.println("Item Count: " + itemCount);
                        for (int i = 0; i < itemCount; i++) {
                            world.spawnItem(item.getId(), 1, (int) object.getX() + RGN.nextInt(32) - 16, (int) object.getY() - RGN.nextInt(16) - 16);
                        }
                        
                    }
                    dropPercent = RGN.nextFloat() * 100;
                }
            }
        }
        return true;
    }
    
    public boolean canExecute(Human human, Drawable object) {
        if (requiredLevel > human.getSkill(requiredSkill).getLevel()) {
            if (human instanceof Player) {
                Player player = (Player) human;
                player.addAlert("A " + requiredSkill + " level " + requiredLevel + " is required!");
            }
            return false;
        }
        if (doesRequireEquipment) {
            if (!human.getInventory().hasItem(requiredEquipmentId, 1)) {
                if (human instanceof Player) {
                    Player player = (Player) human;
                    player.addAlert("Item " + GameItem.get(requiredEquipmentId).name + " is required!");
                }
                return false;
            }
        }
        return true;
    }
    
    public void initializeExecution(Human human, Drawable object) {
        if (canExecute(human, object)) {
            human.setAction(new EntityAction(this, human, object));
            human.setActionDuration(2);
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
