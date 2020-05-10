package org.pokemon.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

public class Heart extends FloatyObject {

    public Heart(int id) {
      super(20, id);
    }
  
  void init(double xPos, double zPos) {
    this.x[0] = xPos; // bottom tip of heart
    this.y[0] = 1D;
    this.x[1] = xPos - 0.8D; // go clockwise along outer rim of heart
    this.y[1] = 0.5D;
    this.x[2] = xPos - 1.38D;
    this.y[2] = -0.13D;
    this.x[3] = xPos - 1.31D;
    this.y[3] = -0.82D;
    this.x[4] = xPos - 0.72D;
    this.y[4] = -1.26D;
    this.x[5] = xPos;
    this.y[5] = -0.8D;
    this.x[6] = xPos + 0.72D;
    this.y[6] = -1.26D;
    this.x[7] = xPos + 1.31D;
    this.y[7] = -0.82D;
    this.x[8] = xPos + 1.38D;
    this.y[8] = -0.13D;
    this.x[9] = xPos + 0.8D;
    this.y[9] = 0.5D;

    this.x[10] = xPos; // bottom tip of heart inner rim
    this.y[10] = 0.6D;
    this.x[11] = xPos -0.47D; // go clockwise
    this.y[11] = 0.3D;
    this.x[12] = xPos - 0.83D;
    this.y[12] = -0.06D;
    this.x[13] = xPos - 0.79D;
    this.y[13] = -0.48D;
    this.x[14] = xPos -0.43D; 
    this.y[14] = -0.72;
    this.x[15] = xPos;
    this.y[15] = -0.46;
    this.x[16] = xPos + 0.43D;
    this.y[16] = -0.72D;
    this.x[17] = xPos + 0.79D;
    this.y[17] = -0.48D;
    this.x[18] = xPos + 0.83D;
    this.y[18] = -0.06D;
    this.x[19] = xPos + 0.47D;
    this.y[19] = -0.3D;
    this.z = zPos; 
    this.active = true;
    for (int i=0; i<this.nodes; i++) {
	this.y[i] += 1D;
    }
  }
  
    // paint method to render obstacle
    public void fill(Graphics g) {

	Color heartColorLight = new Color(227, 20, 103);
    	Color heartColorDark = new Color(187, 20, 73);
    	Color heartColor = new Color(235, 120, 150);
    	if (this.collided) {
	    heartColorLight = new Color(240, 150, 70);
	    heartColorDark = new Color(140, 50, 5);
    	    heartColor = new Color(180, 80, 25);
    	}
    	Graphics2D g2d = (Graphics2D) g;
    	g2d.setColor(heartColorLight);
    	g2d.fillPolygon(this.polyX, this.polyY, 18);
    	//connect(0, 0);
    	//connect(1, 1);
    	//connect(2, 7);
    	//connect(3, 11);
    	//connect(4, 13);
    	//connect(5, 10);
    	//g2d.setColor(heartColorDark);
    	//g2d.fillPolygon(this.polyX1, this.polyY1, 6);
    	//connect(0, 2);
    	//connect(1, 3);
    	//connect(2, 5);
    	//connect(3, 6);
    	//g2d.setColor(heartColor);
    	//g2d.fillPolygon(this.polyX1, this.polyY1, 4);
    	//connect(0, 10);
    	//connect(1, 13);
    	//connect(2, 12);
    	//connect(3, 9);
    	//g2d.setColor(heartColor);
    	//g2d.fillPolygon(this.polyX1, this.polyY1, 4);
    }
  
} // Heart class

