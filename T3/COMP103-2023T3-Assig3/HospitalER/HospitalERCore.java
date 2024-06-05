// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T3, Assignment 3
 * Name: Michael Visser
 * Username: vissermich
 * ID: 300652084
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/**
 * Simple Simulation of a Hospital ER
 * 
 * The Emergency room has a waiting room and a treatment room that has a fixed
 * set of beds for examining and treating patients.
 * 
 * When a patient arrives at the emergency room, they are immediately assessed
 * by the
 * triage team who determines the priority of the patient.
 *
 * They then wait in the waiting room until a bed becomes free, at which point
 * they go from the waiting room to the treatment room.
 *
 * When a patient has finished their treatment, they leave the treatment room
 * and are discharged,
 * at which point information about the patient is added to the statistics.
 *
 * READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCore {

    // Fields for recording the patients waiting in the waiting room and being
    // treated in the treatment room
    private Queue<Patient> waitingRoom = new ArrayDeque<Patient>();
    private static final int MAX_PATIENTS = 5; // max number of patients currently being treated
    private Set<Patient> treatmentRoom = new HashSet<Patient>();
    private Set<Patient> toDelete = new HashSet<Patient>();

    // fields for the statistics
    private int patientsTreated = 0;
    private int priority1PatientsTreated = 0;
    private Collection<Integer> timesTreated = new ArrayList<Integer>();
    private Collection<Integer> timesTreatedPriority1 = new ArrayList<Integer>();

    // Fields for the simulation
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300; // milliseconds of real time for each tick

    /**
     * Reset the simulation:
     * stop any running simulation,
     * reset the waiting and treatment rooms
     * reset the statistics.
     */
    public void reset(boolean usePriorityQueue) {
        running = false;
        UI.sleep(2 * delay); // to make sure that any running simulation has stopped

        time = 0; // set the "tick" to zero.
        // reset the waiting room, the treatment room, and the statistics.
        waitingRoom.clear();
        treatmentRoom.clear();
        patientsTreated = 0;
        timesTreated.clear();
        priority1PatientsTreated = 0;
        timesTreatedPriority1.clear();

        // use priority queue if true and normal queue if false
        if (usePriorityQueue) {
            waitingRoom = new PriorityQueue<Patient>();
        } else {
            waitingRoom = new ArrayDeque<Patient>();
        }

        UI.clearGraphics();
        UI.clearText();
    }

    /**
     * Main loop of the simulation
     */
    public void run() {
        if (running) {
            return;
        } // don't start simulation if already running one!
        running = true;
        while (running) { // each time step, check whether the simulation should pause.

            // Hint: if you are stepping through a set, you can't remove
            // items from the set inside the loop!
            // If you need to remove items, you can add the items to a
            // temporary list, and after the loop is done, remove all
            // the items on the temporary list from the set.

            time++;
            for (Patient p : treatmentRoom){
                if (p.currentTreatmentFinished()){
                    p.removeCurrentTreatment();
                    UI.println(time+ ": Finished: "+p);     
                }
            }
            for (Patient p : treatmentRoom) {
                if (p.allTreatmentsCompleted()) {
                    if (p.getPriority() == 1) {
                        priority1PatientsTreated++;
                        timesTreatedPriority1.add(p.getTotalWaitingTime());
                    }
                    UI.println(time + ": Discharged: " + p);
                    patientsTreated++;
                    timesTreated.add(p.getTotalWaitingTime());
                }
            }
            dischargePatients();
            for (Patient p : toDelete) {
                treatmentRoom.remove(p);
            }
            for (Patient p : waitingRoom) {
                p.waitForATick();
            }
            for (Patient p : treatmentRoom) {
                p.advanceCurrentTreatmentByTick();
            }
            // move patients from waiting room to treatment room
            while (treatmentRoom.size() < MAX_PATIENTS && waitingRoom.size() > 0) {
                treatmentRoom.add(waitingRoom.remove());
            }

            // Gets any new patient that has arrived and adds them to the waiting room
            Patient newPatient = PatientGenerator.getNextPatient(time);
            if (newPatient != null) {
                UI.println(time + ": Arrived: " + newPatient);
                waitingRoom.offer(newPatient);
            }
            redraw();
            UI.sleep(delay);
        }
        // paused, so report current statistics
        reportStatistics();
    }

    // Additional methods used by run() (You can define more of your own)

    public void dischargePatients() {
        for (Patient p : treatmentRoom) {
            if (p.allTreatmentsCompleted()) {
                UI.println(time + ": Discharged: " + p);
                //treatmentRoom.remove(p);
                toDelete.add(p);
            }
        }
    }

    /**
     * Report summary statistics about all the patients that have been discharged.
     */
    public void reportStatistics() {
        UI.println("Patients treated: " + patientsTreated);
        double time = 0;
        double timep1 = 0;
        for (int i : timesTreated) {
            time += i;
        }
        for (int i : timesTreatedPriority1) {
            timep1 += i;
        }
        UI.println("Average time: " + time / patientsTreated);
        UI.println("Patients treated with priority 1: " + priority1PatientsTreated);
        UI.println("Average time for priority 1: " + timep1 / priority1PatientsTreated);
    }

    // METHODS FOR THE GUI AND VISUALISATION

    /**
     * Set up the GUI: buttons to control simulation and sliders for setting
     * parameters
     */
    public void setupGUI() {
        UI.addButton("Reset (Queue)", () -> {
            this.reset(false);
        });
        UI.addButton("Reset (Pri Queue)", () -> {
            this.reset(true);
        });
        UI.addButton("Start", () -> {
            if (!running) {
                run();
            }
        }); // don't start if already running!
        UI.addButton("Pause & Report", () -> {
            running = false;
        });
        UI.addSlider("Speed", 1, 400, (401 - delay), (double val) -> {
            delay = (int) (401 - val);
        });
        UI.addSlider("Av arrival interval", 1, 50, PatientGenerator.getArrivalInterval(),
                PatientGenerator::setArrivalInterval);
        UI.addSlider("Prob of Pri 1", 1, 100, PatientGenerator.getProbPri1(),
                PatientGenerator::setProbPri1);
        UI.addSlider("Prob of Pri 2", 1, 100, PatientGenerator.getProbPri2(),
                PatientGenerator::setProbPri2);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000, 600);
        UI.setDivider(0.5);
    }

    /**
     * Redraws all the patients and the state of the simulation
     */
    public void redraw() {
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0, 32, 400, 32);

        // Draw the treatment room and the waiting room:
        double y = 80;
        UI.setFontSize(14);
        UI.drawString("ER", 0, y - 35);
        double x = 10;
        UI.drawRect(x - 5, y - 30, MAX_PATIENTS * 10, 30); // box to show max number of patients
        for (Patient p : treatmentRoom) {
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for (Patient p : waitingRoom) {
            p.redraw(x, y);
            x += 10;
        }
        UI.drawLine(0, y + 2, 400, y + 2);
    }

    /**
     * main: Construct a new HospitalERCore object, setting up the GUI, and
     * resetting
     */
    public static void main(String[] arguments) {
        HospitalERCore er = new HospitalERCore();
        er.setupGUI();
        er.reset(true); // initialise with an ordinary queue.

        // WRITE HERE WHICH PARTS OF THE ASSIGNMENT YOU HAVE COMPLETED
        // so the markers know what to look for.
        UI.println("done all");
    }
}
