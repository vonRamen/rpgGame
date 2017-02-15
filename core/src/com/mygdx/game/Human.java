/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;
import Persistence.Weapon;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author kristian
 */
public class Human extends Entity {

    protected ArrayList<Entity> enemiesHit; //A list of the current enemies hit. Won't hit the same enemy twice.
    protected HashMap<String, Skill> skills;
    protected Sprite weaponSpriteReference;
    protected int middleX = 16;
    protected int middleY = 16;
    protected int skin; //skin color
    protected int hair; //hair
    protected float hairColorRed;
    protected float hairColorBlue;
    protected float hairColorGreen;
    protected Animation hairAnimation;
    protected int head; //head equipment
    protected int body; //body equipment
    protected int legs; //legs equipment
    protected int weapon; //weapon used
    protected int swing;
    protected int swingEnd = 90;
    protected int weaponSpeed;
    protected int weaponPosX;
    protected int weaponPosY;
    private EntityAction currentAction;
    private float actionDuration;
    private PathFinder pathFinder;

    public Human() {
        super();
        inventory = new Inventory(28);
    }

    public void addSkills() {
        Skill skillCombat = new Skill("Combat", "This is the skill, where you specialize in melee combat.");
        Skill skillMining = new Skill("Mining", "This is the skill, where you mine ores.");
        Skill skillWoodcutting = new Skill("Woodcutting", "In this skill, you chop wood.");
        Skill skillFarming = new Skill("Farming", "In this skill, you farm stuff");
        Skill skillConstruction = new Skill("Construction", "In this skill, you build stuff");
        skills = new HashMap();
        skills.put("combat", skillCombat);
        skills.put("mining", skillMining);
        skills.put("woodcutting", skillWoodcutting);
        skills.put("farming", skillFarming);
        skills.put("construction", skillConstruction);
    }

    @Override
    public void attack() {
        isAttacking = true;
    }

    public Human(GameWorld world) {
        super(world);
        setAnimation("male_01", 0, state);
        inventory = new Inventory(30);
    }

