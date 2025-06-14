package com.example.finalproject;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


// Dialog for managing bookings and cancellations for a single room.
// Displays existing reservations and allows adding or removing date ranges. (reservations)

public class RoomManagerDialog {
    private final Dialog<Void> dialog = new Dialog<>();
    private final Room room;
    private final CustomLinkedList<DateRange> dateRanges; // temporary copy to avoid direct edits
    private final Runnable onSave; // callback to update main UI and rooms after save

    private VBox cancelBox;
    private Runnable refreshRunnable;

    public RoomManagerDialog(Room room, Runnable onSave) {
        this.room = room;
        this.onSave = onSave;
        this.dateRanges = new CustomLinkedList<>(room.getBookedDates());
        buildDialog();
    }

    public void showAndWait() {
        dialog.showAndWait(); // opens modal
    }

    /*
     * Builds the layout of the dialog including cancel and booking sections.
     * Adds Save/Cancel buttons and defines what happens on Save.
     */

    private void buildDialog() {
        dialog.setTitle("Manage Room " + room.getRoomNumber());
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().getStylesheets().add(HelloApplication.class.getResource("style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("modal");

        cancelBox = createCancelSection(); // existing bookings
        VBox bookBox = createBookingSection(); // new reservation area

        ScrollPane scroll = new ScrollPane(cancelBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(150);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox layout = new VBox(20, scroll, new Separator(), bookBox);
        dialog.getDialogPane().setContent(layout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // saves changes on OK
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                room.setBookedDates(dateRanges);
                onSave.run();
            }
            return null;
        });
    }

    /*
     * Creates a VBox listing all current bookings.
     * Each booking is a button that cancels the reservation on click.
     */
    private VBox createCancelSection() {
        VBox box = new VBox(10);
        Label header = new Label("Cancel Reservations (click to cancel)");
        header.getStyleClass().add("section-header");
        box.getChildren().add(header);

        refreshRunnable = () -> {
            box.getChildren().setAll(header);
            for (DateRange r : dateRanges) {
                LocalDate from = DateUtils.toLocal(r.getStartDate());
                LocalDate to = DateUtils.toLocal(r.getEndDate());
                // calculates total price based on number of booked days (inclusive).
                // for example, June 10–12 = 3 days. (10, 11, 12)
                long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;
                double total = days * room.getPriceWithTax();

                // creates a cancel button for each reservation
                Button btn = new Button(String.format("%s (Total: $%.2f)", r, total));
                btn.setOnAction(e -> {
                    dateRanges.remove(r);
                    refreshRunnable.run();
                });
                btn.getStyleClass().add("cancel-button");
                box.getChildren().add(btn);
            }
        };

        refreshRunnable.run();
        return box;
    }

    // creates a section where the user can book new reservations.
    private VBox createBookingSection() {
        Label header = new Label("Book Reservations:");
        header.getStyleClass().add("section-header");

        DatePicker start = new DatePicker();
        DatePicker end = new DatePicker();

        refreshDatePickers(start, end); // disable unavailable days

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Button add = new Button("Add Reservation");
        add.getStyleClass().add("add-reservation-button");

        // adds booking logic
        add.setOnAction(e -> {
            validate(start, end, errorLabel);

            if (!errorLabel.getText().isEmpty()) return;

            // dates are valid → add reservation
            LocalDate s = start.getValue();
            LocalDate ed = end.getValue();

            dateRanges.add(new DateRange(
                    Date.from(s.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(ed.atStartOfDay(ZoneId.systemDefault()).toInstant())
            ));
            start.setValue(null);
            end.setValue(null);
            errorLabel.setText("");

            refreshRunnable.run(); // updates cancel list
            refreshDatePickers(start, end); // refreshes blocked dates
        });

        // live overlap validation
        start.valueProperty().addListener((obs, oldVal, newVal) -> validate(start, end, errorLabel));
        end.valueProperty().addListener((obs, oldVal, newVal) -> validate(start, end, errorLabel));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5));
        grid.addRow(0, new Label("Start Date:"), start);
        grid.addRow(1, new Label("End Date:"), end);

        VBox box = new VBox(10, header, grid, errorLabel, add);
        box.setPadding(new Insets(10));
        box.getStyleClass().add("section-box");
        return box;
    }

    // validates date selection and displays an error message if invalid.
    private void validate(DatePicker start, DatePicker end, Label errorLabel) {
        LocalDate s = start.getValue();
        LocalDate e = end.getValue();

        if (s == null || e == null) {
            errorLabel.setText(Constants.ERROR_DATE_BOTH_REQUIRED);
        } else if (e.isBefore(s)) {
            errorLabel.setText(Constants.ERROR_START_AFTER_END);
        } else if (isRangeBooked(s, e)) {
            errorLabel.setText(Constants.ERROR_EXISTING_BOOKINGS_OVERLAP);
        } else {
            errorLabel.setText(""); // no errors
        }
    }

    // checks if the given date range overlaps with any existing reservation.
    private boolean isRangeBooked(LocalDate from, LocalDate to) {
        for (DateRange r : dateRanges) {
            if (r.overlaps(from, to)) return true;
        }
        return false;
    }

    // creates a factory for disabling booked and past dates in DatePickers.
    private Callback<DatePicker, DateCell> makeDateCellFactory() {
        return dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now()) || isDateBooked(date)) {
                    setDisable(true);
                    getStyleClass().add("date-cell-blocked");
                }
            }
        };
    }

    // returns true if the given single date overlaps an existing reservation.
    private boolean isDateBooked(LocalDate date) {
        for (DateRange r : dateRanges) {
            if(r.overlaps(date, date)) return true;
        }
        return false;
    }

    // applies the date cell factory to both date pickers.
    private void refreshDatePickers(DatePicker start, DatePicker end) {
        start.setDayCellFactory(makeDateCellFactory());
        end.setDayCellFactory(makeDateCellFactory());
    }
}
