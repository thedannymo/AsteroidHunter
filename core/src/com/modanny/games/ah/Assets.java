package com.modanny.games.ah;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.*;
import com.modanny.games.ah.model.Ship;

public final class Assets {

  public static TextureRegion asteroid;
  public static TextureRegion shipIdle;
  public static TextureRegion shipGlow;
  public static Animation shipAnimation;
  public static Animation teleportLeavingAnimation;
  public static Animation teleportArrivingAnimation;
  public static TextureRegion pulseShot;
  public static TextureRegion arrowUp;
  public static TextureRegion arrowUpPressed;
  public static TextureRegion arrowLeft;
  public static TextureRegion arrowLeftPressed;
  public static TextureRegion arrowRight;
  public static TextureRegion arrowRightPressed;
  public static TextureRegion shootButton;
  public static TextureRegion shootButtonPressed;
  public static TextureRegion teleportButton;
  public static TextureRegion teleportButtonPressed;

  public static TextureRegion background;

  public static TextureRegion logo;
  public static TextureRegion newGame;
  public static TextureRegion highScores;
  public static TextureRegion musicOff;
  public static TextureRegion musicOn;
  public static TextureRegion soundOn;
  public static TextureRegion soundOff;

  public static BitmapFont font;
  private static ParticleEffect explosionEffect = new ParticleEffect();
  private static ParticleEffectPool explosionPool;

  public static Sound pulseSound;
  public static Sound explosionSound;

  private static Texture loadTexture(String file) {
    return new Texture(Gdx.files.internal(file));
  }

  public static void load() {
    Texture spriteAtlas = loadTexture("images/asteroids_atlas.png");
    spriteAtlas.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    Texture backgroundAtlas = loadTexture("images/background.png");
    backgroundAtlas.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    shipIdle = new TextureRegion(spriteAtlas, 0, 0, 21, 24);
    shipGlow = new TextureRegion(spriteAtlas, 71, 0, 25, 24);
    TextureRegion shipAccelerating1 = new TextureRegion(spriteAtlas, 21, 0, 21, 24);
    TextureRegion shipAccelerating2 = new TextureRegion(spriteAtlas, 42, 0, 21, 24);
    shipAnimation = new Animation(.05f, shipAccelerating1, shipAccelerating2);
    shipAnimation.setPlayMode(Animation.PlayMode.LOOP);

    teleportLeavingAnimation = new Animation(
        Ship.TELEPORT_DURATION / 5f,
        new TextureRegion(spriteAtlas, 0, 252, 22, 70),
        new TextureRegion(spriteAtlas, 22, 252, 22, 70),
        new TextureRegion(spriteAtlas, 44, 252, 22, 70),
        new TextureRegion(spriteAtlas, 66, 252, 22, 70),
        new TextureRegion(spriteAtlas, 90, 252, 22, 70));
    teleportLeavingAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    teleportArrivingAnimation = new Animation(
        Ship.TELEPORT_DURATION / 5f,
        new TextureRegion(spriteAtlas, 90, 252, 22, 70),
        new TextureRegion(spriteAtlas, 66, 252, 22, 70),
        new TextureRegion(spriteAtlas, 44, 252, 22, 70),
        new TextureRegion(spriteAtlas, 22, 252, 22, 70),
        new TextureRegion(spriteAtlas, 0, 252, 22, 70));
    teleportArrivingAnimation.setPlayMode(Animation.PlayMode.NORMAL);

    asteroid = new TextureRegion(spriteAtlas, 0, 26, 48, 50);
    pulseShot = new TextureRegion(spriteAtlas, 63, 0, 9, 9);

    arrowUp = new TextureRegion(spriteAtlas, 1, 80, 42, 42);
    arrowLeft = new TextureRegion(spriteAtlas, 44, 80, 42, 42);
    arrowRight = new TextureRegion(spriteAtlas, 87, 80, 42, 42);
    shootButton = new TextureRegion(spriteAtlas, 1, 123, 42, 42);
    teleportButton = new TextureRegion(spriteAtlas, 44, 123, 42, 42);

    arrowUpPressed = new TextureRegion(spriteAtlas, 0, 165, 44, 44);
    arrowLeftPressed = new TextureRegion(spriteAtlas, 43, 165, 44, 44);
    arrowRightPressed = new TextureRegion(spriteAtlas, 86, 165, 44, 44);
    shootButtonPressed = new TextureRegion(spriteAtlas, 0, 208, 44, 44);
    teleportButtonPressed = new TextureRegion(spriteAtlas, 43, 209, 44, 44);

    background = new TextureRegion(backgroundAtlas, 0, 0, 800, 600);

    Texture menuAtlas = loadTexture("images/asteroids_menu.png");
    logo = new TextureRegion(spriteAtlas, 0, 400, 320, 110);
    newGame = new TextureRegion(menuAtlas, 0, 76, 170, 35);
    highScores = new TextureRegion(menuAtlas, 0, 110, 190, 40);
    musicOff = new TextureRegion(menuAtlas, 5, 155, 50, 50);
    musicOn = new TextureRegion(menuAtlas, 50, 155, 50, 50);
    soundOn = new TextureRegion(menuAtlas, 90, 155, 50, 50);
    soundOff = new TextureRegion(menuAtlas, 133, 155, 50, 50);

    font = new BitmapFont(Gdx.files.internal("arial.fnt"), Gdx.files.internal("arial.png"), false);
    font.setColor(0, 1, 0, 1);

    explosionEffect.load(Gdx.files.internal("particles/explosion.p"), Gdx.files.internal("particles/"));
    explosionPool = new ParticleEffectPool(explosionEffect, 5, 15);

    pulseSound = Gdx.audio.newSound(Gdx.files.internal("sfx/pulseshot.wav"));
    explosionSound = Gdx.audio.newSound(Gdx.files.internal("sfx/explosion_deep.wav"));
  }

  public static ParticleEffectPool.PooledEffect getExplosionEffect() {
    return explosionPool.obtain();
  }
}
