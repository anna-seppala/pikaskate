package org.pokemon.app;

public class Freeobj {
    FloatyObject head;
    FloatyObject tail;

    public Freeobj(int numberObstacles) {
	FloatyObject floaty1;
	for (int i = 0; i < numberObstacles; i++) {
	    if (i % 10 == 0) { // every 10th obj is heart
		floaty1 = new Heart(i);
	    } else {
		floaty1 = new Obstacle(i);
	    }
	    if (i == 0) {
		this.tail = floaty1;
	    }
	    floaty1.next = this.head;
	    if (this.head != null) {
		this.head.prev = floaty1;
	    }
	    this.head = floaty1;
	} 
    }
  
    // take head obstacle and remove it from linked list
    public FloatyObject popObject() {
	FloatyObject floaty1 = this.head;
	this.head = this.head.next;
	floaty1.next = null;
	return floaty1;
    }
 
    public FloatyObject getHead() {
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
    public boolean activateObject(double xPos, double zPos, String simpleName) {
	FloatyObject floaty1 = this.head;
	while (floaty1 != null) {
	    if ((floaty1.getClass().getSimpleName().equals(simpleName) )
		    && (!floaty1.isActive())) {
		// add new obstacle to game scene at location xPos,zPos
		floaty1.init(xPos, zPos);
		if (floaty1 != this.head) { // only move if not already head
		    floaty1.prev.next = floaty1.next;
		    if (floaty1 != this.tail) { // next is null if tail
			floaty1.next.prev = floaty1.prev;
		    } else { // if moving tail, update this.tail
			this.tail = floaty1.prev;
		    }
		    floaty1.next = this.head;
		    this.head.prev = floaty1;
		    this.head = floaty1;
		    this.head.prev = null;
		}
		return floaty1.isActive();
	    }
	floaty1 = floaty1.next;
	}
	return false;
    }
    
    public void deactivateAll() {
	FloatyObject floaty1 = this.head;
	while (floaty1 != null) {
	    floaty1.deactivate();
	    floaty1 = floaty1.next;
	} 
    }
}
