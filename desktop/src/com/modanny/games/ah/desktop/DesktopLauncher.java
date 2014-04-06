package com.modanny.games.ah.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.modanny.games.ah.AsteroidHunterGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = "Asteroid Hunter";
    config.width = AsteroidHunterGame.RESOLUTION_WIDTH;
    config.height = AsteroidHunterGame.RESOLUTION_HEIGHT;
		new LwjglApplication(new AsteroidHunterGame(), config);
	}
}
