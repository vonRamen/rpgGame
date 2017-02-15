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

    private final double maxTime = 1440;
    private double time;
    private int day;
    private float lightIntensity;

    /**
     * time = days in minutes.
     * @param time 
     */
    public Time(double time) {
        this.time = time;
        this.lightIntensity = 1f;
    }

    public void update(double deltaTime) {
        time+=deltaTime;
        
        if(time > 1440) {
            time -= maxTime;
            day++;
        }
    }
    
    public int getHour() {
        return (int) time / 60;
    }
}
