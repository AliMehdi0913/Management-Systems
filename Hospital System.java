/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.hospitalsystem;

/**
 *
 * @author syed_
 */
import java.io.*;
import java.util.*;

public class HospitalSystem {
    static List<String> doctors = Arrays.asList("Dr. Aahmed", "Dr. Ali", "Dr. Taha", "Dr. Hussain", "Dr. Hassan");
    static List<int[]> doctorTimings = Arrays.asList(
            new int[]{17, 20},
            new int[]{18, 21},
            new int[]{20, 23},
            new int[]{19, 22},
            new int[]{21, 23}
    );
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            System.out.println("\t\tFast Recovery Center");
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            System.out.println("1. Add Appointment");
            System.out.println("2. Display Appointments");
            System.out.println("3. Display Available Doctors");
            System.out.println("4. Clear Appointments");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> addAppointment();
                case 2 -> displayAppointments();
                case 3 -> displayDoctors();
                case 4 -> clearAppointments();
                case 5 -> System.out.println("Exiting the program.");
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 5);
    }

    static void displayDoctors() {
        System.out.println("Available Doctors with Their Timings:");
        System.out.println("-------------------------------------");
        for (int i = 0; i < doctors.size(); i++) {
            int[] timing = doctorTimings.get(i);
            System.out.printf("%d. %s (%02d:00 to %02d:00)%n", i + 1, doctors.get(i), timing[0], timing[1]);
        }
        System.out.println("-------------------------------------");
    }

    static boolean isTimeConflict(String doctor, String time) {
        try (BufferedReader reader = new BufferedReader(new FileReader("appointment.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;

                String doc = parts[2];
                String timeRange = parts[3];

                if (doc.equals(doctor)) {
                    int existingStart = parseTimeToMinutes(timeRange.substring(0, 5));
                    int existingEnd = parseTimeToMinutes(timeRange.substring(6, 11));
                    int requestedStart = parseTimeToMinutes(time);
                    int requestedEnd = requestedStart + 20;

                    if (!(requestedEnd <= existingStart || requestedStart >= existingEnd)) {
                        return true; // Conflict
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment file.");
        }
        return false;
    }

    static void addAppointment() {
        String name, age, time;
        int doctorChoice;

        System.out.print("Enter Patient Name: ");
        while (true) {
            name = scanner.nextLine();
            if (name.matches("[a-zA-Z ]+")) break;
            System.out.print("Error: Name must contain only alphabetic characters. Please re-enter: ");
        }

        System.out.print("Enter Patient Age: ");
        while (true) {
            age = scanner.nextLine();
            if (age.matches("\\d{1,2}")) break;
            System.out.print("Error: Age must be a numeric value less than 3 digits. Please re-enter: ");
        }

        displayDoctors();
        while (true) {
            System.out.print("Enter the number corresponding to your chosen doctor: ");
            try {
                doctorChoice = Integer.parseInt(scanner.nextLine());
                if (doctorChoice >= 1 && doctorChoice <= doctors.size()) break;
            } catch (NumberFormatException ignored) {}
            System.out.println("Error: Invalid choice. Please enter a valid number.");
        }

        String doctor = doctors.get(doctorChoice - 1);
        int[] timings = doctorTimings.get(doctorChoice - 1);

        while (true) {
            System.out.print("Enter Appointment Time (HH:MM): ");
            time = scanner.nextLine();

            if (!time.matches("\\d{2}:\\d{2}")) {
                System.out.println("Error: Invalid time format. Please use HH:MM format.");
                continue;
            }

            int hour = Integer.parseInt(time.substring(0, 2));
            int minute = Integer.parseInt(time.substring(3, 5));

            if (hour < timings[0] || hour >= timings[1] || minute < 0 || minute >= 60) {
                System.out.printf("Error: Appointment time must be between %02d:00 and %02d:00. Please try again.%n",
                        timings[0], timings[1]);
            } else if (isTimeConflict(doctor, time)) {
                System.out.println("Error: Appointment time conflict with Doctor " + doctor + ". Choose another time.");
            } else {
                break;
            }
        }

        int start = parseTimeToMinutes(time);
        int end = start + 20;
        String timeRange = formatMinutesToTimeRange(start, end);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("appointment.txt", true))) {
            writer.write(name + "," + age + "," + doctor + "," + timeRange);
            writer.newLine();
            System.out.println("Appointment booked successfully for " + timeRange + "!");
        } catch (IOException e) {
            System.out.println("Error: Unable to save appointment to file.");
        }
    }

    static void displayAppointments() {
        try (BufferedReader reader = new BufferedReader(new FileReader("appointment.txt"))) {
            String line;
            boolean hasAppointments = false;

            System.out.println("Current Appointments:");
            System.out.println("---------------------");

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;

                System.out.println("Patient Name: " + parts[0]);
                System.out.println("Age: " + parts[1]);
                System.out.println("Doctor: " + parts[2]);
                System.out.println("Appointment Time: " + parts[3]);
                System.out.println("---------------------");
                hasAppointments = true;
            }

            if (!hasAppointments) {
                System.out.println("No appointments found.");
            }

        } catch (IOException e) {
            System.out.println("Error: Unable to open appointments file.");
        }
    }

    static void clearAppointments() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("appointment.txt"))) {
            System.out.println("All appointments have been erased successfully.");
        } catch (IOException e) {
            System.out.println("Error: Unable to access the appointments file.");
        }
    }

    static int parseTimeToMinutes(String time) {
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));
        return hour * 60 + minute;
    }

    static String formatMinutesToTimeRange(int start, int end) {
        int startHour = start / 60;
        int startMin = start % 60;
        int endHour = end / 60;
        int endMin = end % 60;
        return String.format("%02d:%02d-%02d:%02d", startHour, startMin, endHour, endMin);
    }
}

