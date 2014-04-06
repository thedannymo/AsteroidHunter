package com.modanny.games.ah.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Alien {
  public enum AlienType {
    SMALL(3.0f, 1f, 100),
    LARGE(2.5f, 1.5f, 50);

    private final float speed;
    public final float radius;
    private final int points;

    private AlienType(float speed, float radius, int points) {
      this.speed = speed;
      this.radius = radius;
      this.points = points;
    }
  }
	
	private final Vector2 velocity = new Vector2();
  private final Vector2 position = new Vector2();
  private final Vector2 oldPosition = new Vector2();
  private final Circle bounds = new Circle();
  private final AlienType type;
    
  public Alien(AlienType type) {
    this.type = type;

    Random rand = new Random();
    float x = rand.nextFloat() < 0.5f ? -2.0f : 2.0f; // put it out of bounds (either left or right side)
    float y = rand.nextFloat() * World.WORLD_HEIGHT;
    	
		position.set(x, y);
		oldPosition.set(x, y);
		velocity.set(0, 0);
		bounds.set(x, y, type.radius);
  }
    
  public void update(float deltaTime) {

  }
}
