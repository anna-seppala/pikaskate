package org.pokemon.app;

import java.awt.event.*;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
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
  double mywidth;
  int mywidth2;
  int counter;
  int maxcount;
  int score;
  int score_;
  int hiscore;
  int Counter;
  int MCounter;
  int ECounter;
  int PCounter;
  int mode;
  int WCounter;
  double OX1;
  double OX2;
  double OVX;
  int Direction;
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
  
  int round;
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
    this.mywidth = 0.04D;//0.403D;
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
    this.maxcounts = new int[] { 
        4, 4, 4, 3, 3, 2, 2, 2, 1, 1, 
        1, 1 };
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
    // open images and add to media tracker
    myImgs = new Image[14];
    for (int i=0; i<this.myImgs.length; i++) {
	try {
	    this.myImgs[i] = ImageIO.read(getClass().getClassLoader()
		    .getResource("img/img"+String.valueOf(i+1)+".png" ));
	} catch (IOException e) {
	    System.out.println(e);
	}
    //System.out.println(myImgs[0].getWidth() + "x" + myImgs[0].getHeight());
    this.mywidth2 = (int)(this.mywidth * 130.0D / (1.0D + Obstacle.T));//120.0D
    }
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
    this.parent.scoreHigh.setNum(this.hiscore);
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
 
  // main logic of the game??
  void prt() {
    // rounds changed based on score
    if (this.score > this.clearScore[this.round]) {
      this.round++;
      if (this.round > 9) {
        this.round = 9; // why??
      }
      this.maxcount = this.maxcounts[this.round];
    } 
    this.MCounter++;
    if (this.damaged < 40 && this.gameMode == 0) {
      int i;
      this.myImg0 = this.myImgs[0];
      switch (this.PCounter) {
        case 0:
          if (this.MCounter % 24 > 18) {
            this.myImg0 = this.myImgs[1];
            this.PCounter = 1;
            this.ECounter = 0;
          } 
          break;
        case 1:
          this.myImg0 = this.myImgs[1];
          i = (int)Math.abs(Math.random() * 5.0D);
          if (++this.ECounter % 12 > i) {
            this.WCounter = 0;
            this.mode = (int)Math.abs(Math.random() * 100.0D);
            if (this.mode > 30) {
              this.mode = 1;
              this.PCounter = 7;
              break;
            } 
            this.mode = 0;
            this.PCounter = 2;
            break;
          } 
          this.PCounter = 0;
          break;
        case 2:
          this.myImg0 = this.myImgs[2];
          if (++this.WCounter > 2) {
            this.PCounter = (this.mode == 0) ? 3 : 0;
            this.WCounter = 0;
          } 
          break;
        case 3:
          this.myImg0 = this.myImgs[3];
          if (++this.WCounter >= 2) {
            this.PCounter = (this.mode == 0) ? 4 : 2;
            this.ECounter = 0;
            this.WCounter = 0;
          } 
          break;
        case 4:
          this.myImg0 = this.myImgs[4];
          if (this.ECounter++ > 4)
            this.PCounter = (this.mode == 0) ? 5 : 3; 
          break;
        case 5:
          this.myImg0 = this.myImgs[5];
          this.PCounter = (this.mode == 0) ? 6 : 4;
          this.WCounter = 0;
          break;
        case 6:
          this.myImg0 = this.myImgs[6];
          if (++this.WCounter >= 2) {
            this.PCounter = (this.mode == 0) ? 7 : 5;
            this.WCounter = 0;
          } 
          break;
        case 7:
          this.myImg0 = this.myImgs[7];
          if (++this.WCounter > 2) {
            this.PCounter = (this.mode == 0) ? 0 : 6;
            this.WCounter = 0;
          } 
          break;
      } 
      if (this.score < 200)
        this.yy = _h_ - 10 + this.score / 20; 
      if (true) { //left from media tracker
	if (this.vx > 0.2D)
          this.myImg0 = this.myImgs[10]; 
        if (this.vx > 0.4D)
          this.myImg0 = this.myImgs[11]; 
        if (this.vx < -0.2D)
          this.myImg0 = this.myImgs[8]; 
        if (this.vx < -0.4D)
          this.myImg0 = this.myImgs[9]; 
        if (this.damaged != 0)
          this.myImg0 = this.myImgs[12]; 
	if (this.damaged == 0) {
	    // no hits -> normal picture
	    //this.gra.drawImage(this.myImg0, this.centerX - this.mywidth2, this.height - this.yy, this);
	    this.gra.drawImage(this.myImg0, new AffineTransform(0.5,0,0,0.5,this.centerX,this.centerY),this);
        } else {
          if (this.damaged > 4)
	    // draw image of falling
            this.myImg0 = this.myImgs[13]; 
//          this.gra.drawImage(this.myImg0, this.centerX - this.mywidth2, this.height - this.yy + 3 * this.damaged + 8, this);
          this.gra.drawImage(this.myImg0, new AffineTransform(0.5,0,0,0.5,this.centerX,this.centerY), this);
        } 
      } else {
        this.gra.setColor(Color.blue);
        this.gra.fillRect(this.centerX - this.mywidth2, this.height - this.yy, this.mywidth2 * 2, 16);
      } 
    } 
    if (this.damaged > 0) {
	putbomb(); 
    }
    this.ThisGra.drawImage(this.img, 0, 0, null);
    this.gra.setColor(this.bgColors[this.round]);
    this.gra.fillRect(0, 0, this.width, this.height);
    if (this.scFlag && this.gameMode == 0) {
      this.parent.scoreWin.setNum(this.score);
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
    double d5 = 0.8D;
    if (this.round >= 8) {
	d5 = 0.8D; 
    }
    Obstacle obstacle1 = this.Head;
    while (obstacle1 != null) {
      Obstacle obstacle2 = obstacle1.next;
      for (int i = 0; i < Obstacle.lgt; i++) {
        obstacle1.z[i] -= 1.0D;
        obstacle1.x[i] += this.vx;
      } 
      if (obstacle1.z[0] <= 1.3D) {
        int xMin = this.centerX - this.mywidth2;
        int xMax = xMin + 29;
        int yMin = this.height - this.yy;
        int yMax = yMin + 35;
        if (obstacle1.isCollision(xMin, xMax, yMin, yMax)) {
	    collision = true;
	    //TODO make colour change show but not last (rotated obstacles)
	    // Only change colour in play mode (not in demo)
	    obstacle1.setCollided(collision && (this.gameMode == 0));
	}
        if (obstacle1.prev != null)
          obstacle1.prev.next = obstacle1.next; 
        if (obstacle1.next != null)
          obstacle1.next.prev = obstacle1.prev; 
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
      if (this.round >= 8) {
        this.Counter--;
        this.OX1 += this.vx;
        this.OX2 += this.vx;
        if (this.round >= 9 && this.Counter % 13 < 7) {
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
    this.round = 0;
    this.score = 0;
    this.vx = 0.0D;
    if (this.isContinue) {
	for (; this.hiscore >= this.clearScore[this.round]; this.round++); 
    }
    if (this.round > 0) {
	this.score = this.clearScore[this.round - 1] - 1000; 
    }
    this.maxcount = this.maxcounts[this.round];
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
    this.parent.scoreHigh.setNum(this.hiscore);
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
    this.round = 0;
    this.score = 0;
    this.vx = 0.0D;
    this.maxcount = 5;
    Font font1 = new Font("TimesRoman", 1, 24);
    Font font2 = new Font("TimesRoman", 1, 12);
    String str1 = "Click Here!";
    int i = this.gra.getFontMetrics(font1).stringWidth(str1);
    int j = this.centerX - i / 2;
    String str2 = "";
    int k = this.gra.getFontMetrics(font2).stringWidth(str2);
    int m = this.centerX - k / 2;
    while (true) {
      prt();
      moveObstacle();
      this.gra.setColor(Color.yellow);
      this.gra.setFont(font1);
      this.gra.drawString(str1, j, this.centerY - 20);
      this.gra.setFont(font2);
      this.gra.setColor(Color.black);
      this.gra.drawString(this.parent.toStartMsg[this.parent.lang], 46, this.centerY + 32);
      //if (this.mouseX > m && this.mouseX < m + k && 
      //  this.mouseY > this.centerY + 74 && this.mouseY < this.centerY + 90) {
      //  this.gra.setColor(Color.blue);
      //  this.isInPage = true;
      //} else {
      //  this.isInPage = false;
      //} 
      this.gra.drawString(str2, m, this.centerY + 90);
      if (this.hiscore >= this.clearScore[0]) {
        this.gra.setColor(Color.black);
        this.gra.drawString(this.parent.contMsg[this.parent.lang], 
            this.centerX - 108, this.centerY + 64);
      } 
      if (!this.isFocus) {
        this.gra.setColor(Color.yellow);
        this.gra.drawString(this.parent.clickMsg[this.parent.lang], this.centerX - 20, this.centerY);
      } 
      this.score = 0;
    } 
  }
}
