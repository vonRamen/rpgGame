/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.GUIGraphics;
import Persistence.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.AlertType;
import com.mygdx.game.Drawable;
import com.mygdx.game.Game;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Player;
import com.mygdx.game.Property;
import com.mygdx.game.Town;

/**
 * Controls creating a town
 *
 * @author kristian
 */
public class GUITown extends GUIMovable {

    private Table createMenu;
    private Window buildMenu;
    private VerticalGroup townManager;
    private VerticalGroup currentTownManaging;
    private GameWorld world;
    private ClickHistory tileClickHistory;
    private Click draggedClick;
    private int chosenBuildObjectId = 0;

    public GUITown(Player player, Skin skin, Table rightClickBox) {
        super(player, skin, rightClickBox);
        create();
        this.createMenu = this.createCreateMenu();
        this.townManager = this.updateTownManager();
        this.buildMenu = this.createBuildMenu();
        this.currentTownManaging = new VerticalGroup();
        this.root.addActor(this.createMenu);
        this.root.addActor(townManager);
        this.root.addActor(currentTownManaging);
        this.root.addActor(this.buildMenu);
        this.createMenu.setVisible(false);
        this.createMenu.pack();
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
                verticalGroup.setVisible(false);
                createMenu.setVisible(!createMenu.isVisible());
            }

        });
        TextButton manage = new TextButton("Manage Town", this.skin);
        manage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                verticalGroup.setVisible(false);
                townManager = updateTownManager();
                townManager.setVisible(true);
                root.addActor(townManager);
            }

        });
        TextButton build = new TextButton("Build", this.skin);
        build.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                verticalGroup.setVisible(false);
                buildMenu.setVisible(!buildMenu.isVisible());
            }

        });

        TextButton exit = new TextButton("Exit", this.skin);

        this.verticalGroup.addActor(handleBar);
        this.verticalGroup.addActor(create);
        this.verticalGroup.addActor(manage);
        this.verticalGroup.addActor(build);
        this.verticalGroup.addActor(exit);

        //Fit buttons
        this.fitForLargest();
    }

    private Table createCreateMenu() {
        Table table = new Table();
        table.align(Align.left);
        TextButton handleBar = new TextButton("Create Town", skin);
        handleBar.addListener(new DragHandleListener(table));
        final TextField townName = new TextField("Town Name", skin);
        final TextArea townDescription = new TextArea("Description", skin);
        TextButton createTown = new TextButton("Create!", skin);
        TextButton cancel = new TextButton("Cancel", skin);
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
                Town town = world.addTown(townName.getText(), townDescription.getText(), xTile, yTile, wTile, hTile);
                tileClickHistory.clear();
                draggedClick.set(0, 0);

                //if the creation was a success, go back to root menu
                if (town != null) {
                    verticalGroup.setVisible(true);
                    createMenu.setVisible(false);
                }
            }

        });
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                verticalGroup.setVisible(true);
                createMenu.setVisible(false);
            }

        });

        table.add(handleBar);
        table.row();
        table.add(townName);
        table.row();
        table.add(townDescription);
        townDescription.setPrefRows(6);
        table.row();
        HorizontalGroup lastGroup = new HorizontalGroup();
        lastGroup.addActor(cancel);
        lastGroup.addActor(createTown);
        table.add(lastGroup);
        table.setPosition(verticalGroup.getX() + 50, verticalGroup.getY() - 50);
        table.pack();
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

    private VerticalGroup updateTownManager() {
        VerticalGroup newGroup = new VerticalGroup();
        Table root = new Table();

        TextButton handleBar = new TextButton("Manager", skin);
        handleBar.addListener(new DragHandleListener(newGroup));
        newGroup.addActor(handleBar);
        newGroup.addActor(root);
        root.row();

        VerticalGroup towns = new VerticalGroup();
        VerticalGroup properties = new VerticalGroup();
        root.add(towns);
        root.add(properties);

        towns.addActor(new TextButton("Towns:", skin));
        properties.addActor(new TextButton("Properties:", skin));
        //Get Towns:
        if (this.player != null) {
            System.out.println("Is the list empty? " + this.world.getOwnedTowns(player.getUId()));
            for (Town town : this.world.getOwnedTowns(player.getUId())) {
                final Town towny = town;
                TextButton townToAdd = new TextButton(town.getName(), this.skin);
                townToAdd.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                        makeTownMenu(towny);
                    }

                });
                towns.addActor(townToAdd);
            }

            //get Properties:
            for (Property property : this.world.getOwnedProperties(player.getUId())) {
                TextButton propertyToAdd = new TextButton(property.getName(), this.skin);
                properties.addActor(propertyToAdd);
            }
        }

        root.pack();
        newGroup.setVisible(false);
        return newGroup;
    }

    private void makeTownMenu(Town town) {
        currentTownManaging.clear();
        TextButton handleBar = new TextButton(town.getName(), this.skin);
        handleBar.addListener(new DragHandleListener(currentTownManaging));
        currentTownManaging.addActor(handleBar);

        currentTownManaging.setVisible(true);
    }

    private Window createBuildMenu() {
        Window window = new Window("Build!", this.skin);

        Button wall = new Button(new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 0, 32, 32, 32)));
        Button floor = new Button(new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 0, 32 * 3, 32, 32)));
        Button door = new Button(new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 32, 5 * 32, 32, 32)));
        Button roof = new Button(new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 3 * 32, 4 * 32, 32, 32)));

        VerticalGroup categories = new VerticalGroup();
        categories.addActor(wall);
        categories.addActor(floor);
        categories.addActor(door);
        categories.addActor(roof);

        final VerticalGroup itemsShowing = new VerticalGroup();
        itemsShowing.align(Align.topLeft);
        roof.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                updateGroup(GameObject.ObjectType.ROOF, itemsShowing);
            }

        });

        wall.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                updateGroup(GameObject.ObjectType.WALL, itemsShowing);
            }

        });
        door.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                updateGroup(GameObject.ObjectType.DOOR, itemsShowing);
            }

        });

        floor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                updateGroup(GameObject.ObjectType.FLOOR, itemsShowing);
            }

        });
        window.align(Align.topLeft);
        categories.pad(0, 2, 0, 4);
        categories.pack();
        itemsShowing.pad(0, 2, 0, 4);
        itemsShowing.pack();
        Container container = new Container(itemsShowing);
        container.setBackground(new TextureRegionDrawable(Game.textureHandler.generateRegion("world.png", 32, 0, 32, 32)));
        container.height(categories.getHeight());
        window.add(categories, container);
        //window.pack();
        window.setVisible(false);
        

        /*
        TextButton handleBar = new TextButton("Build!", this.skin);
        handleBar.addListener(new DragHandleListener(table));
        table.add(handleBar);
        table.row();
        for (GameObject object : GameObject.getAllGhostObjects()) {
            final GameObject objectHold = object;
            TextButton objectButton = new TextButton(object.getName(), this.skin);
            objectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                    chosenBuildObjectId = objectHold.getId();
                    player.addAlert("Chosen object id: " + objectHold.getId(), AlertType.SCREEN);
                }

            });
            table.add(objectButton);
            table.row();
        }
        table.pack();
        table.setVisible(false);
         */
        return window;
    }

    private void updateGroup(GameObject.ObjectType type, Group group) {
        group.clear();
        for (GameObject object : GameObject.getAllGhostObjects()) {
            final GameObject obj = object;
            if (object.getObjectType().equals(type)) {
                Button button = new Button(object.getSpriteIcon());

                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                        chosenBuildObjectId = obj.getId();
                        player.addAlert("Chosen object id: " + obj.getId(), AlertType.SCREEN);
                    }

                });
                TextTooltip t = new TextTooltip(object.getName(), skin);
                t.setInstant(true);
                button.addListener(t);
                group.addActor(button);
            }
        }
        
    }

    public boolean buildMoveIsActive() {
        return buildMenu.isVisible();
    }

    public int getIdBuildItem() {
        return chosenBuildObjectId;
    }
}
