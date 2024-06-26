// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T2, Assignment 2
 * Name: Micahel Visser
 * Username: vissermich
 * ID: 300652084
 */

import ecs100.*;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;
//use the other jar files in code for some of the functions like finding stations on a line
/**
 * WellingtonTrains
 * A program to answer queries about Wellington train lines and timetables for
 *  the train services on those train lines.
 *
 * See the assignment page for a description of the program and what you have to do.
 */

public class WellingtonTrains{
    //Fields to store the collections of Stations and Lines
    private Map<String, Station> stations = new HashMap<String, Station>();
    private Map<String, TrainLine> lines = new HashMap<String, TrainLine>();
    private Map<String, TrainService> services = new HashMap<String, TrainService>();



    // Fields for the suggested GUI.
    private String stationName;        // station to get info about, or to start journey from
    private String lineName;           // train line to get info about.
    private String destinationName;
    private int startTime = 0;         // time for enquiring about

    private static boolean loadedData = false;  // used to ensure that the program is called from main.

    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args){
        WellingtonTrains wel = new WellingtonTrains();
        wel.loadData();   // load all the data
        wel.setupGUI();   // set up the interface
    }

    /**
     * Load data files
     */
    public void loadData(){
        loadStationData();
        UI.println("Loaded Stations");
        loadTrainLineData();
        UI.println("Loaded Train Lines");
        // The following is only needed for the Completion and Challenge
        loadTrainServicesData();
        UI.println("Loaded Train Services");
        loadedData = true;
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI(){
        UI.addButton("All Stations",        this::listAllStations);
        UI.addButton("Stations by name",    this::listStationsByName);
        UI.addButton("All Lines",           this::listAllTrainLines);
        UI.addTextField("Station",          (String name) -> {this.stationName=name;});
        UI.addTextField("Train Line",       (String name) -> {this.lineName=name;});
        UI.addTextField("Destination",      (String name) -> {this.destinationName=name;});
        UI.addTextField("Time (24hr)",      (String time) ->
            {
                try{
                    this.startTime=Integer.parseInt(time);
                }
                catch(Exception e){
                    UI.println("Enter four digits");
                }
            });
        UI.addButton("Lines of Station",    () -> {listLinesOfStation(this.stationName);});
        UI.addButton("Stations on Line",    () -> {listStationsOnLine(this.lineName);});
        UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
        UI.addButton("Next Services",       () -> {findNextServices(this.stationName, this.startTime);});
        // UI.addButton("Find Trip",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});

        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setWindowSize(900, 400);
        UI.setDivider(0.2);
        // this is just to remind you to start the program using main!
        if (! loadedData){
            UI.setFontSize(36);
            UI.drawString("Start the program from main", 2, 36);
            UI.drawString("in order to load the data", 2, 80);
            UI.sleep(2000);
            UI.quit();
        }
        else {
            UI.drawImage("data/geographic-map.png", 0, 0);
            UI.drawString("Click to list closest stations", 2, 12);
        }
    }

    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            UI.clearText();
            UI.drawString("Click to list closest stations", 2, 12);
            listClosestStations(x, y);
        }
    }

    public void loadStationData(){
        try{
            Scanner scan = new Scanner(new File("data/stations.data"));
            while(scan.hasNext()){
                String name = scan.next();
                int line = Integer.parseInt(scan.next());
                double x = scan.nextDouble();
                double y = scan.nextDouble();
                Station station = new Station(name, line, x, y);
                stations.put(name, station);
            }
            scan.close();
        }
        catch(IOException e){UI.println("File reading failed");}

    }

    public void loadTrainLineData(){

        try{
            Scanner scan = new Scanner(new File("data/train-lines.data"));
            while(scan.hasNext()){
                String name = scan.next();
                TrainLine line = new TrainLine(name);
                lines.put(name, line);
            }
            scan.close();
        }
        catch(IOException e){UI.println("File reading failed");}

        for(String lineName : lines.keySet()){
            try{
                Scanner scan = new Scanner(new File("data/"+lineName+"-stations.data"));
                while(scan.hasNext()){
                    String stationName = scan.next();
                    Station station = stations.get(stationName);
                    TrainLine line = lines.get(lineName);
                    line.addStation(station);
                    station.addTrainLine(line);
                }
                scan.close();
            }
            catch(IOException e){UI.println("File reading failed");}
        }

    }

    public void loadTrainServicesData(){
           
            for(String lineName : lines.keySet()){
                try{
                    Scanner scan = new Scanner(new File("data/"+lineName+"-services.data"));
                    while(scan.hasNext()){
                        String destinationName = scan.next();
                        String stationName = scan.next();
                        TrainLine line = lines.get(lineName);
                        TrainService services = new TrainService(line);
                        line.addTrainService(services);
                        while(scan.hasNextInt()){
                            int time = scan.nextInt();
                            services.addTime(time);
                        }
                    }
                    scan.close();
                }
                catch(IOException e){UI.println("File reading failed");}
            }
        
    }

    private void listAllStations(){
        for(String stationName : stations.keySet()){
            Station station = stations.get(stationName);
            UI.println(station.getName());
        }
    }

    private void listStationsByName(){
        String[] all = new String[stations.size()];
        int numberOfStations = 0;
        for(String stationName : stations.keySet()){
            Station station = stations.get(stationName);
            all[numberOfStations] = station.getName();
            numberOfStations++;
        }

        Arrays.sort(all);

        for(String stationName : all){
            UI.println(stationName);
        }

    }

    private void listAllTrainLines(){
        for(String lineName : lines.keySet()){
            TrainLine line = lines.get(lineName);
            UI.println(line.getName());
        }
    }

    private void listLinesOfStation(String stationName){
        Station station = stations.get(stationName);
        for(TrainLine line : station.getTrainLines()){
            UI.println(line.getName());
        }
    }

    private void listStationsOnLine(String lineName){
        TrainLine line = lines.get(lineName);
        for(Station station : line.getStations()){
            UI.println(station.getName());
        }
    }

    private void checkConnected(String stationName, String destinationName){
        Station station = stations.get(stationName);
        Station destination = stations.get(destinationName);
        for (TrainLine line : station.getTrainLines()){
            if (line.getStations().contains(destination)){
                UI.println("Connected");
                return;
            }
            else{
                UI.println("Not Connected");
                return;
            }
        }    
    }

    private void listClosestStations(double x, double y){
        Station closest = null;
        double minDistance = Double.MAX_VALUE;
        for(String stationName : stations.keySet()){
            Station station = stations.get(stationName);
            double xx = station.getXCoord();
            double yy = station.getYCoord();
            double distance = Math.sqrt((xx-x)*(xx-x) + (yy-y)*(yy-y));
            if (distance < minDistance){
                minDistance = distance;
                closest = station;
            }
        }
        UI.println(closest.getName());
    }

    private void findNextServices(String stationName, int startTime){
        
        Station station = stations.get(stationName);
        for(TrainLine line : station.getTrainLines()){
            for(TrainService service : line.getTrainServices()){
                if (service.getTimes().contains(startTime)){
                    int index = service.getTimes().indexOf(startTime);
                    for(int i = index; i < service.getTimes().size(); i++){
                        UI.println(service.getTimes().get(i));
                    }
                }
                else{
                    UI.println("No services");
                }
            }
        }
    }
}
