package com.modanny.games.ah;

import com.badlogic.gdx.Game;
import com.modanny.games.ah.screen.MainMenuScreen;

public class AsteroidHunterGame extends Game {
  public static final int RESOLUTION_WIDTH = 800;
  public static final int RESOLUTION_HEIGHT = 600;

  private final AsteroidHunterPreferences preferences = new AsteroidHunterPreferences();

  @Override
  public void create() {
    preferences.load();
    Assets.load();

    setScreen(new MainMenuScreen(this));
  }

  public AsteroidHunterPreferences getPreferences() {
    return preferences;
  }
}
