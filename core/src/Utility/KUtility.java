/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import com.mygdx.game.AnimationGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author kristian
 */
public class KUtility<T> {
    
    
    public ArrayList<T> getArrayListOfMap(Map map) {
        Iterator it = map.entrySet().iterator();
        ArrayList<T> listToReturn = new ArrayList<>();
        while(it.hasNext()) {
            listToReturn.add((T) ((Map.Entry) it.next()).getValue());
        }
        return listToReturn;
    }
}
