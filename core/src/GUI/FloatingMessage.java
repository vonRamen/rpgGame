/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 *
 * @author kristian
 */
public class FloatingMessage extends Message {

    public FloatingMessage(Stage stage, Skin skin, float x, float y, String message) {
        super(stage, skin, x, y, message);
    }

    public void act(double deltaTime) {
        alpha -= decaySpeed * deltaTime;
        if (alpha < 0) {
            alpha = 0;
        }
        this.setY((float) deltaTime * 10 + this.getY());
        this.setColor(1, 1, 1, alpha);
    }
}
