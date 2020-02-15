package com.adityagupta.flappybird;

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

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	//ShapeRenderer shapeRenderer;

	int gameState = 0;

	Texture[] birds;
	int flapState = 0;
	float velocity = 0;
	float gravity = 0.2f;
	float birdY = 0;
	Circle birdCircle;

	int score = 0;
	int scoringTube = 0;
	BitmapFont font;

	Texture gameOver;

	Texture topTube;
	Texture bottomTube;

	float gap = 400;
	float tubeVelocity = 4;
	float distanceBetweenTubes;
	int numberOfTubes = 4;
	float[] tubeOffset = new float[numberOfTubes];
	float[] tubeX = new float[numberOfTubes];
	Rectangle[] topTubeRectangle;
	Rectangle[] bottomTubeRectangle;

	Random randomGenerator;


	@Override
	public void create () {
		batch = new SpriteBatch();
		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;
		topTubeRectangle = new Rectangle[numberOfTubes];
		bottomTubeRectangle = new Rectangle[numberOfTubes];

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		startGame();

	}

	public void startGame()
	{

		birdY = Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2;

		for (int i = 0; i < numberOfTubes; i++)
		{
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = (Gdx.graphics.getWidth()/2 - bottomTube.getWidth()/2) + Gdx.graphics.getWidth() + (i * distanceBetweenTubes);
			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {
			for (int i = 0; i < numberOfTubes; i++) {
				if (tubeX[i] < -bottomTube.getWidth()) {
					tubeX[i] = numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

				} else {
					tubeX[i] -= tubeVelocity;

				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangle[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangle[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			}

			if (tubeX[scoringTube] <= Gdx.graphics.getWidth()/2 - topTube.getWidth()) {
				score++;

				if (scoringTube < numberOfTubes - 1)
					scoringTube++;
				else
					scoringTube = 0;
			}

			if (Gdx.input.justTouched()) {
				velocity = -10;
			}


			if (birdY > 0) {

				velocity += gravity;
				birdY -= velocity;

			} else
			{
				gameState = 2;
			}
		}
		else if (gameState == 0)
		{
			if (Gdx.input.justTouched())
				gameState = 1;
		}
		else if (gameState == 2)
		{
			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			if (Gdx.input.justTouched())
			{
				gameState = 0;
				score = 0;
				scoringTube = 0;
				velocity = 0;
				startGame();
			}
		}

		if (flapState == 1)
			flapState = 0;
		else
			flapState = 1;

		batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2, birdY);

		font.draw(batch, Integer.toString(score), 100, 200);

		batch.end();

		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth()/2);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		for (int i=0;i<numberOfTubes;i++)
		{
			//shapeRenderer.rect(topTubeRectangle[i].x, topTubeRectangle[i].y, topTubeRectangle[i].width, topTubeRectangle[i].height);
			//shapeRenderer.rect(bottomTubeRectangle[i].x, bottomTubeRectangle[i].y, bottomTubeRectangle[i].width, bottomTubeRectangle[i].height);

			if (Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i]))
			{
				gameState = 2;
			}
		}
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
		//shapeRenderer.end();



	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
