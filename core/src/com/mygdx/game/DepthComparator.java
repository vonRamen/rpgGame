/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import java.util.Comparator;

/**
 *
 * @author kristian
 */
public class DepthComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Drawable ob1 = (Drawable) o1;
        Drawable ob2 = (Drawable) o2;
        //if the z value is actually -1, then draw first
        if(ob1.getZ() == -1) {
            return -1;
        } else if(ob2.getZ() == -1) {
            return 1;
        }
        
        //If the y value is the same, compare with z
        //Biggest z value gets drawn first
        if (ob1.getY() == ob2.getY()) {
            if(ob1.getZ() == ob2.getZ()) {
                return 0;
            }
            if (ob1.getZ() > ob2.getZ()) {
                return 1;
            } else {
                return -1;
            }
        }
        
        //If they're not the same, compare with y
        if (ob1.getY() < ob2.getY()) {
            return 1;
        } else {
            return -1;
        }
    }

}
