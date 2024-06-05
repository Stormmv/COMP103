// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 2
 * Name: Michael Visser
 * Username: vissermich
 * ID: 300652084
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * EarthquakeSorter
 * Sorts data about a collection of 4335 NZ earthquakes from May 2016 to May
 * 2017
 * Each line of the file "earthquake-data.txt" has a description of one
 * earthquake:
 * ID time longitude latitude magnitude depth region
 * Data is from http://quakesearch.geonet.org.nz/
 * Note the earthquakes' ID have been modified to suit this assignment.
 * Note bigearthquake-data.txt has just the 421 earthquakes of magnitude 4.0 and
 * above
 * which may be useful for testing, since it is not as big as the full file.
 * 
 */

public class EarthquakeSorter {

    private List<Earthquake> earthquakes = new ArrayList<Earthquake>();

    /**
     * Load data from the specified data file into the earthquakes field:
     */
    public void loadData(String filename) {
        try {
            UI.clearText();
            UI.println("Loading data...");
            UI.println("----------------------------");
            Scanner scan = new Scanner(new File(filename));
            while (scan.hasNext()) {
                String ID = scan.next();
                String date = scan.next();
                String time = scan.next();
                double longitude = scan.nextDouble();
                double latitude = scan.nextDouble();
                double magnitude = scan.nextDouble();
                double depth = scan.nextDouble();
                String region = scan.nextLine().trim();
                Earthquake eq = new Earthquake(ID, date, time, longitude, latitude, magnitude, depth, region);
                this.earthquakes.add(eq);
            }
            scan.close();
            UI.printf("Loaded %d earthquakes into list\n", this.earthquakes.size());
            UI.println("----------------------------");
        } catch (IOException e) {
            UI.println("File reading failed");
        }
    }

    /**
     * Sorts the earthquakes by ID
     */
    public void sortByID() {
        UI.clearText();
        UI.println("Earthquakes sorted by ID");
        Collections.sort(this.earthquakes);
        for (Earthquake e : this.earthquakes) {
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the earthquakes by magnitude, largest first
     */
    public void sortByMagnitude() {
        UI.clearText();
        UI.println("Earthquakes sorted by magnitude (largest first)");
        Collections.sort(this.earthquakes, (e1, e2) -> Double.compare(e2.getMagnitude(), e1.getMagnitude()));

        for (Earthquake e : this.earthquakes) {
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the list of earthquakes according to the date and time that they
     * occurred.
     */
    public void sortByTime() {
        UI.clearText();
        UI.println("Earthquakes sorted by time");
        Collections.sort(this.earthquakes, (e1, e2) -> e1.getTime().compareTo(e2.getTime()));

        for (Earthquake e : this.earthquakes) {
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the list of earthquakes according to region. If two earthquakes have
     * the same
     * region, they should be sorted by magnitude (highest first) and then depth
     * (more shallow first)
     */
    public void sortByRegion() {
        UI.clearText();
        UI.println("Earthquakes sorted by region, then by magnitude and depth");
        Collections.sort(this.earthquakes, (e1, e2) -> {
            int regionCompare = e1.getRegion().compareTo(e2.getRegion());
            if (regionCompare == 0) {
                int magnitudeCompare = Double.compare(e2.getMagnitude(), e1.getMagnitude());
                if (magnitudeCompare == 0) {
                    return Double.compare(e1.getDepth(), e2.getDepth());
                }
                return magnitudeCompare;
            }
            return regionCompare;
        });

        for (Earthquake e : this.earthquakes) {
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the earthquakes by proximity to a specified location
     */
    public void sortByProximity(double longitude, double latitude) {
        UI.clearText();
        UI.println("Earthquakes sorted by proximity");
        UI.println("Longitude: " + longitude + " Latitude: " + latitude);
        Collections.sort(this.earthquakes, (e1, e2) -> {
            double distance1 = e1.distanceTo(longitude, latitude);
            double distance2 = e2.distanceTo(longitude, latitude);
            return Double.compare(distance1, distance2);
        });

        UI.println("------------------------");
    }

    /**
     * Add the buttons
     */
    public void setupGUI() {
        UI.initialise();
        UI.addButton("Load", this::loadData);
        UI.addButton("sort by ID", this::sortByID);
        UI.addButton("sort by Magnitude", this::sortByMagnitude);
        UI.addButton("sort by Time", this::sortByTime);
        UI.addButton("sort by Region", this::sortByRegion);
        UI.addButton("sort by Proximity", this::sortByProximity);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(900, 400);
        UI.setDivider(1.0); // text pane only
    }

    public static void main(String[] arguments) {
        EarthquakeSorter obj = new EarthquakeSorter();
        obj.setupGUI();

        // WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println(
                "Completed: loadData, sortByID, sortByMagnitude, sortByTime, sortByRegion, sortByProximity, compareTo");

    }

    public void loadData() {
        this.loadData(UIFileChooser.open("Choose data file"));
    }

    public void sortByProximity() {
        UI.clearText();
        this.sortByProximity(UI.askDouble("Give longitude: "), UI.askDouble("Give latitude: "));
    }

}
