package org.pokemon.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Obstacle {
  static double T = 0.6D;
  
  public static int lgt = 14;
  
  int[] polyX = new int[lgt];
  
  int[] polyY = new int[lgt];
  
  int[] polyX1 = new int[6];
  
  int[] polyY1 = new int[6];
  
  public double[] x = new double[lgt];
  
  public double[] y = new double[lgt];
  
  public double[] z = new double[lgt];
  
  static Color c0 = new Color(71, 214, 158);
  
  static Color c1 = new Color(8, 76, 9);
  
  static Color c2 = new Color(9, 128, 86);
  
  static Color c3 = new Color(10, 128, 87);
  
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
    for (byte b = 0; b < lgt; b++)
      this.z[b] = paramDouble2; 
  }
  
  public void transform(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2) {
    double d = 120.0D / (1.0D + T * this.z[0]);
    for (byte b = 0; b < lgt; b++) {
      double d1 = paramDouble1 * this.x[b] + paramDouble2 * this.y[b];
      double d2 = -paramDouble2 * this.x[b] + paramDouble1 * this.y[b];
      this.polyX[b] = (int)(d1 * d) + paramInt1;
      this.polyY[b] = (int)(d2 * d) + paramInt2;
    } 
  }
  
  void c(int paramInt1, int paramInt2) {
    this.polyX1[paramInt1] = this.polyX[paramInt2];
    this.polyY1[paramInt1] = this.polyY[paramInt2];
  }
  
  public void fill(Graphics paramGraphics) {
    paramGraphics.setColor(c0);
    paramGraphics.fillPolygon(this.polyX, this.polyY, 11);
    c(0, 0);
    c(1, 1);
    c(2, 7);
    c(3, 11);
    c(4, 13);
    c(5, 10);
    paramGraphics.setColor(c1);
    paramGraphics.fillPolygon(this.polyX1, this.polyY1, 6);
    c(0, 2);
    c(1, 3);
    c(2, 5);
    c(3, 6);
    paramGraphics.setColor(c2);
    paramGraphics.fillPolygon(this.polyX1, this.polyY1, 4);
    c(0, 10);
    c(1, 13);
    c(2, 12);
    c(3, 9);
    paramGraphics.setColor(c3);
    paramGraphics.fillPolygon(this.polyX1, this.polyY1, 4);
  }
  
  public boolean ut(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Polygon polygon = new Polygon(this.polyX, this.polyY, lgt);
    if (polygon.inside(paramInt1, paramInt3) || 
      polygon.inside(paramInt1, paramInt4) || 
      polygon.inside(paramInt2, paramInt3) || 
      polygon.inside(paramInt2, paramInt4))
      return true; 
    return false;
  }
}

