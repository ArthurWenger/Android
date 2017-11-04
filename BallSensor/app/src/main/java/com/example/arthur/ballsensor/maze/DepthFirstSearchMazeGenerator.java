package com.example.arthur.ballsensor.maze;

import android.graphics.PointF;

import com.example.arthur.ballsensor.geometry.LineSegment2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DepthFirstSearchMazeGenerator implements MazeGenerator {

	private PointF startLocation;
	private PointF finishLocation;
	private float paddingX, paddingY;
	private Set<LineSegment2D> walls;
	private MazeCell[][] mazeCells;
	private int numCellsX, numCellsY;
	private float cellWidth, cellHeight;
	private Set<PointF> neverReturnedLocations = new HashSet<PointF>();

	// Algorithme de génération du labyrinthe basé sur: http://en.wikipedia.org/wiki/Maze_generation_algorithm
	@Override
	public void generate(float mazeWidth, float mazeHeight, float cellWidth,
			float cellHeight, MazeGeneratorListener delegate) {
		numCellsX = (int) (mazeWidth / cellWidth);
		numCellsY = (int) (mazeHeight / cellHeight);
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;

		mazeCells = new MazeCell[numCellsX][numCellsY];
		ArrayList<MazeCell> unvisitedCells = new ArrayList<MazeCell>();
		ArrayList<MazeCell> visitedCells = new ArrayList<MazeCell>();

		for(int i = 0; i < numCellsX; ++i) {
			for(int j = 0; j < numCellsY; ++j) {
				mazeCells[i][j] = new MazeCell(i,j);
				unvisitedCells.add(mazeCells[i][j]);
			}
		}

		int startX = (int) (Math.random()*numCellsX);
		int startY = (int) (Math.random()*numCellsY);
		paddingX = mazeWidth%cellWidth / 2.0f;
		paddingY = mazeHeight%cellHeight / 2.0f;
		startLocation = new PointF(startX*cellWidth+cellWidth/2+paddingX,startY*cellHeight+cellHeight/2+paddingY);
		ArrayList<MazeCell> cellStack = new ArrayList<MazeCell>();
		cellStack.add(mazeCells[startX][startY]);
		ArrayList<MazeCell> previousUnvisitedNeighbors = new ArrayList<MazeCell>();

		while(!cellStack.isEmpty()) {
			int lastIndex = cellStack.size() - 1;
			MazeCell currentCell = cellStack.get(lastIndex);
			cellStack.remove(lastIndex);
			currentCell.v = true;
			visitedCells.add(currentCell);
			unvisitedCells.remove(currentCell);
			previousUnvisitedNeighbors.remove(currentCell);

			ArrayList<MazeCell> unvisitedNeighbors = getNeighbors(currentCell, false);			
			if(unvisitedNeighbors.size() > 0)
			{
				int randNeighborIndex = (int) (Math.random()*unvisitedNeighbors.size());

				MazeCell randomNeighbor = unvisitedNeighbors.get(randNeighborIndex);
				unvisitedNeighbors.remove(randNeighborIndex);
				previousUnvisitedNeighbors.addAll(unvisitedNeighbors);
				
				// on supprime les murs entre la case actuelle et un proche voisin aléatoire
				openWall(randomNeighbor, currentCell);
				cellStack.add(randomNeighbor);
			}
			
			if(cellStack.isEmpty()) {
				// On place la case de fin du labyrinthe avant que l'empilement des cases ne devienne vide pour la premiere fois
				if(finishLocation == null) {
					finishLocation = new PointF(currentCell.x*cellWidth+cellWidth/2+paddingX,currentCell.y*cellHeight+cellHeight/2+paddingY);
				}
				
				if(!previousUnvisitedNeighbors.isEmpty()) {
					int randomUnvisitedIndex = (int) (Math.random()%previousUnvisitedNeighbors.size());
					MazeCell previousUnvistedNeighbor = previousUnvisitedNeighbors.get(randomUnvisitedIndex);
					cellStack.add(previousUnvistedNeighbor);
					//To avoid having areas of the maze completely inaccessible, we'll open a wall between this unvisited neighbor and a random visited neighbor.
					//Note: This causes unconnected walls. I think that's better than unreachable cells.
					ArrayList<MazeCell> visitedNeighbors = getNeighbors(previousUnvistedNeighbor, true);
					assert (visitedNeighbors.size() > 0) : "At this point there should be at least one adjacent visited neighbor";
					int randomVisitedIndex = (int) (Math.random()%visitedNeighbors.size());
					openWall(previousUnvistedNeighbor, visitedNeighbors.get(randomVisitedIndex));
				}
				else if(!unvisitedCells.isEmpty()) {
					int randomUnvisitedIndex = (int) (Math.random()%unvisitedCells.size());
					cellStack.add(unvisitedCells.get(randomUnvisitedIndex));
				}
			}
		}

		walls = new HashSet<LineSegment2D>();
		// On genere les murs en fonction de la case
		for(MazeCell cell : visitedCells) {
			// cellX et cellY font reference au coin en haut à gauche de la case
			float cellX = cell.x * cellWidth + paddingX;
			float cellY = cell.y * cellHeight + paddingY;
			if(cell.walls[0]) { // mur nord
				walls.add(new LineSegment2D(cellX, cellY, cellX+cellWidth, cellY));
			}
			if(cell.walls[1]) { // mur sud
				walls.add(new LineSegment2D(cellX, cellY+cellHeight, cellX+cellWidth, cellY+cellHeight));				
			}
			if(cell.walls[2]) { // mur est
				walls.add(new LineSegment2D(cellX+cellWidth, cellY, cellX+cellWidth, cellY+cellHeight));

			}
			if(cell.walls[3]) { // mur ouest
				walls.add(new LineSegment2D(cellX, cellY, cellX, cellY+cellHeight));				
			}
			neverReturnedLocations.add(new PointF(cellX + cellWidth/2.0f, cellY + cellHeight/2.0f));
		}
		
		delegate.mazeGenerationDidFinish(this);
	}

	@Override
	public PointF getStartLocation() {
		neverReturnedLocations.remove(startLocation);
		return startLocation;
	}

	@Override
	public PointF getFinishLocation() {
		neverReturnedLocations.remove(finishLocation);
		return finishLocation;
	}

	@Override
	public Set<LineSegment2D> getWalls() {
		return walls;
	}

	@Override
	public Set<PointF> getRandomRoomLocations(int numLocations, boolean exclusive) {
		Set<PointF> locations = new HashSet<PointF>();
		if(exclusive && numLocations > neverReturnedLocations.size()) {
			locations.addAll(neverReturnedLocations);
			neverReturnedLocations.removeAll(neverReturnedLocations);
			return locations;
		}
		else {
			while(numLocations > 0) {
				PointF location;
				if(exclusive) {
					location = (PointF) neverReturnedLocations.toArray()[(int) (Math.random()*neverReturnedLocations.size())];
				}
				else {
					location = new PointF((float)Math.floor(Math.random()*numCellsX) * cellWidth + cellWidth/2.0f + paddingX,
										  (float)Math.floor(Math.random()*numCellsY) * cellWidth + cellHeight/2.0f + paddingY);
				}

				if(locations.add(location)) {
					boolean removed = neverReturnedLocations.remove(location);
					assert (removed) : "ici \"location\" doit être supprimé de \"neverReturnedLocations\".";
					--numLocations;
				}
			}
		}
		return locations;
	}

	private ArrayList<MazeCell> getNeighbors(MazeCell currentCell, boolean visited) {
		ArrayList<MazeCell> neighbors = new ArrayList<MazeCell>();
		for(int i = currentCell.x-1; i <= currentCell.x+1; ++i) {
			for(int j = currentCell.y-1; j <= currentCell.y+1; ++j) {
				// on vérifie que l'indice est dans le tableau et qu'il ne s'agit ni d'une case de la
				// diagonale ni d'une case non visitée
				if((i==currentCell.x || j==currentCell.y) && i>=0 && j>=0 && i<numCellsX && j<numCellsY && mazeCells[i][j].v == visited) {
					neighbors.add(mazeCells[i][j]);
				}
			}
		}
		return neighbors;
	}
	
	private void openWall(MazeCell a, MazeCell b) {
		if(a.x < b.x) {
			a.walls[2] = false;
			b.walls[3] = false;
		} else if(a.x > b.x) {
			a.walls[3] = false;
			b.walls[2] = false;
		} else if(a.y < b.y) {
			a.walls[1] = false;
			b.walls[0] = false;
		} else if(a.y > b.y) {
			a.walls[0] = false;
			b.walls[1] = false;
		}
	}
}
