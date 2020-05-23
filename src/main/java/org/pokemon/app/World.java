package org.pokemon.app;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;

public class World {
    int gameWidth;//size of parent game panel
    int gameHeight;
    int goalPosX; // where goal image painted on game panel
    int goalPosY;
    int cloud1PosX; // where cloud1 image painted on game panel
    int cloud1PosY;
    int cloud1Width;
    int cloud2PosX; // where cloud2 image painted on game panel
    int cloud2PosY;
    int cloud2Width;
    double[] goalVelocity; // in world frame (background img rising from ground)
    int cloudCounter; // used to determine speed of clouds
    double imageScaling;  // make sure goal image always same size
    int[] groundX; // define polygon representing ground X
    int[] groundY; // define polygon representing ground Y
    Color[] skyColors;
    Color groundColor;
    
    int imgCounter; // toggle player movement images with this counter
    int imgTimeCounter; // track time to correctly show movement images

    Image goalImg; // image currently used to paint player
    Image[] cloudImgs; // all player images to choose from

    // gameWidth, gameHeight: size of parent game panel
    public World(int gameWidth, int gameHeight) {
	this.gameWidth = gameWidth;
	this.gameHeight = gameHeight;
	this.goalVelocity = new double[]{0,-1,0}; // only moving up
	// lower left & right corners (2,3) constant, upper ones (0,1) change
	this.groundX = new int[4];
	this.groundY = new int[4];
	this.groundX[2] = this.gameWidth;
	this.groundY[2] = this.gameHeight;
	this.groundX[3] = 0;
	this.groundY[3] = this.gameHeight;

	// set colours
	this.skyColors = new Color[] { new Color(48, 11, 142), 
        new Color(48, 11, 160), new Color(48, 11, 172), 
        new Color(48, 11, 182), new Color(48, 11, 182), 
        new Color(48, 11, 192), new Color(48, 11, 202), 
        new Color(48, 11, 212), new Color(48, 11, 222), 
        new Color(48, 11, 242) };
	this.groundColor = new Color(230, 187, 196);

	// open images
	this.cloudImgs = new Image[2];
	for (int i=0; i<this.cloudImgs.length; i++) {
	    try {
		this.cloudImgs[i] = ImageIO.read(getClass().getClassLoader()
			.getResource("img/cloud"+String.valueOf(i+1)+".png" ));
	    } catch (IOException e) {
		System.out.println("error when loading images: " +e);
	    }
	}
	this.cloud1Width = this.cloudImgs[0].getWidth(null);
	this.cloud2Width = this.cloudImgs[1].getWidth(null);
	try {
	    this.goalImg = ImageIO.read(getClass().getClassLoader()
	    	.getResource("img/mountain1.png" ));
	} catch (IOException e) {
	    System.out.println("error when loading images: " +e);
	}
	// assuming all images have same size:
	int goalWidth = this.goalImg.getWidth(null);
	this.imageScaling = ((double) this.gameWidth)/((double) goalWidth);
	
	this.goalPosX = 0;
	this.goalPosY = this.gameHeight;
	this.cloud1PosX = -this.cloud1Width;
	this.cloud2PosX = this.gameWidth;
    }

    public void paint(Graphics2D g2d, int level) {
	this.goalPosY += this.goalVelocity[1];
	if (this.goalPosY < 20) {
	    this.goalPosY = 20;
	}
	if (this.cloudCounter % 3 == 0) {
	    this.cloud1PosX++;
	    this.cloud2PosX--;
	}
	this.cloudCounter++;
	if (this.cloud1PosX == this.gameWidth) {
	    this.cloud1PosX = -this.cloud1Width;
	}
	if (this.cloud2PosX == -this.cloud2Width) {
	    this.cloud2PosX = this.gameWidth;
	}
	g2d.setColor(this.skyColors[level]);//colour depends on level
	g2d.fillRect(0, 0, this.gameWidth, this.gameHeight);
	//g2d.drawImage(this.goalImg, new AffineTransform(
	//    this.imageScaling,0,0,this.imageScaling,this.goalPosX,
	//    this.goalPosY),null);
	g2d.drawImage(this.cloudImgs[0], new AffineTransform(
	    this.imageScaling,0,0,this.imageScaling,this.cloud1PosX,
	    0),null);
	g2d.drawImage(this.cloudImgs[1], new AffineTransform(
	    this.imageScaling,0,0,this.imageScaling,this.cloud2PosX,
	    50),null);
	g2d.setColor(this.groundColor);
	g2d.fillPolygon(this.groundX, this.groundY, 4);

    }

} // Player class
