/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Server.ExtraCommand;
import Server.WorldSettings;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author kristian
 */
public class KKryo {

    public static void registerAll(Kryo kryo) {
        kryo.register(Packets.Message.class);
        kryo.register(Packets.RequestAccess.class);
        kryo.register(Packets.BeginMovement.class);
        kryo.register(Packets.EndMovement.class);
        kryo.register(Packets.requestChunks.class);
        kryo.register(Chunk.class);
        kryo.register(ArrayList.class);
        kryo.register(WorldObject.class);
        kryo.register(Drawable.class);
        kryo.register(Integer[][].class);
        kryo.register(Integer[].class);
        kryo.register(Entity.class);
        kryo.register(Human.class);
        kryo.register(Player.class);
        kryo.register(int[][].class);
        kryo.register(int[].class);
        kryo.register(EntitySimpleType.class);
        kryo.register(Rectangle.class);
        kryo.register(Inventory.class);
        kryo.register(Skill.class);
        kryo.register(Job.class);
        kryo.register(Task.class);
        kryo.register(Stack.class);
        kryo.register(KAnimation.class);
        kryo.register(Animation[].class);
        kryo.register(TextureRegion[].class);
        kryo.register(TextureRegion.class);
        kryo.register(Texture.class);
        kryo.register(FileTextureData.class);
        kryo.register(GameWorld.class);
        kryo.register(ExtraCommand.class);
        kryo.register(WorldSettings.class);
        kryo.register(java.util.Random.class);
        kryo.register(java.util.concurrent.atomic.AtomicLong.class);
        kryo.register(Persistence.Action.class);
        kryo.register(Persistence.DropItem.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(com.mygdx.game.DroppedItem.class);
    }
}
