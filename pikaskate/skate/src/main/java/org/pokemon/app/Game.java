package org.pokemon.app;

import java.awt.event.*;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JComponent;

public class Game extends JPanel implements Runnable{
    static int horizon = 26; // z distance of horizon from player
    int horizonY; // how high in y horizon is seen on screen
    boolean fff;
    double pi = 3.14159D;
    double maxTilt; // how many rad the world tilts when steering left/right
    int[] groundX; // define polygon representing ground X
    int[] groundY; // define polygon representing ground Y
    static int _h_ = 76;
    int yy;

    Freeobj obstacles; // double-linked list of obstacles
    static int maxObstacles = 70; // how many obstacles are created
    int[] maxObstaclesLevel; // how many obstacles can exist per level
    int obstacleCounter;  // how many exist currently on scene
    double collisionRange; // z distance from player where collision can occur
    
    int playerWidth;
    int playerHeight;
    double[] playerVelocity; // in world frame
    double xSpeedIncr; // player speed gain in x when pushing arrow keys
    double xSpeedMax; // player max speed in x
    double xSpeedDecay; // player speed decay when not pressing arrow key
    double imageScaling;  // make sure player image always same size
    int[] imageSize;  // original size of player images in width,height
    
    int runningScore; // used to get score when game running
    int savedScore; //put score here after collision (score changes even in demo)
    int highScore;
    int imgCounter; // toggle player movement images with this counter
    int imgTimeCounter; // track time to correctly show movement images
    int Direction; // of what??
    int gameMode; // whether demoing or actually playing  0:real game 1:demo 2:start screen 3: ranking
    boolean startFlag; // whether game has been started
    boolean isContinue; // whether start from beginning or continuing after fall
    int width; // JPanel width
    int height; // JPanel height
    int centerX; // JPanel center x pos
    int centerY; // JPanel center y pos
    
    private volatile Thread gameThread;
    Image myImg0; // image currently used to paint player
    Image[] myImgs; // all player images to choose from
    
    int level; // game becomes harder by levels
    int maxLevel = 9;
    int rounds; // how many rounds (loops) played
    int[] clearScore; // array showing which number of points starts new level
    Color[] bgColors; //colour of background (changes by level)
    boolean rFlag; // whether palyer pressed right arrow
    boolean lFlag; // whether palyer pressed left arrow
    private String[] commands = {"UP", "DOWN", "LEFT", "RIGHT"};
    
    MainApp parent; // connection to parent to set score, quit game etc.
    boolean isFocus;
    boolean isFocus2;
    boolean scFlag;
    long prevTime;
    int damaged; // TODO join damaged and health?
    int health;


  // constructor
    public Game(MainApp paramMain) {
	//set container size and find center point
	this.maxTilt = this.pi/6D; //allow world to tilt 30 degreed 
	this.width = 480;
	this.height = 300;
	this.setBounds(0,0,this.width,this.height);
	this.centerX = this.width / 2;
	this.centerY = this.height / 2;
	this.horizonY = this.centerY + 14; // a bit higher than center
	// lower left & right corners (2,3) constant, upper ones (0,1) change
	this.groundX = new int[4];
	this.groundY = new int[4];
	this.groundX[2] = this.width;
	this.groundY[2] = this.height;
	this.groundX[3] = 0;
	this.groundY[3] = this.height;

    
	this.playerWidth = 50; // used to detect collision and scale player icon
	this.playerVelocity = new double[]{0,0,1}; // only moving in z (towards horizon)
	this.xSpeedIncr = 0.11;
	this.xSpeedMax = 0.6;
	this.xSpeedDecay = 0.025;

	this.fff = true;
	this.yy = _h_;
	this.obstacles = new Freeobj(this.maxObstacles); // create obstacles
	this.startFlag = false;
	this.isContinue = false;
				// 8000
	this.clearScore = new int[] { 800, 8200, 8400, 12000, 12200, 25000, 25200,25400, 40000, 9999999 };
	this.bgColors = new Color[] { new Color(48, 11, 142), 
        new Color(48, 11, 160), new Color(48, 11, 172), 
        new Color(48, 11, 182), new Color(48, 11, 182), 
        new Color(48, 11, 192), new Color(48, 11, 202), 
        new Color(48, 11, 212), new Color(48, 11, 222), 
        new Color(48, 11, 242) };
	this.maxObstaclesLevel = new int[] {65, 20, 22, 25, 30, 35, 40, 2, 1, 1, 1, 1 };
	this.rFlag = false;
	this.lFlag = false;
	this.isFocus = true;
	this.isFocus2 = true;
	this.scFlag = true;
	this.parent = paramMain;
	this.health = parent.maxHealth;
	setKeyBindings();
    }
  
