package com.modanny.games.ah;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class AsteroidHunterPreferences {
  private static final String PREFS_FILENAME = "settings";

  private boolean soundOn;
  private boolean musicOn;

  public void load() {
    Preferences prefs = Gdx.app.getPreferences(PREFS_FILENAME);
    soundOn = prefs.getBoolean("soundOn", true);
    musicOn = prefs.getBoolean("musicOn", true);
  }

  public boolean isSoundOn() {
    return soundOn;
  }

  public void setSoundOn(boolean soundOn) {
    this.soundOn = soundOn;

    Preferences prefs = Gdx.app.getPreferences(PREFS_FILENAME);
    prefs.putBoolean("soundOn", soundOn);
    prefs.flush();
  }

  public boolean isMusicOn() {
    return musicOn;
  }

  public void setMusicOn(boolean musicOn) {
    this.musicOn = musicOn;

    Preferences prefs = Gdx.app.getPreferences(PREFS_FILENAME);
    prefs.putBoolean("musicOn", musicOn);
    prefs.flush();
  }
}
