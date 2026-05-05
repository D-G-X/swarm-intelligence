package com.dgx;

import java.util.ArrayList;

import javax.swing.*;

public class Simulation extends JFrame {
    static int sleep = 8; // delay in frame
    static double pix = 0.2; // the scaling factor
    int anzFz = 30; // number of cars (Anzahl Fahrzeuge)


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

                obs_pos[0] = (Math.random() * 800 * Simulation.pix) + 50;
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

        add(new Canvas(allVehicles, pix, allObstacles));
        setSize(1000, 800);
        setVisible(true);

        new Timer(sleep, e -> {
            for (Vehicle v : allVehicles) {
                v.move(allVehicles, allObstacles);
            }
            repaint();
        }).start();
    }

    public static void main(String[] args) {
        new Simulation();
    }
}