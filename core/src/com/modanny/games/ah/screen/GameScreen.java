package com.modanny.games.ah.screen;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.modanny.games.ah.Assets;
import com.modanny.games.ah.AsteroidHunterGame;
import com.modanny.games.ah.model.Ship;
import com.modanny.games.ah.model.World;

public class GameScreen extends InputAdapter implements Screen {
  private static enum GameState {
    READY(1.0f),
    RUNNING(0.0f),
    PAUSED(0.0f),
    FINISHED(0.5f);

    private final float minStateTime;

    private GameState(float minStateTime) {
      this.minStateTime = minStateTime;
    }

    private static boolean isValidStateChange(GameState oldState, GameState newState) {
      if (oldState == null)
        return true;

      switch (oldState) {
        case READY:   return RUNNING == newState;
        case RUNNING: return PAUSED == newState || FINISHED == newState;
        case PAUSED:  return RUNNING == newState;
        default:      return false;
      }
    }
  }

  private final Stage ui =
      new Stage(new StretchViewport(AsteroidHunterGame.RESOLUTION_WIDTH, AsteroidHunterGame.RESOLUTION_HEIGHT));
  private final Label.LabelStyle labelStyle = new Label.LabelStyle(Assets.font, Color.GREEN);
  private final Label scoreLabel = new Label("Score", labelStyle);
  private final Table livesTable = new Table();
  private final Label startLabel = new Label("Tap to start...", labelStyle);
  private final Label quitLabel = new Label("Tap to quit...", labelStyle);
  private final RoundImageButton leftButton = new RoundImageButton(
      32, 32, 32,
      new TextureRegionDrawable(Assets.arrowLeft), new TextureRegionDrawable(Assets.arrowLeftPressed));
  private final RoundImageButton rightButton = new RoundImageButton(
      leftButton.bounds.x + 64, 32, 32,
      new TextureRegionDrawable(Assets.arrowRight), new TextureRegionDrawable(Assets.arrowRightPressed));
  private final RoundImageButton upButton = new RoundImageButton(
      leftButton.bounds.x + leftButton.bounds.radius, leftButton.bounds.y + leftButton.bounds.radius + 16, 40,
      new TextureRegionDrawable(Assets.arrowUp), new TextureRegionDrawable(Assets.arrowUpPressed));
  private final RoundImageButton shootButton = new RoundImageButton(
      AsteroidHunterGame.RESOLUTION_WIDTH - 32 - 8, 40, 48,
      new TextureRegionDrawable(Assets.shootButton), new TextureRegionDrawable(Assets.shootButtonPressed));
  private final RoundImageButton teleportButton = new RoundImageButton(
      shootButton.bounds.x - 64 - 4, 24, 24,
      new TextureRegionDrawable(Assets.teleportButton), new TextureRegionDrawable(Assets.teleportButtonPressed));

  private final InputMechanism leftInput;
  private final InputMechanism rightInput;
  private final InputMechanism upInput;
  private final InputMechanism shootInput;
  private final InputMechanism teleportInput;

  private final AsteroidHunterGame game;
  private final World world;
  private final WorldRenderer worldRenderer;

  private GameState state;
  private float remainingMinStateTime;
  private int lastScore = -1;
  private int lives = -1;

