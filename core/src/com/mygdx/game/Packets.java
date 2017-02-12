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
public class Packets {

    public static class Message{public String message;}
    public static class RequestAccess{public String name; public String password;}
    public static class DenyAccess{public String denyReason;}
    public static class BeginMovement {public EntitySimpleType entity;}
    public static class EndMovement {public EntitySimpleType entity;}
    public static class requestChunks {public EntitySimpleType entity;}
}
