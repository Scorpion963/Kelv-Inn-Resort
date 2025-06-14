package com.example.finalproject;

public final class Constants {
    private Constants() {} // Prevent instantiation

    // action constants
    public static final String ACTION_BOOK = "Book";
    public static final String ACTION_CANCEL = "Cancel";
    public static final String ACTION_ANY = "Any";

    // error messages
    public static final String ERROR_INVALID_ROOM = "Invalid Room Number";
    public static final String ERROR_NEGATIVE_ROOM = "Must be a positive number";
    public static final String ERROR_DATE_BOTH_REQUIRED = "Please select both start and end dates.";
    public static final String ERROR_START_AFTER_END = "Start date must be before end date.";
    public static final String ERROR_INVALID_START_DATE = "Invalid date.";
    public static final String ERROR_EXISTING_BOOKINGS_OVERLAP = "Selected dates overlap existing bookings!";
    public static final String ERROR_FILTERING_DISABLED = "(Fix the error to enable filtering)";
}
