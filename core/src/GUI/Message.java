/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.Game;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class Message extends Label {

    float speed;
    float decaySpeed;
    float alpha;
    BitmapFont font;

    public Message(Stage stage, Skin skin, float x, float y, String message) {
        super(message, skin);
        this.setX(x);
        this.setY(y);
        this.alpha = 1;
        this.decaySpeed = 0.2f;
        this.font = new BitmapFont();
        stage.addActor(this);
    }

    public void act(double deltaTime) {

    }
}
