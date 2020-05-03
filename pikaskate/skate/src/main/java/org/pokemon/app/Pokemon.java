package org.pokemon.app;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;

public class Pokemon extends Applet implements Runnable {
  MainGame game;
  
  Label hiScoreLabel;
  
  ScoreBoard scoreWin;
  
  ScoreBoard scoreHigh;
  
  static boolean isWindows;
  
  static boolean isChecked;
  
  int lang;
  
  String userid;
  
  String[] contMsg = { "Push [C] key to start from this stage!!", " " };
  
  String[] toStartMsg = { "To start, click here or hit the SPACE bar", " " };
  
  String[] clickMsg = { " ", " " };
  
  public void init() {
    System.out.println("Jar version");
    this.userid = getParameter("userid");
    setLayout(new BorderLayout());
    setForeground(Color.white);
    setBackground(new Color(80, 7, 37));
    this.scoreWin = new ScoreBoard("Score: ");
    this.scoreWin.resize(128, 28);
    this.scoreWin.setForeground(Color.white);
    Panel panel1 = new Panel();
    panel1.setLayout(new FlowLayout(0));
    panel1.setForeground(Color.white);
    panel1.add(this.scoreWin);
    add("North", panel1);
    this.game = new MainGame(this);
    this.game.resize(320, 200);
    add("Center", this.game);
    this.scoreHigh = new ScoreBoard("Your Hi-score: ");
    this.scoreHigh.resize(128, 28);
    this.scoreHigh.setForeground(Color.white);
    Panel panel2 = new Panel();
    panel2.setLayout(new FlowLayout(0));
    panel2.setForeground(Color.white);
    panel2.add(this.scoreHigh);
    add("South", panel2);
    this.game.init();
    this.game.requestFocus();
    (new Thread(this)).start();
  }
  
  public void start() { if (isChecked)
      this.game.startGame(false);  }
  
  public void stop() { this.game.stop(); }
  
  public void run() {
    this.game.startGame(false);
    isChecked = true;
  }
  
  public boolean action(Event paramEvent, Object paramObject) {
    if (paramEvent.target instanceof java.awt.Button)
      this.game.gotoRank(); 
    return true;
  }
  
  public boolean keyDown(Event paramEvent, int paramInt) {
    this.game.keyDown(paramEvent, paramInt);
    return true;
  }
  
  public boolean keyUp(Event paramEvent, int paramInt) {
    this.game.keyUp(paramEvent, paramInt);
    return true;
  }
}


