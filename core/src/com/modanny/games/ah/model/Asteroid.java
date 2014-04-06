package com.modanny.games.ah.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Asteroid {
  private static final float    ASTEROID_MAX_SPEED = 3.5f;
  private static final float    ASTEROID_MIN_SPEED = 1.0f;

  public static enum AsteroidSize {
    SMALL(0, 0.25f, 100),
    MEDIUM(1, 0.5f, 50),
    LARGE(2, 0.75f, 20);

    private final int weight;
    private final float radius;
    private final int points;

    private AsteroidSize(int weight, float radius, int points) {
      this.weight = weight;
      this.radius = radius;
      this.points = points;
    }
  }

  private final Vector2 velocity    = new Vector2();
  private final Vector2 position    = new Vector2();
  private final Vector2 oldPosition = new Vector2();
  private final Circle bounds       = new Circle();

  private float acceleration; // TODO?
  private float rotationDegrees;     // degrees
  private float rotationDegreesSecond;
	private AsteroidSize size;
  private boolean isDead;

	public Asteroid(AsteroidSize size, float x, float y) {
    this(size, x, y, null);
	}

	public Asteroid(AsteroidSize size, float x, float y, Vector2 vec) {
    this.size = size;
    position.set(x, y);
    oldPosition.set(position);
    bounds.set(x, y, size.radius);

    Random rand = new Random();
    rotationDegrees = rand.nextInt(360);
    rotationDegreesSecond = rand.nextInt(30) * (3 - size.weight);
    if (vec == null) {
      float speed = Math.max(ASTEROID_MIN_SPEED, rand.nextFloat() * ASTEROID_MAX_SPEED / (1 + (size.weight / 2.0f)));
      velocity.set(((rand.nextFloat() * 2) - 1) * speed, ((rand.nextFloat() * 2) - 1) * speed);
    }
    else {
      float speed = vec.dst(0, 0) + rand.nextFloat() * (2 - size.weight);
      Vector2 v = vec.nor();
      velocity.set(v.x * speed, v.y * speed);
      velocity.rotate(rand.nextInt(50) - 25);
    }
	}
	
	public void update(float deltaTime) {
    oldPosition.set(position);
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    rotationDegrees = (rotationDegrees + rotationDegreesSecond * deltaTime) % 360;
		
		if (position.x + bounds.radius < 0) {
      oldPosition.x = World.WORLD_WIDTH + (oldPosition.x - position.x);
			position.x = World.WORLD_WIDTH + bounds.radius;
		}
    else if (position.x - bounds.radius > World.WORLD_WIDTH) {
      oldPosition.x = oldPosition.x - position.x;
			position.x = 0 - bounds.radius;
		}
		if (position.y + bounds.radius < 0) {
      oldPosition.y = World.WORLD_HEIGHT + (oldPosition.y - position.y);
			position.y = World.WORLD_HEIGHT + bounds.radius;
		}
    else if (position.y - bounds.radius > World.WORLD_HEIGHT) {
      oldPosition.y = oldPosition.y - position.y;
			position.y = 0 - bounds.radius;
		}
		
		bounds.x = position.x;
    bounds.y = position.y;
	}

  public int getPoints() {
    return size.points;
  }

  public AsteroidSize getSize() {
    return size;
  }

  public Vector2 getPosition() {
    return position;
  }

  public Circle getBounds() {
    return bounds;
  }

  public float getRotationDegrees() {
    return rotationDegrees;
  }

  public Vector2 getVelocity() {
    return velocity;
  }

  public boolean isDead() {
    return isDead;
  }

  public void setDead(boolean dead) {
    isDead = dead;
  }
}
