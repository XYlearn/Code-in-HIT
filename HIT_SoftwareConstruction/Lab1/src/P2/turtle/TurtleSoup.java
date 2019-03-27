/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package turtle;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class TurtleSoup {

    /**
     * Draw a square.
     * 
     * @param turtle the turtle context
     * @param sideLength length of each side
     */
    public static void drawSquare(Turtle turtle, int sideLength) {
    	for(int i = 0; i < 4; i++) {
	        turtle.forward(sideLength);
	        turtle.turn(90);
    	}
    	turtle.draw();
    }

    /**
     * Determine inside angles of a regular polygon.
     * 
     * There is a simple formula for calculating the inside angles of a polygon;
     * you should derive it and use it here.
     * 
     * @param sides number of sides, where sides must be > 2
     * @return angle in degrees, where 0 <= angle < 360
     */
    public static double calculateRegularPolygonAngle(int sides) {
        return (sides - 2) * 180.0 / sides;
    }

    /**
     * Determine number of sides given the size of interior angles of a regular polygon.
     * 
     * There is a simple formula for this; you should derive it and use it here.
     * Make sure you *properly round* the answer before you return it (see java.lang.Math).
     * HINT: it is easier if you think about the exterior angles.
     * 
     * @param angle size of interior angles in degrees, where 0 < angle < 180
     * @return the integer number of sides
     */
    public static int calculatePolygonSidesFromAngle(double angle) {
        return (int)Math.round(360 / (180 - angle));
    }

    /**
     * Given the number of sides, draw a regular polygon.
     * 
     * (0,0) is the lower-left corner of the polygon; use only right-hand turns to draw.
     * 
     * @param turtle the turtle context
     * @param sides number of sides of the polygon to draw
     * @param sideLength length of each side
     */
    public static void drawRegularPolygon(Turtle turtle, int sides, int sideLength) {
    	double angle = 180 - calculateRegularPolygonAngle(sides);
        for(int i = 0; i < sides; i++) {
        	turtle.forward(sideLength);
        	turtle.turn(angle);
        }
        turtle.draw();
    }

    /**
     * Given the current direction, current location, and a target location, calculate the heading
     * towards the target point.
     * 
     * The return value is the angle input to turn() that would point the turtle in the direction of
     * the target point (targetX,targetY), given that the turtle is already at the point
     * (currentX,currentY) and is facing at angle currentHeading. The angle must be expressed in
     * degrees, where 0 <= angle < 360. 
     *
     * HINT: look at http://en.wikipedia.org/wiki/Atan2 and Java's math libraries
     * 
     * @param currentHeading current direction as clockwise from north
     * @param currentX current location x-coordinate
     * @param currentY current location y-coordinate
     * @param targetX target point x-coordinate
     * @param targetY target point y-coordinate
     * @return adjustment to heading (right turn amount) to get to target point,
     *         must be 0 <= angle < 360
     */
    public static double calculateHeadingToPoint(double currentHeading, int currentX, int currentY,
                                                 int targetX, int targetY) {
        double angle = 0.0;
        int distanceX = targetX - currentX;
        int distanceY = targetY - currentY;
        if(distanceY == 0) {
        	if(distanceX > 0) {
        		angle = 90;
        	} else {
        		angle = 270;
        	}
        } else {
        	
        	angle = Math.abs(Math.atan((double)distanceX / distanceY)) / Math.PI * 180;
        	// recalculate angle by direction
        	if(distanceX == 0) {
        		if(distanceY > 0)
        			angle = 0;
        		else
        			angle = 180;
        	}
        	if(distanceX > 0 && distanceY < 0) {
        		angle = 180.0 - angle;
        	} else if(distanceX < 0 && distanceY > 0) {
        		angle = 360.0 - angle;
        	} else if(distanceX < 0 && distanceY < 0) {
        		angle = 180.0 + angle;
        	} // else angle is the same
        }
        angle = angle - currentHeading;
        angle = angle >= 0 ? angle : angle + 360; 
        
        return angle;
    }

    /**
     * Given a sequence of points, calculate the heading adjustments needed to get from each point
     * to the next.
     * 
     * Assumes that the turtle starts at the first point given, facing up (i.e. 0 degrees).
     * For each subsequent point, assumes that the turtle is still facing in the direction it was
     * facing when it moved to the previous point.
     * You should use calculateHeadingToPoint() to implement this function.
     * 
     * @param xCoords list of x-coordinates (must be same length as yCoords)
     * @param yCoords list of y-coordinates (must be same length as xCoords)
     * @return list of heading adjustments between points, of size 0 if (# of points) == 0,
     *         otherwise of size (# of points) - 1
     */
    public static List<Double> calculateHeadings(List<Integer> xCoords, List<Integer> yCoords) {
        List<Double> angleList = new ArrayList<Double>();
    	int currentX, currentY;	// current x,y position
    	int targetX, targetY;
    	double currentHeading = 0.0;
    	int pointNum = Math.min(xCoords.size(), yCoords.size());
    	
    	// angleList will not be empty only if pointNumber > 1
    	if(pointNum > 1) {
    		Iterator<Integer> itex = xCoords.iterator();
    		Iterator<Integer> itey = yCoords.iterator();
    		targetX = itex.next();
    		targetY = itey.next();
    		// calculate turnAngle between every two points
    		for(int i = 1; i < pointNum; i++) {
    			//move on
    			currentX = targetX;
    			currentY = targetY;
    			targetX = itex.next();
    			targetY = itey.next();
    			// calculate angle to turn
    			double turnAngle = calculateHeadingToPoint(currentHeading, 
    					currentX, currentY, targetX, targetY);
    			currentHeading += turnAngle;
    			if(currentHeading > 360.0)
    				currentHeading -= 360.0;
    			angleList.add(turnAngle);
    		}
    	}
        return angleList;
    }

    /**
     * Draw your personal, custom art.
     * 
     * Many interesting images can be drawn using the simple implementation of a turtle.  For this
     * function, draw something interesting; the complexity can be as little or as much as you want.
     * 
     * @param turtle the turtle context
     */
    public static void drawPersonalArt(Turtle turtle) {
        turtle.color(PenColor.YELLOW);
        for(int i = 0; i < 150; i++) {
        	turtle.forward(140);
        	turtle.turn(123);
        }
        turtle.color(PenColor.BLUE);
        for(int i = 0; i < 20; i++) {
        	turtle.forward(i * 15);
        	turtle.turn(144);
        }
        turtle.color(PenColor.RED);
        for(int i = 0; i < 150; i++) {
        	turtle.forward(140);
        	turtle.turn(123);
        }
    }

    /**
     * Main method.
     * 
     * This is the method that runs when you run "java TurtleSoup".
     * 
     * @param args unused
     */
    public static void main(String args[]) {
        DrawableTurtle turtle = new DrawableTurtle();

        drawSquare(turtle, 40);
        //drawRegularPolygon(turtle, 7, 100);
        //drawPersonalArt(turtle);
        // draw the window
        turtle.draw();
    }

}
