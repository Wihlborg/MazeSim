package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;

public class Controller implements Initializable {
    @FXML
    Canvas canvas;
    @FXML
    Button exitButton, generateButton;
    @FXML
    TextField widthField, heightField;

    GraphicsContext gc;
    Stack<Cell> stack = new Stack<>();



    Maze maze = new Maze(20, 20);
    Cell currentCell;
    int cellSize = 30;
    final int OFFSET = 4;

    Timeline timeline;
    boolean isPlaying = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);
        timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> { //do one step every DURATION if we are not finished
            if (maze.thereAreUnvisitedCells() && !currentCell.isExit()) {
                nextStep();
            } })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
    }


    private void drawMaze(GraphicsContext gc) {

        for (int i = 0; i < maze.width; i++) {
            for (int j = 0; j < maze.height; j++) {
                boolean[] walls = maze.cells[i][j].walls;
                if (walls[0]) { //NORTH
                    gc.strokeLine(i * cellSize, j * cellSize, (i + 1) * cellSize, j * cellSize);
                }
                if (walls[1]) { //EAST
                    gc.strokeLine((i + 1) * cellSize, j * cellSize, (i + 1) * cellSize, (j + 1) * cellSize);
                }
                if (walls[2]) { //SOUTH
                    gc.strokeLine(i * cellSize, (j + 1) * cellSize, (i + 1) * cellSize, (j + 1) * cellSize);
                }
                if (walls[3]) { //WEST
                    gc.strokeLine(i * cellSize, j * cellSize, i * cellSize, (j + 1) * cellSize);
                }
            }
        }
    }

    @FXML
    private void generate() {
        try {
            int width = widthField.getText().isEmpty() ? 20 : Integer.parseInt(widthField.getText());
            int height = heightField.getText().isEmpty() ? 20 : Integer.parseInt(heightField.getText());

            if (width > 20 || height > 20){
                double widthSize = canvas.getWidth() / width;
                double heightSize = canvas.getHeight() / height;

                if (widthSize <= heightSize){
                    cellSize = (int) widthSize;
                } else  {
                    cellSize = (int) heightSize;
                }
            }
            timeline.stop();
            maze = new Maze(width, height);
            maze.initiateWalls();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            drawMaze(gc);

        } catch (Exception ex) {
            Alert inputAlert = new Alert(Alert.AlertType.ERROR, "Faulty input you doofus");
            inputAlert.show();
        }
    }

    @FXML
    private void addExit() {
        maze.addExit();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawMaze(gc);
    }

    @FXML
    private void pauseSolve(){
        if (isPlaying){
            timeline.pause();
            isPlaying = false;
        } else {
            timeline.play();
            isPlaying = true;
        }
    }

    @FXML
    private void solve() {
        //randomize a starting position
        SecureRandom rng = new SecureRandom();
        int startX = rng.nextInt(maze.width);
        int startY = rng.nextInt(maze.height);

        //redraw maze
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawMaze(gc);

        //Reset visited
        for (Cell[] cellArray : maze.cells) {
            for (Cell c : cellArray) {
                c.setVisited(false);
            }
        }

        currentCell = maze.cells[startX][startY];
        currentCell.setVisited(true);
        stack.push(currentCell);
        gc.setFill(Color.RED);
        gc.fillRect(currentCell.posX * cellSize + OFFSET, currentCell.posY * cellSize + OFFSET, cellSize - 2*OFFSET, cellSize - 2*OFFSET);

        isPlaying = true;
        timeline.play();
    }


    public void nextStep() {          //do the same thing as when removing walls, but use walls as an extra guard and also fill cells depending on status
        SecureRandom rng = new SecureRandom();
            ArrayList<Integer> possibleDirections = new ArrayList<>();

            for (int i = 0; i < currentCell.walls.length; i++) {
                if (!currentCell.walls[i]) {
                    if (i == 0){ //north
                        if (!maze.cells[currentCell.posX][currentCell.posY-1].isVisited()){
                            possibleDirections.add(i);
                        }
                    }
                    if (i == 1){ //east
                        if (!maze.cells[currentCell.posX+1][currentCell.posY].isVisited()){
                            possibleDirections.add(i);
                        }
                    }
                    if (i == 2){ //south
                        if (!maze.cells[currentCell.posX][currentCell.posY+1].isVisited()){
                            possibleDirections.add(i);
                        }
                    }
                    if (i == 3){ //west
                        if (!maze.cells[currentCell.posX-1][currentCell.posY].isVisited()){
                            possibleDirections.add(i);
                        }
                    }
                }
            }

            if (!possibleDirections.isEmpty()) {
                int chosenDirection = possibleDirections.get(rng.nextInt(possibleDirections.size()));

                Cell chosenCell = null;
                if (chosenDirection == 0) { //north
                    chosenCell = maze.cells[currentCell.posX][currentCell.posY - 1];
                } else if (chosenDirection == 1) { //east
                    chosenCell = maze.cells[currentCell.posX + 1][currentCell.posY];
                } else if (chosenDirection == 2) { //south
                    chosenCell = maze.cells[currentCell.posX][currentCell.posY + 1];
                } else if (chosenDirection == 3) { //west
                    chosenCell = maze.cells[currentCell.posX - 1][currentCell.posY];
                }

                possibleDirections.clear();
                chosenCell.setVisited(true);
                gc.setFill(Color.BLUE);
                gc.fillRect(currentCell.posX * cellSize + OFFSET, currentCell.posY * cellSize + OFFSET , cellSize - 2*OFFSET, cellSize - 2*OFFSET);

                gc.setFill(Color.GREEN);
                gc.fillRect(chosenCell.posX * cellSize + OFFSET, chosenCell.posY * cellSize + OFFSET, cellSize - 2*OFFSET, cellSize - 2*OFFSET);
                stack.push(currentCell);
                currentCell = chosenCell;
                if (currentCell.isExit()){
                    gc.setFill(Color.GOLD);
                    gc.fillRect(chosenCell.posX * cellSize + OFFSET, chosenCell.posY * cellSize + OFFSET, cellSize - 2*OFFSET, cellSize - 2*OFFSET);
                }

            } else if (!stack.isEmpty()) {
                gc.setFill(Color.GRAY);
                gc.fillRect(currentCell.posX * cellSize + OFFSET, currentCell.posY * cellSize + OFFSET, cellSize - 2*OFFSET, cellSize - 2*OFFSET);


                currentCell = stack.pop();
                gc.setFill(Color.RED);
                gc.fillRect(currentCell.posX * cellSize + OFFSET, currentCell.posY * cellSize + OFFSET, cellSize - 2*OFFSET, cellSize - 2*OFFSET);

            }
        }
    }