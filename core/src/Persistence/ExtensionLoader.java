package Persistence;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExtensionLoader<C> {
    
    /**
     * Loads all classes from the location.
     * @param classfileLocation ex: Persistence/Items
     * @return
     * @throws ClassNotFoundException 
     */
    public ArrayList<C> loadAllClasses(String classfileLocation) throws ClassNotFoundException {
        ArrayList<C> list = new ArrayList();
        for(String string : getClassesLocation(classfileLocation)) {
            list.add(loadClass(string));
        }
        
        return list;
    }

    /**
     * Loads a class from the location:
     * The formula is ex:
     * Persistence.Items.Test
     * @param classfileLocation
     * @return
     * @throws ClassNotFoundException 
     */
    public C loadClass(String classfileLocation) throws ClassNotFoundException {

        //classfileLocation = Persistence.Items.
        
        try {
            //File pluginsDir = new File(System.getProperty("user.dir") + directory);
            System.out.println("classfileLocation is "+classfileLocation);
            return (C) Class.forName(classfileLocation).newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(ExtensionLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ExtensionLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private String[] getClassesLocation(String classfileLocation) {

        
        File files = new File("");
        String root = files.getAbsolutePath().substring(0, files.getAbsolutePath().length() - "/assets".length());
        files = new File(root);
        System.out.println("Files: " + files.getAbsolutePath());
        
        File classLocation = new File(files+"/src/"+classfileLocation);
        System.out.println("Files: "+classLocation.listFiles()[0]);
        String[] classes = new String[classLocation.listFiles().length];
        int index = 0;
        for(File f : classLocation.listFiles()) {
            classes[index] = classfileLocation.replaceAll("/", "\\.")+"."+f.getName().split("\\.")[0];
            index++;
        }
        
        return classes;
    }

}