  public GameScreen(AsteroidHunterGame game) {
    this.game = game;

    world = new World(game);
    worldRenderer = new WorldRenderer(world);

    ApplicationType applicationType = Gdx.app.getType();
    if (ApplicationType.Android == applicationType || ApplicationType.iOS == applicationType) {
      leftInput = new MobileInputMechanism(leftButton);
      rightInput = new MobileInputMechanism(rightButton);
      upInput = new MobileInputMechanism(upButton);
      shootInput = new MobileInputMechanism(shootButton);
      teleportInput = new MobileInputMechanism(teleportButton);
    }
    else {
      leftInput = new DesktopInputMechanism(Keys.DPAD_LEFT);
      rightInput = new DesktopInputMechanism(Keys.DPAD_RIGHT);
      upInput = new DesktopInputMechanism(Keys.DPAD_UP);
      shootInput = new DesktopInputMechanism(Keys.SPACE);
      teleportInput = new DesktopInputMechanism(Keys.Z);
    }

    OrthographicCamera guiCam = new OrthographicCamera(AsteroidHunterGame.RESOLUTION_WIDTH, AsteroidHunterGame.RESOLUTION_HEIGHT);
    guiCam.position.set(AsteroidHunterGame.RESOLUTION_WIDTH / 2, AsteroidHunterGame.RESOLUTION_HEIGHT / 2, 0);

    Batch uiSpriteBatch = ui.getSpriteBatch();
    uiSpriteBatch.setProjectionMatrix(guiCam.combined);
    uiSpriteBatch.enableBlending();

    Gdx.input.setInputProcessor(ui);

    layoutUI();
    changeState(GameState.READY);
  }

  private void layoutUI() {
    scoreLabel.setPosition(4, AsteroidHunterGame.RESOLUTION_HEIGHT - scoreLabel.getHeight());
    startLabel.setPosition(AsteroidHunterGame.RESOLUTION_WIDTH / 2 - startLabel.getWidth() / 2, AsteroidHunterGame.RESOLUTION_HEIGHT / 2 - startLabel.getHeight() / 2);
    quitLabel.setPosition(AsteroidHunterGame.RESOLUTION_WIDTH / 2 - startLabel.getWidth() / 2, AsteroidHunterGame.RESOLUTION_HEIGHT / 2 - startLabel.getHeight() / 2);


    livesTable.setWidth(300);
    livesTable.setHeight(30);
    livesTable.setPosition(4, scoreLabel.getY() - scoreLabel.getHeight());
    ui.addActor(scoreLabel);
    ui.addActor(livesTable);
    ui.addActor(startLabel);
    ui.addActor(quitLabel);
    ui.addActor(leftButton);
    ui.addActor(rightButton);
    ui.addActor(upButton);
    ui.addActor(shootButton);
    ui.addActor(teleportButton);

    scoreLabel.setVisible(false);
    livesTable.setVisible(false);
    startLabel.setVisible(false);
    quitLabel.setVisible(false);
    leftButton.setVisible(false);
    rightButton.setVisible(false);
    upButton.setVisible(false);
    shootButton.setVisible(false);
    teleportButton.setVisible(false);
  }

  private void changeState(GameState newState) {
    if (!GameState.isValidStateChange(state, newState))
      throw new IllegalStateException(String.format("invalid state change: %s --> %s", state, newState));
    else if (remainingMinStateTime > 0)
      return;

    switch (newState) {
      case READY: {
        scoreLabel.setVisible(true);
        livesTable.setVisible(true);
        leftButton.setVisible(true);
        rightButton.setVisible(true);
        upButton.setVisible(true);
        shootButton.setVisible(true);
        teleportButton.setVisible(true);

        ui.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f)));

