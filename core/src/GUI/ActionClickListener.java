/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Drawable;
import com.mygdx.game.Player;

/**
 *
 * @author kristian
 */
public class ActionClickListener extends ClickListener {

    private Player player;
    private Drawable object;
    private Action action;
    private int itemId;

    public ActionClickListener(Player player, Drawable object, Action action) {
        this.player = player;
        this.object = object;
        this.action = action;
    }

    public ActionClickListener(Player player, int itemId, Action action) {
        this.itemId = itemId;
        this.player = player;
        this.object = object;
        this.action = action;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
        if (object != null) {
            this.action.initializeExecution(player, object);
        } else {
            this.action.initializeExecution(player, null);
        }
    }

}
