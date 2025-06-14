package com.example.finalproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

// controller for the main room filtering UI.
// handles filter logic, form validation, and dynamic UI updates.
public class HelloController implements Initializable {
    // these values are automatically bound by JavaFX from the FXML file
    @FXML private ComboBox<String> roomType;
    @FXML private ComboBox<String> action;
    @FXML private TextField roomNumber;
    @FXML private FlowPane roomsContainer;
    @FXML private Label availableOrCancelTitle;
    @FXML private HBox numberInputContainer;
    @FXML private HBox cardsSizeTitle;
    @FXML private Label cardSize;
    @FXML private DatePicker datePickerTo;
    @FXML private DatePicker datePickerFrom;
    @FXML private Label filterDatePickerError;

    private CustomLinkedList<Room> rooms;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComboBoxes();
        setupListeners();

        datePickerFrom.setEditable(false);
        datePickerTo.setEditable(false);

        // sets default date range
        datePickerFrom.setValue(LocalDate.now());
        datePickerTo.setValue(LocalDate.now().plusDays(30));

        // loads room data
        rooms = RoomInitializer.initializeRooms();
        triggerSearch();
    }

    // populates dropdowns with options
    private void initializeComboBoxes() {
        roomType.getItems().addAll("Standard Single", "Standard Family", "Romantic Getaway",
                "Junior Suite", "Platinum Suite", "Penthouse Suite", Constants.ACTION_ANY);
        action.getItems().addAll(Constants.ACTION_BOOK, Constants.ACTION_CANCEL, Constants.ACTION_ANY);

        roomType.setValue(Constants.ACTION_ANY);
        action.setValue(Constants.ACTION_ANY);
    }

    // sets up event listeners for all filter inputs
    private void setupListeners() {
        roomType.setOnAction(e -> triggerSearch());
        action.setOnAction(e -> triggerSearch());
        roomNumber.setOnKeyReleased(e -> triggerSearch());
        datePickerFrom.setOnAction(e -> triggerSearch());
        datePickerTo.setOnAction(e -> triggerSearch());
        datePickerFrom.setOnKeyReleased(e -> triggerSearch());
        datePickerTo.setOnKeyReleased(e -> triggerSearch());
    }

    // applies filters and updates room display
    public void triggerSearch() {
        clearRoomNumberError();
        filterDatePickerError.setText("");

        RoomFilterCriteria criteria = new RoomFilterCriteria();
        criteria.roomType = roomType.getValue();
        criteria.action = action.getValue();
        criteria.numberText = roomNumber.getText().trim();
        criteria.from = datePickerFrom.getValue();
        criteria.to = datePickerTo.getValue();

        if (!areValidDateFilters(criteria.from, criteria.to)) return;

        CustomLinkedList<Room> filtered = RoomFilter.applyFilters(rooms, criteria);

        if (filtered == null) {
            displayRoomError(Constants.ERROR_INVALID_ROOM + " " + Constants.ERROR_FILTERING_DISABLED);
            return;
        }

        updateTitle(criteria.action);
        showRooms(filtered);
        updateRoomCount(filtered.size());
    }

    // validates date filters before applying them
    private boolean areValidDateFilters(LocalDate from, LocalDate to) {
        if ((from == null && to != null) || (from != null && to == null)) {
            setDateError(Constants.ERROR_DATE_BOTH_REQUIRED + " " + Constants.ERROR_FILTERING_DISABLED);
            return false;
        }

        if (from != null && to != null && from.isAfter(to)) {
            setDateError(Constants.ERROR_START_AFTER_END + " " + Constants.ERROR_FILTERING_DISABLED);
            return false;
        }

        return true;
    }

    // sets the error for date filters
    private void setDateError(String message) {
        filterDatePickerError.setText(message);
    }

    // updates room card container title with the size of filtered rooms
    private void updateRoomCount(int count) {
        cardSize.setText(" (" + count + ")");
        cardSize.getStyleClass().add("titleLabel");
    }

    // displays a validation error below the room number input
    private void displayRoomError(String error) {
        clearRoomNumberError();
        Label label = new Label(error);
        label.getStyleClass().add("error-label");
        label.setId("roomNumberError");
        numberInputContainer.getChildren().add(label);
    }

    // clears error
    private void clearRoomNumberError() {
        Node err = numberInputContainer.lookup("#roomNumberError");
        if (err != null) numberInputContainer.getChildren().remove(err);
    }

    // updates the section title based on selected action
    private void updateTitle(String action) {
        switch (action) {
            case Constants.ACTION_BOOK -> availableOrCancelTitle.setText("Available rooms");
            case Constants.ACTION_CANCEL -> availableOrCancelTitle.setText("Booked rooms");
            default -> availableOrCancelTitle.setText("All rooms");
        }
    }

    // resets all filters to default values and refreshes the room list
    public void clearFields() {
        roomType.setValue(Constants.ACTION_ANY);
        action.setValue(Constants.ACTION_ANY);
        roomNumber.clear();
        datePickerFrom.setValue(LocalDate.now());
        datePickerTo.setValue(LocalDate.now().plusDays(30));
        triggerSearch();
    }

    // renders the list of room cards in the UI
    public void showRooms(CustomLinkedList<Room> list) {
        roomsContainer.getChildren().clear();
        for (Room room : list) {
            roomsContainer.getChildren().add(new RoomCardView(room, this::openSelectDateDialog));
        }
    }

    // opens the manage dialog for a specific room (for booking/canceling)
    private void openSelectDateDialog(Room room) {
        new RoomManagerDialog(room, () -> {
            updateRoomInList(room);
            RoomInitializer.updateRooms(rooms);
            javafx.application.Platform.runLater(this::triggerSearch); // ensures UI is updated on the JavaFX Application Thread after booking/canceling
        }).showAndWait();
    }

    // Updates the internal room list after a room is modified
    private void updateRoomInList(Room updated) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomNumber() == updated.getRoomNumber()) {
                rooms.set(i, updated);
                break;
            }
        }
    }
}




