/**
 * Program Name: Asteroid.java
 * Purpose: Holds all of the details about each asteroid i.e. location
 * 			Also has functionality to update location.
 * Coder: Justin Bonello
 * Date: Aug 14, 2016 
 */


import java.awt.Color;

public class Asteroid
{
    private int 		x;
    private int 		y;
    private double 		vel;
    private int 		radius;
    private int 		hardness;
    private int 		start_hardness;
    private boolean 	host;
    private Color  		col; 
      

	// Constructor, create a new Asteroid located at the top of the screen
	public Asteroid()
	{
		this.x = rand(50, 750);
		this.y = 0;
		this.vel = rand_double(1, 5);
		this.radius = rand(20, 50);
		this.hardness = rand(1,5);
		this.start_hardness = hardness;
		this.host = rand(1,10) == 5;
		this.col = new Color( 0.0f, 0.0f, 1.0f, 1.0f );
	}
	
	// Constructor, given another Asteroid as its parent object
	public Asteroid(Asteroid parent, int deltaX)
	{
		this.x = parent.getX() + deltaX;
		this.y = parent.getY();
		this.vel = parent.getVel() * 0.6;
		this.radius = parent.getRadius() / 2;
		this.hardness = parent.start_hardness;
		this.start_hardness = parent.start_hardness;
		this.host = false;
		this.col =  new Color( 0.0f, 0.0f, 1.0f, 1.0f );
	}	
	
	// Update the position of the Asteroid based on its velocity
	public void updatePos(){ y += vel; }
	
	// The laser has scored a hit on this Asteroid
	public int scoredHit()
	{	
		--hardness;
		float ratio = (float)hardness / (float)start_hardness;
		col = new Color( 1.0f - ratio, 0.0f, ratio, 1.0f );
		return hardness; 
	}
	
	// returns whether a hit was successful or not 
	public boolean getTarget(int mouseX, int mouseY)
	{
		double d = Math.hypot(mouseX-x, mouseY-y);
		return d <= radius;
	}
	
	// Getter method for the x-position
	int getX(){ return x; }
	
	// Getter method for the y-position
	int getY(){ return y; }	
	
	// Getter method for the y-position
	double getVel(){ return vel; }	
	
	// Getter method for the y-position
	Color getCol(){ return col; }	
 
	// Getter method for the radius
	int getRadius(){ return radius; }

	// Is this Asteroid a twin host?
	public boolean isHost(){ return host; }
	
	static public int rand(int hi, int low){ return (int)(Math.random() * (hi - low + 1) + low); }
	static public double rand_double(int hi, int low){ return Math.random() * (hi - low + 1) + low; }
	
}//end class Asteroid
