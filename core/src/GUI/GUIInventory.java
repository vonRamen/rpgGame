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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.Game;
import com.mygdx.game.Inventory;
import com.mygdx.game.Player;
import java.util.ArrayList;
import javax.swing.GroupLayout;

/**
 *
 * @author kristian
 */
public class GUIInventory {

    private Window window;
    private float lastX, lastY;
    private Player player;
    private Skin skin;
    private Table rightClickBox;
    private DragAndDrop dragAndDrop;
    private ArrayList<ButtonItem> buttons;

    public GUIInventory(Player player, Skin skin, Table rightClickBox) {
        this.buttons = new ArrayList();
        this.rightClickBox = rightClickBox;
        this.window = new Window("Inventory", skin);
        this.player = player;
        this.skin = skin;
        this.dragAndDrop = new DragAndDrop();
        create();
    }

    private void create() {
        this.update();
    }

    public void update() {
        this.window.clearChildren();
        this.buttons.clear();
        final Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            ButtonItem button = new ButtonItem(new TextureRegionDrawable(Game.objectManager.getGameItem(inventory.getId(i), false).getTexture()), i);
            this.buttons.add(button);
            button.addListener(new ItemListener(i, Buttons.RIGHT, rightClickBox, skin, player));
            if (i % 4 == 0 && i != 0) {
                window.row();
            }
            window.add(button);

            //Testing
            //End Testing
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
        makeDragable(this.buttons);
        window.pack();
    }

    private void makeDragable(final ArrayList<ButtonItem> buttons) {
        final Inventory inventory = player.getInventory();
        for (ButtonItem btn : buttons) {
            final ButtonItem button = btn;
            dragAndDrop.addSource(new Source(button) {
                public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    Payload payload = new Payload();
                    payload.setObject("Some payload!");

                    System.out.println("Start: " + button.getSlotId());

                    button.setVisible(false);
                    payload.setDragActor(new ButtonItem(button.getBackground(), button.getSlotId()));

                    return payload;
                }
            });

            dragAndDrop.addTarget(new Target(button) {
                @Override
                public boolean drag(Source source, Payload pld, float f, float f1, int i) {
                    return true;
                }

                @Override
                public void drop(Source source, Payload pld, float f, float f1, int i) {
                    
                    System.out.println("Dropped on: " + ((ButtonItem) source.getActor()).getSlotId());
                    inventory.changePosition(((ButtonItem) source.getActor()).getSlotId(), button.getSlotId());
                    update();
                }

            });
        }
    }

    public Window getWindow() {
        return window;
    }

    public void toggleVisibility() {
        this.window.setVisible(!this.window.isVisible());
    }
}
