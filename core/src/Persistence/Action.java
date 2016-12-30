/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.mygdx.game.Drawable;
import com.mygdx.game.Entity;
import com.mygdx.game.Human;
import com.mygdx.game.WorldObject;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class Action {

    private String name;
    private int requiredCombatLevel;
    private int requiredWoodcuttingLevel;
    private int requiredMiningLevel;
    private int requiredfarmingSkill;
    private int requiredEquipmentId;
    private boolean doesRequireEquipment;
    private boolean doesTransform; //Does the tree transform into a stump after chop?
    private int transformIntoId;
    private boolean doesDrop;
    private ArrayList<DropItem> itemDrops;

    public Action() {

    }

    public boolean executeAction(Human human, Drawable object) {


        if(doesTransform) {
            if (object instanceof WorldObject) {
                WorldObject worldObject = (WorldObject) object;
                worldObject.setId(transformIntoId);
            }
        }
        return true;
    }
    
    public boolean canExecute(Human human, Drawable object) {
        if (requiredCombatLevel > human.getSkillCombat().getLevel()) {
            System.out.println("A Combat level " + requiredCombatLevel + " is required!");
            return false;
        }
        if (requiredMiningLevel > human.getSkillMining().getLevel()) {
            System.out.println("A Mining level " + requiredMiningLevel + " is required!");
            return false;
        }
        if (requiredfarmingSkill > human.getFarmingSkill().getLevel()) {
            System.out.println("A Farming level " + requiredfarmingSkill + " is required!");
            return false;
        }
        if (requiredWoodcuttingLevel > human.getSkillWoodcutting().getLevel()) {
            System.out.println("A Woodcutting level " + requiredWoodcuttingLevel + " is required!");
            return false;
        }
        if(doesRequireEquipment) { 
            if(!human.getInventory().hasItem(requiredEquipmentId)) {
                System.out.println("Item "+GameItem.get(requiredEquipmentId).name+" is required!");
                return false;
            }
        }
        return true;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
