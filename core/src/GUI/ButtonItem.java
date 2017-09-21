/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 *
 * @author Kristian
 */
public class ButtonItem extends Button {
    private int slotId;
    
    public ButtonItem(Drawable drawable, int slotId) {
        super(drawable);
        this.slotId = slotId;
    }
    
    

    /**
     * @return the slotId
     */
    public int getSlotId() {
        return slotId;
    }
    
}
