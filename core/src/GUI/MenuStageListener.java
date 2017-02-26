/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Persistence.Sound2D;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 * @author kristian
 */
public class MenuStageListener extends ClickListener {
    
    public MenuStageListener() {
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y); //To change body of generated methods, choose Tools | Templates.
        Sound2D sound = new Sound2D("sound_click_01.mp3");
        sound.play(false);
    }

}
