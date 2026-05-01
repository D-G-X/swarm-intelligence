package com.dgx;

public class Obstacle {
    private double obstacle_width = 40;
    private double obstacle_height = 40;

    final double FZL;
    final double FZB;

    double[] position;

    Obstacle(double[] position) {
        this.FZL = 2;
        this.FZB = 1;
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
