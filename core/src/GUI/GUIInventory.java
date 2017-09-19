/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.GUIGraphics;
import Persistence.GameItem;
import Persistence.Tile;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Game;
import com.mygdx.game.Inventory;
import com.mygdx.game.Player;
import javax.swing.GroupLayout;

/**
 *
 * @author kristian
 */
public class GUIInventory extends GUIMovable {

    private Table table;
    private float lastX, lastY;

    public GUIInventory(Player player, Skin skin, Table rightClickBox) {
        super(player, skin, rightClickBox);
        table = new Table(skin);
        create();
    }

    public Group getGroup() {
        return root;
    }

    public void toggleVisibility() {
        verticalGroup.setVisible(!verticalGroup.isVisible());
    }

    private void create() {
        lastX = verticalGroup.getX();
        lastY = verticalGroup.getY();
        verticalGroup.clear();
        update();
        Label label = new Label("Inventory", skin);
        Button handleBar = new Button(GUIGraphics.get("inventory_handle.png"));
        handleBar.addActor(label);
        handleBar.setHeight(label.getHeight());
        handleBar.addListener(new DragHandleListener(this.groupHandle));
        label.setPosition(handleBar.getX() + handleBar.getWidth() / 2 - label.getWidth() / 2, 0);
        verticalGroup.addActor(handleBar);
        verticalGroup.addActor(table);
        verticalGroup.setZIndex(0);
    }

    @Override
    public void update() {
        table.clearChildren();
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i % 4 == 0 && i != 0) {
                table.row();
            }
            Button button = new Button(new TextureRegionDrawable(Game.objectManager.getGameItem(inventory.getId(i), false).getTexture()));
            button.addListener(new ItemListener(i, Buttons.RIGHT, rightClickBox, skin, player));
            table.add(button);

            //If the count is higher than 1, show number of item.
            if (inventory.getCount(i) > 1) {
                Label count = new Label(String.valueOf(inventory.getCount(i)), skin);
                count.setColor(Color.WHITE);
                if (Integer.parseInt((count.getText().toString())) / 1000 >= 1) {
                    if (Integer.parseInt((count.getText().toString())) / 1000000 >= 1) {
                        count.setColor(0.1f, 1f, 0.1f, 1); //Green
                        int toChar = count.getText().toString().length() - 6;
                        if (toChar < 1) {
                            toChar = 1;
                        }
                        count.setText(String.valueOf(count.getText().toString().substring(0, toChar)) + "m");
                    } else {
                        count.setColor(Color.YELLOW);
                        int toChar = count.getText().toString().length() - 3;
                        if (toChar < 1) {
                            toChar = 1;
                        }
                        count.setText(String.valueOf(count.getText().toString().substring(0, toChar)) + "k");
                    }
                }
                button.add(count);
            }
        }
        table.setBackground(GUIGraphics.get("inventory.png"));
        table.pack();
    }
}
