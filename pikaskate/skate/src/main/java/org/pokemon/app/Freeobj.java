package org.pokemon.app;

public class Freeobj {
    Obstacle head;
    public int creationDelay = 4; // how many rounds before creating new object 

    public Freeobj(int numberObstacles) {
	for (int i = 0; i < numberObstacles; i++) {
	    Obstacle obstacle1 = new Obstacle();
	    obstacle1.next = this.head;
	    if (this.head != null) {
		this.head.prev = obstacle1;
	    }
	    this.head = obstacle1;
	} 
    }
  
    // take head obstacle and remove it from linked list
    public Obstacle popObstacle() {
	Obstacle obstacle1 = this.head;
	this.head = this.head.next;
	obstacle1.next = null;
	return obstacle1;
    }
 
    public Obstacle getHead() {
	return this.head;
    }

    public void deleteObj(Obstacle paramObstacle) {
	if (paramObstacle == null) {
	    return; 
	}
	paramObstacle.next = this.head;
	this.head = paramObstacle;
    }

    // find first non-active obstacle and activate it.
    // put newly activated obstacle at the beginning of linked list
    // -> this way it'll be painted first (and won't go over older obstacles)
    public boolean activateObstacle(double xPos, double zPos) {
	Obstacle obstacle1 = this.head;
	while (obstacle1 != null) {
	    if (!obstacle1.isActive()) {
		// add new obstacle to game scene at location xPos,zPos
		obstacle1.init(xPos, zPos);
		if (obstacle1 != this.head) { // only move if not already head
		    obstacle1.prev.next = obstacle1.next;
		    obstacle1.next.prev = obstacle1.prev;
		    obstacle1.next = this.head;
		    this.head.prev = obstacle1;
		    this.head = obstacle1;
		    this.head.prev = null;
		}
		return obstacle1.isActive();
	    }
	obstacle1 = obstacle1.next;
	}
	return false;
    }
    
    public void deactivateAll() {
	Obstacle obstacle1 = this.head;
	while (obstacle1 != null) {
	    obstacle1.deactivate();
	    obstacle1 = obstacle1.next;
	} 
    }
}
