import java.awt.*;
import java.io.*;
import java.util.*;
/**
 * This DataPlotter object reads a space delimited text file of elevations
 * and plots the result as a range of greyscale images, and then calculates 
 * and plots the steepest downward path from each location in the image.
 * 
 * @author 
 * @version 
 */
public class DataPlotter
{
    private static String fileName = "Colorado";
    private static int[][] grid;
    private static DrawingPanel panel;
    private static Scanner fileReader;
    private static int rows, cols;

    public static void main(String[] args) throws IOException
    {
        readValues();
        plotData();
        try {Thread.sleep(3000); } catch (Exception e){};  // pause display for 3 seconds
        plotAllPaths();
    }

    private static void readValues() throws IOException
    {
        fileReader = new Scanner(new File(fileName + ".dat"));
        rows = fileReader.nextInt();    // first integer in file
        cols = fileReader.nextInt();    // second integer in file

        // instantiate and initialize the instance variable grid 
        grid = new int[rows][cols];
        // then read all of the data into the array in row major order
        for(int r = 0; r < grid.length; r++)
        {
            for(int c = 0; c < grid[r].length; c++)
            {
                grid[r][c] = fileReader.nextInt();
            }
        }
        System.out.println("upper left: " + grid[0][0]);
        System.out.println("upper right: " + grid[0][cols-1]);
        System.out.println("lower left: " + grid[rows-1][0]);
        System.out.println("lower right: " + grid[rows-1][cols-1]);

    }

    // plot the altitude data read from file
    private static void plotData()
    {
        int max = grid[0][0];
        int min = grid[0][0];
        panel = new DrawingPanel(cols, rows);
        for(int r = 0; r< grid.length; r++)
        {
            for(int c = 0; c< grid[r].length; c++)
            {
                min = Math.min(min, grid[r][c]);
                max = Math.max(max, grid[r][c]);
            }
        }
        double scale = 255.0/(max-min);
        for(int r = 0; r < rows; r++)
        {
            for(int c = 0; c < cols; c++)
            {
                if(grid[r][c] == min)
                {
                    Color color = new Color(0, 0, 0);
                    panel.setPixel(c,r, color);
                } 
                else if(grid[r][c] == max)
                {
                    Color color = new Color(255,255,255);
                    panel.setPixel(c,r, color);
                }
                else
                {
                    int val = (int)((grid[r][c] - min) * scale);
                    Color color = new Color(val, val, val);
                    panel.setPixel(c,r, color);
                }
            }
        }
    }

