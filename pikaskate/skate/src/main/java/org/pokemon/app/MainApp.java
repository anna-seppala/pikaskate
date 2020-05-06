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

public class MainApp extends JFrame implements WindowListener {
    Game game;
    ScoreBoard scoreWin;
    ScoreBoard scoreHigh;
    String userid;
    int lang = 0; //TODO implement language options
    String[] contMsg = { "Push [C] key to start from this stage!!", " " };
    String[] toStartMsg = { "To start, hit the SPACE bar", " " };
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


