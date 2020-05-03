package org.pokemon.app;

public class Freeobj {
  Obstacle head;
  
  public Freeobj(int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      Obstacle obstacle1 = new Obstacle();
      obstacle1.next = this.head;
      this.head = obstacle1;
    } 
  }
  
  public Obstacle getObj() {
    Obstacle obstacle1 = this.head;
    this.head = this.head.next;
    obstacle1.next = null;
    return obstacle1;
  }
  
  public void deleteObj(Obstacle paramObstacle) {
    if (paramObstacle == null)
      return; 
    paramObstacle.next = this.head;
    this.head = paramObstacle;
  }
}
