package com.example.finalproject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

// represents a date range with a start and end date.
public class DateRange {
    private Date startDate;
    private Date endDate;

    // constructors
    public DateRange() {
    }

    public DateRange(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // getters/setters (required for JSON serialization)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    // returns the date range in a readable format (e.g. "Jan 01, 2025 - Jan 05, 2025").
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(startDate) + " - " + sdf.format(endDate);
    }

    // checks if the given date range overlaps with this date range.
    public boolean overlaps(LocalDate from, LocalDate to) {
        LocalDate start = DateUtils.toLocal(this.startDate);
        LocalDate end = DateUtils.toLocal(this.endDate);
        return !to.isBefore(start) && !from.isAfter(end);
    }
}