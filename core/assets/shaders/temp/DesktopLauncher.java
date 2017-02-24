package com.alcovegames.fireshader;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {

	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Alcove Games - Fire Shader";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 600;
		cfg.vSyncEnabled = true;
		cfg.resizable = false;
 
		new LwjglApplication(new Game(), cfg);
	}
}
