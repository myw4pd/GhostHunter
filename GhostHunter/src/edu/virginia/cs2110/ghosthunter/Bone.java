package edu.virginia.cs2110.ghosthunter;
public class Bone {
	
	private double x; //x coordinate of Bone
	private double y; //y coordinate of Bone
	
	
	public Bone(int x, int y){
		this.x = x;
		this.y = y;
		
	}
	
	public double getBoneX() {
		return x;
	}
	
	public double getBoneY() {
		return y;
	}

}