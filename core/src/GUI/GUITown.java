/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Game;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Player;

/**
 * Controls creating a town
 *
 * @author kristian
 */
public class GUITown extends GUIMovable {

    private Table createMenu;
    private GameWorld world;
    private ClickHistory tileClickHistory;
    private Click draggedClick;

    public GUITown(Player player, Skin skin, Table rightClickBox) {
        super(player, skin, rightClickBox);
        create();
        this.createMenu = this.createCreateMenu();
        this.root.addActor(this.createMenu);
        this.createMenu.setVisible(false);
    }

    private void create() {
        //Create handle:
        TextButton handleBar = new TextButton("Town Management", this.skin);
        handleBar.addListener(new DragHandleListener(verticalGroup));

        //Create buttons and table
        TextButton create = new TextButton("Create Town", this.skin);
        create.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                createMenu.setVisible(!createMenu.isVisible());
            }

        });
        TextButton manage = new TextButton("Manage Town", this.skin);
        TextButton exit = new TextButton("Exit", this.skin);

        this.verticalGroup.addActor(handleBar);
        this.verticalGroup.addActor(create);
        this.verticalGroup.addActor(manage);
        this.verticalGroup.addActor(exit);

        //Fit buttons
        this.fitForLargest();
    }

    private Table createCreateMenu() {
        Table table = new Table();
        TextButton handleBar = new TextButton("Create Town", skin);
        handleBar.addListener(new DragHandleListener(table));
        final TextField townName = new TextField("Town Name", skin);
        final TextArea townDescription = new TextArea("Description", skin);
        TextButton createTown = new TextButton("Create!", skin);
        createTown.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                Click click1 = tileClickHistory.getClickNewestFirst(1);
                Click click2 = tileClickHistory.getReleaseNewestFirst(0);
                int xTile = (click1.getX() < click2.getX()) ? click1.getX() : click2.getX();
                int yTile = (click1.getY() < click2.getY()) ? click1.getY() : click2.getY();
                int wTile = (click1.getX() < click2.getX()) ? click2.getX() - click1.getX() : click1.getX() - click2.getX();
                int hTile = (click1.getY() < click2.getY()) ? click2.getY() - click1.getY() : click1.getY() - click2.getY();
                world.addTown(townName.getText(), townDescription.getText(), xTile, yTile, wTile, hTile);
                tileClickHistory.clear();
                draggedClick.set(0, 0);
            }

        });
        table.add(handleBar);
        table.row();
        table.add(townName);
        table.row();
        table.add(townDescription);
        table.row();
        table.add(createTown);
        townDescription.setPrefRows(6);
        table.setPosition(verticalGroup.getX() + 50, verticalGroup.getY() - 50);
        return table;
    }

    private Table getCreateMenu() {
        return null;
    }

    public Group getRoot() {
        return this.root;
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    /**
     * @param tileClickHistory the tileClickHistory to set
     */
    public void setTileClickHistory(ClickHistory tileClickHistory) {
        this.tileClickHistory = tileClickHistory;
    }

    public void drawShapes() {
        if (this.createMenu.isVisible() && tileClickHistory.getClickNewestFirst(0) != null) {
            Click click1 = tileClickHistory.getClickNewestFirst(0);
            Click click2 = draggedClick;
            int xTile = (click1.getX() < click2.getX()) ? click1.getX() : click2.getX();
            int yTile = (click1.getY() < click2.getY()) ? click1.getY() : click2.getY();
            int wTile = (click1.getX() < click2.getX()) ? click2.getX() - click1.getX() : click1.getX() - click2.getX();
            int hTile = (click1.getY() < click2.getY()) ? click2.getY() - click1.getY() : click1.getY() - click2.getY();
            Game.shapeRenderer.setColor(0.2f, 0.2f, 0.7f, 1f);
            Game.shapeRenderer.rect(xTile * 32, yTile * 32, wTile * 32, hTile * 32);
        }
    }

    void setDraggedClick(Click click) {
        this.draggedClick = click;
    }
}
