/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class ClickHistory {

    private ArrayList<Click> clicks;
    private ArrayList<Click> releaseClicks;
    
    public ClickHistory() {
        this.clicks = new ArrayList();
        this.releaseClicks = new ArrayList();
    }
    
    public Click getClickNewestFirst(int id) {
        if(clicks.size()-1-id < 0 || clicks.size()-1-id > clicks.size()-1 || clicks.size() == 0) {
            return null;
        }
        return clicks.get(clicks.size()-1-id);
    }
    
    public Click getReleaseNewestFirst(int id) {
        if(releaseClicks.size()-1-id < 0 || releaseClicks.size()-1-id > releaseClicks.size()-1 || releaseClicks.size() == 0) {
            return null;
        }
        return releaseClicks.get(releaseClicks.size()-1-id);
    }
    
    public void clear() {
        this.clicks.clear();
        this.releaseClicks.clear();
    }
    
    public void addRelease(int x, int y) {
        this.releaseClicks.add(new Click(x, y));
    }
    
    public void addClick(int x, int y) {
        this.clicks.add(new Click(x, y));
    }
}
