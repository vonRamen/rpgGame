/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence.AnimationGroups;

import Persistence.LoadableEntityAnimation;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.Game;
import java.util.TreeMap;

/**
 *
 * @author Kristian
 */
public class HumanManAnimationGroup extends LoadableEntityAnimation {

    public HumanManAnimationGroup() {
        animations.put("male_01.png", Game.textureHandler.generateDirectionalAnimation("male_01.png", 3, 0.15f));
    }
}
