package com.mygdx.game;


public class Node implements Comparable<Node>{

    int x;
    int y;

    int g;
    float h;
    float f;
    Node parent;

    public Node(int x, int y, int g, Node goal, Node p){

        this.x = x;
        this.y = y;

        this.g = g;
        this.h = heuristic(goal);
        f = this.g+h;
        parent = p;
    }

    public Node(int x, int y, int g, Node goal){

        this.x = x;
        this.y = y;

        this.g = g;
        this.h = heuristic(goal);
        f = this.g+h;
    }

    public Node(int x, int y){
        this.x = x;
        this.y = y;
    }


    public void setG(int newG)
    {
        g = newG;
    }


    public void setF(int G)
    {
        f = G+h;
    }

    public void setParent(Node p)
    {
        parent = p;
    }

    // Overrides standard equals method (used to check if a list contains a specific node)
    @Override
    public boolean equals(Object other) {

        if(this == other) {
            return true;
        }

        if(other == null) {
            return false;
        }

        if(getClass() != other.getClass()) {
            return false;
        }

        Node test = (Node)other;
        if(this.x == test.x && this.y == test.y) {
            return true;
        }

        return false;
    }


    //used to sort lists (and sorts priority queue by f) -> [//Collections.sort(openNodes);] - sorts Array List by f values]
    @Override
    public int compareTo(Node n) {
        return (int) (f - n.f);
    }


    public float heuristic(Node endNode) {


        //euclidean distance
        /*
        float y = Math.abs (this.y - endNode.y); //y distance
        float x = Math.abs (this.x - endNode.x); //x distance
        float result = (float)Math.sqrt((y)*(y) +(x)*(x));
        return result;
        */


        //delta max
        float x = Math.abs (this.x - endNode.x); //x distance
        float y = Math.abs (this.y - endNode.y); //y distance
        float result = Math.max(x,y);
        return result;

    }
}
