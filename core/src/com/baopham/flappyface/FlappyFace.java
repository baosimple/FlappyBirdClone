// Author: Bao Pham

package com.baopham.flappyface;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;


public class FlappyFace extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	//ShapeRenderer shapeRenderer;

	Texture gameOver;

	// Variables to simulate flapping animation
	Texture[] bird;
	int flapState = 0;
	int slowFlap = 0;

	// Variables to simulate gravity
	float birdY = 0;
	float birdVelocity = 0;
	float gravity = 2;

	// Bird hitbox
	Circle birdCircle;

	// Score management
	int score = 0;
	int scoringTube = 0;

	// font for score
	BitmapFont font;
	// font for name
	BitmapFont font2;

	// different game states such as before starting game, during game, and end of game
	int gameState = 0;

	// tube graphics
	Texture topTube;
	Texture bottomTube;

	// tube placement
	float gap = 450;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeOffset = new float[numberOfTubes];
	float[] tubeX = new float[numberOfTubes];
	float distanceBetweenTubes;

	// tube hitbox
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;


	@Override
	public void create () {

		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");

		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		font = new BitmapFont();
		font.getData().setScale(7);
		font2 = new BitmapFont();
		font2.getData().setScale(3);

		// Create an array of 2 elements to simulate bird flapping
		bird = new Texture[2];
		bird[0] = new Texture("bird.png");
		bird[1] = new Texture("bird2.png");
		birdY = Gdx.graphics.getHeight()/2 - bird[flapState].getHeight()/2;

		// Tube placement
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		// Generate varied y positions of tubes
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth()/2;
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();

	}

	public void startGame() {
		birdY = Gdx.graphics.getHeight()/2 - bird[flapState].getHeight()/2;

		// will rotate through four tubes
		for (int i = 0; i < numberOfTubes; i++) {

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 800);

			tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}

	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		font2.draw(batch, "Flappy Bird Clone by Bao Pham", 50, 50);

		// Before Starting the Game
		if(gameState == 0) {

			// Touch to start the game
			if (Gdx.input.justTouched()) {

				gameState = 1;
			}
		}

		// During the Game
		else if(gameState == 1){


			// Score increases if designated tube goes pass point on screen
			if (tubeX[scoringTube] < Gdx.graphics.getWidth()/2 - topTube.getWidth()) {

				score++;

				Gdx.app.log("Score", String.valueOf(score));

				if (scoringTube < numberOfTubes - 1) {

					scoringTube++;
				}
				else {

					scoringTube = 0;
				}
			}

			// Bird goes up if screen is touched
			if(Gdx.input.justTouched()) {
				birdVelocity = -30;

			}


			// Tube hitbox generation
			for (int i = 0; i < numberOfTubes; i++) {

				if(tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 800);

				}

				else {
					tubeX[i] -= tubeVelocity;
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			// Bird will constantly fall towards bottom of screen unless it hits top or bottom of screen
			if(birdY > 0 && birdY < Gdx.graphics.getHeight()) {
				birdVelocity += gravity;
				birdY -= birdVelocity;
			}

			// If bird hits bottom of screen or top of screen
			else {
				gameState = 2;
			}

			font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2 + 750);

			font2.draw(batch, "Flappy Bird Clone by Bao Pham", 50, 50);
		}

		// Game Over
		else if(gameState == 2) {

			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			// Touch to reset
			if (Gdx.input.justTouched()) {

				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				birdVelocity = 0;
			}
		}

		// Bird hitbox
		batch.draw(bird[flapState], Gdx.graphics.getWidth() / 2 - bird[flapState].getWidth() / 2, birdY);
		batch.end();

		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + bird[flapState].getHeight()/2, bird[flapState].getWidth()/2);


		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0; i < numberOfTubes; i++) {

			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
				gameState = 2;
			}
		}

		//shapeRenderer.end();


		// Algorithm for flapping animation
		if (slowFlap == 20)
			slowFlap = 0;
		if (slowFlap <= 10)
			flapState = 0;
		else if (slowFlap > 10)
			flapState = 1;
		slowFlap++;
	}
}
