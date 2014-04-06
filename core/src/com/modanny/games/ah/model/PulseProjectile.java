package com.modanny.games.ah.model;

import com.badlogic.gdx.math.Vector2;

public class PulseProjectile extends Projectile {
  public PulseProjectile(World world, float x, float y, Vector2 dir) {
    super(world, x, y, dir);
    velocity.scl(10.0f);
  }

  @Override
  public void update(float deltaTime) {
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
    return 8f;
  }
}