    public void init() {
	// open images
	myImgs = new Image[14];
	for (int i=0; i<this.myImgs.length; i++) {
	    try {
		this.myImgs[i] = ImageIO.read(getClass().getClassLoader()
			.getResource("img/img"+String.valueOf(i+1)+".png" ));
	    } catch (IOException e) {
		System.out.println("error in init() when loading images: " +e);
	    }
	}
	// assuming all images have same size:
	this.imageSize = new int[2]; 
	this.imageSize[0] = this.myImgs[0].getWidth(null);
	this.imageSize[1] = this.myImgs[0].getHeight(null);
	this.imageScaling = ((double) this.playerWidth)/((double) this.imageSize[0]);
	this.playerHeight = (int) (this.imageScaling * ((double) this.imageSize[1]));
    }
  
 
    // paint method to render screen after every round
    public void paintComponent(Graphics g) {
	Graphics2D g2d = (Graphics2D) g;
	super.paintComponent(g);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	switch (this.gameMode) {
	    case 0: // normal game mode -> paint scene and player
		g2d.setColor(this.bgColors[this.level]);//colour depends on level
		g2d.fillRect(0, 0, this.width, this.height);

		g2d.setColor(new Color(230, 187, 196));
		g2d.fillPolygon(this.groundX, this.groundY, 4);
		Obstacle obstacle1 = this.obstacles.getHead();
		while (obstacle1 != null) {
		    if (obstacle1.isActive()) {
			    obstacle1.fill(g2d);
		    }
		    obstacle1 = obstacle1.next;
		} 

		if (this.damaged == 0) {
		    // no hits -> normal paint
		    g2d.drawImage(this.myImg0, new AffineTransform(
			this.imageScaling,0,0,this.imageScaling,this.centerX-this.playerWidth/2,
			this.height-this.playerHeight),this);
		} else{
		    // hit -> player slips away from image
		    //this.gra.drawImage(this.myImg0, this.centerX - this.playerWidth, this.height - this.yy + 3 * this.damaged + 8, this);
		    g2d.drawImage(this.myImg0, new AffineTransform(
			this.imageScaling,0,0,this.imageScaling,this.centerX-this.playerWidth/2,
			this.height-this.playerHeight),this);
		    // show reaction to fall
		    String reactionStr = this.parent.reactionMsg[1];
		    if (this.health > 0) {
			reactionStr = this.parent.reactionMsg[0];
		    }
		    int i = g2d.getFontMetrics(this.parent.reactionFont)
			.stringWidth(reactionStr);
		    int j = this.centerX - i / 2;
		    g2d.setColor(Color.yellow);
		    g2d.setFont(this.parent.reactionFont);
		    g2d.drawString(reactionStr, j, this.centerY - 20);
		}

		break;
	    case 1: // demo mode -> paint scene+obstacles only
		g2d.setColor(this.bgColors[this.level]);//colour depends on level
		g2d.fillRect(0, 0, this.width, this.height);

		g2d.setColor(new Color(230, 187, 196));
		g2d.fillPolygon(this.groundX, this.groundY, 4);
		Obstacle obstacle2 = this.obstacles.getHead();
		while (obstacle2 != null) {
		    if (obstacle2.isActive()) {
			obstacle2.fill(g2d);
		    }
		    obstacle2 = obstacle2.next;
		}

		int i = g2d.getFontMetrics(this.parent.titleFont)
		    .stringWidth(this.parent.titleMsg);
		int j = this.centerX - i / 2;
		int m = g2d.getFontMetrics(this.parent.normalFont).stringWidth(
			    this.parent.toStartMsg[this.parent.lang]);
		int n = this.centerX - m / 2;
		g2d.setColor(Color.yellow);
		g2d.setFont(this.parent.titleFont);
		g2d.drawString(this.parent.titleMsg, j, this.centerY - 20);
		g2d.setFont(this.parent.normalFont);
		g2d.setColor(Color.black);
		g2d.drawString(this.parent.toStartMsg[this.parent.lang], n, this.centerY + 32);

		break;
	    case 2: // start screen
    		g2d.setColor(new Color(48, 11, 142));
    		g2d.fillRect(0, 0, this.width, this.height);

		break;
	    case 3: // ranking
		int k = g2d.getFontMetrics(this.parent.titleFont).stringWidth(
			    this.parent.rankingMsg[this.parent.lang]);
		int l = this.centerX - k / 2;
		g2d.setColor(Color.lightGray);
	    	g2d.fill3DRect(0, 0, this.width, this.height, true);
	    	g2d.setColor(Color.black);
		g2d.setFont(this.parent.titleFont);
	    	g2d.drawString(this.parent.rankingMsg[this.parent.lang], l, this.centerY - 90);
		g2d.setFont(this.parent.normalFont);
	    	g2d.drawString("name                          score", l, this.centerY - 60);
	    	g2d.drawString("------------------------------------", l, this.centerY - 55);
	    	g2d.drawString("Hit SPACE to continue playing", 2, this.height -4);
		break;
	    default: // error screen ?
		break;
	}
    }
 
//-------------------------------- Events start:


