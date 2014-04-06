package com.modanny.games.ah.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.modanny.games.ah.Assets;
import com.modanny.games.ah.AsteroidHunterGame;

import java.util.Collections;

public class Ship {
  public enum ShipState {
    IDLE,
    MOVING,
    TELEPORT_LEAVING,
    TELEPORT_ARRIVING
  }

  public static final float TELEPORT_DURATION = 0.5f;

  private static final float RADIUS = 0.35f;
  private static final float SPEED = 7.5f;
  private static final float DECELERATION = -1.5f;
  private static final float ACCELERATION = 4.0f;
  private static final float ROTATION_RATE = 3.5f;
  private static final float SHOOT_DELAY = 0.25f;
  private static final float TELEPORT_DELAY = 2.5f;

  private final AsteroidHunterGame game;
  private final World world;
  private final Vector2 position = new Vector2();
  private final Vector2 velocity = new Vector2(0, 0);
  private final Circle bounds = new Circle();

  private ShipState state = ShipState.IDLE;
  private float rotationDegrees;
  private float stateTime;
  private float lastShot;
  private float teleportTime;
  private float teleportDelay;

  public Ship(AsteroidHunterGame game, World world, float x, float y) {
    this.game = game;
    this.world = world;

    position.set(x, y);
    bounds.set(x, y, RADIUS);

    teleportDelay = 0.0f;

  }

  public void update(float deltaTime) {
    stateTime = (stateTime + deltaTime) % 1000f;
    if (ShipState.TELEPORT_ARRIVING == state || ShipState.TELEPORT_LEAVING == state) {
      updateTeleport(deltaTime);
      return;
    }
    boolean isAccelerating = ShipState.MOVING == state;
    float acceleration = ACCELERATION;
    if (!isAccelerating) {
      if (velocity.len() > .15f)
        acceleration = DECELERATION;
      else {
        acceleration = 0;
        velocity.set(0, 0);
      }
      if (ShipState.IDLE != state) {
        state = ShipState.IDLE;
        stateTime = 0;
      }
    }

    float rotRad = rotationDegrees * MathUtils.degreesToRadians;
    float dirRad = velocity.angle() * MathUtils.degreesToRadians;
    float da = acceleration * deltaTime;
    velocity.x = (float) Math.min(SPEED, Math.max(-SPEED, velocity.x + da * (isAccelerating ? Math.cos(rotRad) : Math.cos(dirRad))));
    velocity.y = (float) Math.min(SPEED, Math.max(-SPEED, velocity.y + da * (isAccelerating ? Math.sin(rotRad) : Math.sin(dirRad))));
    move(velocity.x * deltaTime, velocity.y * deltaTime);

    if (teleportDelay > 0.0f)
      teleportDelay -= deltaTime;
  }

  private void move(float dx, float dy) {
    position.add(dx, dy);

    if (position.x + bounds.radius < 0)
      position.x = World.WIDTH;
    else if (position.x > World.WIDTH)
      position.x = 0 - bounds.radius;

    if (position.y + bounds.radius < 0)
      position.y = World.HEIGHT;
    else if (position.y > World.HEIGHT)
      position.y = 0 - bounds.radius;

    bounds.x = position.x;
    bounds.y = position.y;
  }

  public void accelerate() {
    if (ShipState.MOVING != state) {
      state = ShipState.MOVING;
      stateTime = 0;
    }
  }

  public void idle() {
    if (ShipState.IDLE != state) {
      state = ShipState.IDLE;
      stateTime = 0;
    }
  }

  public void turn(boolean left, boolean right) {
    if (left ^ right)
      rotationDegrees += ((left ? 1 : -1) * Ship.ROTATION_RATE) % 360;
  }

  public void shoot() {
    float curTime = System.nanoTime() / 1000000000.0f;
    if (curTime - lastShot > SHOOT_DELAY) {
      lastShot = curTime;
      Vector2 dir = new Vector2((float) Math.cos(rotationDegrees * MathUtils.degreesToRadians),
                                (float) Math.sin(rotationDegrees * MathUtils.degreesToRadians));
//      world.addProjectile(new PulseProjectile(world, position.x, position.y, dir));
      world.addProjectile(new HomingProjectile(world, position.x, position.y, dir));
      if (game.getPreferences().isSoundOn())
        Assets.pulseSound.play(0.5f);
    }
  }

  public void teleport() {
    if (ShipState.TELEPORT_ARRIVING == state || ShipState.TELEPORT_LEAVING == state)
      return;
    if (teleportDelay > 0.0f)
      return;

    state = ShipState.TELEPORT_LEAVING;
    stateTime = 0;
    teleportTime = TELEPORT_DURATION;
  }

  private void updateTeleport(float deltaTime) {
    teleportTime -= deltaTime;
    if (teleportTime > 0f)
      return;

    switch (state) {
      case TELEPORT_LEAVING: {
        Collections.shuffle(World.SPAWN_POSITIONS);
        positionLoop:
        for (Vector2 spawnPos : World.SPAWN_POSITIONS) {
          if (spawnPos.dst(position) > 4) {
            for (Asteroid a : world.getAsteroids()) {
              if (spawnPos.dst(a.getPosition()) < a.getBounds().radius * 1.5f)
                continue positionLoop;
            }
            position.set(spawnPos);
            bounds.x = position.x;
            bounds.y = position.y;
            teleportTime = TELEPORT_DURATION;
            state = ShipState.TELEPORT_ARRIVING;
            stateTime = 0;
            break;
          }
        }
      }
      break;
      case TELEPORT_ARRIVING: {
        state = ShipState.IDLE;
        stateTime = 0;
        teleportDelay = TELEPORT_DELAY;
      }
      break;
    }
  }

  public Vector2 getPosition() {
    return position;
  }

  public Circle getBounds() {
    return bounds;
  }

  public boolean isTeleporting() {
    return ShipState.TELEPORT_LEAVING == state || ShipState.TELEPORT_ARRIVING == state;
  }

  public ShipState getState() {
    return state;
  }

  public boolean isAccelerating() {
    return ShipState.MOVING == state;
  }

  public float getRotationDegrees() {
    return rotationDegrees;
  }

  public float getStateTime() {
    return stateTime;
  }
}
