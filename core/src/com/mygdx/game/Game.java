package com.mygdx.game;

import GUI.GUIStage;
import GUI.MenuStage;
import Persistence.GUIGraphics;
import Persistence.GameEntity;
import Persistence.GameItem;
import Persistence.Tile;
import Persistence.GameObject;
import Persistence.GlobalGameSettings;
import Persistence.Report;
import Persistence.Sound2D;
import Persistence.Weapon;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends ApplicationAdapter {

    public static SpriteBatch batch;
    public static ShapeRenderer shapeRenderer;
    public static GlobalGameSettings settings;
    public static ShaderProgram shadowShader;

    enum GameState {
        MENU, PLAYING, LOADING
    }
    private GameState gameState;
    private GameWorld world;
    private OrthographicCamera camera;
    private int screenW, screenH;
    private MPClient client;
    private String playerName;
    private String playerPassword;
    private PlayerController playerController;
    private boolean hasSetViewPort;
    public static boolean isDebug;
    private ScreenViewport viewPort;
    private Stage stage;

    @Override
    public void create() {
        shadowShader = new ShaderProgram(Gdx.files.internal("shaders/shadow.vsh"), Gdx.files.internal("shaders/shadow.fsh"));
        playerName = "Mathias";
        playerPassword = "ubv59mve";
        screenW = 800;
        screenH = 600;
        isDebug = false;
        GameItem.load();
        AnimationGroup.load();
        GameObject.loadObjects();
        Tile.load();
        Weapon.load();
        GUIGraphics.load();
        GameEntity.load();
        Report.generateReports();
        settings = GlobalGameSettings.getInstance();
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(GUIGraphics.getPixmap(("cursor.png")), 0, 0));
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) {
            public float getX() {
                return position.x;
            }

            public float getY() {
                return position.y;
            }
        };
        viewPort = new ScreenViewport(camera);
        this.batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        gameState = GameState.MENU;
        world = new GameWorld(false, "", camera);
        client = new MPClient(7777, world, playerName, playerPassword);
        world.setClient(client.getClient());
        //stage = new GUIStage(world, camera, null, client.getClient());
        stage = new MenuStage();
        //Chunk.makeSample();
        ShaderProgram.pedantic = false;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        if (world.getPlayer() != null) {
            if (world.getPlayer().getX() + 32 / 2 - camera.viewportWidth / 2 > 0 && world.getPlayer().getX() + 32 / 2 + camera.viewportWidth / 2 < world.getSizeX()) {
                camera.position.x = world.getPlayer().getX() + 32 / 2;
            }
            if (world.getPlayer().getY() + 32 / 2 - camera.viewportHeight / 2 > 0 && world.getPlayer().getY() + 32 / 2 + camera.viewportHeight / 2 < world.getSizeY()) {
                camera.position.y = world.getPlayer().getY() + 32 / 2;
            }
        }
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        switch (gameState) {
            case PLAYING:
                world.draw();
                ((GUIStage) stage).drawStuff();
                break;
            default:
                break;
        }
        batch.end();
        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        world.drawDebug();
        if(stage instanceof GUIStage) {
            ((GUIStage) stage).drawShapes();
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl20.GL_BLEND);
        stage.draw();
        //update commands:
        switch (gameState) {
            case PLAYING:
                world.update();
                break;

            case LOADING:
                //System.out.println("Loading..");
                if (Loader.isLoaded()) {
                    gameState = GameState.PLAYING; //Switch to game, if all is loaded..
                }
                break;
            default:
                break;
        }
        stage.act();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (playerController != null) {
            playerController.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (!hasSetViewPort) {
            //this.stage.setViewport(this.viewPort);
            hasSetViewPort = true;
        }
        camera.setToOrtho(false, width, height);
        if(this.stage instanceof MenuStage) {
            ((MenuStage) this.stage).resize();
        }
        this.stage.getViewport().update(width, height, true);
    }
}