    @Override
    public void setAnimation(String animationName, int direction, EntityState state) {
        super.setAnimation(animationName, direction, state);

        if (AnimationGroup.getAnimation(hair, 1) != null) {
            switch (state) {
                case WALKING:
                    hairAnimation = AnimationGroup.getAnimation(hair, 1).getDirectionalAnimation(direction);
                    break;

                case SWIMMING:
                    hairAnimation = AnimationGroup.getAnimation(hair, 1).getUnderWaterAnimation(direction);
                    break;

                case IDLE:
                    hairAnimation = AnimationGroup.getAnimation(hair, 1).getIdleAnimation(direction);
                    break;

                default:

                    break;
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if(pathFinder!=null) {
            pathFinder.update();
        }

        //Update actions:
        //fx mining, smithing..
        if (currentAction != null) {
            currentAction.update(deltaTime);
            if (currentAction.isDone()) {
                currentAction = null;
            }
        }
        //update weapon shit
        if (!isAttacking) {
            enemiesHit = new ArrayList();
            swing = 0;
            weaponSpeed = Weapon.get(weapon).getSpeed();
            weaponSpriteReference = new Sprite();
            weaponSpriteReference.set(Weapon.get(weapon).getWeaponSprite());
            switch (animationDirection) { //initial setup position and such - UP(3), DOWN(0), LEFT(1), RIGHT(2);
                case 0:
                    weaponPosY = -22;
                    weaponPosX = middleX + 4;
                    weaponSpriteReference.setRotation(-swing - 90);
                    weaponSpriteReference.setPosition(weaponPosX + x, weaponPosY + y);
                    weaponSpriteReference.setOrigin(0, 12);
                    break;

                case 1:
                    weaponPosX = -38;
                    weaponPosY = 24;
                    weaponSpriteReference.setPosition(weaponPosX + x, weaponPosY + y);
                    weaponSpriteReference.flip(true, false);
                    weaponSpriteReference.setOrigin(38 + 16 - 10, -12);
                    break;

                case 2:
                    weaponPosX = 38;
                    weaponPosY = 24;
                    weaponSpriteReference.setPosition(weaponPosX + x, weaponPosY + y);
                    weaponSpriteReference.setOrigin(-10, -12);
                    break;

                case 3:
                    weaponPosY = 38;
                    weaponPosX = middleX;
                    weaponSpriteReference.setPosition(weaponPosX + x, weaponPosY + y);
                    weaponSpriteReference.setOrigin(0, -12);
                    break;

                default:
                    break;
            }
        } else {
            swing += Weapon.get(weapon).getSpeed() * deltaTime;
            switch (animationDirection) { // UP(3), DOWN(0), LEFT(1), RIGHT(2);
                case 0:
                    weaponSpriteReference.setRotation(-swing - 90);
                    break;

                case 1:
                    weaponSpriteReference.setRotation(swing);
                    break;

                case 2:
                    weaponSpriteReference.setRotation(-swing);
                    break;

                case 3:
                    weaponSpriteReference.setRotation(swing);
                    break;

                default:
                    break;
            }
            weaponSpriteReference.setPosition(weaponPosX + x, weaponPosY + y);
            if (swing > swingEnd) {
                isAttacking = false;
            }
            dealDamage();
        }
    }

    public void dealDamage() {
        Rectangle bounds = weaponSpriteReference.getBoundingRectangle();
        ArrayList<Entity> entitiesToDamage = this.checkEntityCollision(bounds);
        //Deal damage to the entities, but only once.
        for (Entity entity : entitiesToDamage) {
            boolean alreadyHit = false;
            for (Entity entityAlreadyHit : enemiesHit) {
                if (entity == entityAlreadyHit) {
                    alreadyHit = true;
                    break;
                }
            }
            if (!alreadyHit) {
                //add force and damage:
                float ang = (float) Math.atan2(entity.getY() - y, entity.getX() - x);
                entity.damageThis(ang, Weapon.get(weapon).getDamage(), Weapon.get(weapon).getKnockback());
                enemiesHit.add(entity);
            }
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        this.hair = 1;
        this.hairColorBlue = 0.6f;
        this.hairColorGreen = 0.6f;
        this.hairColorRed = 0.6f;
        this.boundsPattingY = 0;
        this.inventory.changePosition(0, 27);
        weaponSpriteReference = Weapon.get(weapon).getWeaponSprite();
        setAnimation("male_01", animationDirection, state);
        updateSlash();
    }

    @Override
    public void draw() {
        //draw weapon behind human if attacking up
        if (isAttacking && animationDirection == Direction.UP.getValue()) {
            Game.batch.draw(weaponSpriteReference, weaponSpriteReference.getX(), weaponSpriteReference.getY(), weaponSpriteReference.getOriginX(), weaponSpriteReference.getOriginY(), weaponSpriteReference.getWidth(), weaponSpriteReference.getHeight(), 1, 1, weaponSpriteReference.getRotation());
        }

        super.draw();
        //Draw Hair
        if (hairAnimation != null) {
            Game.batch.setColor(hairColorRed, hairColorGreen, hairColorBlue, this.alpha);
            Game.batch.draw(hairAnimation.getKeyFrame(this.animationTimer, true), x, y + z);

            //draw weapon in front of human if attacking down
            if (isAttacking && animationDirection != Direction.UP.getValue()) {
                Game.batch.draw(weaponSpriteReference, weaponSpriteReference.getX(), weaponSpriteReference.getY(), weaponSpriteReference.getOriginX(), weaponSpriteReference.getOriginY(), weaponSpriteReference.getWidth(), weaponSpriteReference.getHeight(), 1, 1, weaponSpriteReference.getRotation());
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void removeNonSimpleTypes() {
        super.removeNonSimpleTypes();
        currentAction = null;
        weaponSpriteReference = null;
        this.inventory.prepareSend();
        this.hairAnimation = null;
    }

    public void updateSlash() {
        enemiesHit = new ArrayList();
        swing = 0;
        weaponSpeed = Weapon.get(weapon).getSpeed();
        weaponSpriteReference = new Sprite();
        weaponSpriteReference.set(Weapon.get(weapon).getWeaponSprite());
        switch (animationDirection) { //initial setup position and such - UP(3), DOWN(0), LEFT(1), RIGHT(2);
            case 0:
                weaponPosY = -22;
                weaponPosX = middleX + 4;
                weaponSpriteReference.setRotation(-swing - 90);
                weaponSpriteReference.setPosition(weaponPosX + x, weaponPosY + y);
                weaponSpriteReference.setOrigin(0, 12);
                break;

            case 1:
                weaponPosX = -38;
                weaponPosY = 24;
                weaponSpriteReference.setPosition(weaponPosX + x, weaponPosY + y);
                weaponSpriteReference.flip(true, false);
                weaponSpriteReference.setOrigin(38 + 16 - 10, -12);
                break;

            case 2:
                weaponPosX = 38;
                weaponPosY = 24;
                weaponSpriteReference.setPosition(weaponPosX + x, weaponPosY + y);
                weaponSpriteReference.setOrigin(-10, -12);
                break;

            case 3:
                weaponPosY = 38;
                weaponPosX = middleX;
                weaponSpriteReference.setPosition(weaponPosX + x, weaponPosY + y);
                weaponSpriteReference.setOrigin(0, -12);
                break;

            default:
                break;
        }
    }

    /**
     * @param skillName
     * @return the skillCombat
     */
    public Skill getSkill(String skillName) {
        return skills.get(skillName);
    }

    @Override
    public ArrayList<Action> getActions() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return null;
    }

    public void setAction(EntityAction action) {
        if (currentAction != null) {
            currentAction.cancel();
        }
        currentAction = action;
    }

    /**
     * @return the actionDuration
     */
    public float getActionDuration() {
        return actionDuration;
    }

    /**
     * @param actionDuration the actionDuration to set
     */
    public void setActionDuration(float actionDuration) {
        this.actionDuration = actionDuration;
    }

    public void setPath(int x, int y) {
        this.pathFinder = new PathFinder(this, this.x, this.y, x, y);
    }
}
