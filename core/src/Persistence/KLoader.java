/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author kristian
 */
public class KLoader<T> {

    private FileHandle file;

    public KLoader() {

    }

    public T load(Class<T> c, String path) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("Path: " + path);
        file = Gdx.files.internal(path);
        String text = file.readString();

        return decode(c, text);
    }

    public void save(Object o, String path) throws IllegalArgumentException, IllegalAccessException {
        String toSave = encode(o);

        file = Gdx.files.local(path);
        file.writeString(toSave, false);
    }

    public T decode(Class<T> c, String str) throws InstantiationException, IllegalAccessException {
        T object = c.newInstance();
        String text = str;
        String wordsArray[] = text.split("\\r?\\n");

        for (String string : wordsArray) {
            String[] command = string.split("=");
            String stringWithoutSpaces = command[0].replaceAll("\\s+", "");

            Field field = null;
            try {
                field = object.getClass().getDeclaredField(command[0]);
            } catch (NoSuchFieldException e) {
                System.out.println(e + " Field: " + command[0] + " doesn't exists!");
            }

            if (field != null) {
                switch (field.getType().toString()) {

                    case "int":
                        field.setInt(object, Integer.parseInt(command[1]));
                        System.out.println("here");
                        break;

                    case "double":
                        field.setDouble(object, Double.parseDouble(command[1]));
                        break;

                    case "float":
                        field.setFloat(object, Float.parseFloat(command[1]));
                        break;

                    default:
                        //field is string:
                        System.out.println("field: " + command[0]);
                        field.set(object, command[1]);
                        break;
                }
            }
        }
        return object;
    }

    public String encode(Object object) throws IllegalArgumentException, IllegalAccessException {
        T o = (T) object;
        Field[] fields = null;
        fields = o.getClass().getDeclaredFields();
        String encodedString = "";
        for (Field field : fields) {
            if (field.getModifiers() != Modifier.STATIC && field.getModifiers() != 10 && field.getModifiers() != 12) {
                encodedString += field.getName() + "=" + field.get(o) + "\n";
            }
        }
        return encodedString;
    }
}
