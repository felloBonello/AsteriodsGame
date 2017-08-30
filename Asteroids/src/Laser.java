/**
 * Program Name: Laser.java
 * Purpose: Holds all of the details about each laser i.e. location
 * 			Also has functionality to update location and calculate laser line based on mouse click
 * Coder: Justin Bonello
 * Date: Aug 14, 2016 
 */

import java.awt.Color;

public class Laser {

	private Color  		col;
	private int 		x1;
    private int 		y1;
    private int 		x2;
    private int 		y2;
    private double 		angle;
    private double 		vel;
    
    
    // Constructor, create a new Laser located at the bottom of the screen
 	public Laser(int x2, int y2)
 	{
 		this.x1 = Main.SCREEN_WIDTH / 2;
 		this.y1 = Main.SCREEN_HEIGHT - 10;
 		this.x2 = x2;
 		this.y2 = y2;
 		this.angle = calcAngle();
 		this.vel = 30;
 		this.col = new Color( 1.0f, 0.0f, 0.0f, 1.0f );
 		calcSecondPoint();
 	}
 	
 	public Color getCol() {
		return col;
	}

	public void setCol(Color col) {
		this.col = col;
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	private void calcSecondPoint()
 	{
 		x2   = (int) (x1 + 55 * Math.sin(angle));
 		y2   = (int) (y1 + 55 * Math.cos(angle));
 	}
 	
 	public double calcAngle()
 	{
 		final double deltaY = (x1 - x2);
 	    final double deltaX = (y1 - y2);
 	    final double result = Math.atan2(deltaY, deltaX);
 	    return result;
 	}
 	
 	public void updatePos()
 	{
 		x1 -= vel * Math.sin(angle);
 	    y1 -= vel * Math.cos(angle);
 	    x2 -= vel * Math.sin(angle);
	    y2 -= vel * Math.cos(angle);
 	}
}
