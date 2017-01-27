/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import CustomGUI.ConsistentVerticalGroup;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.mygdx.game.Player;

/**
 *
 * @author kristian
 */
abstract public class GUIMovable {

    float verticalLastX, verticalLastY;
    ConsistentVerticalGroup verticalGroup;
    Container groupHandle;
    Stack root;
    Player player;
    Skin skin;
    Table rightClickBox;

    public GUIMovable(Player player, Skin skin, Table rightClickBox) {
        this.player = player;
        this.skin = skin;
        this.root = new Stack();
        this.rightClickBox = rightClickBox;
        this.verticalGroup = new ConsistentVerticalGroup();
        this.groupHandle = new Container(this.verticalGroup);
        this.root.add(this.groupHandle);
    }

    public void toggle() {
        this.verticalGroup.setVisible(!this.verticalGroup.isVisible());
    }

    public void updatePositioning(Camera camera) {
        root.setPosition(camera.position.x, camera.position.y);
    }

    public void update() {
        if (verticalGroup.getX() == 0 && verticalGroup.getY() == 0) {
            verticalGroup.setPosition(verticalLastX, verticalLastY);
        }
        verticalLastX = verticalGroup.getX();
        verticalLastY = verticalGroup.getY();
    }

    protected void fitForLargest() {
        //get largest width:
        float largestWidth = 0;
        for (Actor actor : verticalGroup.getChildren()) {
            if (actor.getWidth() > largestWidth) {
                largestWidth = actor.getWidth();
            }
        }
        //set largest width
        for (Actor actor : verticalGroup.getChildren()) {
            if (actor instanceof TextButton) {
                ((TextButton) actor).getLabel().setWidth(largestWidth);
            }
        }

        for (Actor actor : verticalGroup.getChildren()) {
            if (actor instanceof TextButton) {
                System.out.println("Button width: " + actor.getWidth());
            }
        }
    }
}
