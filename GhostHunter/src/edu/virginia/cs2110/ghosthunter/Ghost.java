package edu.virginia.cs2110.ghosthunter;

import java.util.Random;

import android.graphics.Rect;

public class Ghost {
	private double x; // x coordinate of Ghost
	private double y; // y coordinate of Ghost
	private int dx; // pixels to move each time step() is called.
	private int dy;
	private Bone ghostBone; // bone associated with Ghost
	private Rect area; // each ghost has a "rectangle" associated that
						// represents a 80/80 image.

	public Ghost(int x, int y) {
		Random rand = new Random();
		ghostBone = new Bone(rand.nextInt(1200), rand.nextInt(1824));
		this.x = x;
		this.y = y;
		dx = 10; // (int)(Math.random()*11-9);
		dy = 10; // (int)(Math.random()*11-9);
		area = new Rect((int) getX() - 40, (int) getY() - 40,
				(int) getX() + 40, (int) getY() + 40);

	}

	public Rect getArea() {
		return area;
	}

	public Bone getBone() {
		return ghostBone;
	}

	public void nullBone() {
		ghostBone = null;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setdx(int x) {
		dx = x;
	}

	public void setdy(int y) {
		dy = y;
	}

	public void move(int rightEdge, int bottomEdge, int r) {
		setX(getX() + dx); // x = x + dx
		setY(getY() + dy);
		int radius = r;
		if (getX() >= rightEdge - radius) // hits the right edge
		{
			setX(rightEdge - radius);
			dx = dx * -1;
		} else if (getX() <= 0 + radius) // hits the left edge
		{
			setX(0 + radius);
			dx = dx * -1;

		}

		else if (getY() <= 0 + radius) // hits the top edge
		{
			setY(0 + radius);
			dy = dy * -1;
		}

		else if (getY() >= bottomEdge - radius) // hits the bottom edge
		{
			setY(bottomEdge - radius);
			dy = dy * -1;
		}

	}

}
