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
import java.util.Map.Entry;

import javax.sound.sampled.Line;

import java.io.*;
import java.nio.file.*;

/**
 * MilanoSubway
 * A program to answer queries about Milan Metro subway lines and timetables for
 * the subway services on those subway lines.
 *
 * See the assignment page for a description of the program and what you have to
 * do.
 */

public class MilanoSubway {
    // Fields to store the collections of Stations and Lines
    private Map<String, Station> allStations = new HashMap<String, Station>(); // all stations, indexed by station name
    private Map<String, SubwayLine> allSubwayLines = new HashMap<String, SubwayLine>(); // all subway lines, indexed by
                                                                                        // name of the line

    // Fields for the GUI (with default values)
    private String currentStationName = "Zara"; // station to get info about, or to start journey from
    private String currentLineName = "M1-north"; // subway line to get info about.
    private String destinationName = "Brenta"; // station to end journey at
    private int startTime = 1200; // time for enquiring about

    /**
     * main method: load the data and set up the user interface
     */
    public static void main(String[] args) {
        MilanoSubway milan = new MilanoSubway();
        milan.setupGUI(); // set up the interface

        // WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("Completed: loadStationData, loadSubwayLineData, listAllStations, listStationsByName, listAllSubwayLines, setCurrentStation, setDestinationStation, setCurrentLine, listLinesOfStation, listStationsOnLine, onSameLine");

        milan.loadData(); // load all the data
    }

    /**
     * Load data files
     */
    public void loadData() {
        loadStationData();
        UI.println("Loaded Stations");
        loadSubwayLineData();
        UI.println("Loaded Subway Lines");
        // The following is only needed for the Completion and Challenge
        loadLineServicesData();
        UI.println("Loaded Line Services");
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations
     * and subway line
     * You will need to implement the methods here, or comment out the button.
     */
    public void setupGUI() {
        UI.addButton("List all Stations", this::listAllStations);
        UI.addButton("List Stations by name", this::listStationsByName);
        UI.addButton("List all Lines", this::listAllSubwayLines);
        UI.addButton("Set Station", this::setCurrentStation);
        UI.addButton("Set Line", this::setCurrentLine);
        UI.addButton("Set Destination", this::setDestinationStation);
        UI.addTextField("Set Time (24hr)", this::setTime);
        UI.addButton("Lines of Station", this::listLinesOfStation);
        UI.addButton("Stations on Line", this::listStationsOnLine);
        UI.addButton("On same line?", this::onSameLine);
        UI.addButton("Next Services", this::findNextServices);
        //UI.addButton("Find Trip", this::findTrip);

        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1500, 750);
        UI.setDivider(0.2);

        UI.drawImage("data/system-map.jpg", 0, 0, 1000, 704);

    }

    // Methods for loading data
    // The loadData method suggests the methods you need to write.

    public void loadStationData() {
        try {
            Scanner scan = new Scanner(new File("data/stations.data"));
            while (scan.hasNext()) {
                String name = scan.next();
                double x = scan.nextDouble();
                double y = scan.nextDouble();
                scan.nextLine();
                Station station = new Station(name, x, y);
                allStations.put(name, station);
            }
            scan.close();
        } catch (IOException e) {
            UI.println("File reading failed");
        }
    }

    public void loadSubwayLineData() {
        try {
            Scanner scan = new Scanner(new File("data/subway-lines.data"));
            while (scan.hasNext()) {
                String name = scan.next();
                SubwayLine line = new SubwayLine(name);
                allSubwayLines.put(name, line);
            }
            scan.close();
        } catch (IOException e) {
            UI.println("File reading failed");
        }

        for (String lineName : allSubwayLines.keySet()) {
            try {
                Scanner scan = new Scanner(new File("data/" + lineName + "-stations.data"));
                while (scan.hasNext()) {
                    String stationName = scan.next();
                    Station station = allStations.get(stationName);
                    SubwayLine line = allSubwayLines.get(lineName);
                    double distance = scan.nextDouble();
                    line.addStation(station, distance);
                    station.addSubwayLine(line);
                }
                scan.close();
            } catch (IOException e) {
                UI.println("File reading failed");
            }
        }
    }

    public void loadLineServicesData() {
        for (String lineName : allSubwayLines.keySet()) {
            try {
                Scanner scan = new Scanner(new File("data/" + lineName + "-services.data"));
                while (scan.hasNext()) {
                    int time = scan.nextInt();
                    LineService lineService = new LineService(allSubwayLines.get(lineName));
                    lineService.addTime(time);
                }
                scan.close();
            } catch (IOException e) {
                UI.println("File reading failed");
            }
        }
    }

