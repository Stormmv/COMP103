// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 5
 * Name: Michael Visser
 * Username: vissermich
 * ID: 300652084
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a
 * tree.
 * You have to write the method to evaluate an expression tree.
 * and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator {

    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args) {
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();

        // WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("core, completion");

        calc.runCalculator();
    }

    /**
     * Setup the GUI
     */
    public void setupGUI() {
        UI.addButton("Clear", UI::clearText);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever: (a REPL - Read Eval Print Loop)
     * - read an expression,
     * - evaluate the expression,
     * - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the program -
     * the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator() {
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true) {
            UI.println();
            try {
                GTNode<ExpElem> expr = readExpr();
                double value = evaluate(expr);
                UI.println(" -> " + value);
            } catch (Exception e) {
                UI.println("Something went wrong! " + e);
            }
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     * => just return the value of the number
     * or it is a named constant
     * => return the appropriate value
     * or it is an operator node with children
     * => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr) {
        if (expr == null) {
            return Double.NaN;
        }
        ExpElem elem = expr.getItem();
        if (elem.getOperator() == "#") {
            return elem.getValue();
        } else if (elem.getOperator().equals("PI")) {
            return Math.PI;
        } else if (elem.getOperator().equals("E")) {
            return Math.E;
        } else if (elem.getOperator().equals("+")) {
            double val = 0;
            for (GTNode<ExpElem> child : expr) {
                val += evaluate(child);
            }
            return val;
        } else if (elem.getOperator().equals("-")) {
            double val = evaluate(expr.getChild(0));
            for (int i = 1; i < expr.numberOfChildren(); i++) {
                val -= evaluate(expr.getChild(i));
            }
            return val;
        } else if (elem.getOperator().equals("*")) {
            double val = 1;
            for (GTNode<ExpElem> child : expr) {
                val *= evaluate(child);
            }
            return val;
        } else if (elem.getOperator().equals("/")) {
            double val = evaluate(expr.getChild(0));
            for (int i = 1; i < expr.numberOfChildren(); i++) {
                val /= evaluate(expr.getChild(i));
            }
            return val;
        } else if (elem.getOperator().equals("^")) {
            double val = evaluate(expr.getChild(0));
            for (int i = 1; i < expr.numberOfChildren(); i++) {
                val = Math.pow(val, evaluate(expr.getChild(i)));
            }
            return val;
        } else if (elem.getOperator().equals("sqrt")) {
            double val = evaluate(expr.getChild(0));
            return Math.sqrt(val);
        } else if (elem.getOperator().equals("log")) {
            if (expr.numberOfChildren() == 2) {
                double val1 = evaluate(expr.getChild(0));
                double val2 = evaluate(expr.getChild(1));
                return Math.log(val1) / Math.log(val2);
            } else if (expr.numberOfChildren() == 1) {
                double val = evaluate(expr.getChild(0));
                return Math.log(val);
            } else {
                UI.println("Too many operands for log");
                return Double.NaN;
            }
        } else if (elem.getOperator().equals("sin")) {
            if (expr.numberOfChildren() == 1) {
                double val = evaluate(expr.getChild(0));
                return Math.sin(val);
            } else {
                UI.println("Too many operands for sin");
                return Double.NaN;
            }
        } else if (elem.getOperator().equals("cos")) {
            if (expr.numberOfChildren() == 1) {
                double val = evaluate(expr.getChild(0));
                return Math.cos(val);
            } else {
                UI.println("Too many operands for cos");
                return Double.NaN;
            }
        } else if (elem.getOperator().equals("tan")) {
            if (expr.numberOfChildren() == 1) {
                double val = evaluate(expr.getChild(0));
                return Math.tan(val);
            } else {
                UI.println("Too many operands for tan");
                return Double.NaN;
            }
        } else {
            UI.println("Operator does not exist");
            return Double.NaN;
        }
    }

    /**
     * Reads an expression from the user and constructs the tree.
     */
    public GTNode<ExpElem> readExpr() {
        String expr = UI.askString("expr:");
        return readExpr(new Scanner(expr)); // the recursive reading method
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc) {
        if (sc.hasNextDouble()) { // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        } else if (sc.hasNext("\\(")) { // next token is an opening bracket
            sc.next(); // read and throw away the opening '('
            ExpElem opElem = new ExpElem(sc.next()); // read the operator
            GTNode<ExpElem> node = new GTNode<ExpElem>(opElem); // make the node, with the operator in it.
            while (!sc.hasNext("\\)")) { // loop until the closing ')'
                GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                node.addChild(child); // and add as a child of the node
            }
            sc.next(); // read and throw away the closing ')'
            return node;
        } else { // next token must be a named constant (PI or E)
                 // make a token with the name as the "operator"
            return new GTNode<ExpElem>(new ExpElem(sc.next()));
        }
    }

}
