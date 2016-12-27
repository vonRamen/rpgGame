/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 *
 * @author kristian
 */
public class KJson<T> {

    private static final Json JSON = new Json();

    public KJson() {

    }

    public T load(Object c, String path) {
        FileHandle fileHandler = new FileHandle(path);
        System.out.println(path);
        T object = (T) JSON.fromJson(c.getClass(), fileHandler);
        return object;
    }

    public void save(Object c, String path) {
        FileHandle fileHandler = new FileHandle(path);
        String stringToSave = JSON.toJson(c);
        fileHandler.writeString(stringToSave, false);
    }
}
