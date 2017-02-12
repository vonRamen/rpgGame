/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.ReportCreatable;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kristian
 */
public class IdComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        ReportCreatable rep1 = (ReportCreatable) o1;
        ReportCreatable rep2 = (ReportCreatable) o2;
        if(rep1.getId() == rep2.getId())
            return 0;
        if(rep1.getId() < rep2.getId()) {
            return -1;
        } else return 0;
    }
    
}
