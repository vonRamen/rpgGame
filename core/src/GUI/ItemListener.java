/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 * @author kristian
 */
public class ItemListener extends ClickListener {

    private int itemId;

    public ItemListener(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (getButton() == Input.Buttons.RIGHT) {
            System.out.println("Right button clicked");
        }
        System.out.println("Clicked " + getButton());
    }

}
