package com.example.lab3;

import java.io.Serializable;
import java.util.Arrays;
import java.util.jar.Manifest;

public class LightsModel implements Serializable {

    protected int[][] grid;
    boolean notStrict = true;
    protected int num;
    int score = 0;

    public LightsModel(int n){
        this.num=n;
        grid = new int[this.num][this.num];
    }

    public void tryFlip(int i, int j)
    {
        try {
            if(isSwitchOn(i,j) || notStrict)
                flipLines(i,j);
        }
        catch (Exception e){
        }
    }

    public boolean isSolved(){
        int total = 0;
        for (int i = 0; i < num; i ++){
            for (int j = 0; j < num; j ++){
               total += grid[i][j];
            }
        }
        return total >= Math.pow(num,2) -1;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num;i++){
            sb.append(Arrays.toString(grid[i]) + "\n");
        }
        return sb.toString();
    }

    private boolean isSwitchOn(int i, int j) {
        return false;
    }

    public void flipLines(int i, int j){

        //Set the selected switch first
        grid[i][j] = grid[i][j] > 0 ? 0 :1;

        for(int col = 0 ; col < num; col++)
           if(col != j) grid[i][col] = grid[i][col] > 0 ? 0 :1;

        for(int row = 0 ; row < num; row++)
           if(row != i) grid[row][j] = grid[row][j] > 0 ? 0 :1;
    }

    public int getScore(){
        score = 0;
        for (int i = 0; i < num; i ++){
            for (int j = 0; j < num; j ++){
                score += grid[i][j];
            }
        }
        return score;
    }

    public void reset() {
        if(grid != null)
        {
            grid = new int[MainActivity.n][MainActivity.n];
            System.out.println("Current: " + MainActivity.n);
            score = 0;
        }
    }
}
