package com.example.arthur.ballsensor.maze;

class MazeCell {
	// coordonnées dans la grille du labyrinthe
	int x, y;
	// tableau de booléens représentant les murs eventuels de la case: nord, s, e, w
	boolean[] walls;
	// booléen permettant de savoir si une case a été visitée
	boolean v;

	MazeCell( int x, int y ) {
		this.x = x;
		this.y = y;
		walls = new boolean[ 4 ];
		for ( int i = 0; i < 4; ++i ) {
			walls[ i ] = true;
		}
		v = false;
	}

	@Override
	public boolean equals( Object o ) {
		MazeCell m = (MazeCell) o;
		return m.x == x && m.y == y;
	}
}
