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
        if (ob1.getY() == ob2.getY()) {
            return 0;
        }
        if (ob1.getY() < ob2.getY()) {
            return 1;
        } else {
            return -1;
        }
    }

}
