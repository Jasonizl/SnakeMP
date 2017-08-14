package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import util.Constants;

import java.util.ArrayList;

/**
 * Created by Jason on 12.08.2017.
 */
public class Snake {

    private int id;
    private ArrayList<Pair> snakepart;
    // private ArrayList<Pair> enemySnakepart;
    private Pair fruit;

    public GraphicsContext gc;
    public boolean up = false, left = false, right = false, down = false;
    public boolean isStarted;
    public boolean gameover;
    public int eatenFruits = 0;

    public Snake(int id) {
        this.id = id;
        this.snakepart = new ArrayList<>();
        this.isStarted = false;
        this.gameover = false;
        this.fruit = new Pair(25,25);

        Pair head = null;
        switch (id) {
            case 0:
                head = new Pair(5, 5);
                break;
            case 1:
                head = new Pair(45, 5);
                break;
            case 2:
                head = new Pair(5, 45);
                break;
            case 3:
                head = new Pair(45, 45);
                break;
        }
        if(head == null) return;
        snakepart.add(head);
    }

    /**
     * gets called if keys are pressed. Sets all booleans accordingly
     * @param movement
     */
    public void setDirection(int movement) {
        switch (movement) {
            case 0: //up
                up = true; left = false; right = false; down = false;
                break;
            case 1: //left
                up = false; left = true; right = false; down = false;
                break;
            case 2: // right
                up = false; left = false; right = true; down = false;
                break;
            case 3: // down
                up = false; left = false; right = false; down = true;
                break;
            default: // don't move anywhere
                up = false; left = false; right = false; down = false;
                break;
        }
    }

    /**
     * updates everything
     */
    public void update() {
        // Calculate all new snake positions
        Pair currentSnakepart = snakepart.get(snakepart.size()-1);
        Pair snakeHead = snakepart.get(0);
        for(int i = snakepart.size()-1; i > 0; i--) {
            currentSnakepart.setX(snakepart.get(i-1).getX());
            currentSnakepart.setY(snakepart.get(i-1).getY());
            currentSnakepart = snakepart.get(i-1);
        }

        // checks out of bounds stuff (top <-> bottom..)
        if(up) snakeHead.setY(Math.floorMod(snakeHead.getY()-1, Constants.GAME_SIZE));
        else if(left) snakeHead.setX(Math.floorMod(snakeHead.getX()-1, Constants.GAME_SIZE));
        else if(right) snakeHead.setX(Math.floorMod(snakeHead.getX()+1, Constants.GAME_SIZE));
        else if(down) snakeHead.setY(Math.floorMod(snakeHead.getY()+1, Constants.GAME_SIZE));


        // TODO: rewrite this part
        // if on fruit, add another tail, if not check if you maybe eat yourself
        if(snakeHead.equals(fruit)) {
            snakepart.add(new Pair(snakeHead.getX(), snakeHead.getY()));
            spawnFruit();
            eatenFruits++;
        }
        else {
            for(int i = snakepart.size()-1; i > 0; i--) {
                currentSnakepart = snakepart.get(i);
                if(snakeHead.equals(currentSnakepart)) {
                    gameover = true;
                    return;
                }
            }
        }
    }

    /**
     * spawns a fruit on a random position. Fruit can also be on a snek.
     */
    public void spawnFruit() {
        int x = (int) Math.floor(Math.random()*50);
        int y = (int) Math.floor(Math.random()*50);

        fruit.setX(x);
        fruit.setY(y);
    }

    public void drawMenu() {
        gc.clearRect(0,0, Constants.canvasWidth, Constants.canvasHeight);
        gc.setFill(Color.BLACK);
        gc.setFont(Constants.gameFont);
        gc.fillText("Press Enter to begin", Constants.canvasWidth/2 - 125, Constants.canvasHeight/2);
    }

    public void drawGame() {
        gc.clearRect(0,0, Constants.canvasWidth, Constants.canvasHeight);
        if(gameover) {
            drawLosingScreen();
        }
        else {
            drawFruit();
            drawSnake();
            drawScore();
        }
        drawOutline();
    }

    private void drawLosingScreen() {
        gc.setFill(Color.BLACK);
        gc.setFont(Constants.gameFont);
        gc.fillText("YOU LOST", Constants.canvasWidth/2 -60, Constants.canvasHeight/2);
        gc.fillText("You have eaten " + eatenFruits + " fruits!", Constants.canvasWidth/2 - 140, Constants.canvasHeight/2 + 25);
    }

    private void drawFruit() {
        gc.setFill(Color.RED);
        gc.fillRect(fruit.getX()*Constants.TILE_SIZE, fruit.getY()*Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
    }

    private void drawSnake() {
        gc.setFill(Color.BLACK);
        for (Pair e: snakepart) {
            gc.fillRect(e.getX()*Constants.TILE_SIZE, e.getY()*Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
        }
    }

    private void drawScore() {
        gc.setFill(Color.BLACK);
        gc.setFont(Constants.gameFont);
        gc.fillText("Score: " + eatenFruits, 0,22);
    }

    private void drawOutline() {
        gc.setStroke(Color.BLACK);
        // draws complete grid
        /*for (int i = 0;i <= 50; i++) {
            gc.strokeLine(i*Constants.TILE_SIZE,0,i*Constants.TILE_SIZE, 700);
        }
        for (int i = 0;i <= 50; i++) {
            gc.strokeLine(0,i*Constants.TILE_SIZE,700, i*Constants.TILE_SIZE);
        }*/
        // draws outline
        gc.strokeRect(0,0, Constants.canvasWidth,Constants.canvasHeight);
    }

}
