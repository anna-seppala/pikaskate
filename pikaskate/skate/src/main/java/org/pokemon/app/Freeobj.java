package org.pokemon.app;

public class Freeobj {
  Obstacle head;
  
  public Freeobj(int numberObstacles) {
    for (int i = 0; i < numberObstacles; i++) {
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
