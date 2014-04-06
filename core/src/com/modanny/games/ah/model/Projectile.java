package com.modanny.games.ah.model;

import com.badlogic.gdx.math.Vector2;

public abstract class Projectile {
  protected final Vector2 velocity = new Vector2();
  protected final Vector2 position = new Vector2();
  protected final Vector2 oldPosition = new Vector2();
  protected final Vector2 distance = new Vector2();

  protected final World world;

  public abstract float getWidth();
  public abstract float getHeight();
  public abstract float getRotationDegrees();
  public abstract void update(float deltaTime);
  protected abstract Float getMaxDistance();

  public Projectile(World world, float x, float y, Vector2 dir) {
    this.world = world;
    position.set(x, y);
    oldPosition.set(position);
    velocity.set(dir);
  }

  public void move(float deltaTime) {
    oldPosition.set(position);

    float dx = velocity.x * deltaTime;
    float dy = velocity.y * deltaTime;
    position.add(dx, dy);
    distance.add(dx, dy); // TODO: this is wrong (absolute value instead?)

    if (position.x < 0) {
      oldPosition.x = World.WIDTH + (oldPosition.x - position.x);
      position.x = World.WIDTH;
    }
    else if (position.x > World.WIDTH) {
      oldPosition.x = oldPosition.x - position.x;
      position.x = 0;
    }
    if (position.y < 0) {
      oldPosition.y = World.HEIGHT + (oldPosition.y - position.y);
      position.y = World.HEIGHT;
    }
    else if (position.y > World.HEIGHT) {
      oldPosition.y = oldPosition.y - position.y;
      position.y = 0;
    }
  }

  public boolean isDead() {
    return distance.dst(0, 0) > getMaxDistance();
  }

  public Vector2 getPosition() {
    return position;
  }

  public Vector2 getOldPosition() {
    return oldPosition;
  }
}
