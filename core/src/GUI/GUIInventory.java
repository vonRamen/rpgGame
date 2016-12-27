/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.GameItem;
import Persistence.Tile;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import com.mygdx.game.Inventory;
import com.mygdx.game.Player;

/**
 *
 * @author kristian
 */
public class GUIInventory {

    private Skin skin;
    private Player player;
    private VerticalGroup root;
    private Table table;

    public GUIInventory(Player player, Skin skin) {
        root = new VerticalGroup();
        table = new Table(skin);
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i % 4 == 0 && i != 0) {
                table.row();
            }
            Button button = new Button(new TextureRegionDrawable(GameItem.get(inventory.getId(i)).getTexture()));
            button.addListener(new ItemListener(inventory.getId(i)));
            table.add(button);
        }
        table.setBackground(new TextureRegionDrawable(Tile.get(0)));
        table.pack();
        Label label = new Label("Inventory", skin);
        root.addActor(label);
        root.addActor(table);
        root.setPosition(200, 400);
    }
    
    public Group getGroup() {
        return root;
    }
    
    public void toggleVisibility() {
        table.setVisible(!table.isVisible());
    }
}
