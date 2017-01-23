/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.math.Vector2;
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
    private int slotId;
    private int requiredLevel;
    private String requiredSkill;
    private int expGain;
    private float baseTime;
    private float maxDistance;
    private int requiredEquipmentId;
    private boolean doesRequireEquipment;
    private boolean doesTransform; //Does the tree transform into a stump after chop?
    private int transformIntoId;
    private boolean doesDrop;
    private ArrayList<DropItem> itemDrops;
    private ArrayList<DropItem> itemGives;
    private ArrayList<DropItem> itemTakes;
    private Random RGN;

    //Music and sounds
    private String soundEffect;
    private boolean soundIsLooping;
    private Sound2D activeSound;

    public Action() {
        RGN = new Random();
    }

    public Action(String name) {
        this.name = name;
    }

    public boolean executeAction(Human human, Drawable object) {

        //Gain exp:
        if (requiredSkill != null) {
            human.getSkill(this.requiredSkill).addExp(expGain);
        }

        if (object != null) {
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
        }

        //Remove and add items!
        if (itemTakes != null) {
            for (DropItem item : itemTakes) {
                human.getInventory().removeItem(item.getId(), item.getCount());
            }
        }
        if (itemGives != null) {
            for (DropItem item : itemGives) {
                human.getInventory().addItem(item.getId(), item.getCount());
            }
        }
        if (human instanceof Player) {
            ((Player) human).setInventoryUpdate(true);
        }
        if (activeSound != null) {
            activeSound.stop();
        }
        return true;
    }

    public boolean canExecute(Human human, Drawable object) {
        if (maxDistance != 0) {
            Vector2 humanPos = new Vector2(human.getX(), human.getY());
            if (humanPos.dst2(object.getX(), object.getY()) > maxDistance) {
                    if (human instanceof Player) {
                        System.out.println(humanPos.dst2(object.getX(), object.getY()));
                        Player player = (Player) human;
                        player.addAlert("Not close enough to perform action!");
                    }
                return false;
            }
        }
        if (itemTakes != null) {
            for (DropItem item : itemTakes) {
                if (!human.getInventory().hasItem(item.getId(), item.getCount())) {
                    if (human instanceof Player) {
                        Player player = (Player) human;
                        player.addAlert("Not sufficient amount of: " + GameItem.get(item.getId()).name);
                    }
                    return false;
                }
            }
        }
        if (requiredSkill != null) {
            if (requiredLevel > human.getSkill(requiredSkill).getLevel()) {
                if (human instanceof Player) {
                    Player player = (Player) human;
                    player.addAlert("A " + requiredSkill + " level " + requiredLevel + " is required!");
                }
                return false;
            }
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
        if (this.name.equals("drop")) {
            //then the "id" is actually the slot id
            human.getInventory().setEntity(human);
            human.getInventory().dropOnSlot(slotId);
            ((Player) human).setInventoryUpdate(true);
            return;
        }
        if (canExecute(human, object)) {
            if (this.soundEffect != null) {
                activeSound = new Sound2D(this.soundEffect);
                Player player = human.getWorld().getPlayer();
                activeSound.play(human.getWorld().getPlayer(), human, soundIsLooping);
            } else {
                System.out.println("Sound missing! " + soundEffect);
            }
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

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }
}
