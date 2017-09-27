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
public class Task {

    private int x, y;
    private Priority priority;
    private Job job;
    
    public Task(int x, int y, Job job, Priority priority) {
        this.x = x;
        this.y = y;
        this.job = job;
        this.priority = priority;
    }
    
    public void update(Entity entity) {
        switch(job) {
            case TRAVEL:
                
                break;
                
            default:
                break;
        }
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * @return the job
     */
    public Job getJob() {
        return job;
    }
}