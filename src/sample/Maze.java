package sample;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Stack;

public class Maze {
    Cell[][] cells;
    int width, height;
    Stack<Cell> stack = new Stack<>();

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;

        this.cells = new Cell[width][height];

        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public void initiateWalls(){
        ArrayList<Cell> unvisitedNeighbors = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        //Choose a starting cell with help of rng
        int startX = random.nextInt(width);
        int startY = random.nextInt(height);

        Cell currentCell = cells[startX][startY];
        currentCell.setVisited(true);

        while (thereAreUnvisitedCells()){
            //First check each neighboring cell if they are unvisited, add them to arraylist if they are
            //don't check all directions if the current cell is on an edge
            if (currentCell.posX > 0) {
                Cell cellWest = cells[currentCell.posX - 1][currentCell.posY];
                if (!cellWest.isVisited()) {
                    unvisitedNeighbors.add(cellWest);
                }
            }
            if (currentCell.posY > 0){
                    Cell cellNorth = cells[currentCell.posX][currentCell.posY-1];
                    if (!cellNorth.isVisited()){
                        unvisitedNeighbors.add(cellNorth);
                    }
            }
            if (currentCell.posX < width-1){
                    Cell cellEast = cells[currentCell.posX+1][currentCell.posY];
                    if (!cellEast.isVisited()){
                        unvisitedNeighbors.add(cellEast);
                    }
            }
            if (currentCell.posY < height-1){
                    Cell cellSouth = cells[currentCell.posX][currentCell.posY+1];
                    if (!cellSouth.isVisited()){
                        unvisitedNeighbors.add(cellSouth);
                    }
            }

            if (!unvisitedNeighbors.isEmpty()){ //If we have an unvisited neighbor we choose one randomly, and put our current Cell on the stack
                Cell chosenCell = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
                stack.push(currentCell);

                //Find out which direction the chosen cell is
                int dX = currentCell.posX - chosenCell.posX;
                int dY = currentCell.posY - chosenCell.posY;
                //Then remove the relevant walls
                if (dX == 1){  //WEST
                    currentCell.walls[3] = false;
                    chosenCell.walls[1] = false;
                } else if (dX == -1){ //EAST
                    currentCell.walls[1] = false;
                    chosenCell.walls[3] = false;
                } else if (dY == 1){ //NORTH
                    currentCell.walls[0] = false;
                    chosenCell.walls[2] = false;
                } else if (dY == -1){ //SOUTH
                    currentCell.walls[2] = false;
                    chosenCell.walls[0] = false;
                }

                unvisitedNeighbors.clear();
                chosenCell.setVisited(true);
                currentCell = chosenCell;

            } else if (!stack.isEmpty()){ //Backtrack if we have a Cell in the stack
                currentCell = stack.pop();
            }
        }

    }

    public boolean thereAreUnvisitedCells(){
        for (int i = 0; i < cells.length; i++){
            for (int j = 0; j < cells[i].length; j++){
                if (!cells[i][j].isVisited()){
                    return true;
                }
            }
        }
        return false;
    }

    public void addExit(){
        //RNG a side and then a cell on that side. Remove the wall that is corresponding to that side on that cell.
        SecureRandom random = new SecureRandom();
        int side = random.nextInt(4);
        int rngCell;

        switch (side){
            case 0: //NORTH
                rngCell = random.nextInt(width);
                cells[rngCell][0].walls[0] = false;
                cells[rngCell][0].setExit(true);
                break;
            case 1: //EAST
                rngCell = random.nextInt(height);
                cells[width-1][rngCell].walls[1] = false;
                cells[width-1][rngCell].setExit(true);
                break;
            case 2: //SOUTH
                rngCell = random.nextInt(width);
                cells[rngCell][height-1].walls[2] = false;
                cells[rngCell][height-1].setExit(true);
                break;
            case 3: //WEST
                rngCell = random.nextInt(height);
                cells[0][rngCell].walls[3] = false;
                cells[0][rngCell].setExit(true);
                break;
        }
    }
}