        // TODO: set help stuff visible
      }
      break;
      case RUNNING: {
        startLabel.setVisible(false);
        world.nextLevel();
        // TODO: fade out help stuff

      }
      break;
      case PAUSED: {
        // TODO: Continue/Quit
      }
      break;
      case FINISHED: {
        leftButton.setVisible(false);
        rightButton.setVisible(false);
        upButton.setVisible(false);
        shootButton.setVisible(false);
        teleportButton.setVisible(false);
      }
      break;
    }
    state = newState;
    remainingMinStateTime = state.minStateTime;
  }

  public void update(float deltaTime) {
    if (deltaTime > 0.1f)
      deltaTime = 0.1f;

    switch (state) {
      case READY:
        updateReady();
        break;
      case RUNNING:
        updateRunning(deltaTime);
        break;
      case FINISHED:
        updateGameOver(deltaTime);
        break;
      case PAUSED:
        break;
    }
    if (remainingMinStateTime > 0)
      remainingMinStateTime -= deltaTime;
  }

  private void updateReady() {
    if (remainingMinStateTime <= 0) {
      if (!startLabel.isVisible()) {
        startLabel.setVisible(true);
        startLabel.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f)));
      }
      if (Gdx.input.justTouched())
        changeState(GameState.RUNNING);
    }
  }

  private void updateRunning(float deltaTime) {
    Ship ship = world.getShip();
    if (ship != null && !ship.isTeleporting()) {
      if (upInput.isPressed())
        ship.accelerate();
      else
        ship.idle();

      ship.turn(leftInput.isPressed(), rightInput.isPressed());

      if (shootInput.isPressed())
        ship.shoot();
      if (teleportInput.isPressed())
        ship.teleport();
    }

    world.update(deltaTime);
    if (world.score != lastScore) {
      lastScore = world.score;
      scoreLabel.setText(Integer.toString(world.score));
    }

    int worldLives = world.getNumLives();
    if (worldLives != lives) {
      lives = worldLives;
      livesTable.clearChildren();
      // TODO: start new row every 5
      for (int i = 0; i < lives; i++)
        livesTable.add(new Image(Assets.shipGlow)).left();

      livesTable.add().expandX();
      livesTable.invalidate();
    }
    if (world.getState() == World.WorldState.GAME_OVER)
      changeState(GameState.FINISHED);
  }

  private void updateGameOver(float deltaTime) {
    world.update(deltaTime);

    if (remainingMinStateTime <= 0) {
      if (!quitLabel.isVisible()) {
        quitLabel.setVisible(true);
        quitLabel.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f)));
      }
      if (Gdx.input.justTouched())
        game.setScreen(new MainMenuScreen(game));
    }
  }

  @Override
  public boolean keyDown(int keycode) {
    if (Keys.Q == keycode)
      worldRenderer.toggleRenderDebug();

    return true;
  }

  @Override
  public void render(float delta) {
    update(delta);

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);

    if (world.getLevel() > 0)
      worldRenderer.render();

    ui.draw();
  }

  @Override
  public void dispose() {
    ui.dispose();
    worldRenderer.dispose();
  }

  @Override
  public void resize(int width, int height) {
    ui.getViewport().update(AsteroidHunterGame.RESOLUTION_WIDTH, AsteroidHunterGame.RESOLUTION_HEIGHT, true);
  }

  @Override
  public void show() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  private static interface InputMechanism {
    public boolean isPressed();
  }

  private static class MobileInputMechanism implements InputMechanism {
    private final Button button;

    public MobileInputMechanism(Button button) {
      this.button = button;
    }

    @Override
    public boolean isPressed() {
      return button.isPressed();
    }
  }

  private static class DesktopInputMechanism implements InputMechanism {
    private final int keyCode;

    private DesktopInputMechanism(int keyCode) {
      this.keyCode = keyCode;
    }

    @Override
    public boolean isPressed() {
      return Gdx.input.isKeyPressed(keyCode);
    }
  }

  public final class RoundImageButton extends ImageButton {
    private final Circle bounds = new Circle();

    public RoundImageButton(float centerX, float centerY, float radius, Drawable imageUp, Drawable imageDown) {
      super(imageUp, imageDown);

      bounds.set(centerX, centerY, radius);

      super.setX(centerX - radius);
      super.setY(centerY - radius);

      float diameter = radius * 2;
      float minWidth = imageUp.getMinWidth();
      float minHeight = imageUp.getMinHeight();

      Image image = getImage();
      image.setOrigin(image.getX() + image.getWidth() / 2, image.getY() + image.getHeight() / 2);
      image.setScale(diameter / minWidth, diameter / minHeight);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
      if (touchable && getTouchable() == Touchable.disabled) return null;
      return bounds.contains(x + getX(), y + getY()) ? this : null;
    }
  }

}
