/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 *
 * @author kristian
 */
public class ScreenMessage extends Message {

    private Camera camera;
    private float x, y;

    public ScreenMessage(Stage stage, Skin skin, String message) {
        super(stage, skin, 0, 0, message);
        this.camera = stage.getCamera();
    }

    public ScreenMessage(Stage stage, Skin skin, float x, float y, String message) {
        super(stage, skin, x, y, message);
        this.camera = stage.getCamera();
        this.x = x;
        this.y = y;
    }

    @Override
    public void act(double deltaTime) {
        super.act(deltaTime); //To change body of generated methods, choose Tools | Templates.
        alpha -= decaySpeed * deltaTime;
        if (alpha < 0) {
            alpha = 0;
        }
    }

}
