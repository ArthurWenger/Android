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
import android.view.View;

import com.example.arthur.ballsensor.gameover.GameOverListener;
import com.example.arthur.ballsensor.geometry.LineSegment2D;
import com.example.arthur.ballsensor.geometry.Math2D;
import com.example.arthur.ballsensor.maze.DepthFirstSearchMazeGenerator;
import com.example.arthur.ballsensor.maze.MazeGenerator;
import com.example.arthur.ballsensor.maze.MazeGeneratorDelegate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MazeGameView extends View implements HeroListener {

	private final float gameSize = 30;
	private int score = 0;
	private Paint wallPaint;
	private Paint floorTextPaint;
	private Paint uiTextPaint;
	private Paint uiTextStrokePaint;
	private Paint coinPaint;
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
	private Timer updateTimer = new Timer();
	private UpdateTimerTask updateTimerTask;
	private PointF cameraPos = new PointF();
	private PointF cameraVelocity = new PointF();
	private float cameraMinDistance = 1.001f;
	private GameOverListener gameOverListener;
	private final int FPS = 30;
	private final int FramePeriod = 1000/FPS;

	public MazeGameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		wallPaint = new Paint();
		wallPaint.setStyle(Paint.Style.STROKE);
		wallPaint.setStrokeWidth(wallThickness);
		floorTextPaint = new Paint();
		floorTextPaint.setTextSize(gameSize*2.0f);
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
	}
	//TODO: ajouter une méthode pause quand le jeu est quitté avec un retour en arrière

	/** Protected **/

	/** méthode permettant de dessiner le labyrinthe et tous les objets du jeu **/
	@Override
	protected void onDraw(android.graphics.Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.translate(-cameraPos.x + getWidth()/2.0f, -cameraPos.y + getHeight()/2.0f);
		assert (walls != null) : "Les murs doivent exister.";
		for(LineSegment2D wall : walls) {
			if(Math2D.pointInRect(wall.a, cameraRect()) || Math2D.pointInRect(wall.b, cameraRect())) {
				// on redimensionne les murs de la moitié de leur épaisseur pour améliorer l'affichage
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

		canvas.drawText("Score: "+Integer.toString(score),10,20,uiTextPaint);
		canvas.drawText("Score: "+Integer.toString(score),10,20,uiTextStrokePaint);
		canvas.drawText("Lives: "+Integer.toString(hero.getLives()),260,20,uiTextPaint);
		canvas.drawText("Lives: "+Integer.toString(hero.getLives()),260,20,uiTextStrokePaint);
	}

	/** si la taille de l'écran change on doit générer un nouveau labyrinthe **/
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(walls == null) { //only generate maze if one doesn't exist.
			generateMaze(w,h);
		}
	}

	/** méthode permettant de mettre à jour l'acceleration de pacman en fonction de l'accelerometre **/
	protected void updateAccel( float sX, float sY ) {
		if(hero!=null) {
			PointF sensorAccel = new PointF( sX, sY );
			hero.updateAccel( sensorAccel );
		}
	}

	/** méthode permettant de lancer une nouvelle partie **/
	protected void newGame() {
		score = 0;
		walls = null;
		coins = null;
		enemies = null;
		stopTimer();
		generateMaze( getWidth(), getHeight() );
	}

	/** setter pour l'écouteur de l'evenement GameOver **/
	protected void setGameOverListener(GameOverListener mEventListener) {
		this.gameOverListener = mEventListener;
	}

	/** quand le pacman meurt, on le notifie à la classe mère pour qu'elle lance l'activité GameOver **/
	@Override
	public void notifyHeroDeath() {
		stopTimer();
		if(gameOverListener!=null) {
			gameOverListener.notifyOfGameOver( score );
		}
	}

	/** méthode permettant de générer aléatoirement le labyrinthe et tous les objets du jeu **/
	private void generateMaze(float screenWidth, float screenHeight) {
		float cellSize = gameSize * 2 + wiggleRoom * 2;

		DepthFirstSearchMazeGenerator mazeGenerator = new DepthFirstSearchMazeGenerator();
		mazeGenerator.generate(screenWidth*3f, screenWidth*3f, cellSize, cellSize, new MazeGeneratorDelegate() { //Deliberately not using screenHeight for a square maze
			@Override
			public void mazeGenerationDidFinish(MazeGenerator generator) {
				startLocation.set(generator.getStartLocation());
				hero = new Hero(startLocation, gameSize, getContext().getAssets() );
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
				updateTimer.schedule(updateTimerTask, 0, FramePeriod );
			}
		});
		hero.setHeroListener( this );
		Log.d("Canvas", "Generation du labyrinthe terminé.");
	}

	/** quand le joueur atteint la case de fin du labyrinthe, on en genere un nouveau aleatoirement **/
	private void winLevel() {
		walls = null;
		coins = null;
		enemies = null;
		updateTimerTask.cancel();
		updateTimer.purge();
		updateTimerTask = null;
		generateMaze( getWidth(), getHeight() );
	}

	/** méthode permettant d'arrêter les animations du jeu: les objets ne sont plus mis à jour **/
	public void stopTimer(){
		if(updateTimerTask!=null) {
			updateTimerTask.cancel();
			updateTimerTask = null;
		}
		updateTimer.purge();
	}

	private RectF cameraRect() {
		return new RectF(cameraPos.x-getWidth()/2.0f,cameraPos.y-getHeight()/2.0f,cameraPos.x+getWidth()/2.0f,cameraPos.y+getHeight()/2.0f);
	}

/***********************************************************************
 * Classe permettant de mettre à jour les objets du jeu périodiquement *
 ***********************************************************************/

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
				if(enemy.detectAndResolveCollisionWithHero(hero)) {
					//enemyIterator.remove();
				}
				else {
					enemy.update(walls, wallThickness, hero);
				}
			}

			if(hero.detectFinishCollision(finishLocation)) {
				winLevel();
			} else {
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
		}
	}
}
