/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;
import Persistence.Weapon;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author kristian
 */
public class Human extends Entity {

    protected ArrayList<Entity> enemiesHit; //A list of the current enemies hit. Won't hit the same enemy twice.
    private Skill skillCombat;
    private Skill skillWoodcutting;
    private Skill skillMining;
    private Skill skillFarming;
    protected Sprite weaponSpriteReference;
    protected int middleX = 16;
    protected int middleY = 16;
    protected int skin; //skin color
    protected int hair; //hair color
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

    public Human() {
        super();
        inventory = new Inventory(30);
    }

    public void addSkills() {
        skillCombat = new Skill("Combat", "This is the skill, where you specialize in melee combat.");
        skillMining = new Skill("Mining", "This is the skill, where you mine ores.");
        skillWoodcutting = new Skill("Woodcutting", "In this skill, you chop wood.");
        skillFarming = new Skill("Farming", "In this skill, you farm stuff");
    }

    @Override
    public void attack() {
        isAttacking = true;
    }

    public Human(GameWorld world) {
        super(world);
        setAnimation(0, 0);
        inventory = new Inventory(30);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

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
                System.out.println("damage");
                float ang = (float) Math.atan2(entity.getY() - y, entity.getX() - x);
                System.out.println("");
                System.out.println(ang);
                entity.damageThis(ang, Weapon.get(weapon).getDamage(), Weapon.get(weapon).getKnockback());
                enemiesHit.add(entity);
            }
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        this.boundsPattingY = 0;
        weaponSpriteReference = Weapon.get(weapon).getWeaponSprite();
        setAnimation(0, animationDirection);
        updateSlash();
    }

    @Override
    public void draw() {
        //draw weapon behind human if attacking up
        if (isAttacking && animationDirection == Direction.UP.getValue()) {
            Game.batch.draw(weaponSpriteReference, weaponSpriteReference.getX(), weaponSpriteReference.getY(), weaponSpriteReference.getOriginX(), weaponSpriteReference.getOriginY(), weaponSpriteReference.getWidth(), weaponSpriteReference.getHeight(), 1, 1, weaponSpriteReference.getRotation());
        }

        super.draw();

        //draw weapon in front of human if attacking down
        if (isAttacking && animationDirection != Direction.UP.getValue()) {
            Game.batch.draw(weaponSpriteReference, weaponSpriteReference.getX(), weaponSpriteReference.getY(), weaponSpriteReference.getOriginX(), weaponSpriteReference.getOriginY(), weaponSpriteReference.getWidth(), weaponSpriteReference.getHeight(), 1, 1, weaponSpriteReference.getRotation());
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
     * @return the skillCombat
     */
    public Skill getSkillCombat() {
        return skillCombat;
    }

    /**
     * @return the skillWoodcutting
     */
    public Skill getSkillWoodcutting() {
        return skillWoodcutting;
    }

    /**
     * @return the skillMining
     */
    public Skill getSkillMining() {
        return skillMining;
    }

    /**
     * @return the farmingSkill
     */
    public Skill getFarmingSkill() {
        return skillFarming;
    }

    @Override
    public ArrayList<Action> getActions() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return null;
    }

    public void setAction(EntityAction action) {
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
}
