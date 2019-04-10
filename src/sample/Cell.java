package sample;
public class Cell {

    int posX, posY;
    private boolean visited, isExit;
    boolean[] walls = new boolean[4];

    public Cell(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.visited = false;
        this.isExit = false;

        for (int i = 0; i < walls.length; i++) { //TRUMP BUILD WALL
            walls[i] = true;
        }
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isExit() {
        return isExit;
    }

    public void setExit(boolean exit) {
        isExit = exit;
    }
}
