package com.example.arthur.ballsensor.maze;

import android.graphics.PointF;

import com.example.arthur.ballsensor.geometry.LineSegment2D;

import java.util.Set;

public interface MazeGenerator {
	void generate( float mazeWidth, float mazeHeight, float cellWidth, float cellHeight, MazeGeneratorListener delegate );
	PointF getStartLocation();
	PointF getFinishLocation();
	Set<LineSegment2D> getWalls();
	Set<PointF> getRandomRoomLocations( int numLocations, boolean exclusive ); //exclusive means it's never been returned before, and not a start or finish location
}
