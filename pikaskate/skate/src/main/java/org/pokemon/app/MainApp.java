package org.pokemon.app;

import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Font;

public class MainApp extends JFrame implements WindowListener {
    Game game;
    ScoreBoard currentScore;
    ScoreBoard highScore;
    ScoreBoard currentLevel;
    HealthMeter healthMeter;
    int maxHealth = 3;
    String userid;
    int lang = 0; //TODO implement language options
    // Text and formatting:
    Font titleFont = new Font("TimesRoman", 1, 24);
    Font normalFont = new Font("TimesRoman", 1, 12);
    String titleMsg = "Pikachu Skate Revived";
    String[] contMsg = { "Hit [C] to continue from last stage", " " };
    String[] toStartMsg = { "Hit SPACE to start", " " };
    String[] rankingMsg = { "Hall of Fame", " " };
    // set images now before any threads are started (could lead to error?)
    
    public MainApp () {
	this.setTitle("Pikachu Skate Revived");
	this.setSize(520, 500); //TODO use this.pack() instead
      	this.setVisible(true);
      	this.addWindowListener(this);
      	this.userid = "Sk8erB0i";
      	this.setLayout(new BorderLayout());

      	//this.setForeground(Color.white);
      	//this.setBackground(new Color(80, 7, 37));
	//Set current score, current level and health meter to top of frame
      	this.currentScore = new ScoreBoard("Score: ");
      	this.currentScore.setForeground(Color.white);
	this.currentLevel = new ScoreBoard("Level: ");
      	this.currentLevel.setForeground(Color.white);
	this.healthMeter = new HealthMeter(this.maxHealth);
      	this.healthMeter.setForeground(Color.white);
      	JPanel panel1 = new JPanel();
      	panel1.setLayout(new FlowLayout(0));
      	panel1.setForeground(Color.white);
      	panel1.add(this.currentScore);
	panel1.add(this.currentLevel);
	panel1.add(this.healthMeter);
      	this.add(panel1, BorderLayout.PAGE_START);

	// add extra space to side with this
	// TODO: make nicer
      	JPanel panel = new JPanel();
      	panel.setLayout(new FlowLayout(0));
      	panel.setForeground(Color.white);
      	panel.add(new JLabel("  "));
	this.add(panel, BorderLayout.LINE_START);
      
	// add high score to bottom of frame
	JPanel panel2 = new JPanel();
      	panel2.setLayout(new FlowLayout(0));
      	panel2.setForeground(Color.white);
      	this.highScore = new ScoreBoard("Your high-score: ");
      	this.highScore.setForeground(Color.white);
	panel2.add(this.highScore);
      	this.add(panel2, BorderLayout.PAGE_END);

	// add game panel into the middle
      	this.game = new Game(this);
      	this.add(this.game, BorderLayout.CENTER);
      	this.game.init();
      	this.game.requestFocus();
      	this.start();
    }
 
    public static void main(String[] args) {
	// GUI creation needs to be inside event dispatch thread to
	// prevent race conditions between different components/threads
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		new MainApp();
	    }
	});
      }

    public void start() {
	this.game.startGame(false); 
	this.game.gameMode = 2;
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


