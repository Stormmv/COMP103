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
 * A treatment Department (Surgery, X-ray room, ER, Ultrasound, etc)
 * Each department will need
 * - A name,
 * - A maximum number of patients that can be treated at the same time
 * - A Set of Patients that are currently being treated
 * - A Queue of Patients waiting to be treated.
 * (ordinary queue, or priority queue, depending on argument to constructor)
 */

public class Department {

    private String name;
    private int maxPatients; // maximum number of patients receiving treatment at one time.

    private Set<Patient> treatmentRoom; // the patients receiving treatment
    private Queue<Patient> waitingRoom; // the patients waiting for treatment

    /**
     * Construct a new Department object
     * Initialise the waiting queue and the current Set.
     */
    public Department(String name, int maxPatients, boolean usePriQueue) {
        this.name = name;
        this.maxPatients = maxPatients;
        this.treatmentRoom = new HashSet<Patient>();
        if (usePriQueue)
            this.waitingRoom = new PriorityQueue<Patient>();
        else
            this.waitingRoom = new LinkedList<Patient>();

    }

    // Methods

    public void addPatient(Patient p) {
        if (treatmentRoom.size() < maxPatients) {
            treatmentRoom.add(p);
        } else {
            waitingRoom.add(p);
        }
    }

    public Collection<Patient> getWaitingPatients() {
        return waitingRoom;
    }

    public Collection<Patient> getTreatingPatients() {
        return treatmentRoom;
    }

    public void movePatients() {
        while (treatmentRoom.size() < maxPatients && waitingRoom.size() > 0) {
            treatmentRoom.add(waitingRoom.remove());
        }
    }

    public void removePatient(Patient p) {
        treatmentRoom.remove(p);
        movePatients();
    }

    /**
     * Draw the department: the patients being treated and the patients waiting
     * You may need to change the names if your fields had different names
     */
    public void redraw(double y) {
        UI.setFontSize(14);
        UI.drawString(name, 0, y - 35);
        double x = 10;
        UI.drawRect(x - 5, y - 30, maxPatients * 10, 30); // box to show max number of patients
        for (Patient p : treatmentRoom) {
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for (Patient p : waitingRoom) {
            p.redraw(x, y);
            x += 10;
        }
    }

}
