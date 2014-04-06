package com.modanny.games.ah.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.modanny.games.ah.Assets;
import com.modanny.games.ah.AsteroidHunterGame;
import com.modanny.games.ah.AsteroidHunterPreferences;

public class MainMenuScreen extends ScreenAdapter {
  private final Stage stage = new Stage(new ScreenViewport());

  private final Image background = new Image(Assets.background);
  private final Image logo = new Image(Assets.logo);
  private final ImageButton newGame = new ImageButton(new TextureRegionDrawable(Assets.newGame));
  private final ImageButton highScores = new ImageButton(new TextureRegionDrawable(Assets.highScores));
  private final Image soundToggle = new Image();
  private final Image musicToggle = new Image();
  private final TextureRegionDrawable soundOnDrawable = new TextureRegionDrawable(Assets.soundOn);
  private final TextureRegionDrawable soundOffDrawable = new TextureRegionDrawable(Assets.soundOff);
  private final TextureRegionDrawable musicOnDrawable = new TextureRegionDrawable(Assets.musicOn);
  private final TextureRegionDrawable musicOffDrawable = new TextureRegionDrawable(Assets.musicOff);


  public MainMenuScreen(final AsteroidHunterGame game) {
    final AsteroidHunterPreferences prefs = game.getPreferences();

    newGame.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        game.setScreen(new GameScreen(game));
      }
    });

    soundToggle.addListener(new ClickListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        boolean soundOn = !prefs.isSoundOn();
        prefs.setSoundOn(soundOn);
        updateSoundButtonState(soundOn);
        return true;
      }
    });

    musicToggle.addListener(new ClickListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        boolean musicOn = !prefs.isMusicOn();
        prefs.setMusicOn(musicOn);
        updateMusicButtonState(musicOn);
        return true;
      }
    });

    Gdx.input.setInputProcessor(stage);

    updateSoundButtonState(prefs.isSoundOn());
    updateMusicButtonState(prefs.isMusicOn());

    layoutUi();
  }

  private void layoutUi() {
    background.setFillParent(true);

    Table table = new Table();
    Table centerTable = new Table();

    stage.addActor(background);
    centerTable.add(logo).padBottom(50).row();
    centerTable.add(newGame).padBottom(25).row();
    centerTable.add(highScores).row();
    table.add(centerTable).colspan(3).expand().row();
    table.add(soundToggle).left();
    table.add();
    table.add(musicToggle).right().row();
    table.setFillParent(true);
    stage.addActor(table);
  }

  private void updateSoundButtonState(boolean soundOn) {
    if (soundOn)
      soundToggle.setDrawable(soundOnDrawable);
    else
      soundToggle.setDrawable(soundOffDrawable);
  }

  private void updateMusicButtonState(boolean musicOn) {
    if (musicOn)
      musicToggle.setDrawable(musicOnDrawable);
    else
      musicToggle.setDrawable(musicOffDrawable);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }
}
