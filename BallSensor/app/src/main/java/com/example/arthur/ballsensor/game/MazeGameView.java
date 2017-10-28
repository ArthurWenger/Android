package com.example.arthur.ballsensor.game;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.arthur.ballsensor.gameOver.GameOverListener;
import com.example.arthur.ballsensor.maze.MazeGenerator;
import com.example.arthur.ballsensor.maze.MazeGeneratorDelegate;
import com.example.arthur.ballsensor.maze.DepthFirstSearchMazeGenerator;
import com.example.arthur.ballsensor.game.Enemy;
import com.example.arthur.ballsensor.game.Heart;
import com.example.arthur.ballsensor.game.Hero;
import com.example.arthur.ballsensor.geometry.LineSegment2D;
import com.example.arthur.ballsensor.geometry.Math2D;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

;

public class MazeGameView extends View {

	private final float gameSize = 30;
	private int score = 0;
	private Paint wallPaint;
	private Paint floorTextPaint;
	private Paint inputIndicatorPaint;
	private Paint inputIndicatorPaint2;
	private Paint uiTextPaint;
	private Paint uiTextStrokePaint;
	private Paint coinPaint;
	private Paint extraPaint;
	private PointF startLocation = new PointF();
	private PointF finishLocation = new PointF();
	private Hero hero;
	private int difficulty = 5;
	private final float wallThickness = gameSize/2.2f;
	private float wiggleRoom = gameSize+wallThickness/2-difficulty;
	private Set<LineSegment2D> walls;
	private Set<PointF> coins;
	private HashSet<Heart> hearts;
	private Set<Enemy> enemies;
	private float coinRadius = gameSize/2.0f;
	private float heartRadius = gameSize/1.5f;
	private PointF initialTapPoint;
	private PointF currentTapPoint;
	private Timer updateTimer = new Timer();
	private UpdateTimerTask updateTimerTask;
	private PointF cameraPos = new PointF();
	private PointF cameraVelocity = new PointF();
	private float cameraMinDistance = 1.001f;
	private final float maxInput1Length = 100.0f;
	private GameOverListener gameOverListener;

