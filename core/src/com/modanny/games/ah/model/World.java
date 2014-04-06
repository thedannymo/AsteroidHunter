package com.modanny.games.ah.model;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.modanny.games.ah.Assets;
import com.modanny.games.ah.AsteroidHunterGame;

import java.util.*;

public class World {
  public static final float WORLD_WIDTH = 15;
  public static final float WORLD_HEIGHT = 10;

  public static final List<Vector2> SPAWN_POSITIONS;

  static {
    int positionsWidth = (int) (WORLD_WIDTH / 0.25f);
    int positionsHeight = (int) (WORLD_HEIGHT / 0.25f);
    SPAWN_POSITIONS = new ArrayList<Vector2>(positionsWidth * positionsHeight);
    for (int i = 0; i < positionsWidth; i++) {
      for (int j = 0; j < positionsHeight; j++)
        SPAWN_POSITIONS.add(new Vector2(0.25f * i, 0.25f * j));
    }
  }

  public Asteroid getNearestAsteroid(Vector2 position) {
    Asteroid nearestAsteroid = null;
    float nearestDistance = Float.MAX_VALUE;
    for (Asteroid a : asteroids) {
      float dst = a.getPosition().dst(position);
      if (nearestAsteroid == null || dst < nearestDistance) {
        nearestAsteroid = a;
        nearestDistance = dst;
      }
    }
    return nearestAsteroid;
  }

  public static enum WorldState {
    RUNNING,
    GAME_OVER,
    WIN,
    DEAD,
    NEXT_LEVEL
  }

  public static final float TIME_BETWEEN_LEVELS = 1.5f;

  private final AsteroidHunterGame game;
  private final List<Projectile> projectiles = new ArrayList<Projectile>();
  private final List<Asteroid> asteroids = new ArrayList<Asteroid>();
  private final List<ParticleEffectPool.PooledEffect> explosions = new ArrayList<ParticleEffectPool.PooledEffect>(15);
  private final Random rand = new Random();

  private Ship ship;
  private WorldState state;
  public int score, bonusCount;
  private int level;
  private int numLives;
  private float waitTime;

