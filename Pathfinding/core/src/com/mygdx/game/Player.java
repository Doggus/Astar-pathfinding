package com.mygdx.game;

        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.Input.Keys;
        import com.badlogic.gdx.graphics.Texture;

public class Player extends Entity {

    public Player(float x, float y, int width, int height, float speed, Texture t) {
        super(x, y, width, height, speed, t);
    }

    @Override
    public void update(float delta) {

        dx = 0;
        dy = 0;

        // move
        if(Gdx.input.isKeyPressed(Keys.UP)) {
            dy = speed * delta;
        }
        if(Gdx.input.isKeyPressed(Keys.DOWN)) {
            dy = -speed * delta;
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            dx = -speed * delta;
        }
        if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            dx = speed * delta;
        }

        gridx = (int)(this.x/20); // update player's x position in grid
        gridy = (int)(this.y/20); // update player's y position in grid

    }

}
