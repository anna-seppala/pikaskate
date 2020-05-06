package org.pokemon.app;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;

public class ScoreBoard extends Canvas {
    int scoreValue;
    String label;
  
    public ScoreBoard(String paramString) {
	this.scoreValue = 0;
	this.label = paramString;
	this.setSize(new Dimension(200,28));
    }
  
    public void paint(Graphics g) {
	super.paint(g);
	Graphics2D g2d = (Graphics2D) g;
	g2d.setColor(Color.BLUE);
	g2d.drawString(" " + this.label + this.scoreValue, 4, 18);
    }
  
    public void setNum(int paramInt) {
	this.scoreValue = paramInt;
	Graphics2D g2d = (Graphics2D) getGraphics();
	g2d.clearRect(0, 0, getWidth(), getHeight());
	paint(g2d);
    }
}// ScoreBoard

