package org.pokemon.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

public abstract class FloatyObject {
    int nodes; //number of point in dustbin polygon
    int[] polyX; // x-position of polygon points
    int[] polyY; // y position of plygon points
    int[] polyHelpX1 = new int[6];  // Additional shape to create shadows
    int[] polyHelpY1 = new int[6];  //       -- .. --
    public double[] x; // these hold the orig non-transformed
    public double[] y; // polygon points
    public double z; // distance from player
    boolean collided = false; // track whether this object has collided with player;
    public FloatyObject next;
    public FloatyObject prev; 
    boolean active = false; // is obstacle on scene or not
    int id;
 
    public FloatyObject (int nodes, int id) {
	this.nodes = nodes;
	this.polyX = new int[nodes];
	this.polyY = new int[nodes];
	this.x = new double[nodes];
	this.y = new double[nodes];
	this.id = id;
    }

    abstract void init(double xPos, double zPos);
      
    public void transform(double angle, int pivotPointX, int pivotPointY) {
	double sine = Math.sin(angle);
	double cosine = Math.cos(angle);
	//double d = 120.0D / (1.0D + T*this.z[0]);
	double scale = 150.0D / (1.0D + this.z);
	for (int i = 0; i < nodes; i++) {
	    double d1 = cosine * this.x[i] + sine * this.y[i];
	    double d2 = -sine * this.x[i] + cosine * this.y[i];
	    this.polyX[i] = (int)(d1 * scale) + pivotPointX;
	    this.polyY[i] = (int)(d2 * scale) + pivotPointY;
	} 
    }
 
  // set a point of polyXY1 to polyXY
  void connect(int polyHelpIdx, int polyIdx) {
    this.polyHelpX1[polyHelpIdx] = this.polyX[polyIdx];
    this.polyHelpY1[polyHelpIdx] = this.polyY[polyIdx];
  }
    
    // set this.collided to true if this obstacle collided with palyer
    public void setCollided(boolean collision) {
	this.collided = collision;
    }

    // paint method to render obstacle
    public abstract void fill(Graphics g);
  
    // check if one of the edges of player rectangle within obstacle--> collision
    public boolean isCollision(int xPos1, int xPos2, int yPos1, int yPos2) {
	Polygon polygon = new Polygon(this.polyX, this.polyY, this.nodes);
	if (polygon.contains(xPos1, yPos1) || polygon.contains(xPos1, yPos2) || 
	polygon.contains(xPos2, yPos1) || polygon.contains(xPos2, yPos2)) {
	    return true;
	}
	return false;
    }
    
    // check if obstacle outside of a rectangle (outside of containing Jpanel)
    // TODO ugly solution, there has to be a better way to confine objects to jpanel
    public boolean isInside(int xUpperLeft, int yUpperLeft, int width, int height) {
	Rectangle rectangle = new Rectangle(xUpperLeft, yUpperLeft, width, height);
	int outPoints = 0;
	for (int i = 0; i<this.nodes; i++) {
	    if (!rectangle.contains(this.polyX[i], this.polyY[i])) {
		outPoints++;
	    }
	} // if around half of obstacle out of JPanel, not inside
	if (outPoints > (this.nodes/2 - 1)) {
	    return false;
	}
	return true;
    }

    public boolean isActive() {
	return this.active;
    }

    public void activate() {
	this.active = true;
    }

    public void deactivate() {
	this.active = false;
	this.collided = false;
    }

} // FloatyObject class

