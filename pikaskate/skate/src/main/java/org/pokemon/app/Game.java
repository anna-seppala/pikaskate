package org.pokemon.app;

import java.awt.event.*;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
  static int horizon = 26;
  boolean fff;
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
  int score;
  int score_;
  int hiscore;
  int Counter;
  int imgCounter; // toggle player movement images with this counter
  int imgTimeCounter; // track time to correctly show movement images
  int WCounter;
  double OX1;
  double OX2;
  double OVX;
  int Direction; // of what??
  int gameMode; // whether demoing or actually playing 1->demo, 0->real game
  boolean startFlag; // whether game has been started
  boolean isContinue;
  boolean registMode;
  int width;
  int height;
  int centerX;
  int centerY;
  
  Thread Game;
  Image myImg0;
  Image img;
  Image[] myImgs;
  
  Graphics2D gra;
  Graphics2D ThisGra;
  
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
  int damaged;
  
  // constructor
  public Game(MainApp paramMain) {
    //set container size and find center point
    this.width = 480;
    this.height = 300;
    this.centerX = this.width / 2;
    this.centerY = this.height / 2;

    this.fff = true;
    this.grX = new int[4];
    this.grY = new int[4];
    this.yy = _h_;
    this.obstacles = new Freeobj(64); // create obstacles
    this.startFlag = false;
    this.isContinue = false;
    this.registMode = false;
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
    setKeyBindings();
  }
  
  public void init() {

    this.img = createImage(this.width, this.height);
    this.gra = (Graphics2D) this.img.getGraphics();
    this.gra.setColor(new Color(48, 11, 142));
    this.gra.fillRect(0, 0, this.width, this.height);
    for (int i = 0; i < 75; i++) {
      sines[i] = Math.sin(3.14159D * i / 75.0D / 6.0D);
      cosines[i] = Math.cos(3.14159D * i / 75.0D / 6.0D);
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
	    System.out.println(e);
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
  
  public void stop() {
    if (this.Game != null)
      this.Game.stop(); 
    this.Game = null;
    this.startFlag = false;
    this.registMode = false;
    this.gameMode = 0;
  }
 
  // paint methods overridden: For what?
  public void paint(Graphics g) {
	Graphics2D g2d = (Graphics2D) g;
    if (this.registMode) {
	super.paint(g);
      g2d.setColor(Color.lightGray);
      g2d.fill3DRect(0, 0, this.width, this.height, true);
      g2d.setColor(Color.black);
      g2d.drawString("Wait a moment!!", this.centerX - 32, this.centerY + 8);
      return;
    } 
    if (this.img != null)
      g2d.drawImage(this.img, 0, 0, this); 
  }
 
//-------------------------------- Events start:


    private void setKeyBindings() { //WHEN_IN_FOCUSED_WINDOW
	// space key to start game from beginning
	this.getInputMap()
	    .put(KeyStroke.getKeyStroke("SPACE"), "spacePressAction");
	this.getActionMap()
	    .put("spacePressAction", spacePressAction);
	// c press to continue at reached level
	this.getInputMap()
	    .put(KeyStroke.getKeyStroke("C"), "cPressAction");
	this.getActionMap()
	    .put("cPressAction", cPressAction);
	// s press to see current ranking 
	this.getInputMap()
	    .put(KeyStroke.getKeyStroke("S"), "sPressAction");
	this.getActionMap()
	    .put("sPressAction", sPressAction);
	// ctrl+q press to quit game
	this.getInputMap()
	    .put(KeyStroke.getKeyStroke("control Q"), "qPressAction");
	this.getActionMap()
	    .put("qPressAction", qPressAction);

	// arrow key events
	for (int i = 0; i < commands.length; i++) { 
	    // arrow key presses
	    registerKeyboardAction(arrowPressAction, commands[i], KeyStroke
		.getKeyStroke(commands[i]), JComponent.WHEN_IN_FOCUSED_WINDOW);
	    // arrow key releases
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
		startGame(true);
	    }
        }
    };
    // pressing s takes user to ranking table
    Action sPressAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
	    System.out.println("hit S");
	    if (!startFlag && hiscore != 0) {
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
		stop();
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
      if (this.Game != null) {
        this.Game.stop();
        this.Game = null;
      } 
      this.startFlag = true;
      this.gameMode = 0;
      this.Game = new Thread(this);
      this.Game.start();
    } else {
	// if playNow == false -> play demo instead of real game
	if (this.gameMode == 0) {
	    if (this.Game != null) {
		this.Game.stop();
		this.Game = null;
	    } 
	    this.gameMode = 1;
	    this.Game = new Thread(this);
	    this.Game.start();
	}
    } 
  }
  
  void gotoRanking() {
    if (this.hiscore == 0)
      return; 
    stop();
    this.hiscore = 0;
    this.parent.highScore.setNum(this.hiscore);
    this.registMode = true;
    repaint();
  }
 
  // draw text if hit dustbin
  void putbomb() {
    if (this.damaged > 40)
      return; 
    if (this.damaged > 4) {
      Font font = new Font("TimesRoman", 1, 32);
      String str = "OOPS !!!";
      int i = this.gra.getFontMetrics(font).stringWidth(str);
      int j = this.centerX - i / 2;
      this.gra.setColor(Color.yellow);
      this.gra.setFont(font);
      this.gra.drawString(str, j, this.centerY - 20);
    } 
    this.damaged++;
  }
 
  // main logic of the game
    void prt() {
	// levels change based on score
	if (this.score > this.clearScore[this.level]) {
	    this.level++;
	    if (this.level > this.maxLevel) {
		this.level = this.maxLevel; // keep playing at max level
	    }
	    this.maxcount = this.maxcounts[this.level];
	} 
	this.imgTimeCounter++;
	if (this.damaged < 40 && this.gameMode == 0) {
	    int i;
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
	    } // if damaged < 40 and gameMode == 0
	    if (this.score < 200) {
		this.yy = _h_ - 10 + this.score / 20; 
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
	    if (this.damaged != 0) { // falling if damaged
		this.myImg0 = this.myImgs[12]; 
	    }
	    if (this.damaged == 0) {
		// no hits -> normal picture
		this.gra.drawImage(this.myImg0, new AffineTransform(
		    this.imageScaling,0,0,this.imageScaling,this.centerX-this.playerWidth/2,
		    this.height-this.playerHeight),this);
	    } else {
		if (this.damaged > 4) {
		    // draw image of falling
		    this.myImg0 = this.myImgs[13];
		}
		//this.gra.drawImage(this.myImg0, this.centerX - this.playerWidth, this.height - this.yy + 3 * this.damaged + 8, this);
		this.gra.drawImage(this.myImg0, new AffineTransform(
		    this.imageScaling,0,0,this.imageScaling,this.centerX-this.playerWidth/2,
		    this.height-this.playerHeight),this);
	    } 
	} 
	if (this.damaged > 0) {
	    putbomb(); 
	}
	this.ThisGra.drawImage(this.img, 0, 0, null);
	this.gra.setColor(this.bgColors[this.level]);//colour depends on level
	this.gra.fillRect(0, 0, this.width, this.height);
	if (this.scFlag && this.gameMode == 0) {
	    this.parent.currentScore.setNum(this.score);
	    this.parent.currentLevel.setNum(this.level);
	    this.scFlag = false;
	} else {
	    this.scFlag = true;
	}
	// compute current score
	if (this.damaged == 0) {
	    long l1;
	    this.score++;
	    if (this.prevTime != 0L) {
		l1 = 55L - System.currentTimeMillis() - this.prevTime;
	    } else {
		l1 = 0L;
	    } 
	    if (l1 < 0L) {
		l1 = 1L;
		if (l1 > -40L)
		    this.score += (int)((40L + l1) / 4L); 
	    } else {
		this.score += 5;
	    } 
	} 
	long l = 40L;
	if (true) {  //!isSpacePressed --> what does this do?
	    long l1 = this.prevTime + l - System.currentTimeMillis();
	    if (l1 <= 0L)
		l1 = 1L; 
	    try {
		Thread.currentThread().sleep(l1);
	    } catch (Exception exception) {}
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
	    if (this.vx > 0.0D)
		this.vx = 0.0D; 
	    } 
	    if (this.vx > 0.0D) {
		this.vx -= 0.025D;
		if (this.vx < 0.0D)
		    this.vx = 0.0D; 
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
        int yMin = this.height - this.yy;
        int yMax = yMin + 35;
        if (obstacle1.isCollision(xMin, xMax, yMin, yMax)) {
	    collision = true;
	    //TODO this won't work because hit obstacles disappear straght away
	    // Only change colour in play mode (not in demo)
	    obstacle1.setCollided(collision && (this.gameMode == 0));
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
      if (this.Head != null)
        this.Head.prev = obstacle1; 
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
    this.gra.setColor(new Color(230, 187, 196));
    this.gra.fillPolygon(this.grX, this.grY, 4);
    obstacle1 = this.Head;
    while (obstacle1 != null) {
      obstacle1.transform(d4, d3, this.centerX, this.centerY);
      obstacle1.fill(this.gra);
      obstacle1 = obstacle1.next;
    } 
    return collision;
  }
  
  void putExtra() {}
 
  // main state machine of game
  public void run() {
    this.ThisGra = (Graphics2D) getGraphics();
    System.gc();
    if (this.gameMode > 0) {
      demo();
      return;
    } 
    clearObstacle();
    this.damaged = 0;
    this.counter = 0;
    this.level = 0;
    this.score = 0;
    this.vx = 0.0D;
    if (this.isContinue) {
	for (; this.hiscore >= this.clearScore[this.level]; this.level++); 
    }
    if (this.level > 0) {
	this.score = this.clearScore[this.level - 1] - 1000; 
    }
    this.maxcount = this.maxcounts[this.level];
    while (!moveObstacle()) { // until collision happens
	prt();
    }
    this.score_ = this.score;
    this.damaged = 1;
    for (int i = 1; i < 24; i++) {
      moveObstacle();
      prt();
    } 
    if (this.score_ > this.hiscore) {
      this.hiscore = this.score_;
      //if (this.parent.userid != null && !this.parent.userid.equals("guest"))
      //  try {
      //    URL uRL = new URL("/applets/hiscore/hs_main.htmp?name=" + URLEncoder.encode(this.parent.userid) + "&high=" + this.hiscore + "&sorMax=" + '\n' + "&userfile=" + "pokemon");
      //    URLConnection uRLConnection = uRL.openConnection();
      //    int i = 2000;
      //    byte[] arrayOfByte = new byte[i];
      //    InputStream inputStream = uRLConnection.getInputStream();
      //    String str = "";
      //    while (true) {
      //      int j = inputStream.read(arrayOfByte);
      //      if (j != -1) {
      //        if (j > i)
      //          j = i; 
      //        str = str + new String(arrayOfByte, 0, 0, j);
      //        Thread.currentThread().yield();
      //        continue;
      //      } 
      //      break;
      //    } 
      //  } catch (Exception exception) {
      //    System.out.println("High Score write Error\n" + exception);
      //  }  
    } 
    this.parent.highScore.setNum(this.hiscore);
    this.startFlag = false;
    this.gameMode = 1;
    try {
      Thread.currentThread().sleep(3000L);
    } catch (InterruptedException interruptedException) {}
    demo();
  }

  // demo shown before game starts (movin dustbins without pikachu)
  void demo() {
    this.gameMode = 2;
    clearObstacle();
    this.damaged = 0;
    this.counter = 0;
    this.level = 0;
    this.score = 0;
    this.vx = 0.0D;
    this.maxcount = 5;
    Font font1 = new Font("TimesRoman", 1, 24);
    Font font2 = new Font("TimesRoman", 1, 12);
    String str1 = "Pikachu Skate Revived";
    int i = this.gra.getFontMetrics(font1).stringWidth(str1);
    int j = this.centerX - i / 2;
    int m = this.gra.getFontMetrics(font2).stringWidth(
	    this.parent.contMsg[this.parent.lang]);
    int n = this.centerX - m / 2;
    int k = this.gra.getFontMetrics(font2).stringWidth(
	    this.parent.toStartMsg[this.parent.lang]);
    int l = this.centerX - k / 2;
    while (true) {
      prt();
      moveObstacle();
      this.gra.setColor(Color.yellow);
      this.gra.setFont(font1);
      this.gra.drawString(str1, j, this.centerY - 20);
      this.gra.setFont(font2);
      this.gra.setColor(Color.black);
      this.gra.drawString(this.parent.toStartMsg[this.parent.lang], l, this.centerY + 32);
      //if (this.mouseX > m && this.mouseX < m + k && 
      //  this.mouseY > this.centerY + 74 && this.mouseY < this.centerY + 90) {
      //  this.gra.setColor(Color.blue);
      //  this.isInPage = true;
      //} else {
      //  this.isInPage = false;
      //} 
      if (this.hiscore >= this.clearScore[0]) {
        this.gra.setColor(Color.black);
        this.gra.drawString(this.parent.contMsg[this.parent.lang], n, this.centerY + 64);
      } 
      if (!this.isFocus) {
        this.gra.setColor(Color.yellow);
        this.gra.drawString(this.parent.clickMsg[this.parent.lang], this.centerX - 20, this.centerY);
      } 
      this.score = 0;
    } 
  }
}
