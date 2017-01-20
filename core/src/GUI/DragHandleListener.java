/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 * @author kristian
 */
public class DragHandleListener extends ClickListener {

    private float localX, localY;
    private boolean isClicked;
    private Group group;

    public DragHandleListener(Group group) {
        this.group = group;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        localX = x - group.getX();
        localY = y - group.getY();
        isClicked = true;
        return super.touchDown(event, x, y, pointer, button); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        isClicked = false;
        super.touchUp(event, x, y, pointer, button); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        if (isClicked) {
            group.setPosition(x + localX, y + localY);
        }
        super.touchDragged(event, x, y, pointer); //To change body of generated methods, choose Tools | Templates.
    }

}