    // Methods for answering the queries
    // The setupGUI method suggests the methods you need to write.

    public void listAllStations() {
        UI.clearText();
        UI.println("All Stations:");
        for (String name : allStations.keySet()) {
            UI.println(name);
        }
    }

    public void listStationsByName() {
        UI.clearText();
        UI.println("Stations by name:");
        List<String> names = new ArrayList<String>(allStations.keySet());
        Collections.sort(names);
        for (String name : names) {
            UI.println(name);
        }
    }

    public void listAllSubwayLines() {
        UI.clearText();
        UI.println("All Subway Lines:");
        for (String name : allSubwayLines.keySet()) {
            SubwayLine line = allSubwayLines.get(name);
            UI.println(line.getName() + " (" + line.getStations().get(0).getName() + " - "
                    + line.getStations().get(line.getStations().size() - 1).getName() + ")");
        }
    }

    public void listLinesOfStation() {
        UI.clearText();
        UI.println("Lines of Station:");
        Station station = allStations.get(currentStationName);
        for (SubwayLine line : station.getSubwayLines()) {
            UI.println(line.getName());
        }
    }

    public void listStationsOnLine() {
        UI.clearText();
        UI.println("Stations on Line:");
        SubwayLine line = allSubwayLines.get(currentLineName);
        for (Station station : line.getStations()) {
            UI.println(station.getName());
        }
    }

    public void onSameLine() {
        UI.clearText();
        UI.println("On same line?");
        Station station1 = allStations.get(currentStationName);
        Station station2 = allStations.get(destinationName);
        for (SubwayLine line1 : station1.getSubwayLines()) {
            for (SubwayLine line2 : station2.getSubwayLines()) {
                if (line1 == line2) {
                    UI.println("Yes");
                    return;
                }
            }
        }
        UI.println("No");
    }

    public void findNextServices() {
        UI.clearText();
        UI.println("Next Services:");
        Station station = allStations.get(currentStationName);
        for (SubwayLine line : station.getSubwayLines()) {
            for (LineService service : line.getLineServices()) {
                if (service.getTimes().contains(startTime)) {
                    int index = service.getTimes().indexOf(startTime);
                    for (int i = index; i < service.getTimes().size(); i++) {
                        UI.println(service.getTimes().get(i));
                    }
                } else {
                    UI.println("No services");
                }
            }
        }
    }

    // ======= written for you ===============
    // Methods for asking the user for station names, line names, and time.

    /**
     * Set the startTime.
     * If user enters an invalid time, it reports an error
     */
    public void setTime(String time) {
        int newTime = startTime; // default;
        try {
            newTime = Integer.parseInt(time);
            if (newTime >= 0 && newTime < 2400) {
                startTime = newTime;
            } else {
                UI.println("Time must be between 0000 and 2359");
            }
        } catch (Exception e) {
            UI.println("Enter time as a four digit integer");
        }
    }

    /**
     * Ask the user for a station name and assign it to the currentStationName field
     * Must pass a collection of the names of the stations to getOptionFromList
     */
    public void setCurrentStation() {
        String name = getOptionFromList("Choose current station", allStations.keySet());
        if (name == null) {
            return;
        }
        UI.println("Setting current station to " + name);
        currentStationName = name;
    }

    /**
     * Ask the user for a destination station name and assign it to the
     * destinationName field
     * Must pass a collection of the names of the stations to getOptionFromList
     */
    public void setDestinationStation() {
        String name = getOptionFromList("Choose destination station", allStations.keySet());
        if (name == null) {
            return;
        }
        UI.println("Setting destination station to " + name);
        destinationName = name;
    }

    /**
     * Ask the user for a subway line and assign it to the currentLineName field
     * Must pass a collection of the names of the lines to getOptionFromList
     */
    public void setCurrentLine() {
        String name = getOptionFromList("Choose current subway line", allSubwayLines.keySet());
        if (name == null) {
            return;
        }
        UI.println("Setting current subway line to " + name);
        currentLineName = name;
    }

    //
    /**
     * Method to get a string from a dialog box with a list of options
     */
    public String getOptionFromList(String question, Collection<String> options) {
        Object[] possibilities = options.toArray();
        Arrays.sort(possibilities);
        return (String) javax.swing.JOptionPane.showInputDialog(UI.getFrame(),
                question, "",
                javax.swing.JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                possibilities[0].toString());
    }

}
