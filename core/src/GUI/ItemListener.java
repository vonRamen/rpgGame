/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.Action;
import Persistence.GameItem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Player;

/**
 *
 * @author kristian
 */
public class ItemListener extends ClickListener {

    private int slotId;
    private Table table;
    private Skin skin;
    private Player player;

    public ItemListener(int itemId) {
        this.slotId = slotId;
    }

    public ItemListener(int slotId, int button, Table table, Skin skin, Player player) {
        super(button);
        this.skin = skin;
        this.slotId = slotId;
        this.table = table;
        this.player = player;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        table.clear();
        int id = player.getInventory().getId(slotId);
        int count = player.getInventory().getCount(id);
        GameItem item = GameItem.get(id);

        if (item.getActions() == null) {
            return;
        }
        float buttonWidth = 0;
        for (Action action : item.getActions()) {
            TextButton actionButton = new TextButton(action.getName(), skin);
            actionButton.addListener(new ActionClickListener(player, id, action));
            table.add(actionButton);
            table.row();
            if (actionButton.getWidth() > buttonWidth) {
                buttonWidth = actionButton.getWidth();
            }
        }

        //Add drop button:
        TextButton actionButton = new TextButton("drop", skin);
        Action dropAction = new Action("drop");
        dropAction.setSlotId(slotId);
        actionButton.addListener(new ActionClickListener(player, slotId, dropAction));
        table.add(actionButton);
        //Make all the buttons the same width based on the largest if size > 1
        if (table.getChildren().size > 1) {
            for (Actor actor : table.getChildren()) {
                table.getCell(actor).width(buttonWidth);
            }
        }

        table.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
    }

}
