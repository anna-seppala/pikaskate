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
  static int horizon = 26; // z distance from player
  boolean fff;
  double pi = 3.14159D;
  static double[] sines = new double[75];
  static double[] cosines = new double[75];
  int[] grX;
  int[] grY;
  static int _h_ = 76;
  int yy;
  Obstacle Head; // first obstacle in double-linked list
  Freeobj obstacles; // double-linked list of obstacles
  double vx;
  int playerWidth;
  int playerHeight;
  double imageScaling;  // make sure player image always same size
  int[] imageSize;  // original size of player images in width,height
  
  int counter;
  int maxcount;
  int runningScore; // used to get score when game running
  int savedScore; // save score here after collision (score changes in demo)
  int highScore;
  int Counter;
  int imgCounter; // toggle player movement images with this counter
  int imgTimeCounter; // track time to correctly show movement images
  int WCounter;
  double OX1;
  double OX2;
  double OVX;
  int Direction; // of what??
  int gameMode; // whether demoing or actually playing  0:real game 1:demo 2:start screen 3: ranking
  boolean startFlag; // whether game has been started
  boolean isContinue;
  int width;
  int height;
  int centerX;
  int centerY;
  
  private volatile Thread gameThread;
  Image myImg0;
  Image img;
  Image[] myImgs;
  
  int level; // game becomes harder by levels
  int maxLevel = 9;
  int[] clearScore; // array showing which number of points starts new level
  Color[] bgColors; //colour of background (changes by level)
  int[] maxcounts;
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
    this.width = 480;
    this.height = 300;
    this.setBounds(0,0,this.width,this.height);
    this.centerX = this.width / 2;
    this.centerY = this.height / 2;

    this.fff = true;
    this.grX = new int[4];
    this.grY = new int[4];
    this.yy = _h_;
    this.obstacles = new Freeobj(64); // create obstacles
    this.startFlag = false;
    this.isContinue = false;
				// 8000
    this.clearScore = new int[] { 800, 8200, 8400, 12000, 12200, 25000, 25200, 25400, 40000, 9999999 };
    this.bgColors = 
      new Color[] { new Color(48, 11, 142), 
        new Color(48, 11, 160), 
        new Color(48, 11, 172), 
        new Color(48, 11, 182), 
        new Color(48, 11, 182), 
        new Color(48, 11, 192), 
        new Color(48, 11, 202), 
        new Color(48, 11, 212), 
        new Color(48, 11, 222), 
        new Color(48, 11, 242) };
    this.maxcounts = new int[] {4, 4, 4, 3, 3, 2, 2, 2, 1, 1, 1, 1 };
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
    this.img = createImage(this.width, this.height);
    for (int i = 0; i < 75; i++) {
      sines[i] = Math.sin(this.pi * i / 75.0D / 6.0D);
      cosines[i] = Math.cos(this.pi * i / 75.0D / 6.0D);
    } 
    this.grX[2] = this.width;
    this.grY[2] = this.height;
    this.grX[3] = 0;
    this.grY[3] = this.height;
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
    this.playerWidth = 50;
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
		g2d.drawImage(this.img, 0, 0, null);
		g2d.setColor(this.bgColors[this.level]);//colour depends on level
		g2d.fillRect(0, 0, this.width, this.height);

		g2d.setColor(new Color(230, 187, 196));
		g2d.fillPolygon(this.grX, this.grY, 4);
		Obstacle obstacle1 = this.Head;
		while (obstacle1 != null) {
		    if (obstacle1.isInside(0,0,this.width, this.height)) {
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
	    case 1: // demo mode -> paint scene only
		g2d.drawImage(this.img, 0, 0, null);
		g2d.setColor(this.bgColors[this.level]);//colour depends on level
		g2d.fillRect(0, 0, this.width, this.height);

		g2d.setColor(new Color(230, 187, 196));
		g2d.fillPolygon(this.grX, this.grY, 4);
		Obstacle obstacle2 = this.Head;
		while (obstacle2 != null) {
		    if (obstacle2.isInside(0,0,this.width, this.height)) {
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
	    default: // error screen
		g2d.drawImage(this.img, 0, 0, this); 
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
    void clearObstacle() {
	Obstacle obstacle1 = this.Head;
	while (obstacle1 != null) {
	    Obstacle obstacle2 = obstacle1.next;
	    this.obstacles.deleteObj(obstacle1);
	    obstacle1 = obstacle2;
	} 
	this.Head = null;
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
	// levels change based on score
	if (this.runningScore > this.clearScore[this.level] && this.gameMode == 0) {
	    this.level++;
	    if (this.level > this.maxLevel) {
		this.level = this.maxLevel; // keep playing at max level
	    }
	    this.maxcount = this.maxcounts[this.level];
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
	    if (this.vx > 0.2D) { // left
		this.myImg0 = this.myImgs[10]; 
	    }
	    if (this.vx > 0.4D) { // sharp left
		this.myImg0 = this.myImgs[11]; 
	    }
	    if (this.vx < -0.2D) { // right
		this.myImg0 = this.myImgs[8]; 
	    }
	    if (this.vx < -0.4D) { // sharp right
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
	    if (this.rFlag)
		this.vx -= 0.11D; 
	    if (this.lFlag)
		this.vx += 0.11D; 
	    if (this.vx < -0.6D)
		this.vx = -0.6D; 
	    if (this.vx > 0.6D)
		this.vx = 0.6D; 
	} 
	if (!this.lFlag && !this.rFlag) {
	    if (this.vx < 0.0D) {
		this.vx += 0.025D;
		if (this.vx > 0.0D) {
		    this.vx = 0.0D; 
		}
	    } 
	    if (this.vx > 0.0D) {
		this.vx -= 0.025D;
		if (this.vx < 0.0D) {
		    this.vx = 0.0D; 
		}
	    } 
	} 
    }

  // define how obstacles behave in the game
  // returns true if player hit obstacle, false otherwise
  boolean moveObstacle() {
    boolean collision = false;
    double d5 = 0.8D; // what is this for??
    if (this.level >= this.maxLevel-1) {
	d5 = 0.8D; 
    }
    Obstacle obstacle1 = this.Head;
    while (obstacle1 != null) {
      Obstacle obstacle2 = obstacle1.next;
      for (int i = 0; i < Obstacle.lgt; i++) {
        obstacle1.z[i] -= 1.0D; // speed at which obstacles move??
        obstacle1.x[i] += this.vx;
      } 
      // is obstacle at the front (risk of collision)?
      if (obstacle1.z[0] <= 1.3D) {
        int xMin = this.centerX - this.playerWidth/2;
        int xMax = xMin + this.playerWidth/2;
	//TODO: set yposition right!
        int yMin = this.height - this.playerHeight; //-this.yy // take velocity into account?
        int yMax = this.height;
        if (obstacle1.isCollision(xMin, xMax, yMin, yMax)) {
	    collision = true;
	    //TODO this won't work because hit obstacles disappear straght away
	    // Only change colour in play mode (not in demo)
	    if (this.gameMode == 0) {
		obstacle1.setCollided(collision);
		this.damaged++;
	    }

	}
	// obstacle moves out of sight -> delete + update linked list
        if (obstacle1.prev != null) {
          obstacle1.prev.next = obstacle1.next;
	}
        if (obstacle1.next != null) {
          obstacle1.next.prev = obstacle1.prev; 
	}
        this.obstacles.deleteObj(obstacle1);
        obstacle1 = obstacle2;
      } 
      obstacle1 = obstacle2;
    } 
    this.counter++;
    if (this.counter >= this.maxcount) {
      double d;
      this.counter = 0;
      obstacle1 = this.obstacles.getObj();
      if (this.Head != null) {
	this.Head.prev = obstacle1; 
      }
      obstacle1.next = this.Head;
      obstacle1.prev = null;
      this.Head = obstacle1;
      if (this.level >= this.maxLevel-1) {
        this.Counter--;
        this.OX1 += this.vx;
        this.OX2 += this.vx;
        if (this.level >= this.maxLevel && this.Counter % 13 < 7) {
          d5 = 0.8D;
          d = Math.random() * 32.0D - 16.0D;
          if (d < this.OX2 && d > this.OX1) {
            d5 = 0.8D;
            if (Math.random() > 0.5D) {
              d = this.OX1;
            } else {
              d = this.OX2;
            } 
          } 
        } else if (this.Counter % 2 == 0) {
          d = this.OX1;
        } else {
          d = this.OX2;
        } 
        if (this.OX2 - this.OX1 > 9.0D) {
          this.OX1 += 0.6D;
          this.OX2 -= 0.6D;
          if (this.OX2 - this.OX1 > 10.0D)
            d5 = 0.8D; 
        } else if (this.OX1 > 18.0D) {
          this.OX2 -= 0.6D;
          this.OX1 -= 0.6D;
        } else if (this.OX2 < -18.0D) {
          this.OX2 += 0.6D;
          this.OX1 += 0.6D;
        } else {
          if (this.Counter < 0) {
            this.Direction = -this.Direction;
            this.Counter += 2 * (int)(Math.random() * 8.0D + 4.0D);
          } 
          if (this.Direction > 0) {
            this.OVX += 0.125D;
          } else {
            this.OVX -= 0.125D;
          } 
          if (this.OVX > 0.7D)
            this.OVX = 0.7D; 
          if (this.OVX < -0.7D)
            this.OVX = -0.7D; 
          this.OX1 += this.OVX;
          this.OX2 += this.OVX;
        } 
      } else {
        d = Math.random() * 32.0D - 16.0D;
      }
      // add new obstacle to game scene at location d
      obstacle1.init(d, horizon);
    } 
    int i = (int)(Math.abs(this.vx) * 80.0D);
    double d3 = sines[i];
    double d4 = cosines[i];
    if (this.vx > 0.0D)
      d3 = -d3; 
    double d2 = 120.0D / (1.0D + Obstacle.T * horizon);
    double d1 = -d3 * -26.0D + 2.0D;
    this.grX[0] = 0;
    this.grY[0] = (int)(d1 * d2) + this.centerY;
    d1 = -d3 * 26.0D + 2.0D;
    this.grX[1] = this.width;
    this.grY[1] = (int)(d1 * d2) + this.centerY;
    obstacle1 = this.Head;
    while (obstacle1 != null) {
      obstacle1.transform(d4, d3, this.centerX, this.centerY);
      obstacle1 = obstacle1.next;
    } 

    return collision;
  }
  
    void putExtra() {}

    void reset() {
	// clear stats
	clearObstacle();
    	this.damaged = 0;
    	this.counter = 0;
    	this.level = 0;
    	this.runningScore = 0;
    	this.vx = 0.0D;
	if (this.gameMode > 0) {
	    // demo -> set correct gameMode
	    this.gameMode = 1;
    	    this.maxcount = 5;
	} else {
	    // real game -> check whether continuing or starting over
    	    if (this.isContinue) {
    	        for (; this.savedScore >= this.clearScore[this.level]; this.level++); 
    	    }
    	    if (this.level > 0) {
    	        this.runningScore = this.clearScore[this.level - 1]; 
    	    }
    	    this.maxcount = this.maxcounts[this.level];
	}
    }


    public void run() {
	Thread thisThread = Thread.currentThread(); // added to try get rid of Thread.stop
	System.gc(); // garbage collector (not guaranteed to work)
	reset();
	if (this.gameMode > 0) {
	    while(true) {
		demo();
		repaint();
	    }
	} else { 
	    while (!moveObstacle() && (thisThread == this.gameThread)) { // until collision happens
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
		moveObstacle();
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
	moveObstacle();
	prt();
	this.runningScore = 0;
    }
}
