package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class Entity{
    float x;
    float y;
    float dx;
    float dy;
    int width;
    int height;
    float speed;
    Texture texture;

    int gridx;
    int gridy;


    public Entity(float x, float y, int width, int height, float speed, Texture tex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.texture = tex;

        //these values may need update as entity moves
        this.gridx = (int)(this.x/20); // x position in grid
        this.gridy = (int)(this.y/20); // y position in grid


    }

    public void update(float delta) {

        gridx = (int)(this.x/20); // update entity's x position in grid
        gridy = (int)(this.y/20); // update entity's y position in grid
    }


    public void move(float newX, float newY) {
        x = newX;
        y = newY;
    }

}
