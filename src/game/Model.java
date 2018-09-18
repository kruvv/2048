package game;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    int score;
    int maxTile;
    private boolean isSaveNeeded = true;
    private Stack<Tile[][]> previousStates;
    private Stack<Integer> previousScores;


    public Model() {
        resetGameTiles();
        this.score = 0;
        this.maxTile = 2;
        this.previousScores = new Stack<>();
        this.previousStates = new Stack<>();

    }
    private List<Tile> getEmptyTiles(){
        List<Tile> tileList = new ArrayList<>();
        for (int i = 0; i <gameTiles.length ; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
               if(gameTiles[i][j].value == 0){
                   tileList.add(gameTiles[i][j]);
               }
            }
        }
        return tileList;
    }

    private void addTile(){
        List<Tile> tiles = getEmptyTiles();
        if (tiles != null && tiles.size() != 0){
            tiles.get((int) (tiles.size()*Math.random())).setValue(Math.random() < 0.9 ? 2 : 4);
        }
    }
    public void resetGameTiles(){
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i <gameTiles.length ; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                gameTiles[i][j] = new Tile();
            }

        }
        addTile();
        addTile();
    }
    private boolean compressTiles(Tile[] tiles){
        boolean isFlag = false;
        Tile temp;
        for (int i = 0; i < 3 ; i++) {
            for (int j = 0; j < 3 ; j++) {
                if(tiles[j].getValue() == 0 && tiles[j+1].getValue() != 0){
                    temp = tiles[j];
                    tiles[j] = tiles[j+1];
                    tiles[j+1] = temp;
                    isFlag = true;
                }
            }

        }
        return isFlag;
    }
    private boolean mergeTiles(Tile[] tiles){
        boolean isFlag = false;
        for (int i = 0; i < 3 ; i++) {
            if(tiles[i].getValue() != 0 && tiles[i].getValue() == tiles[i+1].getValue()) {
                tiles[i].setValue(tiles[i].getValue() * 2);
                tiles[i + 1].setValue(0);

                if (tiles[i].getValue() > maxTile) {
                    maxTile = tiles[i].getValue();
                }
                    score += tiles[i].getValue();
                isFlag = true;
                
            }
            Tile temp;
            for (int j = 0; j < 3; j++) {
                if(tiles[j].getValue() == 0 && tiles[j+1].getValue() != 0){
                    temp = tiles[j];
                    tiles[j] = tiles[j+1];
                    tiles[j+1] = temp;
                }
            }
        }
        return isFlag;

    }

    public void left(){
        if (isSaveNeeded) saveState(this.gameTiles);
        boolean isFlag = false;
        for (int i = 0; i <FIELD_WIDTH ; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])){
                isFlag = true;
            }

        }
        if(isFlag) addTile();
        isSaveNeeded = true;
    }
    public void right(){
        saveState(this.gameTiles);
        rotate();
        rotate();
        left();
        rotate();
        rotate();
    }
    public void up(){
        saveState(this.gameTiles);
        rotate();
        left();
        rotate();
        rotate();
        rotate();
    }
    public void down(){
        saveState(this.gameTiles);
        rotate();
        rotate();
        rotate();
        left();
        rotate();
    }

    private void rotate(){
        int x = FIELD_WIDTH;
        for (int i = 0; i < x/2 ; i++) {
            for (int j = i; j < x - 1 - i; j++) {
                Tile tmp = gameTiles[i][j];
                gameTiles[i][j] = gameTiles[j][x - 1 - i];
                gameTiles[j][x - 1 - i] = gameTiles[x - 1 - i][x - 1 - j];
                gameTiles[x - 1 - i][x - 1 - j] = gameTiles[x - 1 - j][i];
                gameTiles[x - 1 - j][i] = tmp;
            }
            
        }
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove(){
        if(!getEmptyTiles().isEmpty()) return true;
        for (int i = 0; i <gameTiles.length ; i++) {
            for (int j = 1; j < gameTiles.length; j++) {
                if(gameTiles[i][j].value == gameTiles[i][j - 1].value)
                    return true;

            }

        }for (int j = 0; j < gameTiles.length; j++) {
            for (int i = 1; i < gameTiles.length; i++) {
                if (gameTiles[i][j].value == gameTiles[i - 1][j].value)
                    return true;
            }
        }
            return false;
    }

    private void saveState(Tile[][] tiles){
       Tile[][] newTitle = new Tile[tiles.length][tiles[0].length];
        for (int i = 0; i <tiles.length ; i++) {
            for (int j = 0; j <tiles[0].length ; j++) {
                newTitle[i][j] = new Tile(tiles[i][j].getValue());
            }

        }
       previousStates.push(newTitle);
        int newScore = score;
        previousScores.push(newScore);
       isSaveNeeded = false;
    }

    public void rollback(){
        if(!previousStates.isEmpty() && !previousScores.isEmpty()){
            this.gameTiles = previousStates.pop();
            this.score = previousScores.pop();
        }
    }

    public void randomMove(){
       switch (((int) (Math.random() * 100)) % 4){
           case 0: left(); break;
           case 1: right(); break;
           case 2: up(); break;
           case 3: down(); break;
       }
    }
    private boolean hasBoardChanged(){
           // boolean result = false;
            int sumNow = 0;
            int sumPrevious = 0;
            Tile[][] temp = previousStates.peek();
        for (int i = 0; i <gameTiles.length ; i++) {
            for (int j = 0; j <gameTiles[0].length ; j++) {
                sumNow += gameTiles[i][j].getValue();
                sumPrevious += temp[i][j].getValue();

            }

        }return sumNow != sumPrevious;
    }

    private MoveEfficiency getMoveEfficiency(Move move){
       MoveEfficiency efficiency;
       move.move();
       if(hasBoardChanged()) efficiency = new MoveEfficiency(getEmptyTiles().size(),score,move);
       else efficiency = new MoveEfficiency(-1, 0 , move);
       rollback();

       return efficiency;
    }

    public void autoMove(){
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.add(getMoveEfficiency(this::left));
        queue.add(getMoveEfficiency(this::right));
        queue.add(getMoveEfficiency(this::up));
        queue.add(getMoveEfficiency(this::down));

        Move move = queue.peek().getMove();
        move.move();
    }
}
