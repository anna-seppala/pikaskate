package org.pokemon.app;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;

public class Player {
    int playerWidth;
    int playerHeight;
    int paintPosX; // where player image painted on game panel
    int paintPosY;
    double[] playerVelocity; // in world frame
    double xSpeedIncr; // player speed gain in x when pushing arrow keys
    double xSpeedMax; // player max speed in x
    double xSpeedDecay; // player speed decay when not pressing arrow key
    double imageScaling;  // make sure player image always same size
    int[] imageSize;  // original size of player images in width,height
    
    long prevTime;
    int runningScore; // used to get score when game running
    int savedScore; //put score here after collision (score changes even in demo)
    int highScore;
    int imgCounter; // toggle player movement images with this counter
    int imgTimeCounter; // track time to correctly show movement images

    Image myImg0; // image currently used to paint player
    Image[] myImgs; // all player images to choose from
    int damaged; // TODO join damaged and health?
    int maxHealth;
    int health;

    // maxHealth: maximum number of hearts a player has
    // playerX: x position of player on game panel
    // playerY: y position of player on game panel
    public Player(int maxHealth, int playerX, int playerY) {
	this.maxHealth = maxHealth;
	this.health = maxHealth;
	this.playerWidth = 50; // used to detect collision and scale player icon
	this.playerVelocity = new double[]{0,0,1}; // only moving in z (towards horizon)
	this.xSpeedIncr = 0.11;
	this.xSpeedMax = 0.6;
	this.xSpeedDecay = 0.025;

	// open images
	myImgs = new Image[14];
	for (int i=0; i<this.myImgs.length; i++) {
	    try {
		this.myImgs[i] = ImageIO.read(getClass().getClassLoader()
			.getResource("img/img"+String.valueOf(i+1)+".png" ));
	    } catch (IOException e) {
		System.out.println("error in init() when loading images: " +e);
	    }
	}
	// assuming all images have same size:
	this.imageSize = new int[2]; 
	this.imageSize[0] = this.myImgs[0].getWidth(null);
	this.imageSize[1] = this.myImgs[0].getHeight(null);
	this.imageScaling = ((double) this.playerWidth)/((double) this.imageSize[0]);
	this.playerHeight = (int) (this.imageScaling * ((double) this.imageSize[1]));
	
	this.paintPosX = playerX - this.playerWidth/2;
	this.paintPosY = playerY - this.playerHeight;
    }

    public void paint(Graphics2D g2d) {
	this.chooseImage();
	if (this.damaged == 0) {
	    // no hits -> normal paint
	    g2d.drawImage(this.myImg0, new AffineTransform(
		this.imageScaling,0,0,this.imageScaling,this.paintPosX,
		this.paintPosY),null);
	} else {
	    // hit -> player slips away from image
	    //this.gra.drawImage(this.myImg0, this.centerX - this.playerWidth, this.height - this.yy + 3 * this.damaged + 8, this);
	    g2d.drawImage(this.myImg0, new AffineTransform(
		this.imageScaling,0,0,this.imageScaling,this.paintPosX,
		this.paintPosY),null);

	}
    }

