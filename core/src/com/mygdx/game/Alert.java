/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

/**
 *
 * @author kristian
 */
public class Alert {

    private AlertType type;
    private String message;

    public Alert(String message, AlertType type) {
        this.message = message;
        this.type = type;
    }

    /**
     * @return the type
     */
    public AlertType getType() {
        return type;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
