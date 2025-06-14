package com.example.finalproject;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Room {
    private static final double TAX_RATE = 0.2; // 20% tax rate
    private String roomType;
    private int roomNumber;
    private String[] includes;
    private int price;
    private String imageName;
    private CustomLinkedList<DateRange> bookedDates;

    public Room() {

    }

    // constructor to initialize all room fields and prepare empty booking list
    public Room(String roomType, int roomNumber, String[] includes, int price, String imageName) {
        this.roomType = roomType;
        this.roomNumber = roomNumber;
        this.includes = includes;
        this.price = price;
        this.imageName = imageName;
        this.bookedDates = new CustomLinkedList<DateRange>();
    }

    // getters/setters
    public CustomLinkedList<DateRange> getBookedDates() {
        return bookedDates;
    }

    public void setBookedDates(CustomLinkedList<DateRange> bookedDates) {
        this.bookedDates = bookedDates;
    }

    public void addBookedDate(DateRange dateRange) {
        bookedDates.add(dateRange);
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String[] getIncludes() {
        return includes;
    }

    public int getPrice() {
        return price;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // excludes this from JSON, and returns price with 20% tax
    @JsonIgnore
    public double getPriceWithTax() {
        return Math.round(price * (1 + TAX_RATE));
    }

    // excludes from JSON, indicates availability based on bookings
    @JsonIgnore
    public boolean isAvailable() {
        return bookedDates.size() == 0;
    }

    // deserializes list into internal linked list
    @JsonProperty("bookedDates")
    public void setBookedDatesFromJson(List<DateRange> dates) {
        bookedDates = new CustomLinkedList<>();
        for (DateRange date : dates) {
            bookedDates.add(date);
        }
    }

    // serializes internal linked list as a List for JSON
    @JsonProperty("bookedDates")
    public List<DateRange> getBookedDatesForJson() {
        return bookedDates.toList();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Room Type: ").append(roomType).append("\n");
        sb.append("Room Number: ").append(roomNumber).append("\n");
        sb.append("Includes: ");
        if (includes != null && includes.length > 0) {
            for (String item : includes) {
                sb.append(item).append(", ");
            }
            sb.setLength(sb.length() - 2);
        } else {
            sb.append("None");
        }
        sb.append("\n");
        sb.append("Price: $").append(price).append("\n");
        sb.append("Price with Tax: $").append(String.format("%.2f", getPriceWithTax())).append("\n");
        return sb.toString();
    }
}
