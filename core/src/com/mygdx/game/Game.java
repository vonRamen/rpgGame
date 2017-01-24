package com.mygdx.game;

import Persistence.GUIGraphics;
import Persistence.GameEntity;
import Persistence.GameItem;
import Persistence.Tile;
import Persistence.GameObject;
import Persistence.Sound2D;
import Persistence.Weapon;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends ApplicationAdapter {

    public static SpriteBatch batch;
    public static ShapeRenderer shapeRenderer;

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

    @Override
    public void create() {
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
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) {
            public float getX() {
                return position.x;
            }
            public float getY() {
                return position.y;
            }
        };
        viewPort = new ScreenViewport(camera);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        gameState = GameState.PLAYING;
        world = new GameWorld(false, "", camera);
        client = new MPClient(7777, world, playerName, playerPassword);
        world.setClient(client.getClient());
        //Chunk.makeSample();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
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
                break;
            default:
                break;
        }
        batch.end();
        if (playerController == null) {
            playerController = world.getPlayerController();
        } else {
            playerController.draw();
        }
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        world.drawDebug();
        shapeRenderer.end();
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
        if (world.getPlayerController() != null) {
            if(!hasSetViewPort) {
                world.getPlayerController().getStage().setViewport(this.viewPort);
                hasSetViewPort = true;
            }
            camera.setToOrtho(false, width, height);
            world.getPlayerController().getStage().getViewport().update(width, height, true);
        }
    }
}