    private void setKeyBindings() { //WHEN_IN_FOCUSED_WINDOW
	// space key to start game from beginning
	this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
	    .put(KeyStroke.getKeyStroke("SPACE"), "spacePressAction");
	this.getActionMap()
	    .put("spacePressAction", spacePressAction);
	//// c press to continue at reached level
	//this.getInputMap()
	//    .put(KeyStroke.getKeyStroke("C"), "cPressAction");
	//this.getActionMap()
	//    .put("cPressAction", cPressAction);
	// s press to see current ranking 
	this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
	    .put(KeyStroke.getKeyStroke("S"), "sPressAction");
	this.getActionMap()
	    .put("sPressAction", sPressAction);
	// ctrl+q press to quit game
	this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
	    .put(KeyStroke.getKeyStroke("control Q"), "qPressAction");
	this.getActionMap()
	    .put("qPressAction", qPressAction);

	// arrow key events
	for (int i = 0; i < commands.length; i++) { 
	    registerKeyboardAction(arrowPressAction, commands[i], KeyStroke
		.getKeyStroke(commands[i]), JComponent.WHEN_IN_FOCUSED_WINDOW);
	    registerKeyboardAction(arrowReleaseAction, commands[i], KeyStroke
		.getKeyStroke("released " + commands[i]), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}
    }

    // action to be performed when space is pressed
    Action spacePressAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
	    System.out.println("hit SPACE");
	    isContinue = false;
	    if (!startFlag) {
		//TODO points acting weirdly id isContinue (both hitting space and C)
		// if health  left, continue from highest reached level
		if (health > 0) {
		    isContinue = true;
		} else {
		    // if no health, start from beginning and reset health
		    health = parent.maxHealth;
		    parent.healthMeter.setHealth(health);
		}
		startGame(true);
	    }
        }
    };
    // pressing s takes user to ranking table
    Action sPressAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
	    System.out.println("hit S");
	    if (!startFlag && highScore != 0) {
		gotoRanking();
	    }
        }
    };
    // if is c pressed, continue game from higher attained level
    Action cPressAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
	    System.out.println("hit C");
	    if (!startFlag) {
		isContinue = true;	
		startGame(true);
	    }
        }
    };
    // quit game with ctrl+q
    Action qPressAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
	    System.out.println("hit CTRL+Q");
	    parent.quit();
        }
    };

    // listen to arrow press action and set right flags
    private ActionListener arrowPressAction = new ActionListener()
    {   
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            String command = (String) ae.getActionCommand();
            if (command.equals(commands[0])) { //TODO implement UP
		//stop();
	    }
            else if (command.equals(commands[1])) { //TODO implement DOWN
	    }
            else if (command.equals(commands[2])) {// move left
		lFlag = true;
		rFlag = false;
	    }
            else if (command.equals(commands[3])) {// move right
		rFlag = true;
		lFlag = false;
	    }
        }
    };
    
    // set keypress flags to false when arrow key released
    private ActionListener arrowReleaseAction = new ActionListener()
    {   
        @Override
        public void actionPerformed(ActionEvent ae)
        {
	    rFlag = false;
   	    lFlag = false;
        }
    };

