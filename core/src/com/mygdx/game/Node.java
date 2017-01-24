/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

/**
 *
 * @author kristian
 */
public class Node extends Point {

    Node parent;
    ArrayList<Node> children;
    Node end;
    float f, g, h;
    boolean isCollision;

    public Node(Node parent, Node end, int x, int y) {
        super(x, y);
        this.parent = parent;
        this.children = new ArrayList();
        if (parent == null) {
            this.g = 0;
        } else {
            this.g = parent.getGValue() + Vector2.dst(x, y, parent.getX(), parent.getY());
        }
        if(end != null) {
            this.h = Vector2.dst(x, y, end.getX(), end.getY());
        } else {
            this.h = 0;
        }
        this.f = this.g + this.h;
    }
    
    public float distanceTo(Node node) {
        return Vector2.dst(this.getX(), this.getY(), node.getX(), node.getY());
    }

    public ArrayList<Node> getChildren() {
        if(this.children.size() == 0) {
            this.generateChildren();
        }
        return this.children;
    }

    public void removeChild(Node node) {
        this.children.remove(node);
    }
    
    public Node getParent() {
        return parent;
    }

    public float getFValue() {
        return f;
    }

    public float getGValue() {
        return g;
    }

    public float getHValue() {
        return h;
    }

    private void generateChildren() {
        this.children.add(new Node(this, end, this.getX() - 32, this.getY() - 32));
        this.children.add(new Node(this, end, this.getX(), this.getY() - 32));
        this.children.add(new Node(this, end, this.getX() + 32, this.getY() - 32));
        this.children.add(new Node(this, end, this.getX() - 32, this.getY()));
        this.children.add(new Node(this, end, this.getX() + 32, this.getY()));
        this.children.add(new Node(this, end, this.getX() - 32, this.getY() + 32));
        this.children.add(new Node(this, end, this.getX(), this.getY() + 32));
        this.children.add(new Node(this, end, this.getX() + 32, this.getY() + 32));
    }

    void flagCollision() {
        this.isCollision = true;
    }

}
