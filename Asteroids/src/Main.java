
/**
 * Program Name: Main.java
 * Purpose: The main file for a replica Asteroids game. Contains the main thread, paint, and listeners
 * Coder: Justin Bonello
 * Date: Aug 14, 2016 
 */



import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Vector;

import javax.swing.*;


public class Main extends JApplet implements Runnable
{
	
	private static final long serialVersionUID = 1L;
	
	Thread 	gameThread;				//main game thread
	int screen = MENU_SCRN;		//control flag for the thread object
	
	private Image bufferImage;  	//create an Image object to hold the off-screen image that will be drawn
	private Graphics2D bufferBrush;	//pain brush for the back room
	
	static final int SCREEN_WIDTH = 800;
	static final int SCREEN_HEIGHT = 800;
	static final int GAME_SCRN = 1;
	static final int GAME_OVER_SCRN = 2;
	static final int WIN_SCRN = 3;
	static final int MENU_SCRN = 4;
	
	private AudioClip laser_sound;
	private AudioClip nuke_sound;
	private AudioClip hit_sound;
	private AudioClip game_over_1_sound;
	private AudioClip game_over_2_sound;
	private AudioClip win_sound;
	private Image 	  backGround;
	
	private int score = 0;
	private int nukeCount = 10;
	private Vector<Asteroid> asteroids = new Vector<Asteroid>(10);	
	private Vector<Laser> lasers = new Vector<Laser>(5);
	
	private boolean running = false;
	private int spawnChance = 100;
	private int pauseTime = 25;
	private int winScore = 15;
	private int level = 1;
	
	
	
	public void init()// used to "set the stage"
	{
		//set the size and color of the content pane of the applet
		this.setSize( SCREEN_WIDTH, SCREEN_HEIGHT );
		this.setLayout(new BorderLayout());
		
		//make image size exactly same as the screen area
		bufferImage = this.createImage(SCREEN_WIDTH, SCREEN_HEIGHT);
		bufferBrush = (Graphics2D)bufferImage.getGraphics();
		setFocusable(true);
		this.addKeyListener( new KeyHandler() );
		this.addMouseListener( new MouseHandler() );
		
		//set up audio file paths
		URL audioURL = Main.class.getResource("audio/laser.wav");
		laser_sound = Applet.newAudioClip(audioURL);
		audioURL = Main.class.getResource("audio/nuke.wav");
		nuke_sound = Applet.newAudioClip(audioURL);
		audioURL = Main.class.getResource("audio/hit.wav");
		hit_sound = Applet.newAudioClip(audioURL);
		audioURL = Main.class.getResource("audio/game_over_tune.wav");
		game_over_1_sound = Applet.newAudioClip(audioURL);
		audioURL = Main.class.getResource("audio/game_over_voice.wav");
		game_over_2_sound = Applet.newAudioClip(audioURL);
		audioURL = Main.class.getResource("audio/win.wav");
		win_sound = Applet.newAudioClip(audioURL);
		backGround = getImage(getCodeBase(), "img/logo.jpg");
		
		//set one asteroid
		asteroids.addElement( new Asteroid() );
		
		//here is where we actually build the thread
		this.gameThread = new Thread(this);//this applet will ride on this thread
				
		//DUH! Start the engine.
		//this.gameThread.start();//thread is now eligible to run on the CPU. 		
	}//end init
	
	
	//use the paint() method to get things started
	public void paint(Graphics g)//a graphics context object i.e. a "Paint brush
	{
		//have bufferBrush clear its rectangle for the next "frame"
		bufferBrush.setColor(Color.BLACK);
		bufferBrush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		
		if( screen == GAME_SCRN )
		{			
			synchronized(asteroids)
			{
				//paint the all of the asteroids
				for(Asteroid a : asteroids)
				{
					bufferBrush.setColor(a.getCol());
					bufferBrush.fillOval(a.getX() - a.getRadius(), a.getY() - a.getRadius(), a.getRadius()*2, a.getRadius()*2);
				}
			}
			
			synchronized(lasers)
			{
				//paint the all of the asteroids
				for(Laser l : lasers)
				{
					bufferBrush.setStroke( new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND) );
					bufferBrush.setColor(l.getCol());
					bufferBrush.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
				}				
			}
			
			
			
			this.repaint();
		}
		else if ( screen == GAME_OVER_SCRN )
			gameOver();
		else if ( screen == WIN_SCRN )
			youWin();
		else if ( screen == MENU_SCRN )
			menu();
		else
			stop();
		
