package com.dgx;

import java.util.ArrayList;
import java.util.Arrays;

public class Vehicle {
	static int allId = 0;
	int id; 
	double rad_sep; 
	double rad_zus; 
	int type;


	final double FZL; 
	final double FZB; 
	
	double[] pos; 
	double[] vel; 
	
	final double max_acc; 
	final double max_vel; 

	Vehicle() {
		allId++;
		this.id = allId;
		this.FZL = 2;
		this.FZB = 1;
		this.rad_sep = 5;
		this.rad_zus = 25;
		this.max_acc = 0.2;
		this.max_vel = 1;

		pos = new double[2];
		vel = new double[2];
		
		pos[0] = Simulation.pix * 500 * Math.random();
		pos[1] = Simulation.pix * 500 * Math.random();
		double angle = 2 * Math.PI * Math.random();
		vel[0] = Math.cos(angle) * max_vel;
		vel[1] = Math.sin(angle) * max_vel;
	}

	ArrayList<Vehicle> neighbours(ArrayList<Vehicle> all, double radius1, double radius2) {
		ArrayList<Vehicle> neighbours = new ArrayList<Vehicle>();
		for (int i = 0; i < all.size(); i++) {
			Vehicle v = all.get(i);
			if (v.id != this.id) {
				double dist = Math.sqrt(Math.pow(v.pos[0] - this.pos[0], 2) + Math.pow(v.pos[1] - this.pos[1], 2));
				if (dist >= radius1 && dist < radius2) {
					neighbours.add(v);
				}
			}
		}
		return neighbours;
	}

	double[] calculateAcc(double[] vel_dest) {
		double[] acc_dest = new double[2];

		if (VectorCalculation.length(vel_dest) > 1e-8) {
		    vel_dest = VectorCalculation.normalize(vel_dest);
		}
		
		vel_dest[0] = vel_dest[0] * max_vel;
		vel_dest[1] = vel_dest[1] * max_vel;

		acc_dest[0] = vel_dest[0] - vel[0];
		acc_dest[1] = vel_dest[1] - vel[1];

		return acc_dest;
	}

	double[] cohesion(ArrayList<Vehicle> all) {
		ArrayList<Vehicle> neighbours;
		
		double[] pos_dest = new double[2];
		double[] vel_dest = new double[2];
		double[] acc_dest = new double[2];

		acc_dest[0] = 0;
		acc_dest[1] = 0;
		neighbours = neighbours(all, rad_sep, rad_zus);

		if (neighbours.size() > 0) {
			pos_dest[0] = 0;
			pos_dest[1] = 0;
			for (int i = 0; i < neighbours.size(); i++) {
				Vehicle v = neighbours.get(i);
				pos_dest[0] = pos_dest[0] + v.pos[0];
				pos_dest[1] = pos_dest[1] + v.pos[1];
			}
			pos_dest[0] = pos_dest[0] / neighbours.size();
			pos_dest[1] = pos_dest[1] / neighbours.size();

			vel_dest[0] = pos_dest[0] - pos[0];
			vel_dest[1] = pos_dest[1] - pos[1];

			acc_dest = calculateAcc(vel_dest);
			acc_dest = VectorCalculation.truncate(acc_dest, max_acc);

		}
		return acc_dest;
	}

	double[] separation(ArrayList<Vehicle> all) {
		ArrayList<Vehicle> neighbours;
		double[] vel_dest = new double[2];
		double[] acc_dest = new double[2];

		acc_dest[0] = 0;
		acc_dest[1] = 0;
		neighbours  = neighbours(all, 0, rad_sep);

		if (neighbours.size() > 0) {
			vel_dest[0] = 0;
			vel_dest[1] = 0;
			
			for (int i = 0; i < neighbours.size(); i++) {
				Vehicle v    = neighbours.get(i);
				double[] vel = new double[2];
				double dist;

				vel[0] = v.pos[0] - pos[0];
				vel[1] = v.pos[1] - pos[1];
				
				dist   = rad_sep  - VectorCalculation.length(vel);
				if (dist < 0)System.out.println("mistake in rad");
			
				if (VectorCalculation.length(vel) > 1e-8) {
				    vel = VectorCalculation.normalize(vel);
				}
				
				vel[0] = -vel[0] * dist;
				vel[1] = -vel[1] * dist;
				
				vel_dest[0] = vel_dest[0] + vel[0];
				vel_dest[1] = vel_dest[1] + vel[1];
			}

			acc_dest = calculateAcc(vel_dest);
			acc_dest = VectorCalculation.truncate(acc_dest, max_acc);

		}

		return acc_dest;
	}

