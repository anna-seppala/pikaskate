package org.pokemon.app;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public class HealthMeter extends Canvas {
    int health;
    int maxHealth;
    Image heartFull;
    Image heartEmpty;
    int[] heartSize = {30,30};
    double imageScaling;

    public HealthMeter(int maxHealth) {
	this.health = maxHealth;
	this.maxHealth = maxHealth;
	try {
	    this.heartFull = ImageIO.read(getClass().getClassLoader()
		    .getResource("img/heart4.png" ));
	} catch (IOException e) {
	    System.out.println("error opening heartFull image: " + e);
	}
	try {
	    this.heartEmpty = ImageIO.read(getClass().getClassLoader()
		    .getResource("img/heart5.png" ));
	} catch (IOException e) {
	    System.out.println("error opening heartEmpty image: " + e);
	}
	this.setSize(new Dimension(this.maxHealth* (this.heartSize[0] + 5), this.heartSize[1]));
	int sizeX = this.heartFull.getWidth(null);
	this.imageScaling = ((double) this.heartSize[0])/((double) sizeX);

    }
  
    public void paint(Graphics g) {
	super.paint(g);
	Graphics2D g2d = (Graphics2D) g;
	// paint full hearts (based on this.health)
	for (int i=0; i< this.health; i++) {
	    g2d.drawImage(this.heartFull, new AffineTransform(this.imageScaling,
			0,0,this.imageScaling,i*(this.heartSize[0]+5),0),this);
	}
	// paint the rest empty
	for (int i=0; i< this.maxHealth-this.health; i++) {
	    g2d.drawImage(this.heartEmpty, new AffineTransform(this.imageScaling,
			0,0,this.imageScaling,(this.health+i)*(this.heartSize[0]+5),0),this);
	}
    }
  
    public void setHealth(int currentHealth) {
	// make sure no negative healt or too much health given
	if (currentHealth <=0) {
	    this.health = 0;
	} else if (currentHealth >= this.maxHealth) {
	    this.health = this.maxHealth;
	} else {
	    this.health = currentHealth;
	}
	repaint();
    }
}// HealthMeter

