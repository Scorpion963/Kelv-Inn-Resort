package com.example.finalproject;

import java.time.LocalDate;

// holds filter criteria for room searches.
// used by RoomFilter to determine which filters to apply.

public class RoomFilterCriteria {
    public String roomType;
    public String action;
    public String numberText;
    public LocalDate from;
    public LocalDate to;

    // checks if a valid room number input is provided
    public boolean hasRoomNumber() {
        return numberText != null && !numberText.trim().isEmpty();
    }

    // determines if filtering by room type is needed
    public boolean shouldFilterByType() {
        return roomType != null && !roomType.equalsIgnoreCase(Constants.ACTION_ANY);
    }

    // determines if filtering by action (book/cancel) is needed
    public boolean shouldFilterByAction() {
        return action != null && !action.equalsIgnoreCase(Constants.ACTION_ANY);
    }

    // returns true if the action selected is "Cancel"
    public boolean isCancelAction() {
        return action != null && action.equalsIgnoreCase(Constants.ACTION_CANCEL);
    }
}