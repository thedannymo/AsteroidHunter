package com.modanny.games.ah.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.modanny.games.ah.Assets;
import com.modanny.games.ah.model.Asteroid;
import com.modanny.games.ah.model.Projectile;
import com.modanny.games.ah.model.Ship;
import com.modanny.games.ah.model.World;

public class WorldRenderer {
  private static final float FRUSTUM_WIDTH = 15;
  private static final float FRUSTUM_HEIGHT = 10;

  private final OrthographicCamera cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
  private final SpriteBatch batch;
  private final ShapeRenderer debugRenderer = new ShapeRenderer();
  private final World world;

  private boolean renderDebug;

  public WorldRenderer(World world) {
    this.world = world;

    batch = new SpriteBatch();
    batch.enableBlending();

    cam.position.set(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2, 0);
    cam.update();

    debugRenderer.setColor(Color.RED);
  }

  public void render() {
    //cam.update(); // TODO: see if commenting this out will have any negative affects (I don't think it will since the camera never moves)
    debugRenderer.setProjectionMatrix(cam.combined);
    batch.setProjectionMatrix(cam.combined);
    renderObjects();
  }

  public void renderObjects() {
    batch.begin();
    renderBackground();
    renderShip();
    renderAsteroids();
    renderProjectiles();
    renderExplosions();
    batch.end();

    if (renderDebug) {
      debugRenderer.begin(ShapeRenderer.ShapeType.Line);
      renderShipDebug();
      renderAsteroidsDebug();
      debugRenderer.end();
    }
  }

  private void renderBackground() {
    batch.draw(Assets.background, 0, 0, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
  }

  private void renderShipDebug() {
    Ship ship = world.getShip();
    if (ship == null)
      return;
    Circle bounds = ship.getBounds();
    debugRenderer.circle(bounds.x, bounds.y, bounds.radius, 25);
  }

  private void renderShip() {
    Ship ship = world.getShip();
    if (ship == null)
      return;

    Vector2 position = ship.getPosition();
    Circle bounds = ship.getBounds();
    if (!ship.isTeleporting()) {
      batch.draw(ship.isAccelerating() ? Assets.shipAnimation.getKeyFrame(ship.getStateTime(), true) : Assets.shipIdle,
                 position.x - bounds.radius, position.y - bounds.radius,
                 bounds.radius, bounds.radius, bounds.radius * 2, bounds.radius * 2,
                 1, 1, ship.getRotationDegrees() - 90);
    }
    else {
      TextureRegion textureRegion = (Ship.ShipState.TELEPORT_LEAVING == ship.getState() ?
                                     Assets.teleportLeavingAnimation :
                                     Assets.teleportArrivingAnimation).getKeyFrame(ship.getStateTime(), false);
      batch.draw(textureRegion,
                 position.x - bounds.radius, position.y - bounds.radius * 4,
                 bounds.radius, bounds.radius * 4, bounds.radius * 2, bounds.radius * 8,
                 1, 1, ship.getRotationDegrees() - 90);
    }
  }

  private void renderAsteroidsDebug() {
    for (Asteroid asteroid : world.getAsteroids()) {
      Circle bounds = asteroid.getBounds();
      debugRenderer.circle(bounds.x, bounds.y, bounds.radius, 50);
    }
  }

  private void renderAsteroids() {
    for (Asteroid asteroid : world.getAsteroids()) {
      Circle bounds = asteroid.getBounds();
      batch.draw(Assets.asteroid, bounds.x - bounds.radius, bounds.y - bounds.radius, bounds.radius, bounds.radius,
                 bounds.radius * 2, bounds.radius * 2, 1, 1, asteroid.getRotationDegrees());
    }
  }

  private void renderProjectiles() {
    for (Projectile p : world.getProjectiles()) {
      Vector2 position = p.getPosition();
      float width = p.getWidth();
      float height = p.getHeight();
      // TODO: render homing
      batch.draw(Assets.pulseShot, position.x - width / 2, position.y - height / 2,
                 width / 2, height / 2, width, height, 1, 1, p.getRotationDegrees());
    }
  }

  private void renderExplosions() {
    for (ParticleEffectPool.PooledEffect explosion : world.getExplosions())
      explosion.draw(batch);
  }

  public void toggleRenderDebug() {
    this.renderDebug = !renderDebug;
  }

  public void dispose() {
    batch.dispose();
    debugRenderer.dispose();
  }
}
