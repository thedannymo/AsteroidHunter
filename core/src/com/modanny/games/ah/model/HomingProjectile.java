package com.modanny.games.ah.model;

import com.badlogic.gdx.math.Vector2;

public class HomingProjectile extends Projectile {
  private static final float ACCELERATION = 0.5f;
  private static final float MAX_SPEED = 3.75f;

  private Asteroid targetAsteroid = null;

  public HomingProjectile(World world, float x, float y, Vector2 dir) {
    super(world, x, y, dir);
    velocity.scl(2.0f);
  }

  @Override
  public void update(float deltaTime) {
    if (distance.len() > 1f) {
      if (targetAsteroid == null || targetAsteroid.isDead())
        targetAsteroid = world.getNearestAsteroid(position);
      else {
        Vector2 desiredVelocity = targetAsteroid.getPosition().cpy().sub(position).nor().scl(MAX_SPEED);
        Vector2 steering = desiredVelocity.sub(velocity);
        velocity.add(steering.nor().scl(0.05f));
      }
    }
    move(deltaTime);
  }

  @Override
  public float getWidth() {
    return 0.25f;
  }

  @Override
  public float getHeight() {
    return 0.25f;
  }

  @Override
  public float getRotationDegrees() {
    return 0;
  }

  @Override
  protected Float getMaxDistance() {
    return 15f;
  }
}