    public void chooseImage() {
	this.imgTimeCounter++;
	if (this.damaged == 0) {
	    this.myImg0 = this.myImgs[0];
	    switch (this.imgCounter) {
		case 0:
		    if ((Math.random()*100 < 5) && this.imgTimeCounter > 19) { // randomly time jumps
			this.imgCounter = 2;
			this.imgTimeCounter = 0;
		    } else if (this.imgTimeCounter > 20) {
			this.imgCounter = 1;
			this.imgTimeCounter = 0;
		    } 
		    break;
		case 1:
		    // crouch
		    this.myImg0 = this.myImgs[1];
		    if (this.imgTimeCounter > 6) {
			this.imgCounter = 0;
		    }
		    break;
		case 2:
		    // turning without jump
		    this.myImg0 = this.myImgs[2];
		    if (this.imgTimeCounter > 2 ) {
			this.imgCounter = 3;
			this.imgTimeCounter = 0;
		    }
		    break;
		case 3:
		    // look towards camera
		    this.myImg0 = this.myImgs[3];
		    if (this.imgTimeCounter > 10 ) {
			this.imgCounter = 7;
			this.imgTimeCounter = 0;
		    }
		    break;
		case 4:
		    // not in use!
		    // lean forwards/break
		    this.myImg0 = this.myImgs[4];
		    break;
		case 5:
		    // not in use!
		    // look towards camera (duplicate)
		    this.myImg0 = this.myImgs[5];
		    break;
		case 6:
		    // not in use!
		    // jump
		    this.myImg0 = this.myImgs[6];
		    break;
		case 7:
		    // turning with jump
		    this.myImg0 = this.myImgs[7];
		    if (this.imgTimeCounter > 2) {
			this.imgCounter = 0;
			this.imgTimeCounter = 0;
		    }
		    break;
		default:
		    break;
	    }
	    if (this.playerVelocity[0] < -0.2D) { // left
		this.myImg0 = this.myImgs[10]; 
	    }
	    if (this.playerVelocity[0] < -0.4D) { // sharp left
		this.myImg0 = this.myImgs[11]; 
	    }
	    if (this.playerVelocity[0] > 0.2D) { // right
		this.myImg0 = this.myImgs[8]; 
	    }
	    if (this.playerVelocity[0] > 0.4D) { // sharp right
		this.myImg0 = this.myImgs[9]; 
	    }
	     
	} else { 
	    if (this.damaged == 1) { // image of falling player if damaged
		this.myImg0 = this.myImgs[12]; 
		this.imgTimeCounter = 0;
		this.damaged++; // add 1 to damaged -> do not come back here
	    }
	    if (this.damaged > 0 && this.imgTimeCounter > 4 && this.health > 0) {
		this.damaged = 0; // return to normal after delay unless game over
	    }
	    if (this.damaged > 0 && this.imgTimeCounter > 4) {
		// draw image of fallen player
		this.myImg0 = this.myImgs[13];
	    }
	}

    }

    public void computeScore() {
	if (this.damaged == 0) {
	    long l1;
	    this.runningScore++;
	    if (this.prevTime != 0L) {
		l1 = 55L - System.currentTimeMillis() - this.prevTime;
	    } else {
		l1 = 0L;
	    } 
	    if (l1 < 0L) {
		l1 = 1L;
		if (l1 > -40L)
		    this.runningScore += (int)((40L + l1) / 4L); 
	    } else {
		this.runningScore += 5;
	    } 
	} 
	long l = 40L;
	long l1 = this.prevTime + l - System.currentTimeMillis();
	if (l1 <= 0L)
	    l1 = 1L; 
	try {
	    Thread.currentThread().sleep(l1);
	} catch (Exception exception) {
	    System.out.println("exception in computeScore(): " + exception);
	}
	 
	this.prevTime = System.currentTimeMillis();
    }

    public void setVelocity(boolean rFlag, boolean lFlag) {
	if (this.damaged == 0) {
	    if (rFlag) {
		this.playerVelocity[0] += this.xSpeedIncr; 
	    }
	    if (lFlag) {
		this.playerVelocity[0] -= this.xSpeedIncr; 
	    }
	    if (this.playerVelocity[0] < -this.xSpeedMax) {
		this.playerVelocity[0] = -this.xSpeedMax; 
	    }
	    if (this.playerVelocity[0] > this.xSpeedMax) {
		this.playerVelocity[0] = this.xSpeedMax; 
	    }
	} 
	if (!lFlag && !rFlag) {
	    if (this.playerVelocity[0] < 0.0D) {
		this.playerVelocity[0] += this.xSpeedDecay;
		if (this.playerVelocity[0] > 0.0D) {
		    this.playerVelocity[0] = 0.0D; 
		}
	    } 
	    if (this.playerVelocity[0] > 0.0D) {
		this.playerVelocity[0] -= this.xSpeedDecay;
		if (this.playerVelocity[0] < 0.0D) {
		    this.playerVelocity[0] = 0.0D; 
		}
	    } 
	}
    }

    public void reset() {
	this.damaged = 0;
	this.runningScore = 0;
    	this.playerVelocity[0] = 0.0D;
    }

} // Player class
