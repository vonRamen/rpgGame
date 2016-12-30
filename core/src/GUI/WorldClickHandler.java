/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.game.Drawable;
import com.mygdx.game.Game;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Player;

/**
 *
 * @author kristian
 */
public class WorldClickHandler {

    private OrthographicCamera camera;
    private GameWorld world;
    private Player player;

    public WorldClickHandler(Player player, OrthographicCamera camera, GameWorld world) {
        this.player = player;
        this.camera = camera;
        this.world = world;
    }

    public void click(int x, int y) {
        int tileX = getTileX(x);
        int tileY = getTileY(y)-1;
        System.out.println("X "+tileX*32+" Y "+tileY*32);

        for(Drawable drawable : world.getDrawable()) {
            if(drawable.getX() >= tileX*32 && drawable.getX() <= tileX*32+32 &&
                    drawable.getY() >= tileY*32 && drawable.getY() <= tileY*32+32) {
                drawable.getActions().get(0).executeAction(player, drawable);
            }
        }
    }
    
    public int getTileX(int x) {
        return (int) (getWorldPositionX(x)/32);
    }
    
    public int getTileY(int y) {
        return (int) (getWorldPositionY(y)/32);
    }
    
    public int getWorldPositionX(int x) {
        return (int) (x+camera.position.x-camera.viewportWidth/2);
    }
    
    public int getWorldPositionY(int y) {
        return (int) (Math.abs(y-Gdx.graphics.getHeight())+camera.position.y-camera.viewportHeight/2);
    }
}
