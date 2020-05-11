package org.pokemon.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

public class Obstacle extends FloatyObject{
    static double T = 0.6D;

    public Obstacle(int id) {
	super(14, id);
    }
  
    void init(double xPos, double zPos) {
	this.x[0] = xPos - 1.02D;
    	this.y[0] = 2.7800000000000002D;
    	this.x[1] = xPos - 1.16D;
    	this.y[1] = -0.3800000000000001D;
    	this.x[2] = xPos - 1.3D;
    	this.y[2] = this.y[1];
    	this.x[3] = this.x[2];
    	this.y[3] = -0.7D;
    	this.x[4] = xPos;
    	this.y[4] = -1.0000000000000002D;
    	this.x[5] = xPos + 1.3D;
    	this.y[5] = this.y[3];
    	this.x[6] = this.x[5];
    	this.y[6] = this.y[1];
    	this.x[7] = xPos + 1.16D;
    	this.y[7] = this.y[1];
    	this.x[8] = xPos + 1.02D;
    	this.y[8] = this.y[0];
    	this.x[9] = xPos + 0.5D;
    	this.y[9] = 2.92D;
    	this.x[10] = xPos - 0.5D;
    	this.y[10] = this.y[9];
    	this.x[11] = xPos + 1.14D;
    	this.y[11] = -0.040000000000000036D;
    	this.x[12] = this.x[9];
    	this.y[12] = this.y[12];
    	this.x[13] = this.x[10];
    	this.y[13] = this.y[12];
    	this.z = zPos; 
    	this.active = true;
    }
  
    // paint method to render obstacle
    public void fill(Graphics g) {

	Color obsColorLight = new Color(71, 214, 158);
    	Color obsColorDark = new Color(8, 76, 9);
    	Color obsColor = new Color(9, 128, 86);
    	if (this.collided) {
	    obsColorLight = new Color(240, 150, 70);
	    obsColorDark = new Color(140, 50, 5);
    	    obsColor = new Color(180, 80, 25);
    	}
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.setColor(obsColorLight);
    	g2d.fillPolygon(this.polyX, this.polyY, 11);
    	connect(0, 0);
    	connect(1, 1);
    	connect(2, 7);
    	connect(3, 11);
    	connect(4, 13);
    	connect(5, 10);
    	g2d.setColor(obsColorDark);
    	g2d.fillPolygon(this.polyHelpX, this.polyHelpY, 6);
    	connect(0, 2);
    	connect(1, 3);
    	connect(2, 5);
    	connect(3, 6);
    	g2d.setColor(obsColor);
    	g2d.fillPolygon(this.polyHelpX, this.polyHelpY, 4);
    	connect(0, 10);
    	connect(1, 13);
    	connect(2, 12);
    	connect(3, 9);
    	g2d.setColor(obsColor);
    	g2d.fillPolygon(this.polyHelpX, this.polyHelpY, 4);
    }

} // Obstacle class

