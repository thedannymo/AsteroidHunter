package com.modanny.games.ah.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.modanny.games.ah.Assets;
import com.modanny.games.ah.AsteroidHunterGame;
import com.modanny.games.ah.AsteroidHunterPreferences;

public class MainMenuScreen extends ScreenAdapter {
  private final Stage stage =
      new Stage(new StretchViewport(AsteroidHunterGame.RESOLUTION_WIDTH, AsteroidHunterGame.RESOLUTION_HEIGHT));
  public MainMenuScreen(final AsteroidHunterGame game) {
    final AsteroidHunterPreferences prefs = game.getPreferences();

    int resWidth = AsteroidHunterGame.RESOLUTION_WIDTH;
    int resHeight = AsteroidHunterGame.RESOLUTION_HEIGHT;

    OrthographicCamera guiCam = new OrthographicCamera(resWidth, resHeight);
    guiCam.position.set(resWidth / 2, resHeight / 2, 0);

    int y = resHeight - Assets.logo.getRegionHeight() - 10;

    Image background = new Image(Assets.background);
    background.setPosition(0, 0);
    background.setFillParent(true);

    Image logo = new Image(Assets.logo);
    logo.setX(resWidth / 2 - Assets.logo.getRegionWidth() / 2);
    logo.setY(y);
    y -= (Assets.newGame.getRegionHeight() + 50);

    ImageButton newGame = new ImageButton(new TextureRegionDrawable(Assets.newGame));
    newGame.setX(resWidth / 2 - Assets.newGame.getRegionWidth() / 2);
    newGame.setY(y);
    newGame.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        game.setScreen(new GameScreen(game));
      }
    });
    y -= (Assets.highScores.getRegionHeight() + 15);

    ImageButton highScores = new ImageButton(new TextureRegionDrawable(Assets.highScores));
    highScores.setX(resWidth / 2 - Assets.highScores.getRegionWidth() / 2);
    highScores.setY(y);
    highScores.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        return false;  //TODO
      }
    });

    final ImageButton soundOff = new ImageButton(new TextureRegionDrawable(Assets.soundOff));
    soundOff.setY(0);
    soundOff.setX(resWidth - Assets.soundOff.getRegionWidth());
    soundOff.setWidth(50);
    soundOff.setHeight(50);
    final ImageButton soundOn = new ImageButton(new TextureRegionDrawable(Assets.soundOn));
    soundOn.setY(0);
    soundOn.setX(soundOff.getX());
    soundOn.setWidth(50);
    soundOn.setHeight(50);
    soundOff.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        soundOff.setVisible(false);
        soundOn.setVisible(true);
        prefs.setSoundOn(true);
      }
    });
    soundOn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        soundOff.setVisible(true);
        soundOn.setVisible(false);
        prefs.setSoundOn(false);
      }
    });

    final ImageButton musicOff = new ImageButton(new TextureRegionDrawable(Assets.musicOff));
    musicOff.setY(0);
    musicOff.setX(0);
    musicOff.setWidth(50);
    musicOff.setHeight(50);
    final ImageButton musicOn = new ImageButton(new TextureRegionDrawable(Assets.musicOn));
    musicOn.setY(0);
    musicOn.setX(musicOff.getX());
    musicOn.setWidth(50);
    musicOn.setHeight(50);
    musicOff.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        musicOff.setVisible(false);
        musicOn.setVisible(true);
        prefs.setMusicOn(true);
      }
    });
    musicOn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        musicOff.setVisible(true);
        musicOn.setVisible(false);
        prefs.setMusicOn(false);
      }
    });

//    logo.setOrigin(logo.getWidth() / 2, logo.getHeight() / 2);
//    logo.addListener(new DragListener() {
//      private float offsetX, offsetY;
//
//      @Override
//      public void dragStart(InputEvent event, float x, float y, int pointer)
//      {
//        Actor target = event.getTarget();
//        System.out.println(event.getStageX() + " ==? " + x);
//        System.out.println(event.getStageY() + " ==? " + y);
//        this.offsetX = event.getStageX() - target.getX();
//        this.offsetY = event.getStageY() - target.getY();
//      }
//
//      public void drag(InputEvent event, float x, float y, int pointer) {
//        event.getTarget().setPosition(event.getStageX() - offsetX, event.getStageY() - offsetY);
//      }
//    });
//    logo.rotate(130);

    stage.addActor(background);
    stage.addActor(logo);
    stage.addActor(newGame);
    stage.addActor(highScores);
    stage.addActor(soundOff);
    stage.addActor(soundOn);
    stage.addActor(musicOff);
    stage.addActor(musicOn);

    if (prefs.isMusicOn()) {
      musicOn.setVisible(true);
      musicOff.setVisible(false);
    }
    else {
      musicOn.setVisible(false);
      musicOff.setVisible(true);
    }
    if (prefs.isSoundOn()) {
      soundOn.setVisible(true);
      soundOff.setVisible(false);
    }
    else {
      soundOn.setVisible(false);
      soundOff.setVisible(true);
    }


    Batch spriteBatch = stage.getSpriteBatch();
    spriteBatch.setProjectionMatrix(guiCam.combined);
    spriteBatch.enableBlending();
    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void show() {
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
