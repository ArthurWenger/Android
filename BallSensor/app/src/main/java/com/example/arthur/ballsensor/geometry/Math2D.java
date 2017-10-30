package com.example.arthur.ballsensor.geometry;

import android.graphics.PointF;
import android.graphics.RectF;

/** Classe permettant de comparer et d'effectuer des opérations sur des formes géométriques dans un espace à deux dimensions.
 *  Les méthodes implémentées concernent notament l'intersection de formes,  **/
public class Math2D {
	
	private Math2D(){}

	/** Méthodes de detection des collisions **/
	public static boolean pointInCircle(float px, float py, PointF c, float r) {
		return Math.sqrt((c.y-py)*(c.y-py) + (c.x-px)*(c.x-px)) < r;	
	}
	
	public static boolean circleIntersection(PointF c1, float r1, PointF c2, float r2) {
		return Math.sqrt((c2.y-c1.y)*(c2.y-c1.y) + (c2.x-c1.x)*(c2.x-c1.x)) < r1 + r2;
	}
	
	public static boolean pointInRect(PointF p, RectF r) {
		return r.contains(p.x, p.y);
	}
	
	public static boolean circleIntersectsRect(PointF center, float radius, RectF rect) {
		if(pointInRect(center, rect)) {
			return true;
		}
		LineSegment2D top = new LineSegment2D(rect.left, rect.top, rect.right, rect.top);
		LineSegment2D left = new LineSegment2D(rect.left, rect.top, rect.left, rect.bottom);
		LineSegment2D right = new LineSegment2D(rect.right, rect.top, rect.right, rect.bottom);
		LineSegment2D bottom = new LineSegment2D(rect.left, rect.bottom, rect.right, rect.bottom);
		return top.intersectsCircle(center, radius) || left.intersectsCircle(center, radius) || right.intersectsCircle(center, radius) || bottom.intersectsCircle(center, radius);
	}
	
	/** Opérations sur les vecteurs **/

	// addition de vecteurs
	public static PointF add(PointF a, PointF b) {
		return new PointF(a.x+b.x,a.y+b.y);
	}

	// soustaction de vecteurs
	public static PointF subtract(PointF a, PointF b) {
		return new PointF(a.x-b.x,a.y-b.y);
	}

	// Produit d'une constante par un vecteur
	public static PointF scale(PointF v, float s) {
		return new PointF(v.x*s, v.y*s);
	}

	// Normalisation d'un vecteur
	public static PointF normalize(PointF v) throws IllegalArgumentException {
		float length = v.length();
		if(length <= 0.0f) {
			throw new IllegalArgumentException("Vector length is zero.");
		}
		return new PointF(v.x/length, v.y/length);
	}

	// Simple produit des vecteurs OA et OB ou O est l'origine et A et B sont des points
	public static float dot(PointF a, PointF b) {
		return a.x*b.x + a.y*b.y;
	}

	// Norme du vecteur OA ou A est un point et O est l'origine
	public static float lengthSquared(PointF a) {
		return a.x*a.x + a.y*a.y;
	}

	// Produit scalaire de deux points A et B <=> Projection d'un point A sur le segment OB ou B est un point et O est l'origine
	public static PointF project(PointF a, PointF b) throws IllegalArgumentException {
		float lengthSquaredB = lengthSquared(b);
		if(lengthSquaredB <= 0.0f) {
			throw new IllegalArgumentException("Vector length of 'b' is zero.");
		}
		float dotProduct = dot(a,b);
		return scale(b, dotProduct/lengthSquaredB);
	}

	// Angle entre deux points en radian. Les valeurs sont comprises entre -pi et pi.
	public static float angle(PointF a, PointF b) throws IllegalArgumentException {
		float aLength = a.length();
		float bLength = b.length();
		if(aLength <= 0.0f) {
			throw new IllegalArgumentException("Vector 'a' length is zero.");
		}
		if(bLength <= 0.0f) {
			throw new IllegalArgumentException("Vector 'b' length is zero.");
		}
		double cosAngle = Math.max(-1.0, Math.min(1.0, dot(a,b)/(aLength*bLength)));
		double angle = Math.acos(cosAngle);
		
		PointF perpendicularToB = new PointF(b.y,-b.x);
		if(dot(a,perpendicularToB) < 0) {
			angle = -angle;
		}
		return (float)angle;
	}

	// Rotation d'un point par rapport à l'origine
	public static PointF rotate(PointF v, float angle) throws IllegalArgumentException { // l'angle est en radians
		return new PointF((float)(v.x*Math.cos(angle) - v.y*Math.sin(angle)), (float)(v.x*Math.sin(angle) + v.y*Math.cos(angle)));
	}
}
