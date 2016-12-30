/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import Persistence.Action;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class EntitySimpleType extends Entity {

    public EntitySimpleType() {
        //remove non-simple types
        currentFrame=null;
        bodyDef=null;
        bounds=null;
        animation=null;
    }

    @Override
    public ArrayList<Action> getActions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
