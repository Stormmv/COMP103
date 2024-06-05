// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 5
 * Name: Michael Visser
 * Username: vissermich
 * ID: 300652084
 */

import ecs100.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class BusNetworks {

    /** Map of towns, indexed by their names */
    private Map<String, Town> busNetwork = new HashMap<String, Town>();

    /**
     * CORE
     * Loads a network of towns from a file.
     * Constructs a Map of Town objects in the busNetwork field
     * Each town has a name and a set of neighbouring towns
     * First line of file contains the names of all the towns.
     * Remaining lines have pairs of names of towns that are connected.
     */
    public void loadNetwork(String filename) {
        try {
            busNetwork.clear();
            UI.clearText();
            List<String> lines = Files.readAllLines(Path.of(filename));
            String firstLine = lines.remove(0);
            Scanner sc = new Scanner(firstLine);
            while (sc.hasNext()) {
                String name = sc.next();
                busNetwork.put(name, new Town(name));
            }
            for (String line : lines) {
                sc = new Scanner(line);
                String name1 = sc.next();
                String name2 = sc.next();
                Town town1 = busNetwork.get(name1);
                Town town2 = busNetwork.get(name2);
                if (town1 == null || town2 == null) {
                    UI.println("Error: " + name1 + " or " + name2 + " is not a recognised town");
                } else {
                    town1.addNeighbour(town2);
                    town2.addNeighbour(town1);
                }
            }
            UI.println("Loaded " + busNetwork.size() + " towns:");

        } catch (IOException e) {
            throw new RuntimeException("Loading data.txt failed" + e);
        }
    }

    /**
     * CORE
     * Print all the towns and their neighbours:
     * Each line starts with the name of the town, followed by
     * the names of all its immediate neighbours,
     */
    public void printNetwork() {
        UI.println("The current network: \n====================");
        for (String name : busNetwork.keySet()) {
            Town town = busNetwork.get(name);
            UI.print(town.getName() + " -> ");
            for (Town neighbour : town.getNeighbours()) {
                UI.print(neighbour.getName() + " ");
            }
            UI.println();
        }
        UI.println("====================");
    }

    /**
     * CORE
     * Print out the towns on a route (not necessarily the shortest)
     * from a starting town to a destination town.
     * OK to print the towns on the route in reverse order.
     * Use a recursive (post-order) depth first search.
     * Use a helper method with a visited set.
     */
    public void findRoute(Town town, Town dest) {
        UI.println("Looking for route between " + town.getName() + " and " + dest.getName() + ":");
        findRouteHelper(town, dest, new HashSet<Town>());
    }

    public boolean findRouteHelper(Town town, Town dest, Set<Town> visited) {
        if (town == dest) {
            UI.println(town.getName());
            return true;
        }
        visited.add(town);
        for (Town neighbour : town.getNeighbours()) {
            if (!visited.contains(neighbour)) {
                if (findRouteHelper(neighbour, dest, visited)) {
                    UI.println(town.getName());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * COMPLETION
     * Print all the towns that are reachable through the network from
     * the given town. The Towns should be printed in order of distance from the
     * town
     * where distance is the number of stops along the way.
     */
    public void printReachable(Town town) {
        UI.println("\nFrom " + town.getName() + " you can get to:");
        Set<Town> visited = new HashSet<Town>();
        Queue<Town> queue = new LinkedList<Town>();
        queue.add(town);
        visited.add(town);
        while (!queue.isEmpty()) {
            Town currentTown = queue.poll();
            UI.println(currentTown.getName());
            for (Town neighbour : currentTown.getNeighbours()) {
                if (!visited.contains(neighbour)) {
                    queue.add(neighbour);
                    visited.add(neighbour);
                }
            }
        }
    }

    /**
     * COMPLETION
     * Print all the connected sets of towns in the busNetwork
     * Each line of the output should be the names of the towns in a connected set
     * Works through busNetwork, using findAllConnected on each town that hasn't
     * yet been printed out.
     */
    public void printConnectedGroups() {
        UI.println("Groups of Connected Towns: \n================");
        int groupNum = 1;
        Set<Town> printed = new HashSet<Town>();
        for (String name : busNetwork.keySet()) {
            Town town = busNetwork.get(name);
            if (!printed.contains(town)) {
                UI.println("Group " + groupNum + ":");
                Set<Town> reachable = findAllConnected(town);
                for (Town t : reachable) {
                    UI.print(t.getName() + " ");
                    printed.add(t);
                }
                UI.println();
                groupNum++;
            }
        }
        UI.println("================");

    }

    // Suggested helper method for printConnectedGroups
    /**
     * Return a set of all the nodes that are connected to the given node.
     * Traverse the network from this node using a
     * visited set, and then return the visited set.
     */
    public Set<Town> findAllConnected(Town town) {
        Set<Town> visited = new HashSet<Town>();
        Stack<Town> stack = new Stack<Town>();
        stack.push(town);
        while (!stack.isEmpty()) {
            Town currentTown = stack.pop();
            if (!visited.contains(currentTown)) {
                visited.add(currentTown);
                for (Town neighbour : currentTown.getNeighbours()) {
                    stack.push(neighbour);
                }
            }
        }
        return visited;

    }

    /**
     * Set up the GUI (buttons and mouse)
     */
    public void setupGUI() {
        UI.addButton("Load", () -> {
            loadNetwork(UIFileChooser.open());
        });
        UI.addButton("Print Network", this::printNetwork);
        UI.addButton("Find Route", () -> {
            findRoute(askTown("From"), askTown("Destination"));
        });
        UI.addButton("All Reachable", () -> {
            printReachable(askTown("Town"));
        });
        UI.addButton("All Connected Groups", this::printConnectedGroups);
        UI.addButton("Clear", UI::clearText);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100, 500);
        UI.setDivider(1.0);
        loadNetwork("data-small.txt");
    }

    // Main
    public static void main(String[] arguments) {
        new BusNetworks().setupGUI();

        // WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("core, completion");

    }

    // Utility method
    /**
     * Method to get a Town from a dialog box with a list of options
     */
    public Town askTown(String question) {
        Object[] possibilities = busNetwork.keySet().toArray();
        Arrays.sort(possibilities);
        String townName = (String) javax.swing.JOptionPane.showInputDialog(UI.getFrame(),
                question, "",
                javax.swing.JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                possibilities[0].toString());
        return busNetwork.get(townName);
    }

}