	public MazeGameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		wallPaint = new Paint();
		wallPaint.setStyle(Paint.Style.STROKE);
		wallPaint.setStrokeWidth(wallThickness);
		floorTextPaint = new Paint();
		floorTextPaint.setTextSize(gameSize*2.0f);
		inputIndicatorPaint = new Paint();
		inputIndicatorPaint.setARGB(100, 100, 0, 75);
		inputIndicatorPaint.setStyle(Paint.Style.STROKE);
		inputIndicatorPaint.setStrokeWidth(16);
		inputIndicatorPaint2 = new Paint();
		inputIndicatorPaint2.setARGB(150, 255, 75, 0);
		inputIndicatorPaint2.setStyle(Paint.Style.STROKE);
		inputIndicatorPaint2.setStrokeWidth(16);
		uiTextPaint = new Paint();
		uiTextPaint.setTextSize(24);
		uiTextPaint.setStyle(Paint.Style.FILL);
		uiTextPaint.setColor(Color.GREEN);
		uiTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		uiTextStrokePaint = new Paint();
		uiTextStrokePaint.setStyle(Paint.Style.STROKE);
		uiTextStrokePaint.setStrokeWidth(1.5f);
		uiTextStrokePaint.setTextSize(24);
		uiTextStrokePaint.setTypeface(Typeface.DEFAULT_BOLD);
		uiTextStrokePaint.setColor(Color.BLACK);
		coinPaint = new Paint();
		coinPaint.setARGB(255,180,190,0);
		extraPaint = new Paint();
		extraPaint.setColor(Color.BLUE);
	}
	//TODO: ajouter une méthode pause quand le jeu est quitté avec un retour en arrière

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getActionMasked() == MotionEvent.ACTION_UP) {
			initialTapPoint = null;
			currentTapPoint = null;
		}
		else {
			currentTapPoint = new PointF(event.getX(), event.getY());

			if(initialTapPoint == null) {
				initialTapPoint = new PointF(currentTapPoint.x, currentTapPoint.y);
			}

			return true; //true means this event was handled
		}
		return super.onTouchEvent(event);
	}

	/** Protected **/

	@Override
	protected void onDraw(android.graphics.Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.translate(-cameraPos.x + getWidth()/2.0f, -cameraPos.y + getHeight()/2.0f);
		assert (walls != null) : "Walls must exist here.";
		for(LineSegment2D wall : walls) {
			if(Math2D.pointInRect(wall.a, cameraRect()) || Math2D.pointInRect(wall.b, cameraRect())) {
				//stretch the walls by half their thickness so the corners are nicer
				float x1Offset = 0, y1Offset = 0, x2Offset = 0, y2Offset = 0;
				if(wall.a.x < wall.b.x) {
					x1Offset = -wallThickness/2.0f;
					x2Offset = wallThickness/2.0f;
				}
				else if(wall.a.x > wall.b.x) {
					x1Offset = wallThickness/2.0f;
					x2Offset = -wallThickness/2.0f;
				}
				else if(wall.a.y < wall.b.y) {
					y1Offset = -wallThickness/2.0f;
					y2Offset = wallThickness/2.0f;
				}
				else if(wall.a.y > wall.b.y) {
					y1Offset = wallThickness/2.0f;
					y2Offset = -wallThickness/2.0f;
				}
				canvas.drawLine(wall.a.x+x1Offset, wall.a.y+y1Offset, wall.b.x+x2Offset, wall.b.y+y2Offset, wallPaint);
			}
		}

		synchronized ( coins ) {
			for ( PointF coin : coins ) {
				canvas.drawCircle( coin.x, coin.y, coinRadius, coinPaint );
			}
		}

		synchronized ( hearts ) {
			for ( Heart heart : hearts ) {
				heart.draw(canvas);
				//canvas.drawCircle( heart.x, heart.y, heartRadius, extraPaint );
			}
		}

		for(Enemy enemy : enemies) {
			enemy.draw(canvas);
		}
		hero.draw(canvas);

		canvas.drawText("S", startLocation.x, startLocation.y, floorTextPaint);
		canvas.drawText("F", finishLocation.x, finishLocation.y, floorTextPaint);

		canvas.restore();

		if(isUserInputtingDirection()) {
			LineSegment2D inputLineSegment1 = inputLineSegment1();
			LineSegment2D inputLineSegment2 = inputLineSegment2();
			if(inputLineSegment2 != null) {
				canvas.drawLine(inputLineSegment2.a.x, inputLineSegment2.a.y, inputLineSegment2.b.x, inputLineSegment2.b.y, inputIndicatorPaint2);
			}
			if(inputLineSegment1 != null) {
				canvas.drawLine(inputLineSegment1.a.x, inputLineSegment1.a.y, inputLineSegment1.b.x, inputLineSegment1.b.y, inputIndicatorPaint);
			}
		}
		canvas.drawText("Score: "+Integer.toString(score),10,20,uiTextPaint);
		canvas.drawText("Score: "+Integer.toString(score),10,20,uiTextStrokePaint);
		canvas.drawText("Lives: "+Integer.toString(hero.getLives()),260,20,uiTextPaint);
		canvas.drawText("Lives: "+Integer.toString(hero.getLives()),260,20,uiTextStrokePaint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(walls == null) { //only generate maze if one doesn't exist.
			generateMaze(w,h);
		}
	}

	protected void updateAccel( float sX, float sY ) {
		if(hero!=null) {
			PointF sensorAccel = new PointF( sX, sY );
			hero.updateAccel( sensorAccel );
		}
	}

	protected void newGame() {
		score = 0;
		walls = null;
		coins = null;
		enemies = null;
		initialTapPoint = null;
		currentTapPoint = null;
		purgeTimer();
		generateMaze( getWidth(), getHeight() );
	}

	protected void setGameOverListener(GameOverListener mEventListener) {
		this.gameOverListener = mEventListener;
	}

	/** Private **/

	private class UpdateTimerTask extends TimerTask {

		@Override
		public void run() {
			hero.update();
			synchronized ( coins ) {
				for ( Iterator<PointF> coinIterator = coins.iterator(); coinIterator.hasNext(); ) {
					if ( hero.detectCoinCollision( coinIterator.next(), coinRadius ) ) {
						coinIterator.remove();
						++score;
						break;
					}
				}
			}

			synchronized ( hearts ) {
				for ( Iterator<Heart> heartIterator = hearts.iterator(); heartIterator.hasNext(); ) {
					Heart heart = heartIterator.next();
					if ( hero.detectAndResolveHeartCollision( heart.getCenter(), heartRadius ) ) {
						heartIterator.remove();
						break;
					}
				}
			}

			for(Iterator<Enemy> enemyIterator = enemies.iterator(); enemyIterator.hasNext(); ) {
				Enemy enemy = enemyIterator.next();
				if(enemy.detectAndResolveCollisionWithHero(hero) && hero.getLives() == 0 && gameOverListener != null) {
					//enemyIterator.remove();
					purgeTimer();
					gameOverListener.notifyOfGameOver(score);
					return;
				}
				else {
					enemy.update(walls, wallThickness, hero);
				}
			}

			/*if(hero.detectFinishCollision(finishLocation)) {
				winLevel();
			} */
			//else {
				for(LineSegment2D wall : walls) {
					hero.detectAndResolveWallCollision(wall, wallThickness);
				}

				PointF cameraAcceleration = Math2D.subtract(hero.getLocation(), cameraPos);
				float heroDistanceFromCamera = cameraAcceleration.length();
				if(heroDistanceFromCamera >= cameraMinDistance) {
					cameraAcceleration = Math2D.normalize(cameraAcceleration);
					float accelerationMagnitude = heroDistanceFromCamera * 0.02f;
					cameraAcceleration = Math2D.scale(cameraAcceleration, accelerationMagnitude);
					float cameraDragMagnitude = 0.6f;

					cameraVelocity = Math2D.add(cameraVelocity, cameraAcceleration);

					float cameraVelocityLength = cameraVelocity.length();
					if(cameraVelocityLength > 0.0f) {
						PointF cameraDrag = Math2D.scale(Math2D.normalize(cameraVelocity), -Math.min(cameraVelocityLength, cameraDragMagnitude));
						cameraVelocity = Math2D.add(cameraVelocity, cameraDrag);
					}

					cameraPos = Math2D.add(cameraPos, cameraVelocity);
				}
				else {
					cameraVelocity.set(0,0);
					cameraPos.set(hero.getLocation());
				}

				postInvalidate();
			}
		//}
	}

	private void generateMaze(float screenWidth, float screenHeight) {
		float cellSize = gameSize * 2 + wiggleRoom * 2;

		DepthFirstSearchMazeGenerator mazeGenerator = new DepthFirstSearchMazeGenerator();
		mazeGenerator.generate(screenWidth*3f, screenWidth*3f, cellSize, cellSize, new MazeGeneratorDelegate() { //Deliberately not using screenHeight for a square maze
			@Override
			public void mazeGenerationDidFinish(MazeGenerator generator) {
				startLocation.set(generator.getStartLocation());
				hero = new Hero(startLocation, gameSize, getContext().getAssets());
				cameraPos.set(startLocation);
				finishLocation.set(generator.getFinishLocation());
				walls = generator.getWalls();
				coins = generator.getRandomRoomLocations((int) (Math.random()*10.0+40.0), true);
				AssetManager assets = getContext().getAssets();

				Set<PointF> heartLocations = generator.getRandomRoomLocations((int) (4.0), true);
				hearts = new HashSet<Heart>();
				for(PointF location : heartLocations) {
					hearts.add(new Heart(location,gameSize,assets));
				}
				Set<PointF> enemyLocations = generator.getRandomRoomLocations((int) (Math.random()*4.0+6.0), false);
				enemies = new HashSet<Enemy>();
				for(PointF location : enemyLocations) {
					enemies.add(new Enemy(location,gameSize,assets));
				}
				updateTimerTask = new UpdateTimerTask();
				updateTimer.schedule(updateTimerTask, 0, 33);
			}
		});

		Log.d("Canvas", "Generation du labyrinthe terminé.");
	}

	private void winLevel() {
		walls = null;
		coins = null;
		enemies = null;
		initialTapPoint = null;
		currentTapPoint = null;
		updateTimerTask.cancel();
		updateTimer.purge();
		updateTimerTask = null;
		generateMaze( getWidth(), getHeight() );
	}

	private void purgeTimer(){
		if(updateTimerTask!=null) {
			updateTimerTask.cancel();
			updateTimerTask = null;
		}
		updateTimer.purge();
	}

	private RectF cameraRect() {
		return new RectF(cameraPos.x-getWidth()/2.0f,cameraPos.y-getHeight()/2.0f,cameraPos.x+getWidth()/2.0f,cameraPos.y+getHeight()/2.0f);
	}

	private boolean isUserInputtingDirection() {
		return initialTapPoint != null && currentTapPoint != null;
	}

	private PointF inputVector() {
		if(isUserInputtingDirection()) {
			return Math2D.subtract(currentTapPoint, initialTapPoint);
		}
		return null;
	}

	private PointF inputVector1() {
		PointF vector = inputVector();
		if(isUserInputtingDirection() && vector.length() > 0.0) {
			if(vector.length() > maxInput1Length) {
				vector = Math2D.scale(vector, maxInput1Length/vector.length());
			}
			return vector;
		}
		return null;
	}

	private LineSegment2D inputLineSegment1() {
		PointF inputVector1 = inputVector1();
		if(inputVector1 != null) {
			return new LineSegment2D(initialTapPoint.x, initialTapPoint.y, initialTapPoint.x+inputVector1.x, initialTapPoint.y+inputVector1.y);
		}
		return null;
	}

	private LineSegment2D inputLineSegment2() {
		PointF inputVector = inputVector();
		if(inputVector != null && inputVector.length() > maxInput1Length) {
			PointF inputVector1 = inputVector1();
			return new LineSegment2D(initialTapPoint.x+inputVector1.x, initialTapPoint.y+inputVector1.y, currentTapPoint.x, currentTapPoint.y);
		}
		return null;
	}
}