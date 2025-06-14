package com.example.finalproject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class RoomInitializer {
    // path to the rooms.json file in the user's home directory
    private static final String ROOMS_FILE = System.getProperty("user.home") + File.separator + "rooms.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    // loads room data from a file or creates default rooms if file is missing or empty.
    public static CustomLinkedList<Room> initializeRooms() {
        File file = new File(ROOMS_FILE);
        CustomLinkedList<Room> rooms = new CustomLinkedList<Room>();

        try {
            if (!file.exists()) {
                // file doesn't exist: create and save default rooms
                rooms = createDefaultRooms();
                updateRooms(rooms);
            } else {
                if (Files.size(Path.of(ROOMS_FILE)) == 0) {
                    // file exists but is empty: regenerate default rooms
                    rooms = createDefaultRooms();
                    updateRooms(rooms);
                } else {
                    // file exists and has content: load rooms from JSON
                    List<Room> loadedRooms = mapper.readValue(file, new TypeReference<List<Room>>() {
                    });
                    for (Room room : loadedRooms) {
                        rooms.add(room);
                    }
                }
            }
        } catch (IOException e) {
            rooms = createDefaultRooms(); // fallback in case of read error
        }

        return rooms;
    }

    // writes the given room list to the rooms.json file.
    public static int updateRooms(CustomLinkedList<Room> rooms) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ROOMS_FILE), rooms.toList());
            return 1;
        } catch (IOException e) {
            return -1;
        }
    }

    // creates a default set of rooms divided by category and number range.
    private static CustomLinkedList<Room> createDefaultRooms() {
        CustomLinkedList<Room> rooms = new CustomLinkedList<Room>();

        // Rooms 1–30: Standard Single
        for (int i = 1; i <= 30; i++) {
            rooms.add(new Room(
                    "Standard Single",
                    i,
                    new String[]{"Single Double Bed"},
                    79,
                    "standard_single.jpg"
            ));
        }

        // Rooms 31–45: Standard Family
        for (int i = 31; i <= 45; i++) {
            rooms.add(new Room(
                    "Standard Family",
                    i,
                    new String[]{"Two Queen Beds"},
                    129,
                    "standard_family.jpg"
            ));
        }

        // Rooms 46–50: Romantic Getaway
        for (int i = 46; i <= 50; i++) {
            rooms.add(new Room(
                    "Romantic Getaway",
                    i,
                    new String[]{"Single King Bed", "Jacuzzi"},
                    159,
                    "romantic_getaway.jpg"
            ));
        }

        // Rooms 51–60: Junior Suite
        for (int i = 51; i <= 60; i++) {
            rooms.add(new Room(
                    "Junior Suite",
                    i,
                    new String[]{
                            "Single Queen Bed", "Fold-out couch", "Kitchen", "Standard Washroom"
                    },
                    199,
                    "junior_suite.jpg"
            ));
        }

        // Rooms 61–65: Platinum Suite
        for (int i = 61; i <= 65; i++) {
            rooms.add(new Room(
                    "Platinum Suite",
                    i,
                    new String[]{
                            "Single King Bed", "Fold-out couch", "Kitchen", "Luxury Washroom", "Jacuzzi"
                    },
                    299,
                    "platinum_suite.jpg"
            ));
        }

        // Rooms 66–67: Penthouse Suite
        for (int i = 66; i <= 67; i++) {
            rooms.add(new Room(
                    "Penthouse Suite",
                    i,
                    new String[]{
                            "One King Bed", "Two Queen Beds", "Living Room with Entertainment Unit",
                            "Luxury Kitchen", "Luxury Washroom", "Jacuzzi"
                    },
                    599,
                    "penthouse.jpg"
            ));
        }

        return rooms;
    }
}