  public World(AsteroidHunterGame game) {
    this.game = game;
    ship = new Ship(game, this, WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
    state = WorldState.RUNNING;
    numLives = 3;
    bonusCount = 0;
  }

  public void nextLevel() {
    state = WorldState.RUNNING;
    level++;

    if (level <= 0)
      throw new IllegalStateException("level must be >= 1");

    int numAsteroids = 5 + (level - 1);
    ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>(numAsteroids);
//    for (int i = 0; i < numAsteroids; i++)
//    {
//      float x = rand.nextFloat() * WORLD_WIDTH;
//      float y = rand.nextFloat() * WORLD_HEIGHT;
//      Asteroid asteroid = new Asteroid(Asteroid.AsteroidSize.LARGE, x, y);
//      do // TODO can we do this better?
//      {
//        x = rand.nextFloat() * WORLD_WIDTH;
//        y = rand.nextFloat() * WORLD_HEIGHT;
//
//        asteroid.getPosition().set(x, y);
//      }
//      while (asteroid.getPosition().dst(ship.getPosition()) < 3);
//    }
    for (int i = 0; i < numAsteroids; i++) {
      Collections.shuffle(SPAWN_POSITIONS);
      for (Vector2 spawnPos : SPAWN_POSITIONS) {
        if (spawnPos.dst(ship.getPosition()) > 3) {
          asteroids.add(new Asteroid(Asteroid.AsteroidSize.LARGE, spawnPos.x, spawnPos.y));
          break;
        }
      }
    }
    this.asteroids.addAll(asteroids);
  }

  private boolean respawnShip() {
    // can't respawn while asteroids block the respawn point
    for (Asteroid a : asteroids) {
      if (a.getPosition().dst(WORLD_WIDTH / 2, WORLD_HEIGHT / 2) < a.getBounds().radius * 2.0f)
        return false;
    }
    ship = new Ship(game, this, WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
    return true;
  }

  public void update(float deltaTime) {
    switch (state) {
      case RUNNING:
      case GAME_OVER:
        updateRunning(deltaTime);
        break;
      case WIN:
        updateWin();
        break;
      case NEXT_LEVEL: // TODO: don't let this happen if player is dead!
        waitTime -= deltaTime;
        updateRunning(deltaTime);
        if (waitTime <= 0) {
          nextLevel();
          state = WorldState.RUNNING;
        }
        break;
      case DEAD:
        updateDead(deltaTime);
        break;
    }
  }

  private void updateWin() {
    waitTime = TIME_BETWEEN_LEVELS;
    state = WorldState.NEXT_LEVEL;
  }

  private void updateRunning(float deltaTime) {
    if (ship != null)
      ship.update(deltaTime);

    if (asteroids.isEmpty() && state != WorldState.NEXT_LEVEL)
      state = WorldState.WIN;
    else {
      for (Asteroid asteroid : asteroids)
        asteroid.update(deltaTime);
    }

    Iterator<Projectile> projectileIter = projectiles.iterator();
    while (projectileIter.hasNext()) {
      Projectile p = projectileIter.next();
      p.update(deltaTime);
      if (p.isDead())
        projectileIter.remove();
    }
    Iterator<ParticleEffectPool.PooledEffect> explosionIter = explosions.iterator();
    while (explosionIter.hasNext()) {
      ParticleEffectPool.PooledEffect explosion = explosionIter.next();
      if (explosion.isComplete())
        explosionIter.remove();
      else
        explosion.update(deltaTime);
    }

    // TODO: update aliens

    checkCollisions();
    if (score / 10000 > bonusCount) {
      bonusCount++;
      numLives++;
    }
  }

  private void updateDead(float deltaTime) {
    waitTime -= deltaTime;
    updateRunning(deltaTime);
    if (waitTime <= 0) {
      waitTime = 0;
      if (respawnShip())
        state = WorldState.RUNNING;
    }
  }

  private void checkCollisions() {
    checkProjectileCollisions();
    if (ship != null && !ship.isTeleporting())
      checkShipCollisions();

    checkAlienCollisions();
  }

  private void checkProjectileCollisions() {
    Iterator<Projectile> projectileIter = projectiles.iterator();
    while (projectileIter.hasNext()) {
      Projectile p = projectileIter.next();
      Iterator<Asteroid> asteroidIter = asteroids.iterator();
      while (asteroidIter.hasNext()) {
        Asteroid a = asteroidIter.next();
        boolean intersects = Intersector.intersectSegmentCircle(
            p.getOldPosition(), p.getPosition(), a.getPosition(), a.getBounds().radius * a.getBounds().radius);
        if (intersects) {
          asteroidIter.remove();
          projectileIter.remove();
          killAsteroid(a);
          score += a.getPoints();
          break;
        }
      }
    }
  }

  private void checkShipCollisions() {
    Iterator<Asteroid> asteroidIter = asteroids.iterator();
    while (asteroidIter.hasNext()) {
      Asteroid asteroid = asteroidIter.next();
      if (Intersector.overlaps(asteroid.getBounds(), ship.getBounds())) {
        asteroidIter.remove();
        killAsteroid(asteroid);

        ship = null;
        score += asteroid.getPoints();
        if (numLives == 0)
          state = WorldState.GAME_OVER;
        else {
          state = WorldState.DEAD;
          waitTime = TIME_BETWEEN_LEVELS;
          numLives--;
        }
        break;
      }
    }
  }

  private void checkAlienCollisions() {
    // TODO
  }

  private void killAsteroid(Asteroid old) {
    // TODO: fix particle size issue
    ParticleEffectPool.PooledEffect explosionEffect = Assets.getExplosionEffect();
    explosionEffect.setPosition(old.getPosition().x, old.getPosition().y);
    explosions.add(explosionEffect);


    if (game.getPreferences().isSoundOn()) {
      if (ship == null)
        Assets.explosionSound.play(0.5f);
      else {
        float adjusted = 1f - ship.getPosition().dst(old.getPosition()) / 10f;
        Assets.explosionSound.play(Math.min(0.5f, adjusted));
      }
    }

    old.setDead(true);
    Asteroid.AsteroidSize oldSize = old.getSize();
    if (oldSize == Asteroid.AsteroidSize.SMALL)
      return;

    Asteroid.AsteroidSize newSize = oldSize == Asteroid.AsteroidSize.MEDIUM ? Asteroid.AsteroidSize.SMALL :
                                    Asteroid.AsteroidSize.MEDIUM;
    Vector2 oldPosition = old.getPosition();
    Vector2 oldVelocity = old.getVelocity();

    asteroids.add(new Asteroid(newSize, oldPosition.x, oldPosition.y, oldVelocity));
    asteroids.add(new Asteroid(newSize, oldPosition.x, oldPosition.y, oldVelocity));
  }

  public void addProjectile(Projectile projectile) {
    projectiles.add(projectile);
  }

  public int getLevel() {
    return level;
  }

  public WorldState getState() {
    return state;
  }

  public List<Asteroid> getAsteroids() {
    return asteroids;
  }

  public List<Projectile> getProjectiles() {
    return projectiles;
  }

  public Ship getShip() {
    return ship;
  }

  public List<ParticleEffectPool.PooledEffect> getExplosions() {
    return explosions;
  }

  public int getNumLives() {
    return numLives;
  }
}
