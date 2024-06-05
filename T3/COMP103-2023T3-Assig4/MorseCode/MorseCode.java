// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 4
 * Name: Michael Visser
 * Username: vissermich
 * ID: 300652084
 */

/**
 * Implements a binary tree that represents the Morse code symbols, named after its inventor
 *   Samuel Morse.
 * Each Morse code symbol is formed by a sequence of dots and dashes.
 *
 * A Morse code chart has been provided with this assignment. This chart only contains the 26 letters
 * and 10 numerals. These are given in alphanumerical order. 
 *
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class MorseCode {

    public SymbolNode root = new SymbolNode(null, // root of the morse code binary tree;
            new SymbolNode("E",
                    new SymbolNode("I"),
                    new SymbolNode("A")),
            new SymbolNode("T",
                    new SymbolNode("N"),
                    new SymbolNode("M")));

    /**
     * Setup the GUI and creates the morse code with characters up to 2 symbols
     */
    public static void main(String[] args) {
        new MorseCode().setupGUI();

        // WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("Completed: Core, Completion");

    }

    /**
     * Set up the interface
     */
    public void setupGUI() {
        UI.addButton("Print Tree", this::printTree);
        UI.addTextField("Decode ", this::decode);
        UI.addTextField("Add Code (Core)", this::addCodeCore);
        UI.addTextField("Add Code (Compl)", this::addCodeCompl);
        UI.addButton("Load File", this::loadFile);
        UI.addButton("Reset", () -> {
            root = new SymbolNode();
        });
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000, 400);
        UI.setDivider(0.25);
    }

    /**
     * Decode a code by starting at the top (root), and working
     * down the tree following the dot or dash nodes according to the
     * code
     */
    public void decode(String code) {
        if (!isValidCode(code)) {
            return;
        }
        SymbolNode node = root;
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (ch == '.') {
                node = node.getDotChild();
            } else if (ch == '-') {
                node = node.getDashChild();
            }
            if (node == null) {
                UI.println("Code not found");
                return;
            }
        }
        UI.println("Symbol: " + node.getSymbol());
    }

    /**
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "dot" subtree, and then
     * its "dash" subtree.
     * Each node should be indented by how deep it is in the tree.
     * Needs a recursive "helper method" which is passed a node and an indentation
     * string.
     * (The indentation string will be a string of space characters plus the morse
     * code leading
     * to the node)
     */
    public void printTree() {
        printTree(root, "");
    }

    public void printTree(SymbolNode node, String indent) {
        if (node == null) {
            return;
        }
        UI.println(indent + " = " + node.getSymbol());
        printTree(node.getDotChild(), "  " + indent + ".");
        printTree(node.getDashChild(), "  " + indent + "-");
    }

    /**
     * Add a new code to the tree (as long as it will be in a node just below an existing node).
     * Follows the code down the tree (like decode)
     * If it finds a node for the code, then it reports the current symbol for that code
     * If it it gets to a node where there is no child for the next . or - in the code then
     *  If this is the last . or - in the code, it asks for a new symbol and adds a new node
     *  If there is more than one . or - left in the code, it gives up and says it can't add it.
     * For example,
     *  If it is adding the code (.-.) and the tree has "A" (.-) but doesn't have (.-.),
     *   then it should ask for the symbol (R) add a child node with R
     *  If it is adding the code (.-..) and the tree has "A" (.-) but doesn't have (.-.),
     *   then it would not attempt to add a node for (.-..) (L) because that would require
     *   adding more than one node.
     */
    public void addCodeCore(String code) {
        if (!isValidCode(code)) {
            return;
        }
        SymbolNode node = root;
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (ch == '.') {
                if (node.getDotChild() == null) {
                    if (i == code.length() - 1) {
                        UI.println("New symbol for " + code);
                        String symbol = UI.askString("New symbol for " + code);
                        node.setDotChild(new SymbolNode(symbol));
                    } else {
                        UI.println("Can't add " + code);
                        return;
                    }
                }
                node = node.getDotChild();
            } else if (ch == '-') {
                if (node.getDashChild() == null) {
                    if (i == code.length() - 1) {
                        UI.println("New symbol for " + code);
                        String symbol = UI.askString("New symbol for " + code);
                        node.setDashChild(new SymbolNode(symbol));
                    } else {
                        UI.println("Can't add " + code);
                        return;
                    }
                }
                node = node.getDashChild();
            }
        }
        UI.println("Symbol: " + node.getSymbol());
    }

    // COMPLETION ======================================================
    /**
     * Grow the tree by allowing the user to add any symbol, whether there is a path
     * leading to it.
     * Like addCodeCore, it starts at the top (root), and works its way down the
     * tree
     * following the dot or dash nodes according to the given sequence of the code.
     * If an intermediate node does not exist, it needs to be created with a text
     * set to null.
     */
    public void addCodeCompl(String code) {
        if (!isValidCode(code)) {
            return;
        }
        SymbolNode node = root;
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            if (ch == '.') {
                if (node.getDotChild() == null) {
                    node.setDotChild(new SymbolNode());
                }
                node = node.getDotChild();
            } else if (ch == '-') {
                if (node.getDashChild() == null) {
                    node.setDashChild(new SymbolNode());
                }
                node = node.getDashChild();
            }
        }
        if (node.getSymbol() == null) {
            UI.println("New symbol for " + code);
            String symbol = UI.askString("New symbol for " + code);
            node.setSymbol(symbol);
        } else {
            UI.println("Symbol: " + node.getSymbol());
        }
    }

    /**
     * Load a collection of symbols and their codes from a file
     * Each line contains the symbol and the corresponding morse code.
     */
    public void loadFile() {
        try {
            Scanner sc = new Scanner(new File(UIFileChooser.open()));
            while (sc.hasNext()) {
                String symbol = sc.next();
                String code = sc.next();
                SymbolNode node = root;
                for (int i = 0; i < code.length(); i++) {
                    char ch = code.charAt(i);
                    if (ch == '.') {
                        if (node.getDotChild() == null) {
                            node.setDotChild(new SymbolNode());
                        }
                        node = node.getDotChild();
                    } else if (ch == '-') {
                        if (node.getDashChild() == null) {
                            node.setDashChild(new SymbolNode());
                        }
                        node = node.getDashChild();
                    }
                }
                node.setSymbol(symbol);
            }
            printTree();
        } catch (IOException e) {
            UI.println("File reading failed: " + e);
        }
    }

    // Utility methods ===============================================

    /**
     * Checks whether the code is a sequence of . and - only.
     */
    public boolean isValidCode(String code) {
        if (code.equals("")) {
            UI.println("Code is empty");
            return false;
        }
        for (int index = 0; index < code.length(); index++) {
            char c = code.charAt(index);
            if (c != '-' && c != '.') {
                UI.println("Code has an invalid character: " + c);
                return false;
            }
        }
        return true;
    }
}
