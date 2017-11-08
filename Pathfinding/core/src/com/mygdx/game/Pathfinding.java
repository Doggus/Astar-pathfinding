package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Contact;

import java.util.*;

public class Pathfinding extends ApplicationAdapter {

	private SpriteBatch batch;

	private int screenWidth;
	private int screenHeight;

	int[][] map = {
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,1,0,0,0,0,0,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,1,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,1,1,1,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,1,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,1,0,0,1},
			{1,0,0,0,1,1,1,0,0,0,0,0,0,0,1},
			{1,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
			{1,1,1,1,1,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,1,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
			{1,0,0,0,0,0,1,1,1,1,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
			{1,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, //22


	};

	private int mapWidth = 22;
	private int mapHeight = 15;
	private int tileSize = 20;

	private Texture tileTexture;
	private Player player;
	private Entity enemy;

	private ArrayList<Entity> entities = new ArrayList<Entity>();

    //Path finding
    private Node goal;
    private Node start;
    private float moveTimer = 0;

    private int diag = (int)(20*Math.sqrt(2)); //diagonal g value

    private ArrayList<Node> openNodes = new ArrayList<Node>();
    private ArrayList<Node> closedNodes = new ArrayList<Node>();
    private ArrayList<Node> path = new ArrayList<Node>();

	enum Axis { X, Y };
	enum Direction { U, D, L, R};
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		tileTexture = new Texture("block.png");
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		player = new Player(20, 20, 20, 20, 120.0f, new Texture("player.png"));
		enemy = new Entity(400, 260, 20, 20, 120.0f, new Texture("enemy.png"));

		// add some entities including a player
		entities.add(player);
		entities.add(enemy);

		goal = new Node(player.gridx,player.gridy);
		start = new Node(enemy.gridx,enemy.gridy,0,goal);

    }

	public void moveEntity(Entity e, float newX, float newY) {
		// just check x collisions keep y the same
		moveEntityInAxis(e, Axis.X, newX, e.y);
		// just check y collisions keep x the same
		moveEntityInAxis(e, Axis.Y, e.x, newY);
	}

	public void moveEntityInAxis(Entity e, Axis axis, float newX, float newY) {
		Direction direction;

		// determine axis direction
		if(axis == Axis.Y) {
			if(newY - e.y < 0) direction = Direction.U;
			else direction = Direction.D;
		}
		else {
			if(newX - e.x < 0) direction = Direction.L;
			else direction = Direction.R;
		}

			if (!tileCollision(e, direction, newX, newY) && !entityCollision(e, direction, newX, newY)) {
				// full move with no collision
				e.move(newX, newY);
			}

		// else collision with either tile or entity occurred
	}

	public boolean tileCollision(Entity e, Direction direction, float newX, float newY) {
		boolean collision = false;

		// determine affected tiles
		int x1 = (int) Math.floor(Math.min(e.x, newX) / tileSize);
		int y1 = (int) Math.floor(Math.min(e.y, newY) / tileSize);
		int x2 = (int) Math.floor((Math.max(e.x, newX) + e.width - 0.1f) / tileSize);
		int y2 = (int) Math.floor((Math.max(e.y, newY) + e.height - 0.1f) / tileSize);

		// tile checks
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				if(map[x][y] == 1) {
					collision = true;
				}
			}
		}

		return collision;
	}

	public boolean entityCollision(Entity e1, Direction direction, float newX, float newY) {
		boolean collision = false;

		for(int i = 0; i < entities.size(); i++) {
			Entity e2 = entities.get(i);

			// we don't want to check for collisions between the same entity
			if(e1 != e2) {
				// axis aligned rectangle rectangle collision detection
				if(newX < e2.x + e2.width && e2.x < newX + e1.width &&
						newY < e2.y + e2.height && e2.y < newY + e1.height) {
					collision = true;
				}
			}
		}

		return collision;
	}


