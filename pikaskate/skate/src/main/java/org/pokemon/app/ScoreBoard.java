package org.pokemon.app;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class ScoreBoard extends Canvas {
  int value;
  
  String label;
  
  public ScoreBoard(String paramString) {
    this.value = 0;
    this.label = paramString;
  }
  
  public void paint(Graphics paramGraphics) {
    paramGraphics.setColor(Color.white);
    paramGraphics.drawString(" " + this.label + this.value, 4, 18);
  }
  
  public void setNum(int paramInt) {
    this.value = paramInt;
    Graphics graphics = getGraphics();
    graphics.clearRect(0, 0, (size()).width, (size()).height);
    paint(graphics);
  }
}