		bufferBrush.setColor(Color.WHITE);
		bufferBrush.setFont( new Font("Impact", Font.BOLD, 40) );
		bufferBrush.drawString("SCORE: " + score + " /" + winScore, 5, 50);
		bufferBrush.drawString("LEVEL: " + level, SCREEN_WIDTH - 140, 50);
		
		if( nukeCount != 0 )
			bufferBrush.drawString("NUKE IN: " + nukeCount, 5, 100);
		else
		{
			bufferBrush.setColor(Color.RED);
			bufferBrush.drawString("NUKE READY", 5, 100);
		}
		
		
		//NOW, call drawImage() to have the ON SCREEN brush 'g' paint this completed image
		g.drawImage(bufferImage, 0, 0, this);
		
	}//end paint
	
	
	public void menu()
	{
		asteroids.clear();
		lasers.clear();
		
		bufferBrush.setColor(Color.WHITE);
		bufferBrush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		bufferBrush.drawImage(backGround, 0, -150, SCREEN_WIDTH, SCREEN_HEIGHT, null);
		this.setVisible(true);
		
		this.getGraphics().drawImage(bufferImage, 0, 0, this);
		repaint();
		
		sleep(200);
			
		bufferBrush.setColor(Color.BLACK);
		drawCenteredString(	bufferBrush, "PRESS SPACE TO PLAY", 
							new Rectangle( SCREEN_WIDTH, SCREEN_HEIGHT + 300 ), 
							new Font("Arial", Font.BOLD, 30));
		this.getGraphics().drawImage(bufferImage, 0, 0, this);
		sleep(300);		
		
	}
	
	public void gameOver()
	{
		asteroids.clear();
		lasers.clear();
		
		running = false;
		stop();
		
		
		bufferBrush.setColor(Color.RED);
		drawCenteredString(	bufferBrush, "BYE BYE", 
							new Rectangle(SCREEN_WIDTH, SCREEN_HEIGHT - 90 ), 
							new Font("Chiller", Font.BOLD, 90));
		drawCenteredString(	bufferBrush, "EARTHLINGS", 
				new Rectangle(SCREEN_WIDTH, SCREEN_HEIGHT + 50 ), 
				new Font("Chiller", Font.BOLD, 90));
		game_over_1_sound.play();
		game_over_2_sound.play();
		
		
		bufferBrush.setColor(Color.WHITE);
		drawCenteredString(	bufferBrush, "PRESS SPACE TO PLAY", 
							new Rectangle( SCREEN_WIDTH, SCREEN_HEIGHT + 300 ), 
							new Font("Arial", Font.BOLD, 30));
		this.getGraphics().drawImage(bufferImage, 0, 0, this);

		score = 0;
		nukeCount = 10;
	}
	
	public void youWin()
	{
		asteroids.clear();
		lasers.clear();

		running = false;
		stop();
		
		bufferBrush.setColor(Color.WHITE);
		drawCenteredString(	bufferBrush, "Earth is Safe", 
							new Rectangle(SCREEN_WIDTH, SCREEN_HEIGHT - 90), 
							new Font("Forte", Font.PLAIN, 90));
		drawCenteredString(	bufferBrush, "For Another Day", 
							new Rectangle(SCREEN_WIDTH, SCREEN_HEIGHT + 50), 
							new Font("Forte", Font.PLAIN, 90));
		win_sound.play();
		
		bufferBrush.setColor(Color.WHITE);
		drawCenteredString(	bufferBrush, "PRESS SPACE TO PLAY NEXT LEVEL", 
							new Rectangle( SCREEN_WIDTH, SCREEN_HEIGHT + 300 ), 
							new Font("Arial", Font.BOLD, 30));
		this.getGraphics().drawImage(bufferImage, 0, 0, this);

		score = 0;
		nukeCount = 10;
		spawnChance -= 10;
		pauseTime -= 2;
		winScore += 5;
		++level;
		
	}
	
	public void explosion()
	{
		asteroids.clear();
		nuke_sound.play();
		
		for(int i = 0; i < 10; ++i)
		{
			//have bufferBrush clear its rectangle for the next "frame"
			bufferBrush.setColor(Color.RED);
			bufferBrush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			this.getGraphics().drawImage(bufferImage, 0, 0, this);
			sleep(20);
			
			//have bufferBrush clear its rectangle for the next "frame"
			bufferBrush.setColor(Color.WHITE);
			bufferBrush.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);	
			this.getGraphics().drawImage(bufferImage, 0, 0, this);
			sleep(20);
		}
		
		nukeCount = 10;
	}
	
	/**
	 * Draw a String centered in the middle of a Rectangle.
	 *
	 * @param g The Graphics instance.
	 * @param text The String to draw.
	 * @param rect The Rectangle to center the text in.
	 */
	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    FontMetrics metrics = g.getFontMetrics(font);
	    int x = (rect.width - metrics.stringWidth(text)) / 2;
	    int y = ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    g.setFont(font);
	    g.drawString(text, x, y);
	}
	
	
	/*
	 * Method Name: update()
	 * Purpose: OVER-RIDES the call to parent class update() and prevents the throwing
	 *          of the bucket of paint before the paint() method goes to work
	 * Accepts: a Graphics object 
	 * Returns: nothing. Void method
	 * NOTE: this method is always present in good buffering examples
	 */
	public void update(Graphics g){ paint(g); }	
	
	
	/*
	 * Method Name: pause()
	 * Purpose: suspends the execution of the AWT thread for the specified number
	 *          of milliseconds.
	 * Accepts: an int representing an interval in milliseconds
	 * Returns: NOTHING! void method
	 */
	public void pause(int interval)
	{
		//use a try catch 
		try
		{
			Thread.sleep(interval);
		}
		catch(InterruptedException ex)
		{
			System.out.println("Exception message is " + ex.getMessage());
		}
	}
	
	@Override
	public void run()
	{
		// set up an infinite loop based on thread state and call the 
		//method calcPosition() to determine position of the ball objects to be drawn
		while(running)
		{		
			if( score == winScore )
				screen = WIN_SCRN;
			
			for( int i = 0; i < asteroids.size(); ++i )
			{
				Asteroid a = asteroids.get(i);
				a.updatePos();
				if( a.getY() - a.getRadius() >= SCREEN_HEIGHT)
					screen = GAME_OVER_SCRN;
				
				for( int j = 0; j < lasers.size(); ++j )
				{
					Laser l = lasers.get(j);
					if(a.getTarget(l.getX1(), l.getY1()) || a.getTarget(l.getX2(), l.getY2()))
					{
						lasers.remove(l);
						hit_sound.play();
						if(a.scoredHit() == 0)
						{
							if( a.isHost() )
							{
								asteroids.addElement( new Asteroid( a, -a.getRadius() ) );
								asteroids.addElement( new Asteroid( a, a.getRadius() ) );
							}
							asteroids.removeElement(a);
							++score;
							if( nukeCount != 0 )
								--nukeCount;
						}					
					}
				}			
			}
			if ( (int)Asteroid.rand(0, spawnChance) == 1 )
				asteroids.addElement( new Asteroid() );
			
			synchronized(lasers)
			{
				for( int j = 0; j < lasers.size(); ++j )
				{
					Laser l = lasers.get(j);
					l.updatePos();
					if( l.getX2() > SCREEN_WIDTH || l.getX2() < 0 || l.getY2() > SCREEN_HEIGHT || l.getY2() < 0 )
						lasers.removeElement(1);
				}								
			}
			
			sleep(pauseTime);
			
		}//end while	
	}//end run
	
	
	public void sleep( int ms)
	{
		//call the pause method to slow things down a bit
		try
		{
			Thread.sleep(ms);//10 milliseconds
		}
		catch(InterruptedException ex)
		{
			System.out.println("Exception, message is " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void stop()
	{
		//if we were running this on a web page, and user moved to another web page,
		// the browser will call this method to stop the thread
		gameThread = new Thread(this);
	}
	
	private class MouseHandler implements MouseListener
	{
		@Override
		public void mousePressed(MouseEvent e) {
			if( screen == GAME_SCRN )
			{
				laser_sound.play();
				synchronized(lasers)
				{
					lasers.addElement( new Laser(e.getX(), e.getY()) );		
				}
			}								
		}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mouseClicked(MouseEvent e) {}
	}
	
	private class KeyHandler implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent e) {
			synchronized(asteroids)
			{
				if( e.getKeyCode() == KeyEvent.VK_SPACE)
				{
					if( screen != GAME_SCRN )
					{
						running = true;
						gameThread.start();//thread is now eligible to run on the CPU.
						screen = GAME_SCRN;
						repaint();
					}						
					if( nukeCount == 0 && screen == GAME_SCRN )
						explosion();
				}
					
			}
		}

		@Override
		public void keyTyped(KeyEvent e) { }
		@Override
		public void keyReleased(KeyEvent e) { }
		
	}
	
	
	//running this using a main to prevent a difficult to kill applet in 
	// applet viewer
	public static void main(String[] args)
	{
		// create a Frame
		JFrame frame = new JFrame("Asteroids");
		
		//create an instance of the applet
		Main applet = new Main();
		//call its init()
		applet.init();
		
		//add applet instance to the frame
		frame.getContentPane().add(applet, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		
		//last line!
		frame.setVisible(true);
	}

}
