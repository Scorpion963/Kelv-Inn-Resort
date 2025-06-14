package com.example.finalproject;

import java.time.LocalDate;

// applies filtering logic to a list of rooms based on user-selected criteria
public class RoomFilter {
    // Applies all selected filters to the given list of rooms.
    // Returns a new filtered list, or null if the room number input is invalid.
    public static CustomLinkedList<Room> applyFilters(CustomLinkedList<Room> rooms, RoomFilterCriteria criteria) {
        CustomLinkedList<Room> filtered = new CustomLinkedList<>(rooms);

        if (criteria.hasRoomNumber()) {
            filtered = filterByNumber(filtered, criteria.numberText);
            if (filtered == null) return null;
        }

        if (criteria.shouldFilterByType()) {
            filtered = filterByType(filtered, criteria.roomType);
        }

        if (criteria.shouldFilterByAction()) {
            filtered = filterByDate(filtered, criteria.from, criteria.to, criteria.isCancelAction());
        }

        return filtered;
    }

    // filters by room number; returns null if input is invalid
    public static CustomLinkedList<Room> filterByNumber(CustomLinkedList<Room> rooms, String text) {
        if (text.isEmpty()) return rooms;
        try {
            int num = Integer.parseInt(text);
            if (num <= 0) return null;
            return rooms.filter(r -> r.getRoomNumber() == num);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // filters by exact room type (case-insensitive) Example: "Standard Single"
    public static CustomLinkedList<Room> filterByType(CustomLinkedList<Room> rooms, String type) {
        return rooms.filter(r -> r.getRoomType().equalsIgnoreCase(type));
    }

    /*
     * Filters rooms based on whether they overlap with a given date range.
     * - If cancel = true: includes rooms with overlapping bookings (cancelable)
     * - If cancel = false: includes only rooms with no overlap (available)
     */
    public static CustomLinkedList<Room> filterByDate(CustomLinkedList<Room> rooms, LocalDate from, LocalDate to, boolean cancel) {
        if (from == null || to == null || from.isAfter(to)) return rooms;

        return rooms.filter(room -> {
            for (DateRange r : room.getBookedDates()) {
                boolean overlaps = r.overlaps(from, to);
                if (cancel && overlaps) return true; // includes booked rooms for cancellation
                if (!cancel && overlaps) return false; // excludes booked rooms when reserving
            }
            return !cancel;
        });
    }
}
