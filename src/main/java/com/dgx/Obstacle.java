package com.dgx;

public class Obstacle {
    private double obstacle_width = 20;
    private double obstacle_height = 20;

    double[] position;

    Obstacle(double[] position) {
        this.position = position;
        if(position[0] < obstacle_width){
            this.position[0] += 40;
        }
        if(position[1] < obstacle_width){
            this.position[1] += 40;
        }
    }

    public double getObstacle_width() {
        return obstacle_width;
    }

    public void setObstacle_width(double obstacle_width) {
        this.obstacle_width = obstacle_width;
    }

    public double getObstacle_height() {
        return obstacle_height;
    }

    public void setObstacle_height(double obstacle_height) {
        this.obstacle_height = obstacle_height;
    }
}
