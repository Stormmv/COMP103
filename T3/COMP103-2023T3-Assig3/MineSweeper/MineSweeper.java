// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 3
 * Name: Michael Visser
 * Username: vissermich
 * ID: 300652084
 */

import ecs100.*;
import java.awt.Color;
import javax.swing.JButton;

/**
 * Simple 'Minesweeper' program.
 * There is a grid of squares, some of which contain a mine.
 * 
 * The user can click on a square to either expose it or to
 * mark/unmark it.
 * 
 * If the user exposes a square with a mine, they lose.
 * Otherwise, it is uncovered, and shows a number which represents the
 * number of mines in the eight squares surrounding that one.
 * If there are no mines adjacent to it, then all the unexposed squares
 * immediately adjacent to it are exposed (and so on)
 *
 * If the user marks a square, then they cannot expose the square,
 * (unless they unmark it first)
 * When all the squares without mines are exposed, the user has won.
 */
public class MineSweeper {

    public static final int ROWS = 15;
    public static final int COLS = 15;

    public static final double LEFT = 10;
    public static final double TOP = 10;
    public static final double SQUARE_SIZE = 20;

    // Fields
    private boolean marking;

    private Square[][] squares;

    private JButton mrkButton;
    private JButton expButton;
    Color defaultColor;

    /** Set up the GUI: buttons and mouse to play the game */
    public void setupGUI() {
        UI.setMouseListener(this::doMouse);
        UI.addButton("New Game", this::makeGrid);
        this.expButton = UI.addButton("Expose", () -> setMarking(false));
        this.mrkButton = UI.addButton("Mark", () -> setMarking(true));

        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0);
        setMarking(false);
        makeGrid();
    }

    /** Respond to mouse events */
    public void doMouse(String action, double x, double y) {
        if (action.equals("released")) {
            int row = (int) ((y - TOP) / SQUARE_SIZE);
            int col = (int) ((x - LEFT) / SQUARE_SIZE);
            if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
                if (marking) {
                    mark(row, col);
                } else {
                    tryExpose(row, col);
                }
            }
        }
    }

    // Other Methods
    /**
     * Mark (or unmark) the square.
     */
    public void mark(int row, int col) {
        Square square = squares[row][col];
        if (squares[row][col].isExposed()) {
            return;
        } else if (squares[row][col].isMarked()) {
            squares[row][col].unMark();
        } else {
            squares[row][col].mark();
        }
        square.draw(row, col);
    }

    /**
     * Respond to the player clicking on a square to expose it
     */
    public void tryExpose(int row, int col) {
        if (hasWon()) {
            drawWin();
        } else if (squares[row][col].isExposed() || squares[row][col].isMarked()) {
            return;
        } else if (squares[row][col].hasMine()) {
            drawLose();
        } else {
            exposeSquareAt(row, col);
        }

    }

    /**
     * Ensures that the square at row and col is exposed.
     */
    public void exposeSquareAt(int row, int col) {
        if (squares[row][col].isExposed()) {
            return;
        } else {
            squares[row][col].setExposed();
            squares[row][col].draw(row, col);
        }

        if (squares[row][col].getAdjacentMines() == 0) {
            for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, ROWS); r++) {
                for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, COLS); c++) {
                    if (r != row || c != col) {
                        exposeSquareAt(r, c);
                    }
                }
            }
        }
    }

    /**
     * Returns true iff the player has won:
     */
    public boolean hasWon() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Square square = squares[row][col];
                if (!square.hasMine() && !square.isExposed()) {
                    return false;
                }
            }
        }
        return true;
    }

    // completed methods

    /**
     * Respond to the Mark and Expose buttons
     */
    public void setMarking(boolean v) {
        marking = v;
        if (marking) {
            mrkButton.setBackground(Color.red);
            expButton.setBackground(null);
        } else {
            expButton.setBackground(Color.red);
            mrkButton.setBackground(null);
        }
    }

    /**
     * Construct and draw a grid with random mines.
     * Compute the number of adjacent mines in each Square
     */
    public void makeGrid() {
        UI.clearGraphics();
        this.squares = new Square[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                boolean isMine = Math.random() < 0.1; // approx 1 in 10 squares is a mine
                this.squares[row][col] = new Square(isMine);
                this.squares[row][col].draw(row, col);
            }
        }
        // now compute the number of adjacent mines for each square
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int count = 0;
                // look at each square in the neighbourhood.
                for (int r = Math.max(row - 1, 0); r < Math.min(row + 2, ROWS); r++) {
                    for (int c = Math.max(col - 1, 0); c < Math.min(col + 2, COLS); c++) {
                        if (squares[r][c].hasMine())
                            count++;
                    }
                }
                if (this.squares[row][col].hasMine())
                    count--; // we weren't suppose to count this square, just the adjacent ones.

                this.squares[row][col].setAdjacentMines(count);
            }
        }
    }

    /**
     * Draw a message telling the player they have won
     */
    public void drawWin() {
        UI.setFontSize(28);
        UI.drawString("You Win!", LEFT + COLS * SQUARE_SIZE + 20, TOP + ROWS * SQUARE_SIZE / 2);
        UI.setFontSize(12);
    }

    /**
     * Draw a message telling the player they have lost
     * and expose all the squares and redraw them
     */
    public void drawLose() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                squares[row][col].setExposed();
                squares[row][col].draw(row, col);
            }
        }
        UI.setFontSize(28);
        UI.drawString("You Lose!", LEFT + COLS * SQUARE_SIZE + 20, TOP + ROWS * SQUARE_SIZE / 2);
        UI.setFontSize(12);
    }

    /**
     * Construct a new MineSweeper object
     * and set up the GUI
     */
    public static void main(String[] arguments) {
        MineSweeper ms = new MineSweeper();
        ms.setupGUI();

        // WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("done all, you have to click again for the has won to work");

    }

}
