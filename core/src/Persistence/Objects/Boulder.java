/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.Objects;

import Persistence.GameObject;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Game;
import com.mygdx.game.Light;

/**
 *
 * @author Kristian
 */
public class Boulder extends GameObject {

    public Boulder() {
        this.id = 2;
        rectangle = new Rectangle(0, 0, 32, 32);
        this.sprite = Game.textureHandler.generateRegion("world.png", 4 * 32, 0, 32 * 1, 2 * 32);
        this.offsetX = 0;
        this.offsetY = 0;
    }
}