	double[] alignment(ArrayList<Vehicle> all) {
		ArrayList<Vehicle> neighbours = new ArrayList<Vehicle>();
		double[] vel_dest = new double[2];
		double[] acc_dest = new double[2];
		acc_dest[0] = 0;
		acc_dest[1] = 0;

		neighbours = neighbours(all, 0, rad_zus);


		if (neighbours.size() > 0) {
			vel_dest[0] = 0;
			vel_dest[1] = 0;
			
			for (int i = 0; i < neighbours.size(); i++) {
				Vehicle v = neighbours.get(i);
				vel_dest[0] = vel_dest[0] + v.vel[0];
				vel_dest[1] = vel_dest[1] + v.vel[1];
			}
			vel_dest[0] = vel_dest[0] / neighbours.size();
			vel_dest[1] = vel_dest[1] / neighbours.size();

			
			acc_dest = calculateAcc(vel_dest);
			acc_dest = VectorCalculation.truncate(acc_dest, max_acc);

		}

		return acc_dest;
	}

    public double[] calculateWeightedAcc(ArrayList<Vehicle> allVehicles, ArrayList<Obstacle> obstacles) {
        double[] acc_dest;
        double f_zus = 0.05;
        double f_sep = 0.55;
        double f_aus = 0.4;
        double f_obs = 0.8; // High priority to avoid hitting boxes[cite: 1]

        double[] acc_cohesion = cohesion(allVehicles);
        double[] acc_sep = separation(allVehicles);
        double[] acc_align = alignment(allVehicles);
        double[] acc_obs = obstacleAvoidance(obstacles); // New force[cite: 1]

        double x = (f_zus * acc_cohesion[0]) + (f_sep * acc_sep[0]) +
                (f_aus * acc_align[0]) + (f_obs * acc_obs[0]);
        double y = (f_zus * acc_cohesion[1]) + (f_sep * acc_sep[1]) +
                (f_aus * acc_align[1]) + (f_obs * acc_obs[1]);

        acc_dest = new double[]{x, y};
        acc_dest = VectorCalculation.truncate(acc_dest, max_acc);
        return acc_dest;
    }

	void move(ArrayList<Vehicle> allVehicles, ArrayList<Obstacle> obstacles) {
		//STEP 1: Accelaration or Force 
		double[] acc = calculateWeightedAcc(allVehicles, obstacles);
	
		//STEP 2: Speed
		vel[0] = vel[0] + acc[0];
		vel[1] = vel[1] + acc[1];

		if (VectorCalculation.length(vel) > 1e-8) {
		    vel = VectorCalculation.normalize(vel);
		}

		vel[0] = vel[0] * max_vel;
		vel[1] = vel[1] * max_vel;

		//STEP 3: Position
		pos[0] = pos[0] + vel[0];
		pos[1] = pos[1] + vel[1];
		
		
		//STEP 4: Box-Simulation
		position_Box();
	}

    double[] obstacleAvoidance(ArrayList<Obstacle> obstacles) {
        double[] vel_dest = new double[2];
        double[] acc_dest = new double[2];
        acc_dest[0] = 0;
        acc_dest[1] = 0;

        // The distance at which the vehicle starts to "see" and avoid the box
        double avoidanceRadius = 30;

        for (Obstacle obs : obstacles) {
            // Calculate distance between vehicle and obstacle center[cite: 2]
            double dist = Math.sqrt(Math.pow(obs.position[0] - pos[0], 2) +
                    Math.pow(obs.position[1] - pos[1], 2));

            if (dist < avoidanceRadius) {
                // Vector pointing from obstacle to vehicle (the "push away" direction)
                double[] pushAway = new double[2];
                pushAway[0] = pos[0] - obs.position[0];
                pushAway[1] = pos[1] - obs.position[1];

                // Normalize and scale the push[cite: 2]
                if (VectorCalculation.length(pushAway) > 1e-8) {
                    pushAway = VectorCalculation.normalize(pushAway);
                }

                // The closer the obstacle, the stronger the push
                double strength = (avoidanceRadius - dist) / avoidanceRadius;
                vel_dest[0] += pushAway[0] * strength;
                vel_dest[1] += pushAway[1] * strength;
            }
        }

        if (VectorCalculation.length(vel_dest) > 1e-8) {
            acc_dest = calculateAcc(vel_dest);
            acc_dest = VectorCalculation.truncate(acc_dest, max_acc);
        }

        return acc_dest;
    }

	public void position_Box() {

        //   If the position is close to the left-edge then
		if (pos[0] < 10) {
			vel[0] = Math.abs(vel[0]);
			pos[0] = pos[0] + vel[0];
		}

        //   If the position is close to the right-edge then velocity is set to negative to move back to the left edge
		if (pos[0] > 1000 * Simulation.pix) {
			vel[0] = -Math.abs(vel[0]);
			pos[0] = pos[0] + vel[0];
		}

        //   If the position is close to the top-edge then
		if (pos[1] < 10) {
			vel[1] = Math.abs(vel[1]);
			pos[1] = pos[1] + vel[1];
		}

        //   If the position is close to the bottom-edge then
		if (pos[1] > 700 * Simulation.pix) {
			vel[1] = -Math.abs(vel[1]);
			pos[1] = pos[1] + vel[1];
		}
	}
}
