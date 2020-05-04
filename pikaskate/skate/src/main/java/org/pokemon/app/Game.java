package org.pokemon.app;

import javax.swing.JPanel;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Game extends Canvas implements Runnable {
  static int horizon = 26;
  boolean fff;
  static double[] si = new double[75];
  static double[] co = new double[75];
  int[] grX;
  int[] grY;
  static int _h_ = 76;
  int yy;
  Obstacle Head;
  Freeobj obstacles;
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
  int gameMode;
  boolean startFlag;
  boolean isContinue;
  boolean registMode;
  int width;
  int height;
  int centerX;
  int centerY;
  int mouseX;
  int mouseY;
  boolean isInPage;
  
  Thread Game;
  Image myImg0;
  Image img;
  Image[] myImgs;
  
  Graphics2D gra;
  Graphics2D ThisGra;
  MediaTracker tracker;
  
  boolean isLoaded;
  int round;
  int[] clearScore;
  Color[] bkcolors;
  int[] maxcounts;
  boolean rFlag;
  boolean lFlag;
  boolean spcFlag;
  MainApp parent;
  boolean isFocus;
  boolean isFocus2;
  boolean scFlag;
  long prevTime;
  int damaged;
  
  public Game(MainApp paramMain) {
    this.fff = true;
    this.grX = new int[4];
    this.grY = new int[4];
    this.yy = _h_;
    this.obstacles = new Freeobj(64);
    this.mywidth = 0.403D;
    this.startFlag = false;
    this.isContinue = false;
    this.registMode = false;
    this.isInPage = false;
    this.isLoaded = false;
    this.clearScore = new int[] { 8000, 8200, 8400, 12000, 12200, 25000, 25200, 25400, 40000, 9999999 };
    this.bkcolors = 
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
    this.spcFlag = false;
    this.isFocus = true;
    this.isFocus2 = true;
    this.scFlag = true;
    this.parent = paramMain;
  }
  
  public void init() {
    this.width = 320;
    this.height = 200;
    this.centerX = this.width / 2;
    this.centerY = this.height / 2;
    this.img = createImage(this.width, this.height);
    this.gra = (Graphics2D) this.img.getGraphics();
    this.gra.setColor(new Color(48, 11, 142));
    this.gra.fillRect(0, 0, this.width, this.height);
    for (byte b = 0; b < 75; b++) {
      si[b] = Math.sin(3.14159D * b / 75.0D / 6.0D);
      co[b] = Math.cos(3.14159D * b / 75.0D / 6.0D);
    } 
    this.grX[2] = this.width;
    this.grY[2] = this.height;
    this.grX[3] = 0;
    this.grY[3] = this.height;
    this.mywidth2 = (int)(this.mywidth * 120.0D / (1.0D + Obstacle.T));
    // open images and add to media tracker
    myImgs = new Image[14];
    this.tracker = new MediaTracker(this.parent);
    for (int i=0; i<this.myImgs.length; i++) {
	try {
	    this.myImgs[i] = ImageIO.read(getClass().getClassLoader()
		    .getResource("img/img"+String.valueOf(1)+".png"));
	    this.tracker.addImage(this.myImgs[i], 0);
	} catch (IOException e) {
	}
    }
    this.tracker.checkAll(true);
  }
  
  public void stop() {
    if (this.Game != null)
      this.Game.stop(); 
    this.Game = null;
    this.startFlag = false;
    this.registMode = false;
    this.gameMode = 0;
  }
  
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
  
  public boolean mouseDown(Event paramEvent, int paramInt1, int paramInt2) {
    if (paramEvent.modifiers == 4) {
      this.rFlag = true;
      this.lFlag = false;
    } else if (paramEvent.modifiers == 0) {
      this.rFlag = false;
      this.lFlag = true;
    } 
    if (this.startFlag)
      return false; 
    if (!this.isFocus2) {
      this.isFocus2 = true;
      return false;
    } 
    this.isContinue = false;
    startGame(true);
    return false;
  }
  
  public boolean mouseUp(Event paramEvent, int paramInt1, int paramInt2) {
    this.rFlag = false;
    this.lFlag = false;
    return false;
  }
  
  public boolean mouseMove(Event paramEvent, int paramInt1, int paramInt2) {
    this.mouseX = paramInt1;
    this.mouseY = paramInt2;
    return true;
  }
  
  public boolean keyDown(Event paramEvent, int paramInt) {
    if (paramInt == 1007 || paramInt == 108)
      this.rFlag = true; 
    if (paramInt == 1006 || paramInt == 106)
      this.lFlag = true; 
    if (paramInt == 97)
      this.spcFlag = true; 
    if (!this.startFlag && (paramInt == 32 || paramInt == 99 || paramInt == 67)) {
      this.isContinue = false;
      if (paramInt != 32)
        this.isContinue = true; 
      startGame(true);
    } 
    if (!this.startFlag && (paramInt == 115 || paramInt == 83) && this.hiscore != 0)
      gotoRank(); 
    return false;
  }
  
  public boolean keyUp(Event paramEvent, int paramInt) {
    if (paramInt == 1007 || paramInt == 108)
      this.rFlag = false; 
    if (paramInt == 1006 || paramInt == 106)
      this.lFlag = false; 
    if (paramInt == 97)
      this.spcFlag = false; 
    return false;
  }
  
  public boolean gotFocus(Event paramEvent, Object paramObject) {
    this.isFocus = this.isFocus2 = true;
    return true;
  }
  
  public boolean lostFocus(Event paramEvent, Object paramObject) {
    this.isFocus = this.isFocus2 = false;
    return true;
  }
  
  void clearObstacle() {
    Obstacle obstacle1 = this.Head;
    while (obstacle1 != null) {
      Obstacle obstacle2 = obstacle1.next;
      this.obstacles.deleteObj(obstacle1);
      obstacle1 = obstacle2;
    } 
    this.Head = null;
  }
  
  public void startGame(boolean paramBoolean) {
    if (this.startFlag)
      return; 
    if (paramBoolean) {
      if (this.Game != null) {
        this.Game.stop();
        this.Game = null;
      } 
      this.startFlag = true;
      this.gameMode = 0;
      this.Game = new Thread(this);
      this.Game.start();
      return;
    } 
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
  
  void gotoRank() {
    if (this.hiscore == 0)
      return; 
    stop();
    this.hiscore = 0;
    this.parent.scoreHigh.setNum(this.hiscore);
    this.registMode = true;
    repaint();
  }
  
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
  
  void prt() {
    if (this.score > this.clearScore[this.round]) {
      this.round++;
      if (this.round > 9)
        this.round = 9; 
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
      if (this.isLoaded) {
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
          this.gra.drawImage(this.myImg0, this.centerX - this.mywidth2, this.height - this.yy, this);
        } else {
          if (this.damaged > 4)
            this.myImg0 = this.myImgs[13]; 
          this.gra.drawImage(this.myImg0, this.centerX - this.mywidth2, this.height - this.yy + 3 * this.damaged + 8, this);
        } 
      } else {
        if (this.tracker.checkAll())
          this.isLoaded = true; 
        this.gra.setColor(Color.blue);
        this.gra.fillRect(this.centerX - this.mywidth2, this.height - this.yy, this.mywidth2 * 2, 16);
      } 
    } 
    if (this.damaged > 0)
      putbomb(); 
    this.ThisGra.drawImage(this.img, 0, 0, null);
    this.gra.setColor(this.bkcolors[this.round]);
    this.gra.fillRect(0, 0, this.width, this.height);
    if (this.scFlag && this.gameMode == 0) {
      this.parent.scoreWin.setNum(this.score);
      this.scFlag = false;
    } else {
      this.scFlag = true;
    } 
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
    if (!this.spcFlag) {
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
  
  boolean moveObstacle() {
    boolean bool = false;
    double d5 = 0.8D;
    if (this.round >= 8)
      d5 = 0.8D; 
    Obstacle obstacle1 = this.Head;
    while (obstacle1 != null) {
      Obstacle obstacle2 = obstacle1.next;
      for (byte b = 0; b < Obstacle.lgt; b++) {
        obstacle1.z[b] = obstacle1.z[b] - 1.0D;
        obstacle1.x[b] = obstacle1.x[b] + this.vx;
      } 
      if (obstacle1.z[0] <= 1.3D) {
        int j = this.centerX - this.mywidth2;
        int k = j + 29;
        int m = this.height - this.yy;
        int n = m + 35;
        if (obstacle1.ut(j, k, m, n))
          bool = true; 
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
    double d3 = si[i];
    double d4 = co[i];
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
    return bool;
  }
  
  void putExtra() {}
  
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
    if (this.isContinue)
      for (; this.hiscore >= this.clearScore[this.round]; this.round++); 
    if (this.round > 0)
      this.score = this.clearScore[this.round - 1] - 1000; 
    this.maxcount = this.maxcounts[this.round];
    while (!moveObstacle())
      prt(); 
    this.score_ = this.score;
    this.damaged = 1;
    for (byte b = 1; b < 24; b++) {
      moveObstacle();
      prt();
    } 
    if (this.score_ > this.hiscore) {
      this.hiscore = this.score_;
      if (this.parent.userid != null && !this.parent.userid.equals("guest"))
        try {
          URL uRL = new URL("/applets/hiscore/hs_main.htmp?name=" + URLEncoder.encode(this.parent.userid) + "&high=" + this.hiscore + "&sorMax=" + '\n' + "&userfile=" + "pokemon");
          URLConnection uRLConnection = uRL.openConnection();
          int i = 2000;
          byte[] arrayOfByte = new byte[i];
          InputStream inputStream = uRLConnection.getInputStream();
          String str = "";
          while (true) {
            int j = inputStream.read(arrayOfByte);
            if (j != -1) {
              if (j > i)
                j = i; 
              str = str + new String(arrayOfByte, 0, 0, j);
              Thread.currentThread().yield();
              continue;
            } 
            break;
          } 
        } catch (Exception exception) {
          System.out.println("High Score write Error\n" + exception);
        }  
    } 
    this.parent.scoreHigh.setNum(this.hiscore);
    this.startFlag = false;
    this.gameMode = 1;
    try {
      Thread.currentThread().sleep(3000L);
    } catch (InterruptedException interruptedException) {}
    demo();
  }
  
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
      if (this.mouseX > m && this.mouseX < m + k && 
        this.mouseY > this.centerY + 74 && this.mouseY < this.centerY + 90) {
        this.gra.setColor(Color.blue);
        this.isInPage = true;
      } else {
        this.isInPage = false;
      } 
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