/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

/**
 *
 * @author kristian
 */
public class Time {

    private final double MAXTIME = 1440;
    private double dawnAt, duskAt;
    private double time;
    private int day;
    private float darknessIntensity;
    private final String[] WEEKDAYS = new String[]{
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    /**
     * time = days in minutes.
     *
     * @param time
     */
    public Time(double time) {
        this.time = time;
        this.darknessIntensity = 0f;
        this.dawnAt = 6 * 60;
        this.duskAt = 21 * 60;
    }

    public void update(double deltaTime) {
        time += deltaTime;

        if (time > 1440) {
            time -= MAXTIME;
            day++;
        }
        if ((this.time < this.dawnAt || this.time > this.duskAt) && this.darknessIntensity < 0.8f) {
            this.darknessIntensity += deltaTime / 30;
        } else {
            if (this.darknessIntensity != 0) {
                this.darknessIntensity -= deltaTime / 30;
                if (this.darknessIntensity < 0) {
                    this.darknessIntensity = 0;
                }
            }
        }
    }

    public int getHour() {
        return (int) time / 60;
    }

    public float getIntensityOfDarkness() {
        return this.darknessIntensity;
    }
}
