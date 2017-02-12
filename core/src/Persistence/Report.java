/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import Persistence.ReportCreatable;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.AnimationGroup;
import com.mygdx.game.IdComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kristian
 */
public class Report {

    private ArrayList<ReportCreatable> list;
    private ReportCreatable object;
    private String saveString;
    
    public static void generateReports() {
        try {
            String path = "reports/";
            new Report(GameItem.class, path);
            new Report(AnimationGroup.class, path);
            new Report(GameObject.class, path);
            new Report(Weapon.class, path);
            new Report(Tile.class, path);
        } catch (InstantiationException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Report(Class<? extends ReportCreatable> reportClass, String path) throws InstantiationException, IllegalAccessException {
        ReportCreatable newObj = (ReportCreatable) reportClass.newInstance();
        this.object = newObj;
        prepare();
        createString();
        saveString(path);
    }

    private void prepare() {
        list = new ArrayList();
        list.addAll(this.object.getAll());
        Collections.sort(list, new IdComparator());
    }
    
    private void createString() {
        saveString = "";
        for(ReportCreatable reportItem : this.list) {
            saveString = saveString + "Id: "+reportItem.getId()+"    Name: "+reportItem.getName()+"    File Name: "+reportItem.getFileName()+"\n";
        }
    }
    
    private void saveString(String path) {
        FileHandle file = new FileHandle(path+object.getClass().toString()+"_report.txt");
        file.writeString(saveString, false);
    }
}
