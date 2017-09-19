/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;

/**
 *
 * @author Kristian
 */
public class FileLoader {
    private ArrayList<FileHandle> files;
    
    public FileLoader(String folder) {
        files = new ArrayList();
        this.recursiveLoad(folder);
    }

    private void recursiveLoad(String folder) {
        FileHandle dirHandle = Gdx.files.internal(folder);
        for (FileHandle f : dirHandle.list()) {
            if(f.isDirectory()) {
                recursiveLoad(f.path());
            } else {
                files.add(f);
            }
        }
    }
    
    public ArrayList<FileHandle> getFiles() {
        return files;
    }
}
