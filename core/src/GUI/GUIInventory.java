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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Inventory;
import com.mygdx.game.Player;
import javax.swing.GroupLayout;

/**
 *
 * @author kristian
 */
public class GUIInventory {

    private Skin skin;
    private Player player;
    private VerticalGroup root;
    private Table table;
    private Table rightClickBox;

    public GUIInventory(Player player, Skin skin, Table rightClickBox) {
        this.rightClickBox = rightClickBox;
        this.player = player;
        this.skin = skin;
        root = new VerticalGroup();
        table = new Table(skin);
        update();
    }

    public Group getGroup() {
        return root;
    }

    public void toggleVisibility() {
        root.setVisible(!root.isVisible());
    }

    void update() {
        table.clear();
        root.clear();
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i % 4 == 0 && i != 0) {
                table.row();
            }
            Button button = new Button(new TextureRegionDrawable(GameItem.get(inventory.getId(i)).getTexture()));
            button.addListener(new ItemListener(i, Buttons.RIGHT, rightClickBox, skin, player));
            table.add(button);
            
            //If the count is higher than 1, show number of item.
            if (inventory.getCount(i) > 1) {
                Label count = new Label(String.valueOf(inventory.getCount(i)), skin);
                count.setPosition(button.getX(), button.getY() + 20);
                button.add(count);
            }
        }
        table.setBackground(GUIGraphics.get("inventory.png"));
        table.pack();
        Label label = new Label("Inventory", skin);
        Button handleBar = new Button(GUIGraphics.get("inventory_handle.png"));
        handleBar.addActor(label);
        handleBar.setHeight(label.getHeight());
        handleBar.addListener(new DragHandleListener(root));
        label.setPosition(handleBar.getX()+handleBar.getWidth()/2 - label.getWidth()/2, 0);
        root.addActor(handleBar);
        root.addActor(table);
        root.setPosition(200, 400);
        root.setZIndex(0);
    }
}
