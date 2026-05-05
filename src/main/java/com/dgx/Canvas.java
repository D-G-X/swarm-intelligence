package com.dgx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;

import javax.swing.JPanel;


public class Canvas extends JPanel {

	ArrayList<Vehicle> 			allVehicles;
	double pix;
    ArrayList<Obstacle>		allObstacles;
	
	Canvas(ArrayList<Vehicle> allVehicles, double pix,  ArrayList<Obstacle> obstacles) {
		this.allVehicles = allVehicles;
		this.pix         = pix;
        this.allObstacles = obstacles;
		this.setBackground(Color.WHITE);
		setSize(1000,800);
	}

	public Polygon kfzInPolygon(Vehicle fz){
		Polygon   q = new Polygon();
		int    l    = (int)(fz.FZL/pix);
		int    b    = (int)(fz.FZB/pix);
		int    x    = (int)(fz.pos[0]/pix);
		int    y    = (int)(fz.pos[1]/pix);
		int    dia  = (int)(Math.sqrt(Math.pow(l/2, 2)+Math.pow(b/2, 2)));
		double    t = VectorCalculation.angle(fz.vel);
		double phi1 = Math.atan(fz.FZB/fz.FZL);
		double phi2 = Math.PI-phi1;
		double phi3 = Math.PI+phi1;
		double phi4 = 2*Math.PI-phi1;
		int      x1 = (int)(x+(dia*Math.cos(t+  phi1)));
		int      y1 = (int)(y+(dia*Math.sin(t+  phi1)));
		int      x2 = (int)(x+(dia*Math.cos(t+  phi2)));
		int      y2 = (int)(y+(dia*Math.sin(t+  phi2)));
		int      x3 = (int)(x+(dia*Math.cos(t+  phi3)));
		int      y3 = (int)(y+(dia*Math.sin(t+  phi3)));
    	int      x4 = (int)(x+(dia*Math.cos(t+  phi4)));
    	int      y4 = (int)(y+(dia*Math.sin(t+  phi4)));
    	q.addPoint(x1, y1);
    	q.addPoint(x2, y2);
    	q.addPoint(x3, y3);
    	q.addPoint(x4, y4);
		return q;
	}

    public Polygon kfzInPolygonObs(Obstacle obs) {
        Polygon q = new Polygon();

        // Scale dimensions and center position to pixels
        int halfW = (int)((obs.getObstacle_width() / 2) / pix);
        int halfH = (int)((obs.getObstacle_height() / 2) / pix);
        int x = (int)(obs.position[0] / pix);
        int y = (int)(obs.position[1] / pix);

        // Directly define the four corners of the axis-aligned square
        q.addPoint(x + halfW, y + halfH); // Top Right
        q.addPoint(x - halfW, y + halfH); // Top Left
        q.addPoint(x - halfW, y - halfH); // Bottom Left
        q.addPoint(x + halfW, y - halfH); // Bottom Right

        return q;
    }
	
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g;
    	
   	  	for(int i=0;i<allVehicles.size();i++){
      		Vehicle fz = allVehicles.get(i);
      		Polygon q = kfzInPolygon(fz);

          	g2d.setColor(Color.BLACK);
        	g2d.draw(q);

       		int    x  = (int)(fz.pos[0]/pix);
       		int    y  = (int)(fz.pos[1]/pix);
       		
       		if(fz.type==1){
        		int seite = (int)(fz.rad_zus/pix);
            	g2d.drawOval(x-seite, y-seite, 2*seite, 2*seite);
        		seite = (int)(fz.rad_sep/pix);
            	g2d.drawOval(x-seite, y-seite, 2*seite, 2*seite);
            	
       		}
   	  	}

        for(int i=0;i<allObstacles.size();i++){
            Obstacle fz = allObstacles.get(i);
            Polygon q = kfzInPolygonObs(fz);

            g2d.setColor(Color.GRAY);
            g2d.fill(q);
            g2d.setColor(Color.BLACK);
            g2d.draw(q);
        }
    }
}
