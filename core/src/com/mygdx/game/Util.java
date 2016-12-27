/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import java.io.File;

/**
 *
 * @author kristian
 */
public class Util {


    public static void createDirectory(String path) {
        System.out.println(System.getProperty("user.dir")+"/"+path);
        new File("/"+path).mkdir();
    }
}
