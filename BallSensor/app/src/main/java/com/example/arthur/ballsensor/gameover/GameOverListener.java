package com.example.arthur.ballsensor.gameover;

/** Interface permettant d'informer les écouteurs que la partie est terminée **/
public interface GameOverListener {
	public void notifyOfGameOver(int finalScore);
}
