package com.dgx;

import java.util.ArrayList;

import javax.swing.*;

public class Simulation extends JFrame {
    static int sleep = 8; // delay in frame
    static double pix = 0.2; // the scaling factor
    int anzFz = 10; // number of cars (Anzahl Fahrzeuge)

    double[] currentTarget = null;
    boolean isConsuming = false;
    long consumptionStartTime = 0;
    Canvas myCanvas;
    boolean isDispersing = false;
    long dispersalStartTime = 0;


    ArrayList<Vehicle> allVehicles = new ArrayList<>();
    ArrayList<Obstacle> allObstacles = new ArrayList<>();

    Simulation() {
        setTitle("Swarm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (int k = 0; k < anzFz; k++) {
            Vehicle car = new Vehicle();
            if (k < 0) car.type = 1; // type 1 has visible boundary
            allVehicles.add(car);
        }

        int numObstacles = 3;
        double minGap = 100.0;

        System.out.println("Generating Obstacles");// Minimum distance between obstacles

        for (int k = 0; k < numObstacles; k++) {
            double[] obs_pos = new double[2];
            boolean overlapping;
            int attempts = 0; // Track how many times we've tried to place this one

            do {
                overlapping = false;
                attempts++;

                obs_pos[0] = (Math.random() * 500 * Simulation.pix) + 50;
                obs_pos[1] = (Math.random() * 500 * Simulation.pix) + 50;

                for (Obstacle existing : allObstacles) {
                    double distance = Math.sqrt(Math.pow(obs_pos[0] - existing.position[0], 2) +
                            Math.pow(obs_pos[1] - existing.position[1], 2));
                    if (distance < minGap) {
                        overlapping = true;
                        break;
                    }
                }

                // If we tried 100 times and couldn't find a spot, just stop trying
                if (attempts > 100) break;

            } while (overlapping);

            allObstacles.add(new Obstacle(obs_pos));
        }

        System.out.println("Obstacles Generated");
        myCanvas = new Canvas(allVehicles, pix, allObstacles);

        add(myCanvas);
        setSize(1000, 800);
        setVisible(true);

        spawnNextTarget();

        myCanvas.updateTarget(currentTarget, isConsuming);

        new Timer(sleep, e -> {
            checkTargetStatus();
            myCanvas.updateTarget(currentTarget, isConsuming);
            for (Vehicle v : allVehicles) {
                System.out.println(currentTarget[0]+","+currentTarget[1]);
                v.move(allVehicles, allObstacles, currentTarget, isConsuming, isDispersing);
            }
            repaint();
        }).start();
    }

    void spawnNextTarget() {
        currentTarget = new double[2];
        boolean invalidLocation;
        int attempts = 0;

        // Define the same boundaries the vehicles use in position_Box()
        double minX = 15; // Slightly inside the 10-unit left wall
        double maxX = (1000 * pix) - 15;
        double minY = 15; // Slightly inside the 10-unit top wall
        double maxY = (700 * pix) - 15;

        do {
            invalidLocation = false;
            attempts++;

            // 1. Generate position strictly within the vehicle's "position_Box" limits
            currentTarget[0] = Math.random() * (maxX - minX) + minX;
            currentTarget[1] = Math.random() * (maxY - minY) + minY;

            // 2. Check if it's inside an obstacle
            for (Obstacle obs : allObstacles) {
                double d = Math.sqrt(Math.pow(currentTarget[0] - obs.position[0], 2) +
                        Math.pow(currentTarget[1] - obs.position[1], 2));

                // Check if distance is less than half-width (10) + small buffer[cite: 4]
                if (d < 15) {
                    invalidLocation = true;
                    break;
                }
            }

            if (attempts > 100) break;

        } while (invalidLocation);

        isConsuming = false;
    }



    void checkTargetStatus() {
        if (currentTarget == null) return;

        if (!isConsuming) {
            // If we are currently dispersing, check if 2 seconds have passed to stop
            if (isDispersing && System.currentTimeMillis() - dispersalStartTime > 2000) {
                isDispersing = false;
            }

            for (Vehicle v : allVehicles) {
                double d = Math.sqrt(Math.pow(v.pos[0] - currentTarget[0], 2) +
                        Math.pow(v.pos[1] - currentTarget[1], 2));
                if (d < 5) {
                    isConsuming = true;
                    consumptionStartTime = System.currentTimeMillis();
                    break;
                }
            }
        } else {
            if (System.currentTimeMillis() - consumptionStartTime > 3000) {
                // Target finished! Start dispersing before spawning next target
                isDispersing = true;
                dispersalStartTime = System.currentTimeMillis();
                spawnNextTarget();
            }
        }
    }

    public static void main(String[] args) {
        new Simulation();
    }
}