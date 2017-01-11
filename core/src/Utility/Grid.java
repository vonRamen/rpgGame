/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author kristian
 */
public class Grid {

    int cellSizeX, cellSizeY;
    int cellsX, cellsY;
    int[][] grid_;

    public Grid(int cellSizeX, int cellSizeY, int cellsX, int cellsY) {
        grid_ = new int[cellSizeY * cellsY][cellsX * cellSizeX];
        this.cellSizeX = cellSizeX;
        this.cellSizeY = cellSizeY;
        this.cellsX = cellsX;
        this.cellsY = cellsY;
        saveImage();
    }

    public static void main(String[] args) {
        new Grid(20, 20, 32, 32);
    }

    private void saveImage() {
        //this takes and array of doubles between 0 and 1 and generates a grey scale image from them

        BufferedImage image = new BufferedImage(grid_.length, grid_[0].length, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < cellSizeY * cellsY; y++) {
            for (int x = 0; x < cellsX * cellSizeX; x++) {
                if (y % cellSizeY == 0 || x % cellSizeX == 0) {
                    grid_[y][x] = 1;
                } else {
                    grid_[y][x] = 0;
                }
                Color col = new Color(grid_[y][x]*166+60, grid_[y][x]*166+60, grid_[y][x]*166+60, 255);
                image.setRGB(x, y, col.getRGB());
            }
        }
        try {
            // retrieve image
            File outputfile = new File("grid.png");
            outputfile.createNewFile();

            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            //o no!
        }
    }

}
