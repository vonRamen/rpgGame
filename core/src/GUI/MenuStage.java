/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.GUIGraphics;
import Persistence.GlobalGameSettings;
import Persistence.Sound2D;
import Server.MPServer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.reflectasm.shaded.org.objectweb.asm.Type;
import com.mygdx.game.Game;
import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kristian
 */
public class MenuStage extends Stage {

    private Skin skin;
    private Table table;
    private Group root;
    private TextureRegion backgroundImage;
    private MPServer server;
    private Game game;

    private TextButton keyReference;
    private String fieldReference;

    private Label statusLabel;

    public MenuStage(Game game) {
        super();
        this.game = game;
        Sound2D.playMusic("Deep Forest.ogg");
        this.backgroundImage = new TextureRegion(GUIGraphics.get("menu_background.png").getRegion());
        //setup camera and viewport
        OrthographicCamera cam = new OrthographicCamera();
        this.setViewport(new ScreenViewport(cam));

        root = new Group();
        skin = new Skin(Gdx.files.internal("gui skins/uiskin.json"));
        this.switchMenu(this.menuMain());

        this.addActor(root);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void act() {
        super.act();
        Sound2D.updateMusic(Gdx.graphics.getDeltaTime());
        if (this.statusLabel != null) {
            String newLog = this.server.getLog();
            if (newLog != null) {
                this.statusLabel.setText(newLog);
            }
        }
        if (this.server != null) {
            if(this.server.isReady()) {
                this.game.joinGame("127.0.0.1", this.server.getPort(), "Host", "");
            }
        }
    }

    @Override
    public void draw() {
        Camera camera = this.getViewport().getCamera();
        camera.update();

        if (!root.isVisible()) {
            return;
        }

        Batch batch = this.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(this.backgroundImage, 0, 0);
        this.getRoot().draw(batch, 1);
        batch.end();
    }

    private void switchMenu(Table table) {
        this.root.clear();
        for (Actor b : table.getChildren()) {
            if (b instanceof TextButton) {
            }
        }
        this.root.addActor(table);
        this.resize();
    }

    private Table menuMain() {
        Table table = new Table();
        TextButton textButton = new TextButton("Play", skin);
        textButton.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuPlay());
            }

        });
        TextButton settings = new TextButton("Settings", skin);
        settings.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuSettings());
            }

        });
        textButton.setWidth(200);
        table.add(textButton);
        textButton.getLabel().setAlignment(Align.left);
        table.row();
        table.add(settings);
        table.row();
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                Gdx.app.exit();
            }

        });
        table.add(exitButton);
        table.row();
        //table.align(Align.center);
        //table.pack();
        return table;
    }

    private Table menuPlay() {
        Table table = new Table();

        TextButton textButton = new TextButton("Connect", skin);
        textButton.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuConnect());
            }

        });
        table.add(textButton);
        table.row();

        TextButton host = new TextButton("Host", skin);
        host.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuHost());
            }

        });
        table.add(host);
        table.row();

        TextButton back = new TextButton("Back", skin);
        back.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuMain());
            }

        });
        table.add(back);
        table.row();

        table.pack();
        return table;
    }
    
    private Table menuConnect() {
        Table table = new Table();
        
        Label ipLabel = new Label("Ip Adress:", this.skin);
        final TextField ipAddress = new TextField("127.0.0.1", this.skin);
        table.add(ipLabel, ipAddress);
        table.row();
        
        Label portLabel = new Label("Port:", this.skin);
        final TextField port = new TextField("7777", this.skin);
        table.add(portLabel, port);
        table.row();
        
        Label userNameLabel = new Label("Username:", this.skin);
        final TextField userName = new TextField("", this.skin);
        table.add(userNameLabel, userName);
        table.row();
        
        Label passWordLabel = new Label("Password:", this.skin);
        final TextField passWord = new TextField("", this.skin);
        passWord.setPasswordMode(true);
        passWord.setPasswordCharacter('*');
        table.add(passWordLabel, passWord);
        table.row();
        
        TextButton back = new TextButton("Back", skin);
        back.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuPlay());
            }

        });
        table.add(back);
        
        TextButton connect = new TextButton("Connect", skin);
        connect.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                game.joinGame(ipAddress.getText(), Integer.parseInt(port.getText()), userName.getText(), passWord.getText());
            }

        });
        table.add(connect);
        
        return table;
    }

    private Table menuHost() {
        Table table = new Table();
        
        TextButton loadWorld = new TextButton("Load World", skin);
        loadWorld.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuLoadWorld());
            }

        });
        table.add(loadWorld);
        table.row();

        TextButton newWorld = new TextButton("New World", skin);
        newWorld.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuNewWorld());
            }

        });
        table.add(newWorld);
        table.row();

        TextButton back = new TextButton("Back", skin);
        back.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuPlay());
            }

        });
        table.add(back);

        return table;
    }
    
    private Table menuLoadWorld() {
        Table table = new Table();
        
        ScrollPane pane = new ScrollPane(table, this.skin);
        
        //add a button for each world.
        FileHandle worlds = Gdx.files.internal("worlds/");
        for(FileHandle file : worlds.list()) {
            TextButton newButton = new TextButton(file.nameWithoutExtension(), this.skin);
            final FileHandle finalizedFile = file;
            newButton.addListener(new MenuStageListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                    String[] args = {finalizedFile.nameWithoutExtension(), "7777"};
                    server = new MPServer(args);
                    
                    server.start();
                    
                    switchMenu(gotoWorld());
                }
                
            });
            table.add(newButton);
            table.row();
        }
        
        TextButton back = new TextButton("Back", skin);
        back.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuHost());
            }

        });
        table.add(back);
        
        return table;
    }

    private Table menuNewWorld() {
        Table table = new Table();
        table.row();

        final TextField name = new TextField("Name", skin);
        name.setTextFieldFilter(new TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return (c == 32) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
            }

        });
        table.add(name);
        table.row();

        final TextField width = new TextField("Width", skin);
        width.setTextFieldFilter(new TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return (c >= 48 && c <= 57);
            }

        });
        table.add(width);
        table.row();

        final TextField height = new TextField("Height", skin);
        height.setTextFieldFilter(new TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return (c >= 48 && c <= 57);
            }

        });
        table.add(height);
        table.row();

        TextButton createWorld = new TextButton("Create!", skin);
        createWorld.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                String[] args = {name.getText(), "7777", width.getText(), height.getText()};
                server = new MPServer(args);

                server.start();
                switchMenu(gotoWorld());
            }

        });
        table.add(createWorld);

        TextButton back = new TextButton("Back", skin);
        back.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuPlay());
            }

        });
        table.add(back);

        return table;
    }

    private Table gotoWorld() {
        Table table = new Table();

        this.statusLabel = new Label("Creating World..", this.skin);
        table.add(this.statusLabel);

        return table;
    }

    private Table menuSettings() {
        Table table = new Table();
        final GlobalGameSettings settings = Game.settings;

        String screenText = Gdx.graphics.isFullscreen() ? "Go Windowed!" : "Go Fullscreen!";

        TextButton fullScreen = new TextButton(screenText, skin);
        fullScreen.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                if (!Gdx.graphics.isFullscreen()) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(600, 800);
                }
                settings.setIsFullScreen(Gdx.graphics.isFullscreen());
                switchMenu(menuSettings());
            }

        });
        table.add(fullScreen);
        table.row();

        TextButton keybindings = new TextButton("Keybindings", skin);
        keybindings.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuSettingsKeybindings());
            }

        });
        table.add(keybindings);
        table.row();

        HorizontalGroup masterSound = new HorizontalGroup();
        Label soundMaster = new Label("Sound Master Volume", skin);
        table.add(soundMaster);
        final Slider slider = new Slider(0, 100, 1, false, skin);
        slider.setValue(settings.getMasterSoundVolume() * 100);
        final Label volume = new Label(String.valueOf(slider.getValue()), skin);
        slider.addListener(new ClickListener() {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer); //To change body of generated methods, choose Tools | Templates.
                settings.setMasterSoundVolume(slider.getValue() / 100);
                volume.setText(String.valueOf(slider.getValue()));
            }
        });
        masterSound.addActor(volume);
        masterSound.addActor(slider);
        table.row();
        table.add(masterSound);
        table.row();

        HorizontalGroup masterMusic = new HorizontalGroup();
        Label musicMaster = new Label("Music Master Volume", skin);
        table.add(musicMaster);
        final Slider musicSlider = new Slider(0, 100, 1, false, skin);
        musicSlider.setValue(settings.getMasterMusicVolume() * 100);
        final Label musicVolume = new Label(String.valueOf(musicSlider.getValue()), skin);
        musicSlider.addListener(new ClickListener() {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer); //To change body of generated methods, choose Tools | Templates.
                settings.setMasterMusicVolume(musicSlider.getValue() / 100);
                musicVolume.setText(String.valueOf(musicSlider.getValue()));
            }
        });
        masterMusic.addActor(musicVolume);
        masterMusic.addActor(musicSlider);
        table.row();
        table.add(masterMusic);
        table.row();

        HorizontalGroup buttons = new HorizontalGroup();
        TextButton back = new TextButton("Cancel", skin);
        back.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuMain());
            }

        });
        buttons.addActor(back);

        TextButton confirm = new TextButton("Confirm", skin);
        confirm.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                settings.save();
                switchMenu(menuMain());
            }

        });
        buttons.addActor(confirm);

        TextButton defaultButton = new TextButton("Default", skin);
        defaultButton.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                Game.settings = GlobalGameSettings.setDefaultValue();
                switchMenu(menuSettings());
            }
        });
        buttons.addActor(defaultButton);
        buttons.pack();
        table.add(buttons);
        table.pack();

        return table;
    }

    private Table menuSettingsKeybindings() {
        Table table = new Table();
        GlobalGameSettings settings = Game.settings;

        for (Field numb : settings.getKeybindings().getClass().getDeclaredFields()) {
            HorizontalGroup g = new HorizontalGroup();
            Label name = new Label(camelToNormal(numb.getName()), skin);
            g.addActor(name);

            final String fieldName = numb.getName();

            try {
                final TextButton newKey = new TextButton(Input.Keys.toString(numb.getInt(settings.getKeybindings())), skin);
                newKey.addListener(new KeybindingListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                        keyReference = newKey;
                        fieldReference = fieldName;
                    }

                });
                g.addActor(newKey);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(MenuStage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MenuStage.class.getName()).log(Level.SEVERE, null, ex);
            }

            table.add(g);
            table.row();
        }

        TextButton keybindings = new TextButton("Back", skin);
        keybindings.addListener(new MenuStageListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
                switchMenu(menuSettings());
            }

        });
        table.add(keybindings);
        table.row();

        return table;
    }

    private void play() {
        this.root.clear();
    }

    public void resize() {
        this.root.setPosition(Gdx.graphics.getWidth() / 2 - this.root.getChildren().get(0).getWidth() / 2, Gdx.graphics.getHeight() / 2 - this.root.getChildren().get(0).getHeight() / 2);
    }

    public static String camelToNormal(String string) {
        char charAt = string.charAt(0);
        String newString = "" + charAt;
        newString = newString.toUpperCase();
        string = newString + string.substring(1, string.length());
        return string.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    @Override
    public boolean keyUp(int keyCode) {
        if (this.keyReference != null) {
            try {
                this.keyReference.setText(Input.Keys.toString(keyCode));

                //Save settings
                GlobalGameSettings settings = Game.settings;

                try {
                    settings.getKeybindings().getClass().getDeclaredField(fieldReference).set(settings.getKeybindings(), keyCode);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(MenuStage.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(MenuStage.class.getName()).log(Level.SEVERE, null, ex);
                }

                this.keyReference = null;
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(MenuStage.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(MenuStage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return super.keyUp(keyCode); //To change body of generated methods, choose Tools | Templates.
    }

}