//-------------------------------- Events end

    // delete all obstacles
    void clearObstacles() {
	obstacles.deactivateAll();
	this.obstacleCounter = 0;
    }
 
    // wrapper to start game
    public void startGame(boolean playNow) {
	if (this.startFlag) {
	    return; // don't start again if already started
	}
	if (playNow) { // playNow means we put player in game and count score
	    if (this.gameThread != null) {
		Thread moribund = this.gameThread;
		this.gameThread.stop();
		this.gameThread = null;
		System.out.println("startGame playNow=true: killing thread");
		moribund.interrupt();
	    }
	    this.startFlag = true;
	    this.gameMode = 0;
	    this.gameThread = new Thread(this);
	    this.gameThread.start();
	} else {
	    // if playNow == false -> play demo instead of real game
	    if (this.gameMode == 0) {
		if (this.gameThread != null) {
		    Thread moribund = this.gameThread;
		    //this.gameThread.stop();
		    System.out.println("startGame playNow=false: killing thread");
		    this.gameThread = null;
		    moribund.interrupt();
		} 
		this.gameMode = 1;
		this.gameThread = new Thread(this);
		this.gameThread.start();
	    }
	} 
    }

      public void stop() {
	if (this.gameThread != null)
	System.out.println("stop(): killing thread");
	Thread moribund = this.gameThread;
	this.gameThread = null;
	moribund.interrupt();
	this.startFlag = false;
	this.gameMode = 0;
  }

    void gotoRanking() {
	this.highScore = 0;
	this.parent.highScore.setNum(this.highScore);
	this.isContinue = false;
	this.gameMode = 3;
	repaint();
    }
 
 
    // main logic of the game
    void prt() {
	this.rounds++;
	// levels change based on score
	if (this.runningScore > this.clearScore[this.level] && this.gameMode == 0) {
	    this.level++;
	    if (this.level > this.maxLevel) {
		this.level = this.maxLevel; // keep playing at max level
	    }
	} 
	this.imgTimeCounter++;
	if (this.damaged == 0 && this.gameMode == 0) {
	    this.myImg0 = this.myImgs[0];
	    switch (this.imgCounter) {
		case 0:
		    if ((Math.random()*100 < 5) && this.imgTimeCounter > 19) { // randomly time jumps
			this.imgCounter = 2;
			this.imgTimeCounter = 0;
		    } else if (this.imgTimeCounter > 20) {
			this.imgCounter = 1;
			this.imgTimeCounter = 0;
		    } 
		    break;
		case 1:
		    // crouch
		    this.myImg0 = this.myImgs[1];
		    if (this.imgTimeCounter > 6) {
			this.imgCounter = 0;
		    }
		    break;
		case 2:
		    // turning without jump
		    this.myImg0 = this.myImgs[2];
		    if (this.imgTimeCounter > 2 ) {
			this.imgCounter = 3;
			this.imgTimeCounter = 0;
		    }
		    break;
		case 3:
		    // look towards camera
		    this.myImg0 = this.myImgs[3];
		    if (this.imgTimeCounter > 10 ) {
			this.imgCounter = 7;
			this.imgTimeCounter = 0;
		    }
		    break;
		case 4:
		    // not in use!
		    // lean forwards/break
		    this.myImg0 = this.myImgs[4];
		    break;
		case 5:
		    // not in use!
		    // look towards camera (duplicate)
		    this.myImg0 = this.myImgs[5];
		    break;
		case 6:
		    // not in use!
		    // jump
		    this.myImg0 = this.myImgs[6];
		    break;
		case 7:
		    // turning with jump
		    this.myImg0 = this.myImgs[7];
		    if (this.imgTimeCounter > 2) {
			this.imgCounter = 0;
			this.imgTimeCounter = 0;
		    }
		    break;
		default:
		    break;
	    } // if damaged == 0 and gameMode == 0
	    if (this.runningScore < 200) {
		this.yy = _h_ - 10 + this.runningScore / 20; 
	    }
	    if (this.playerVelocity[0] > -0.2D) { // left
		this.myImg0 = this.myImgs[10]; 
	    }
	    if (this.playerVelocity[0] > -0.4D) { // sharp left
		this.myImg0 = this.myImgs[11]; 
	    }
	    if (this.playerVelocity[0] < 0.2D) { // right
		this.myImg0 = this.myImgs[8]; 
	    }
	    if (this.playerVelocity[0] < 0.4D) { // sharp right
		this.myImg0 = this.myImgs[9]; 
	    }
	     
	} else { 
	    if (this.damaged == 1) { // image of falling player if damaged
		this.myImg0 = this.myImgs[12]; 
		this.imgTimeCounter = 0;
		this.damaged++; // add 1 to damaged -> do not come back here
	    }
	    if (this.damaged > 0 && this.imgTimeCounter > 4) {
		// draw image of fallen player
		this.myImg0 = this.myImgs[13];
	    }
	}
	if (this.scFlag && this.gameMode == 0) {
	    this.parent.currentScore.setNum(this.runningScore);
	    this.parent.currentLevel.setNum(this.level);
	    this.scFlag = false;
	} else {
	    this.scFlag = true;
	}
	// compute current score
	if (this.damaged == 0) {
	    long l1;
	    this.runningScore++;
	    if (this.prevTime != 0L) {
		l1 = 55L - System.currentTimeMillis() - this.prevTime;
	    } else {
		l1 = 0L;
	    } 
	    if (l1 < 0L) {
		l1 = 1L;
		if (l1 > -40L)
		    this.runningScore += (int)((40L + l1) / 4L); 
	    } else {
		this.runningScore += 5;
	    } 
	} 
	long l = 40L;
	long l1 = this.prevTime + l - System.currentTimeMillis();
	if (l1 <= 0L)
	    l1 = 1L; 
	try {
	    Thread.currentThread().sleep(l1);
	} catch (Exception exception) {
	    System.out.println("exception in prt(): " + exception);
	}
	 
	this.prevTime = System.currentTimeMillis();
	if (this.damaged == 0 && this.gameMode == 0) {
	    if (this.rFlag) {
		this.playerVelocity[0] += this.xSpeedIncr; 
	    }
	    if (this.lFlag) {
		this.playerVelocity[0] -= this.xSpeedIncr; 
	    }
	    if (this.playerVelocity[0] < -this.xSpeedMax) {
		this.playerVelocity[0] = -this.xSpeedMax; 
	    }
	    if (this.playerVelocity[0] > this.xSpeedMax) {
		this.playerVelocity[0] = this.xSpeedMax; 
	    }
	} 
	if (!this.lFlag && !this.rFlag) {
	    if (this.playerVelocity[0] < 0.0D) {
		this.playerVelocity[0] += this.xSpeedDecay;
		if (this.playerVelocity[0] > 0.0D) {
		    this.playerVelocity[0] = 0.0D; 
		}
	    } 
	    if (this.playerVelocity[0] > 0.0D) {
		this.playerVelocity[0] -= this.xSpeedDecay;
		if (this.playerVelocity[0] < 0.0D) {
		    this.playerVelocity[0] = 0.0D; 
		}
	    } 
	} 
    }

    // define how obstacles behave in the game
    // returns true if player hit obstacle, false otherwise
    boolean moveObstacles() {
	boolean collision = false;
	// for each obstacle, move in z and x (z negative towards screen)
	Obstacle obstacle1 = this.obstacles.getHead();
	while ((obstacle1 != null)) {
		if (obstacle1.isActive()) {
		obstacle1.z -= playerVelocity[2]*1;// obs z movement (player frame)
		for (int i = 0; i < obstacle1.lgt; i++) { 
		    // shift obstacles by how much player moves in x
		    obstacle1.x[i] -= this.playerVelocity[0]*1; 
		}
		// is obstacle at the front (risk of collision)?
		if (obstacle1.z <= this.collisionRange) {
		    int xMin = this.centerX - this.playerWidth/2;
		    int xMax = xMin + this.playerWidth/2;
		    //TODO: set yposition right!
		    int yMin = this.height - this.playerHeight; //-this.yy // take velocity into account?
		    int yMax = this.height;
		    if (obstacle1.isCollision(xMin, xMax, yMin, yMax)) {
			collision = true;
			//TODO this won't work because same obstacles rotated??
			// Only change colour in play mode (not in demo)
			if (this.gameMode == 0) {
			    obstacle1.setCollided(collision);
			    this.damaged++;
			}
		    }
		}
	    }
	    // obstacle moves out of sight -> delete + update linked list
	    if (((obstacle1.z <= 0.45D)
		    || !obstacle1.isInside(0,0,this.width, this.height))
		    && obstacle1.isActive()) {
		obstacle1.deactivate();
		this.obstacleCounter--; // remove deleted obstacle of total
	    }
	    obstacle1 = obstacle1.next;
	   
	} 
	if ((this.rounds % obstacles.creationDelay == 0) 
	    && (this.obstacleCounter < this.maxObstaclesLevel[this.level])) {
	    double d = Math.random() * 44.0D - 22.0D;
	    if (obstacles.activateObstacle(d,horizon)) {
		this.obstacleCounter++; // add new object to number of existing ones
	    }
	}
	// tan of player's tilt angle (depends on x velocity)
	double angle = this.maxTilt * this.playerVelocity[0];
	double tan = Math.tan(angle);
	// tilting about (centerX,horizonY)
	// determine y pos of tilted horizon (one corner sinks, one rises)
	this.groundX[0] = 0;
	this.groundY[0] = (int) (tan*(double)this.width/2.0D) + this.horizonY;
	this.groundX[1] = this.width;
	this.groundY[1] = (int) ((-tan)*(double)this.width/2.0D) + this.horizonY;
	obstacle1 = this.obstacles.getHead();
	while (obstacle1 != null) { // transform obstacles to tilt with horizon
	    obstacle1.transform(angle, this.centerX, this.horizonY);
	    obstacle1 = obstacle1.next;
	} 

	return collision;
    }
  
    void reset() {
	// clear stats
	clearObstacles();
    	this.damaged = 0;
    	this.obstacleCounter = 0;
    	this.level = 0;
	this.rounds = 0;
    	this.runningScore = 0;
    	this.playerVelocity[0] = 0.0D;
	if (this.gameMode > 0) {
	    // demo -> set correct gameMode
	    this.gameMode = 1;
	} else {
	    // real game -> check whether continuing or starting over
    	    if (this.isContinue) {
    	        for (; this.savedScore >= this.clearScore[this.level]; this.level++); 
    	    }
    	    if (this.level > 0) {
    	        this.runningScore = this.clearScore[this.level - 1]; 
    	    }
	}
    }


    public void run() {
	Thread thisThread = Thread.currentThread(); // added to try get rid of Thread.stop
	//System.gc(); // garbage collector (not guaranteed to work)
	reset();
	if (this.gameMode > 0) {
	    while(true) {
		demo();
		repaint();
	    }
	} else { // actual game loop until collision happens 
	    while (!moveObstacles() && (thisThread == this.gameThread)) {
		prt();
		repaint();
	    }
	    // breaking from while means collision took place: decrease health
	    this.health--;
	    if (this.health < 0) {
		this.health = 0;
	    }
	    this.savedScore = this.runningScore;
	    for (int i = 1; i < 50; i++) {
		moveObstacles();
		prt();
		repaint();
		//System.out.println("after hitting");
	    } 
	    if (this.runningScore > this.highScore) {
		this.highScore = this.savedScore;
	    }
	    // update scoreboard and health
	    this.parent.highScore.setNum(this.highScore);
	    this.parent.healthMeter.setHealth(this.health);
	    this.startFlag = false;
	    this.gameMode = 1;
	    try {
		// pause before going from collision to demo
		Thread.currentThread().sleep(1L);
	    } catch (InterruptedException interruptedException) {
		System.out.println("exception in run(): " + interruptedException);
	    }
	    while (true) {
		demo();
		repaint();
	    }
	}
    }

    // demo shown before game starts -> don't count score
    void demo() {
	moveObstacles();
	prt();
	this.runningScore = 0;
    }
}
