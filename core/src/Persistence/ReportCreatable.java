/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public interface ReportCreatable {
    public int getId();
    public String getName();
    public String getFileName();
    public ArrayList<? extends ReportCreatable> getAll();
}
