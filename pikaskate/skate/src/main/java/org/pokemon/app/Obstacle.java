package org.pokemon.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class Obstacle {
  static double T = 0.6D;
  public static int lgt = 14; //number of point in dustbin polygon
  int[] polyX = new int[lgt]; // x-position of polygon points
  int[] polyY = new int[lgt]; // y position of plygon points
  int[] polyX1 = new int[6];  // Additional shape to create shadows
  int[] polyY1 = new int[6];  //       -- .. --
  public double[] x = new double[lgt];
  public double[] y = new double[lgt];
  public double[] z = new double[lgt];
  boolean collided = false; // track whether this object has collided with player;
  public Obstacle next;
  public Obstacle prev; 
  
  void init(double paramDouble1, double paramDouble2) {
    this.x[0] = paramDouble1 - 1.02D;
    this.y[0] = 2.7800000000000002D;
    this.x[1] = paramDouble1 - 1.16D;
    this.y[1] = -0.3800000000000001D;
    this.x[2] = paramDouble1 - 1.3D;
    this.y[2] = this.y[1];
    this.x[3] = this.x[2];
    this.y[3] = -0.7D;
    this.x[4] = paramDouble1;
    this.y[4] = -1.0000000000000002D;
    this.x[5] = paramDouble1 + 1.3D;
    this.y[5] = this.y[3];
    this.x[6] = this.x[5];
    this.y[6] = this.y[1];
    this.x[7] = paramDouble1 + 1.16D;
    this.y[7] = this.y[1];
    this.x[8] = paramDouble1 + 1.02D;
    this.y[8] = this.y[0];
    this.x[9] = paramDouble1 + 0.5D;
    this.y[9] = 2.92D;
    this.x[10] = paramDouble1 - 0.5D;
    this.y[10] = this.y[9];
    this.x[11] = paramDouble1 + 1.14D;
    this.y[11] = -0.040000000000000036D;
    this.x[12] = this.x[9];
    this.y[12] = this.y[12];
    this.x[13] = this.x[10];
    this.y[13] = this.y[12];
    for (int i = 0; i < lgt; i++)
      this.z[i] = paramDouble2; 
  }
  
  public void transform(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2) {
    double d = 120.0D / (1.0D + T * this.z[0]);
    for (int i = 0; i < lgt; i++) {
      double d1 = paramDouble1 * this.x[i] + paramDouble2 * this.y[i];
      double d2 = -paramDouble2 * this.x[i] + paramDouble1 * this.y[i];
      this.polyX[i] = (int)(d1 * d) + paramInt1;
      this.polyY[i] = (int)(d2 * d) + paramInt2;
    } 
  }
 
  // set a point of polyXY1 to polyXY
  void connect(int paramInt1, int paramInt2) {
    this.polyX1[paramInt1] = this.polyX[paramInt2];
    this.polyY1[paramInt1] = this.polyY[paramInt2];
  }
    
    // set this.collided to true if this obstacle collided with palyer
    public void setCollided(boolean collision) {
	this.collided = collision;
    }


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
    g2d.fillPolygon(this.polyX1, this.polyY1, 6);
    connect(0, 2);
    connect(1, 3);
    connect(2, 5);
    connect(3, 6);
    g2d.setColor(obsColor);
    g2d.fillPolygon(this.polyX1, this.polyY1, 4);
    connect(0, 10);
    connect(1, 13);
    connect(2, 12);
    connect(3, 9);
    g2d.setColor(obsColor);
    g2d.fillPolygon(this.polyX1, this.polyY1, 4);
  }
  
  // check if one of the edges of player rectangle within obstacle--> collision
  public boolean isCollision(int xPos1, int xPos2, int yPos1, int yPos2) {
    Polygon polygon = new Polygon(this.polyX, this.polyY, lgt);
    if (polygon.contains(xPos1, yPos1) || polygon.contains(xPos1, yPos2) || 
      polygon.contains(xPos2, yPos1) || polygon.contains(xPos2, yPos2)) {
	return true;
      }
    return false;
  }

} // Obstacle class

