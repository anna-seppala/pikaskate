package org.pokemon.app;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class ScoreBoard extends Canvas {
  int value;
  
  String label;
  
  public ScoreBoard(String paramString) {
    this.value = 0;
    this.label = paramString;
  }
  
  public void paint(Graphics g) {
      super.paint(g);
      Graphics2D g2d = (Graphics2D) g;
      
    g2d.setColor(Color.BLUE);

    g2d.drawString(" " + this.label + this.value, 4, 18);
  }
  
  public void setNum(int paramInt) {
    this.value = paramInt;
    Graphics2D g2d = (Graphics2D) getGraphics();
    g2d.clearRect(0, 0, (size()).width, (size()).height);
    //super.paint(g); //????
    paint(g2d);
  }
}

