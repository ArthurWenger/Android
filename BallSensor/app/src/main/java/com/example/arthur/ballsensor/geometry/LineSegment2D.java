package com.example.arthur.ballsensor.geometry;

import android.graphics.PointF;

/** Classe modélisant une ligne / un mur dans le labyrinthe **/
public class LineSegment2D {
	public PointF a = null;
	public PointF b = null;

	// Une ligne est définie par deux points
	public LineSegment2D( float x1, float y1, float x2, float y2) {
		a = new PointF(x1,y1);
		b = new PointF(x2,y2);
	}

	public LineSegment2D( PointF a, PointF b) {
		if(a != null) {
			this.a = new PointF(a.x,a.y);
		}
		if(b != null) {
			this.b = new PointF(b.x,b.y);
		}
	}

	@Override
	public boolean equals(Object o) {
		LineSegment2D other = (LineSegment2D)o;
		return (int)(other.a.x) == (int)(a.x) &&
				(int)(other.a.y) == (int)(a.y) &&
				(int)(other.b.x) == (int)(b.x) &&
				(int)(other.b.y) == (int)(b.y);
	}

	/*  Point de la ligne le plus proche d'un cercle de centre c
	 *  Cette méthode sert à détecter les collisions entre un cercle et un mur du labyrinthe.
	 *  Pour plus de précisions: http://doswa.com/2009/07/13/circle-segment-intersectioncollision.html */
	public PointF closestPointToCircle(PointF c, float r) {
		PointF seg_v = Math2D.subtract(b, a);
		if(seg_v.length() <= 0.0f) {
			return new PointF(a.x,a.y);
		}
		PointF pt_v = Math2D.subtract(c,a);
		float seg_v_length = seg_v.length();
		PointF seg_v_normalized = Math2D.normalize(seg_v);
		float proj_v_length = Math2D.dot(pt_v, seg_v_normalized);
		PointF closest = new PointF();
		if(proj_v_length < 0) {
			closest.set(a);
		}
		else if(proj_v_length > seg_v_length) {
			closest.set(b);
		}
		else  {
			PointF proj_v = Math2D.scale(seg_v_normalized, proj_v_length);
			closest = Math2D.add(a, proj_v);
		}
		return closest;			
	}

	// Méthode permettant de récupérer le décalage entre le centre d'un cercle et le point le proche
	// de celui-ci sur la ligne. Cette méthode ne renvoie null si le cercle n'est pas en collision avec la ligne
	public PointF circleIntersectionResolutionOffset(PointF c, float r, float lineThickness) {
		PointF closest = closestPointToCircle(c, r);
		PointF dist_v = Math2D.subtract(c, closest);
		if(dist_v.length() < r+lineThickness/2) {
			float dist_v_length = dist_v.length();
			float multiplier = ((r-dist_v_length+lineThickness/2)/dist_v_length);
			PointF offset = Math2D.scale(dist_v, multiplier);
			return offset;
		}
		return null;
	}

	// Detection d'une colision entre la ligne et un cercle
	public boolean intersectsCircle(PointF c, float r) {
		return circleIntersectionResolutionOffset(c, r, 0) != null;
	}
}