	@Override
	public void render () {


		// update
		float delta = Gdx.graphics.getDeltaTime();

		// update all entities
		for(int i = entities.size() - 1; i >= 0; i--) {
			Entity e = entities.get(i);
			// update entity based on input/ai/physics etc
			// this is where we determine the change in position
			e.update(delta);
			// now we try move the entity on the map and check for collisions
			moveEntity(e, e.x + e.dx, e.y + e.dy);
		}

		//A.I

        goal = new Node(player.gridx,player.gridy); //player
		start = new Node(enemy.gridx,enemy.gridy); //enemy
		Astar();

		//Moves enemy along path (one movement every 0.75 seconds, easier to see that its following the path this way)
		moveTimer += delta;
        int j = path.size()-1;
        if(moveTimer >= 0.75f) {

            if(j>=0) {
                moveAlongPath(path.get(j).x, path.get(j).y);
            }

            moveTimer = 0; //reset timer
        }





		// draw
		// ---
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		// draw tile map
		// go over each row bottom to top
		for(int y = 0; y < mapHeight; y++) {
			// go over each column left to right
			for(int x = 0; x < mapWidth; x++) {
				// tile
				if(map[x][y] == 1) {
					batch.draw(tileTexture, x * tileSize, y * tileSize);
				}
				// draw other types here...
			}
		}

		// draw all entities
		for(int i = entities.size() - 1; i >= 0; i--) {
			Entity e = entities.get(i);
			batch.draw(e.texture, e.x, e.y);
		}


		    //draws path to target
             for (Node n : path) {
                batch.draw(new Texture("path.png"), n.x*20, n.y*20);
             }


		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}


	public void Astar() { // parameters would of had to have been used if multiple enemies on screen
                          // but because one enemy on screen, global start variable is used

	    // NB for drawing path to player (render)
        path.clear();
        openNodes.clear();
        closedNodes.clear();

        //Main algorithm
        openNodes.add(this.start); //starting node (enemy - is updated)

	    while (!openNodes.isEmpty()) {

            //priority queues automatically sorted (by f)
            //Node current = openNodes.peek(); //retrieves node with lowest f

            Collections.sort(openNodes); //sorts list by f values
            Node current = openNodes.get(0); // current = to node with lowest f


            //if goal is reached
            if (current.x == goal.x && current.y == goal.y) {
                goal = current;
                getPath(); //method gets path to target
                break;

            } else {

                openNodes.remove(current);
                closedNodes.add(current);

                // neighbor checks
                for (int x = current.x - 1; x <= current.x + 1; x++) {
                    for (int y = current.y - 1; y <= current.y + 1; y++) {
                        if ((x != current.x || y != current.y) && map[x][y] == 0) {

                            if ((x == current.x - 1 && y == current.y + 1) ||
                                    (x == current.x + 1 && y == current.y + 1) ||
                                    (x == current.x - 1 && y == current.y - 1) ||
                                    (x == current.x + 1 && y == current.y - 1)) {

                                Node DiagonalNeighbor = new Node(x, y, current.g + diag, goal,current); //diagonal node


                                if (closedNodes.contains(DiagonalNeighbor)) {
                                    continue;
                                }

                                if (!(openNodes.contains(DiagonalNeighbor))) {
                                    openNodes.add(DiagonalNeighbor);
                                } else {

                                    // updates g value and parent nodes of neighbors if new path is better than previous path
                                    int tempG = current.g + diag;
                                    int n = findNeighbor(DiagonalNeighbor);
                                    int G = openNodes.get(n).g;

                                    if (tempG >= G) {
                                        continue;
                                    } else {
                                        openNodes.get(n).setG(tempG);
                                        openNodes.get(n).setF(tempG);
                                        openNodes.get(n).setParent(current);
                                    }
                                }

                            } else {

                                Node Neighbor = new Node(x, y, current.g + 20, goal,current);


                                if (closedNodes.contains(Neighbor)) {
                                    continue;
                                }

                                if (!(openNodes.contains(Neighbor))) {
                                    openNodes.add(Neighbor);
                                } else {

                                    // updates g value and parent nodes of neighbors if new path is better than previous path
                                    int tempG = current.g + 20;
                                    int n = findNeighbor(Neighbor);
                                    int G = openNodes.get(n).g;

                                    if (tempG >= G) {
                                        continue;
                                    } else {
                                        openNodes.get(n).setG(tempG);
                                        openNodes.get(n).setF(tempG);
                                        openNodes.get(n).setParent(current);
                                    }
                                }

                            }

                        }
                    }
                }

            }
        }
    }

    //gets path to target
    public void getPath() {

        Node n = goal.parent;

        while(!(n.equals(start)))
        {
            path.add(n);
            n = n.parent;
        }

    }

    //finds position/index of neighbor
    public int findNeighbor(Node neighbor)
    {
        int index = 0;
        for (int i = 0; i<openNodes.size();i++)
        {
            if(openNodes.get(i).equals(neighbor))
            {
                index = i;
            }
        }

        return index;
    }

    public void moveAlongPath(int x, int y){
        enemy.move(x * 20, y * 20);
    }

}
