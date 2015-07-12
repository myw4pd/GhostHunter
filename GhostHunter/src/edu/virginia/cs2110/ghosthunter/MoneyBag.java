package edu.virginia.cs2110.ghosthunter;

public class MoneyBag {
	
	private double x; //x coordinate of MoneyBag
	private double y; //y coordinate of MoneyBag
	
	
	public MoneyBag(int x_, int y_){
		this.x = x_;
		this.y = y_;
		
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
	
	public  void setY(double y) {
		this.y = y;
	}

}