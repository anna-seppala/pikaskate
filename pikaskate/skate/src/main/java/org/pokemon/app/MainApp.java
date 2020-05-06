package org.pokemon.app;

import java.awt.event.*;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;

public class MainApp extends JFrame implements WindowListener {
    Game game;
    ScoreBoard currentScore;
    ScoreBoard highScore;
    ScoreBoard currentLevel;
    String userid;
    int lang = 0; //TODO implement language options
    String[] contMsg = { "Hit [C] to continue from last stage", " " };
    String[] toStartMsg = { "Hit SPACE to start", " " };
    String[] clickMsg = { " ", " " };
    // set images now before any threads are started (could lead to error?)
    ImageIcon heart1 = new ImageIcon(getClass().getClassLoader().getResource("img/heart4.png"));
    ImageIcon heart2 = new ImageIcon(getClass().getClassLoader().getResource("img/heart4.png"));
    ImageIcon heart3 = new ImageIcon(getClass().getClassLoader().getResource("img/heart5.png"));
    JLabel imageLabel1;
    JLabel imageLabel2;
    JLabel imageLabel3;
    
    public MainApp () {
	this.setSize(520, 500);
      	this.setVisible(true);
      	this.addWindowListener(this);

      	//this.repaint();
      	this.userid = "Sk8erB0i";
      	this.setLayout(new BorderLayout());
      	this.setForeground(Color.white);
      	this.setBackground(new Color(80, 7, 37));
      	this.currentScore = new ScoreBoard("Score: ");
      	this.currentScore.setForeground(Color.white);

	this.currentLevel = new ScoreBoard("Level: ");
      	this.currentLevel.setForeground(Color.white);

      	JPanel panel1 = new JPanel();
      	panel1.setLayout(new FlowLayout(0));
      	panel1.setForeground(Color.white);
	
	
	imageLabel1 = new JLabel(heart1);
	imageLabel2 = new JLabel(heart2);
	imageLabel3 = new JLabel(heart3);
      	panel1.add(this.currentScore);
	panel1.add(this.currentLevel);
	panel1.add(this.imageLabel1);
	panel1.add(this.imageLabel2);
	panel1.add(this.imageLabel3);
      	this.add(panel1, BorderLayout.PAGE_START);

      	JPanel panel = new JPanel();
      	panel.setLayout(new FlowLayout(0));
      	panel.setForeground(Color.white);
      	panel.add(new JLabel("  "));
	//panel.add(heart3);
      	this.add(panel, BorderLayout.LINE_START);


      	this.game = new Game(this);
      	this.add(this.game, BorderLayout.CENTER);
      	this.highScore = new ScoreBoard("Your high-score: ");
      	this.highScore.setForeground(Color.white);
      	JPanel panel2 = new JPanel();
      	panel2.setLayout(new FlowLayout(0));
      	panel2.setForeground(Color.white);
      	panel2.add(this.highScore);
      	this.add(panel2, BorderLayout.PAGE_END);

      	this.game.init();
      	this.game.requestFocus();
      	this.start();
    }
 
      public static void main(String[] args) {
          new MainApp();
      }

    public void start() {
	this.game.startGame(false); 
    }
    
    public void stop() {
        this.game.stop();
    }

    public void quit() {
	this.stop();
	dispose();
	System.exit(0); // normal exit of programme
    }

    public void windowClosing(WindowEvent e)
    {
	this.quit();
    }
    public void windowOpened(WindowEvent e){}// simply add definition if no code to be added.
    public void windowIconified(WindowEvent e){}
    public void windowClosed(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}

}


