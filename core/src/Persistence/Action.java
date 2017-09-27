/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.AlertType;
import com.mygdx.game.Drawable;
import com.mygdx.game.Entity;
import com.mygdx.game.EntityAction;
import com.mygdx.game.Game;
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
    private float maxDistance = 10000;
    private int requiredEquipmentId = 0;
    private int transformIntoId = -1;
    private boolean transforms;
    private ArrayList<DropItem> itemDrops;
    private ArrayList<DropItem> itemGives;
    private ArrayList<DropItem> itemTakes;
    private Random RGN;

    //Music and sounds
    private String soundEffect;
    private boolean soundIsLooping;
    private Sound2D activeSound;

    //Permissions
    //0 : everybody
    //1 : guest
    //2 : builder
    //3 : owner
    private int permissionLevel;

    public Action() {
        RGN = new Random();
    }

    public Action(String name) {
        super();
        this.name = name;
    }

    public boolean executeAction(Human human, Drawable object) {

        //Gain exp:
        if (getRequiredSkill() != null) {
            human.getSkill(this.getRequiredSkill()).addExp(getExpGain());
        }

        if (object != null) {
            if (object instanceof WorldObject) {
                if (doesTransform()) {
                    WorldObject worldObject = (WorldObject) object;
                    worldObject.setId(getTransformIntoId());
                }
                if (dropsItem()) {
                    //random number generator
                    float dropPercent = getRGN().nextFloat() * 100;
                    GameWorld world = human.getWorld();
                    for (DropItem item : getItemDrops()) {
                        if (dropPercent < item.getChance()) {
                            //Make sure, at least one item is dropped.
                            int itemCount = getRGN().nextInt(item.getCount() + 1);
                            if (itemCount == 0) {
                                itemCount = 1;
                            }
                            System.out.println("Item Count: " + itemCount);
                            for (int i = 0; i < itemCount; i++) {
                                world.spawnItem(item.getId(), 1, (int) object.getX() + getRGN().nextInt(32) - 16, (int) object.getY() - getRGN().nextInt(16) - 16);
                            }

                        }
                        dropPercent = getRGN().nextFloat() * 100;
                    }
                }
            }
        }

        //Remove and add items!
        if (getItemTakes() != null) {
            for (DropItem item : getItemTakes()) {
                human.getInventory().removeItem(item.getId(), item.getCount());
            }
        }
        if (getItemGives() != null) {
            for (DropItem item : getItemGives()) {
                human.getInventory().addItem(item.getId(), item.getCount());
            }
        }
        if (human instanceof Player) {
            ((Player) human).setInventoryUpdate(true);
        }
        if (getActiveSound() != null) {
            getActiveSound().stop();
        }
        this.executeSpecialEvent(human);
        return true;
    }

    public boolean canExecute(Human human, Drawable object) {
        if (getMaxDistance() != 0) {
            Vector2 humanPos = new Vector2(human.getX(), human.getY());
            if (humanPos.dst2(object.getX(), object.getY()) > getMaxDistance()) {
                if (human instanceof Player) {
                    System.out.println(humanPos.dst2(object.getX(), object.getY()));
                    Player player = (Player) human;
                    player.addAlert("Not close enough to perform action!", AlertType.WORLD);
                }
                return false;
            }
        }
        if (getItemTakes() != null) {
            for (DropItem item : getItemTakes()) {
                if (!human.getInventory().hasItem(item.getId(), item.getCount())) {
                    if (human instanceof Player) {
                        Player player = (Player) human;
                        player.addAlert("Not sufficient amount of: " + Game.objectManager.getGameItem(item.getId(), false).name, AlertType.WORLD);
                    }
                    return false;
                }
            }
        }
        if (getRequiredSkill() != null) {
            if (getRequiredLevel() > human.getSkill(getRequiredSkill()).getLevel()) {
                if (human instanceof Player) {
                    Player player = (Player) human;
                    player.addAlert("A " + getRequiredSkill() + " level " + getRequiredLevel() + " is required!", AlertType.WORLD);
                }
                return false;
            }
        }
        if (this.requiredEquipmentId != 0) {
            if (!human.getInventory().hasItem(requiredEquipmentId, 1)) {
                if (human instanceof Player) {
                    Player player = (Player) human;
                    player.addAlert("Item " + Game.objectManager.getGameItem(getRequiredEquipmentId(), false).name + " is required!", AlertType.WORLD);
                }
                return false;
            }
        }
        return true;
    }

    public void initializeExecution(Human human, Drawable object) {
        if (this.getName().equals("drop")) {
            //then the "id" is actually the slot id
            human.getInventory().setEntity(human);
            human.getInventory().dropOnSlot(getSlotId());
            ((Player) human).setInventoryUpdate(true);
            return;
        }
        if (canExecute(human, object)) {
            if (this.getSoundEffect() != null) {
                if (getActiveSound() != null) {
                    getActiveSound().stop();
                }
                setActiveSound(new Sound2D(this.getSoundEffect()));
                Player player = human.getWorld().getPlayer();
                getActiveSound().play(human.getWorld().getPlayer(), human, isSoundIsLooping());
            } else {
                System.out.println("Sound missing! " + getSoundEffect());
            }
            human.setAction(new EntityAction(this, human, object));
            human.setActionDuration(this.baseTime);
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

    public void cancel() {
        if (this.getActiveSound() != null) {
            this.getActiveSound().stop();
        }
    }

    /**
     * @return the permissionLevel
     */
    public int getPermissionLevel() {
        return permissionLevel;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the slotId
     */
    public int getSlotId() {
        return slotId;
    }

    /**
     * @return the requiredLevel
     */
    public int getRequiredLevel() {
        return requiredLevel;
    }

    /**
     * @param requiredLevel the requiredLevel to set
     */
    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    /**
     * @return the requiredSkill
     */
    public String getRequiredSkill() {
        return requiredSkill;
    }

    /**
     * @param requiredSkill the requiredSkill to set
     */
    public void setRequiredSkill(String requiredSkill) {
        this.requiredSkill = requiredSkill;
    }

    /**
     * @return the expGain
     */
    public int getExpGain() {
        return expGain;
    }

    /**
     * @param expGain the expGain to set
     */
    public void setExpGain(int expGain) {
        this.expGain = expGain;
    }

    /**
     * @return the baseTime
     */
    public float getBaseTime() {
        return baseTime;
    }

    /**
     * @param baseTime the baseTime to set
     */
    public void setBaseTime(float baseTime) {
        this.baseTime = baseTime;
    }

    /**
     * @return the maxDistance
     */
    public float getMaxDistance() {
        return maxDistance;
    }

    /**
     * @param maxDistance the maxDistance to set
     */
    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    /**
     * @return the requiredEquipmentId
     */
    public int getRequiredEquipmentId() {
        return requiredEquipmentId;
    }

    /**
     * @param requiredEquipmentId the requiredEquipmentId to set
     */
    public void setRequiredEquipmentId(int requiredEquipmentId) {
        this.requiredEquipmentId = requiredEquipmentId;
    }

    /**
     * @return the doesRequireEquipment
     */
    public boolean requiresEquipment() {
        return this.requiredEquipmentId != -1;
    }

    /**
     * @return the doesTransform
     */
    public boolean doesTransform() {
        return transforms;
    }

    /**
     * @return the transformIntoId
     */
    public int getTransformIntoId() {
        return transformIntoId;
    }

    /**
     * @param transformIntoId the transformIntoId to set
     */
    public void setTransformIntoId(int transformIntoId) {
        this.transforms = true;
        this.transformIntoId = transformIntoId;
    }

    /**
     * @return the doesDrop
     */
    public boolean dropsItem() {
        if (itemDrops == null) {
            return false;
        }
        return getItemDrops().size() > 0;
    }

    /**
     * @return the itemDrops
     */
    public ArrayList<DropItem> getItemDrops() {
        return itemDrops;
    }

    /**
     * @param itemDrops the itemDrops to set
     */
    public void setItemDrops(ArrayList<DropItem> itemDrops) {
        this.itemDrops = itemDrops;
    }

    /**
     * @return the itemGives
     */
    public ArrayList<DropItem> getItemGives() {
        return itemGives;
    }

    /**
     * @param itemGives the itemGives to set
     */
    public void setItemGives(ArrayList<DropItem> itemGives) {
        this.itemGives = itemGives;
    }

    /**
     * @return the itemTakes
     */
    public ArrayList<DropItem> getItemTakes() {
        return itemTakes;
    }

    /**
     * @param itemTakes the itemTakes to set
     */
    public void setItemTakes(ArrayList<DropItem> itemTakes) {
        this.itemTakes = itemTakes;
    }

    /**
     * @return the RGN
     */
    public Random getRGN() {
        return RGN;
    }

    /**
     * @param RGN the RGN to set
     */
    public void setRGN(Random RGN) {
        this.RGN = RGN;
    }

    /**
     * @return the soundEffect
     */
    public String getSoundEffect() {
        return soundEffect;
    }

    /**
     * @param soundEffect the soundEffect to set
     */
    public void setSoundEffect(String soundEffect) {
        this.soundEffect = soundEffect;
    }

    /**
     * @return the soundIsLooping
     */
    public boolean isSoundIsLooping() {
        return soundIsLooping;
    }

    /**
     * @param soundIsLooping the soundIsLooping to set
     */
    public void setSoundIsLooping(boolean soundIsLooping) {
        this.soundIsLooping = soundIsLooping;
    }

    /**
     * @return the activeSound
     */
    public Sound2D getActiveSound() {
        return activeSound;
    }

    /**
     * @param activeSound the activeSound to set
     */
    public void setActiveSound(Sound2D activeSound) {
        this.activeSound = activeSound;
    }

    /**
     * @param permissionLevel the permissionLevel to set
     */
    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    /**
     *
     * @param entity
     */
    public void executeSpecialEvent(Entity entity) {
    }
}