    // for a given x, y value, plot the downhill path from there
    private static void plotDownhillPath(int x, int y)
    {
        int curr = grid[y][x];
        int ne = curr;
        int n = curr; 
        int e = curr;
        int se = curr;
        int s = curr;
        int sw = curr;
        int w = curr;
        int nw = curr;
        ArrayList<int[]> arr = new ArrayList<int[]>();
        int[] add = new int[3];
        if(x == 0 && y == 0)///left upper cornerof array
        {
            //no n, ne, nw, w, sw
            e = grid[y][x+1];
            s = grid[y+1][x];
            se = grid[y+1][x+1];
            arr.add(create(e, y, x+1));
            arr.add(create(s, y+1, x));
            arr.add(create(se, y+1, x+1));
        }
        else if(x == cols - 1 && y == 0)//upperright corner or array
        {
            //no e, se, ne, n, nw
            w = grid[y][x-1];
            sw = grid[y+1][x-1];
            s = grid[y+1][x];
            arr.add(create(w, y, x-1));
            arr.add(create(sw, y+1, x-1));
            arr.add(create(s, y+1, x));
        }
        else if(y == rows - 1 && x == 0)//lower left corner of the array
        {
            //no s, se, sw, w, nw
            e = grid[y][x+1];
            n = grid[y-1][x];
            ne = grid[y-1][x+1];
            arr.add(create(e, y, x+1));
            arr.add(create(n, y-1, x));
            arr.add(create(ne, y-1, x+1));
        }
        else if(x == cols - 1 && y == rows - 1)//lower right corner or array
        {
            //no e, ne, se, s, sw
            w = grid[y][x-1];
            nw = grid[y-1][x-1];
            n = grid[y-1][x];
            arr.add(create(w, y, x-1));
            arr.add(create(n, y-1, x));
            arr.add(create(nw, y-1, x-1));
        }
        else if(y == 0)//first row of the array
        {
            //no ne, n, nw
            w = grid[y][x-1];
            sw = grid[y+1][x-1];
            s = grid[y+1][x];
            e = grid[y][x+1];
            se = grid[y+1][x+1];
            arr.add(create(w, y, x-1));
            arr.add(create(sw, y+1, x-1));
            arr.add(create(s, y+1, x));
            arr.add(create(e, y, x+1));
            arr.add(create(se, y+1, x+1));
        }
        else if(y == (rows-1))//last row of the array
        {
            //no se, s, sw
            w = grid[y][x-1];
            nw = grid[y-1][x-1];
            n = grid[y-1][x];
            e = grid[y][x+1];
            ne = grid[y-1][x+1];
            arr.add(create(w, y, x-1));
            arr.add(create(nw, y-1, x-1));
            arr.add(create(n, y-1, x));
            arr.add(create(e, y, x+1));
            arr.add(create(ne, y-1, x+1));
        }
        else if(x == (rows-1))//last col of the array
        {
            //no se, e, ne
            w = grid[y][x-1];
            s = grid[y+1][x];
            n = grid[y-1][x];
            sw = grid[y+1][x-1];
            nw = grid[y-1][x-1];
            arr.add(create(w, y, x-1));
            arr.add(create(nw, y-1, x-1));
            arr.add(create(n, y-1, x));
            arr.add(create(s, y+1, x));
            arr.add(create(sw, y+1, x-1));
        }
        else if(x == 0)//firsr col of the array
        {
            //no sw, w, nw
            e = grid[y][x+1];
            s = grid[y+1][x];
            n = grid[y-1][x];
            se = grid[y+1][x+1];
            ne = grid[y-1][x+1];
            arr.add(create(e, y, x+1));
            arr.add(create(ne, y-1, x+1));
            arr.add(create(n, y-1, x));
            arr.add(create(s, y+1, x));
            arr.add(create(se, y+1, x+1));
        }
        else if (x != (cols-1) && x != 0 && y != 0 && y != (rows-1))//all other places
        //else
        {
            e = grid[y][x+1];
            s = grid[y+1][x];
            n = grid[y-1][x];
            w = grid[y][x-1];
            sw = grid[y+1][x-1];
            nw = grid[y-1][x-1];
            se = grid[y+1][x+1];
            ne = grid[y-1][x+1];
            arr.add(create(e, y, x+1));
            arr.add(create(ne, y-1, x+1));
            arr.add(create(n, y-1, x));
            arr.add(create(s, y+1, x));
            arr.add(create(se, y+1, x+1));
            arr.add(create(w, y, x-1));
            arr.add(create(nw, y-1, x-1));
            arr.add(create(sw, y+1, x-1));
        }
        //print(arr);
        int min = curr;
        int indexMin = 0;
        for(int i = 0; i < arr.size(); i++)
        {
            if(min >= arr.get(i)[0])
            {
                min = arr.get(i)[0];
                indexMin = i;
            }
        }
        if(min != curr)
        {
            x = arr.get(indexMin)[2];
            y = arr.get(indexMin)[1];
            panel.setPixel(x, y, Color.blue);
            plotDownhillPath(x,y);
        }
        //= grid[y+1][x];
        /*
        int minX = Math.max(x - 1, 0);
        int minY = Math.max(y - 1, 0);
        int maxX = Math.min(x + 1, cols);
        int maxY = Math.min(y + 1, rows);
        int lowestX = x;
        int lowestY = y;
        for(int newY = minY; newY < maxY; newY++)
        {
            for(int newX = minX; newX < maxX; newX++)
            {
                if(grid[newY][newX] <= grid[lowestY][lowestX])
                {
                    lowestX = newX;
                    lowestY = newY;
                }
            }
        }
        if(lowestX != x || lowestY != y)
        {
            x = lowestX;
            y = lowestY;
            panel.setPixel(x, y, Color.blue);
            plotDownhillPath(x, y);
        }*/
    }
    
    private static void print(ArrayList<int[]> a)
    {
        for(int r = 0; r < a.size(); r++)
        {   
            System.out.println("data: " + a.get(r)[0] + " y: " +a.get(r)[1]+" x: "+a.get(r)[2]);
            
        }
    }
    private static int[] create(int a, int b, int c)
    {
        int[] s = {a, b, c};
        return s; 
    }

    private static void plotAllPaths()
    {
        for(int r = 0; r < grid.length; r++)
        {
            for(int c = 0; c < grid[r].length; c++)    
            {    
                plotDownhillPath(c, r);
            }
        }
    }

}