/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 * @author kristian
 */
public class MenuStage extends Stage {

    private Skin skin;
    private Table table;
    private Group root;
    private OrthographicCamera camera;

    public MenuStage(OrthographicCamera camera) {
        super();
        this.camera = camera;
        root = new Group();
        skin = new Skin(Gdx.files.internal("gui skins/uiskin.json"));
        this.table = new Table();
        this.addActor(root);
        root.addActor(table);
        root.setPosition(0, 0);
        this.main(table);
        Gdx.input.setInputProcessor(this);
        System.out.println("Camera Position: " + camera.position.x + " " + camera.position.y);
        this.table.setPosition(camera.position.x / 2, camera.position.y);
        System.out.println("Size " + table.getRows());
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void act() {
        super.act();
        this.table.setPosition(camera.position.x - table.getWidth() / 2, camera.position.y - table.getHeight() / 2);
    }

    @Override
    public void draw() {
        super.draw();
    }

    private Table main(Table table) {
        table.clear();
        TextButton textButton = new TextButton("Play", skin);
        textButton.addListener(new MenuStageListener(this) {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                this.stage.play();
            }

        });
        table.add(textButton);
        table.row();
        table.add(new TextButton("Settings", skin));
        table.row();
        table.add(new TextButton("Exit", skin));
        table.row();
        table.pack();
        return table;
    }

    private void play() {
        this.table.clear();
    }

    public void resize() {
        this.table.setPosition(camera.position.x, camera.position.y);
    }
}
