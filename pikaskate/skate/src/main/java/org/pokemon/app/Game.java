package org.pokemon.app;

import java.awt.event.*;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
    Player player;

    Freeobj objects; // double-linked list of objects (obstacles/hearts)
    static int maxObjects = 70; // how many obstacles are created
    int[] maxObstaclesLevel; // how many obstacles can exist per level
    int obstacleCounter;  // how many exist currently on scene
    int[] maxHeartsLevel; // how many hearts can exist per level
    int heartCounter; // number of hearts in scene
    double collisionRange; // z distance from player where collision can occur
    
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

	this.player = new Player(3, this.centerX, this.height);

   	this.fff = true;
	this.yy = _h_;
	this.objects = new Freeobj(this.maxObjects); // create objects
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
	this.maxObstaclesLevel = new int[] {15, 20, 22, 25, 30, 35, 40, 45, 50, 60};
	this.maxHeartsLevel = new int[] {5, 5, 4, 4, 3, 3, 2, 2, 1, 1};
	this.rFlag = false;
	this.lFlag = false;
	this.isFocus = true;
	this.isFocus2 = true;
	this.scFlag = true;
	this.parent = paramMain;
	setKeyBindings();
    }
  
    public void init() {
    }
  
 
    // paint method to render screen after every round
    public void paintComponent(Graphics g) {
	Graphics2D g2d = (Graphics2D) g;
	super.paintComponent(g);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	switch (this.gameMode) {
	    case 4:
	    case 0: // normal game mode -> paint scene and player
		g2d.setColor(this.bgColors[this.level]);//colour depends on level
		g2d.fillRect(0, 0, this.width, this.height);

		g2d.setColor(new Color(230, 187, 196));
		g2d.fillPolygon(this.groundX, this.groundY, 4);
		FloatyObject floaty1 = this.objects.getHead();
		while (floaty1 != null) {
		    if (floaty1.isActive()) {
			    floaty1.fill(g2d);
		    }
		    floaty1 = floaty1.next;
		} 

		this.player.paint(g2d);
		if (this.player.damaged > 0) {
		    // show reaction to fall
		    String reactionStr = this.parent.reactionMsg[1];
		    if (this.player.health > 0) {
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
	    case 1: // demo mode -> paint scene+objects only
		g2d.setColor(this.bgColors[this.level]);//colour depends on level
		g2d.fillRect(0, 0, this.width, this.height);

		g2d.setColor(new Color(230, 187, 196));
		g2d.fillPolygon(this.groundX, this.groundY, 4);
		FloatyObject floaty2 = this.objects.getHead();
		while (floaty2 != null) {
		    if (floaty2.isActive()) {
			floaty2.fill(g2d);
		    }
		    floaty2 = floaty2.next;
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
		if (player.health > 0) {
		    isContinue = true;
		} else {
		    // if no health, start from beginning and reset health
		    player.health = parent.maxHealth;
		    parent.healthMeter.setHealth(player.health);
		}
		startGame(true);
	    }
        }
    };
    // pressing s takes user to ranking table
    Action sPressAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
	    System.out.println("hit S");
	    if (!startFlag && player.highScore != 0) {
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

    // delete all objects
    void clearObjects() {
	objects.deactivateAll();
	this.obstacleCounter = 0;
	this.heartCounter = 0;
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
	this.player.highScore = 0;
	this.parent.highScore.setNum(this.player.highScore);
	this.isContinue = false;
	this.gameMode = 3;
	repaint();
    }
 
 
    // main logic of the game
    void prt() {
	this.rounds++;
	// levels change based on score
	if (this.player.runningScore > this.clearScore[this.level] && this.gameMode == 0) {
	    this.level++;
	    if (this.level > this.maxLevel) {
		this.level = this.maxLevel; // keep playing at max level
	    }
	}
	// compute current score
	this.player.computeScore(this.rFlag, this.lFlag);
	 // if damaged == 0 and gameMode == 0
	    if (this.player.runningScore < 200) {
		this.yy = _h_ - 10 + this.player.runningScore / 20; 
	    }
	if (this.scFlag && this.gameMode == 0) {
	    this.parent.currentScore.setNum(this.player.runningScore);
	    this.parent.currentLevel.setNum(this.level);
	    this.scFlag = false;
	} else {
	    this.scFlag = true;
	}
 
    }

    // define how objects behave in the game
    // returns true if player hit obstacle, false otherwise
    boolean moveObjects() {
	boolean collision = false;
	System.out.println("hearts: " + this.heartCounter + ", obs: " + this.obstacleCounter);
	// for each obstacle, move in z and x (z negative towards screen)
	FloatyObject floaty1 = this.objects.getHead();
	while ((floaty1 != null)) {
		if (floaty1.isActive()) {
		floaty1.z -= this.player.playerVelocity[2]*1;// obs z movement (player frame)
		for (int i = 0; i < floaty1.nodes; i++) { 
		    // shift objects by how much player moves in x
		    floaty1.x[i] -= this.player.playerVelocity[0]*1; 
		}
		// is obstacle at the front (risk of collision)?
		if (floaty1.z <= this.collisionRange) {
		    int xMin = this.centerX - this.player.playerWidth/2;
		    int xMax = xMin + this.player.playerWidth/2;
		    //TODO: set yposition right!
		    int yMin = this.height - this.player.playerHeight; //-this.yy // take velocity into account?
		    int yMax = this.height;
		    if (floaty1.isCollision(xMin, xMax, yMin, yMax)) {
			//TODO this won't work because same objects rotated??
			// Only change colour in play mode (not in demo)
			if (this.gameMode == 0) {
			    floaty1.setCollided(collision);
			    if (floaty1 instanceof Obstacle) {
				collision = true;
				this.player.damaged++;
			    } else if (floaty1 instanceof Heart) {
				this.player.health++;
				if (this.player.health > this.parent.healthMeter.maxHealth) {
				    this.player.health = this.parent.healthMeter.maxHealth;
				}
				this.parent.healthMeter.setHealth(this.player.health);
			    }
			}
		    }
		}
	    }
	    // oobject moves out of sight -> delete + update linked list
	    if (((floaty1.z <= 0.45D)
		    || !floaty1.isInside(0,0,this.width, this.height))
		    && floaty1.isActive()) {
		floaty1.deactivate();
		if (floaty1 instanceof Obstacle) {
		    this.obstacleCounter--; // remove deleted obstacle of total
		} else if (floaty1 instanceof Heart) {
		    this.heartCounter--;
		}
	    }
	    floaty1 = floaty1.next;
	}
	// create new obstacles if not enough on scene
	if ((this.rounds % objects.creationDelay == 0) 
	    && (this.obstacleCounter <= this.maxObstaclesLevel[this.level])) {
	    double d = Math.random() * 44.0D - 22.0D;
	    if (objects.activateObject(d,horizon, "Obstacle")) {
		this.obstacleCounter++; // add new obstacle to number of existing ones
	    }
	}
	// create new hearts if not enough
	if ((this.rounds % objects.creationDelay == 0) 
	    && (this.heartCounter <= this.maxHeartsLevel[this.level])) {
	    double d = Math.random() * 44.0D - 22.0D;
	    if (objects.activateObject(d,horizon, "Heart")) {
		this.heartCounter++; // add new heart to total
	    }
	}
	// tan of player's tilt angle (depends on x velocity)
	double angle = this.maxTilt * this.player.playerVelocity[0];
	double tan = Math.tan(angle);
	// tilting about (centerX,horizonY)
	// determine y pos of tilted horizon (one corner sinks, one rises)
	this.groundX[0] = 0;
	this.groundY[0] = (int) (tan*(double)this.width/2.0D) + this.horizonY;
	this.groundX[1] = this.width;
	this.groundY[1] = (int) ((-tan)*(double)this.width/2.0D) + this.horizonY;
	floaty1 = this.objects.getHead();
	while (floaty1 != null) { // transform objects to tilt with horizon
	    floaty1.transform(angle, this.centerX, this.horizonY);
	    floaty1 = floaty1.next;
	} 

	return collision;
    }
  
    void reset() {
	// clear stats
	clearObjects();
    	this.player.reset();
    	this.obstacleCounter = 0;
    	this.heartCounter = 0;
    	this.level = 0;
	this.rounds = 0;
	if (this.gameMode > 0) {
	    // demo -> set correct gameMode
	    this.gameMode = 1;
	} else {
	    // real game -> check whether continuing or starting over
    	    if (this.isContinue) {
    	        for (; this.player.savedScore >= this.clearScore[this.level]; this.level++); 
    	    }
    	    if (this.level > 0) {
    	        this.player.runningScore = this.clearScore[this.level - 1]; 
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
	    while (!moveObjects() && (thisThread == this.gameThread)) {
		prt();
		repaint();
	    }
	    // breaking from while means collision took place: decrease health
	    this.player.health--;
	    if (this.player.health < 0) {
		this.player.health = 0;
	    }
	    this.player.savedScore = this.player.runningScore;
	    this.gameMode = 4; // fallen slide mode
	    for (int i = 1; i < 50; i++) { // TODO set back to ~50
		moveObjects();
	    try {
		// pause before going from collision to demo
		Thread.currentThread().sleep(40L);
	    } catch (InterruptedException interruptedException) {
		System.out.println("exception in run(): " + interruptedException);
	    }

		//prt();
		repaint();
		//System.out.println("after hitting");
	    } 
	    if (this.player.runningScore > this.player.highScore) {
		this.player.highScore = this.player.savedScore;
	    }
	    // update scoreboard and health
	    this.parent.highScore.setNum(this.player.highScore);
	    this.parent.healthMeter.setHealth(this.player.health);
	    this.startFlag = false;
	    this.gameMode = 1;
	    try {
		// pause before going from collision to demo
		Thread.currentThread().sleep(1L);
	    } catch (InterruptedException interruptedException) {
		System.out.println("exception in run(): " + interruptedException);
	    }
	    while (true) {
		System.out.print("here");
		demo();
		repaint();
	    }
	}
    }

    // demo shown before game starts -> don't count score
    void demo() {
	this.player.playerVelocity[0] = 0;
	this.player.playerVelocity[1] = 0;
	moveObjects();
	this.rounds++;
	//prt();
	    try {
		// pause before going from collision to demo
		Thread.currentThread().sleep(40L);
	    } catch (InterruptedException interruptedException) {
		System.out.println("exception in run(): " + interruptedException);
	    }
    }
}
