/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author kristian
 */
public class PathFinder {

    private int x1, x2, y1, y2;
    private Entity entity;
    private LinkedList<Node> queue;
    private Node currentGoal;
    private int endX, endY;

    public PathFinder(Entity entity, float x1, float y1, float x2, float y2) {
        this.entity = entity;
        this.x1 = ((int) x1 / 32) * 32;
        this.y1 = ((int) y1 / 32) * 32;
        this.x2 = ((int) x2 / 32) * 32;
        this.y2 = ((int) y2 / 32) * 32;
        queue = new LinkedList();
        
        queue.add(new Node(null, null, this.x2, this.y2));
        //generatePath();
    }

    public void update() {
        if (queue.size() > 0 || currentGoal != null) {
            if (currentGoal == null) {
                currentGoal = queue.pop();
            }

            int dirX = (currentGoal.getX() - entity.getX() > 0) ? 1 : -1;
            int dirY = (currentGoal.getY() - entity.getY() > 0) ? 1 : -1;
            if(Math.abs(currentGoal.getX()-entity.getX()) < 1) {
                dirX = 0;
            }
            if(Math.abs(currentGoal.getY()-entity.getY()) < 1) {
                dirY = 0;
            }
            entity.move(dirX, dirY);

            if (Vector2.dst(entity.x, entity.y, currentGoal.getX(), currentGoal.getY()) < 8) {
                currentGoal = null;
            }
            if (Vector2.dst(entity.x, entity.y, x2, y2) < 8) {
                queue.clear();
            }
        }
    }

    private boolean generatePath() {
        queue = new LinkedList();
        LinkedList<Node> closedList = new LinkedList();
        ArrayList<Node> unwantedList = new ArrayList();
        
        Node endNode = new Node(null, null, (int) x2, (int) y2);
        Node startNode = new Node(null, endNode, (int) x1, (int) y1);
        queue.push(startNode);
        Node q;
        while (!queue.isEmpty()) {
            q = getLowestFValue(queue);
            queue.remove(q);
            for (Node successor : q.getChildren()) {
                if (successor.distanceTo(endNode) == 0) {
                    queue.push(successor);
                    this.generateFinalQueue(successor);
                    return true;
                }
                if (entity.solidAtLocation((int) successor.getX(), (int) successor.getY())) {
                    closedList.push(successor);
                    unwantedList.add(successor);
                    successor.flagCollision();
                }
                if (this.existsBetterNode(queue, successor) || this.existsBetterNode(closedList, successor) || successor.isCollision || existsOnList(unwantedList, successor)) {
                } else {
                    queue.push(successor);
                }
            }
            closedList.push(q);
        }
        return false;
    }
    
    private void generateFinalQueue(Node node) {
        Node currentNode = node;
        LinkedList<Node> newQueue = new LinkedList();
        newQueue.addFirst(node);
        while(currentNode.parent != null) {
            newQueue.addFirst(currentNode);
            currentNode = currentNode.parent;
        }
        this.queue = newQueue;
    }

    private Node getLowestFValue(LinkedList<Node> list) {
        Node nodeHold = list.get(0);
        for (Node node : list) {
            if (node.getFValue() < nodeHold.getFValue()) {
                nodeHold = node;
            } else if(node.getFValue() == nodeHold.getFValue()) {
                if(nodeHold.getHValue() > node.getHValue()) {
                    nodeHold = node;
                }
            }
        }
        return nodeHold;
    }

    private boolean existsBetterNode(LinkedList<Node> list, Node node) {
        for (Node anotherNode : list) {
            if (anotherNode.getFValue() < node.getFValue()) {
                if (anotherNode.getX() == node.getX() && anotherNode.getY() == node.getY()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean existsOnList(List<Node> list, Node node) {
        for(Node existingNode : list) {
            if(node.getX() == existingNode.getX() && node.getY() == existingNode.getY()) {
                return true;
            }
        }
        return false;
    }
}
