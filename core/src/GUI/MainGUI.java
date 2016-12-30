/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.GUIGraphics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 *
 * @author kristian
 */
public class MainGUI extends Table {
    private Button inventoryButton;
    private Skin skin;
    
    public MainGUI(Skin skin) {
        this.skin = skin;
        inventoryButton = new Button(GUIGraphics.get("inventory_button.png"));
        this.addActor(inventoryButton);
    }
}
