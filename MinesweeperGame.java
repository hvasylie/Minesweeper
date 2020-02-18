// THIS FILE MUST INCLUDE THE ENGINE THAT IT WAS BUILT WITH.
// .jar file with the engine is included

package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import javafx.geometry.Side;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private static final String MINE = "*BOOM*";
    private static final String FLAG = "¶";
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles = SIDE * SIDE;
    private int score = 0;
    private boolean isGameStopped = false;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
//        isGameStopped = false;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.AQUA);
                setCellValue(x, y, "");
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors(){
        for(int y = 0; y < gameField.length; y++){
            for (int x = 0; x < gameField[y].length; x++){
                if (!gameField[y][x].isMine){
                    List<GameObject> list = getNeighbors(gameField[y][x]);
                    for (int i = 0; i < list.size(); i++){
                        if(list.get(i).isMine)
                            gameField[y][x].countMineNeighbors++;
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {

        if(gameField[y][x].isOpen) return;
        else if(gameField[y][x].isFlag) return;
        else if(isGameStopped) return;


        if (gameField[y][x].isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameField[y][x].isOpen = true;
            countClosedTiles--;
            gameOver();
        }
        else if (gameField[y][x].countMineNeighbors == 0) {
            setCellValue(x, y, "");
            setCellColor(x, y, Color.WHITESMOKE);
            gameField[y][x].isOpen = true;
            score += 5;
            countClosedTiles--;
            setScore(score);
            List<GameObject> neighbors = getNeighbors(gameField[y][x]);
            for (GameObject gameObject : neighbors) {
                if (!gameObject.isOpen) {
                    openTile(gameObject.x, gameObject.y);
                }
            }
        } else {
            setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            setCellColor(x, y, Color.WHITESMOKE);
            gameField[y][x].isOpen = true;
            score += 5;
            setScore(score);
            countClosedTiles--;

        }
        if (countClosedTiles == countMinesOnField && !gameField[y][x].isMine) win();
    }

    private void markTile(int x, int y){
        if(gameField[y][x].isOpen) return;

        if(countFlags == 0 && !gameField[y][x].isFlag) return;

        if(!gameField[y][x].isFlag && !isGameStopped) {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.LIGHTGOLDENRODYELLOW);
        }
        else if(gameField[y][x].isFlag && !isGameStopped){
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.AQUA);
        }
    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.BLACK, " TRY AGAIN HOMIE ", Color.RED, 50);
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.BEIGE, " YOU WIN! CONGRATS ", Color.GREEN, 50);
    }

    private void restart(){
        isGameStopped = false;
        countMinesOnField = 0;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        createGame();

    }

    @Override
    public void onMouseLeftClick(int x, int y) {

        super.onMouseLeftClick(x, y);
        if(!isGameStopped)
            openTile(x, y);
        else restart();
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);
    }
}
