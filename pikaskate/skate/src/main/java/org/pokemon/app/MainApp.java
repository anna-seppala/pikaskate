package org.pokemon.app;

import java.awt.event.*;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

public class MainApp extends JFrame implements WindowListener {
  Game game;
  ScoreBoard scoreWin;
  ScoreBoard scoreHigh;
  static boolean isWindows;
  static boolean isChecked = true;
  int lang;
  String userid;
  String[] contMsg = { "Push [C] key to start from this stage!!", " " };
  String[] toStartMsg = { "To start, click here or hit the SPACE bar", " " };
  String[] clickMsg = { " ", " " };
  
  public MainApp () {
    this.setSize(520, 500);
    this.setVisible(true);
    this.addWindowListener(this);

    //this.repaint();
    this.userid = "Sk8erB0i";
    this.setLayout(new BorderLayout());
    this.setForeground(Color.white);
    this.setBackground(new Color(80, 7, 37));
    this.scoreWin = new ScoreBoard("Score: ");
    this.scoreWin.resize(150, 28);
    this.scoreWin.setForeground(Color.white);
    JPanel panel1 = new JPanel();
    panel1.setLayout(new FlowLayout(0));
    panel1.setForeground(Color.white);
    panel1.add(this.scoreWin);
    this.add(panel1, BorderLayout.PAGE_START);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(0));
    panel.setForeground(Color.white);
    panel.add(new JLabel("  "));
    this.add(panel, BorderLayout.LINE_START);


    this.game = new Game(this);
    this.add(this.game, BorderLayout.CENTER);
    this.scoreHigh = new ScoreBoard("Your high-score: ");
    this.scoreHigh.resize(150, 28);
    this.scoreHigh.setForeground(Color.white);
    JPanel panel2 = new JPanel();
    panel2.setLayout(new FlowLayout(0));
    panel2.setForeground(Color.white);
    panel2.add(this.scoreHigh);
    this.add(panel2, BorderLayout.PAGE_END);

    this.game.init();
    this.game.requestFocus();
    this.start();
  }
 
    public static void main(String[] args) {
	new MainApp();
    }

  public void start() {
      if (isChecked) {
	this.game.startGame(false); 
      }
  }
  
  public void stop() {
      this.game.stop();
  }
  
  public void run() {
    this.game.startGame(false);
    isChecked = true;
  }
  
//  public boolean action(Event paramEvent, Object paramObject) {
//    if (paramEvent.target instanceof java.awt.Button)
//      this.game.gotoRank(); 
//    return true;
//  }
//  
//  public boolean keyDown(Event e, int paramInt) {
//    this.game.keyDown(e, paramInt);
//    return true;
//  }
//  
//  public boolean keyUp(Event paramEvent, int paramInt) {
//    this.game.keyUp(paramEvent, paramInt);
//    return true;
//  }
  public void windowClosing(WindowEvent e)
  {
    dispose();
    System.exit(0);// normal exit of program
  }
  public void windowOpened(WindowEvent e){}// simply add definition if no code to be added.
  public void windowIconified(WindowEvent e){}
  public void windowClosed(WindowEvent e){}
  public void windowDeiconified(WindowEvent e){}
  public void windowActivated(WindowEvent e){}
  public void windowDeactivated(WindowEvent e){}

}


