package com.example.finalproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class HelloController implements Initializable {
    private static final String ACTION_BOOK = "Book";
    private static final String ACTION_CANCEL = "Cancel";
    private static final String ACTION_ANY = "Any";
    private static final String ERROR_INVALID_ROOM = "Invalid Room Number";
    private static final String ERROR_NEGATIVE_ROOM = "Must be a positive number";
    private static final String ERROR_DATE_BOTH_REQUIRED = "Please select both start and end dates.";
    private static final String ERROR_START_AFTER_END = "Start date must be before end date.";
    private static final String ERROR_INVALID_START_DATE = "Invalid date.";
    private static final String ERROR_EXISTING_BOOKINGS_OVERLAP = "Selected dates overlap existing bookings!";

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

        datePickerFrom.setValue(LocalDate.now());
        datePickerTo.setValue(LocalDate.now().plusDays(7));

        rooms = RoomInitializer.initializeRooms();
        triggerSearch();
    }

    private void initializeComboBoxes() {
        roomType.getItems().addAll("Standard Single", "Standard Family", "Romantic Getaway",
                "Junior Suite", "Platinum Suite", "Penthouse Suite", ACTION_ANY);
        action.getItems().addAll(ACTION_BOOK, ACTION_CANCEL, ACTION_ANY);

        roomType.setValue(ACTION_ANY);
        action.setValue(ACTION_ANY);
    }

    private void setupListeners() {
        roomType.setOnAction(e -> triggerSearch());
        action.setOnAction(e -> triggerSearch());
        roomNumber.setOnKeyReleased(e -> triggerSearch());
        datePickerFrom.setOnAction(e -> triggerSearch());
        datePickerTo.setOnAction(e -> triggerSearch());
        datePickerFrom.setOnKeyReleased(e -> triggerSearch());
        datePickerTo.setOnKeyReleased(e -> triggerSearch());
    }

    public void triggerSearch() {
        clearRoomNumberError();
        filterDatePickerError.setText("");

        String selectedType = roomType.getValue();
        String selectedAction = action.getValue();
        String numberText = roomNumber.getText().trim();

        LocalDate from = datePickerFrom.getValue();
        LocalDate to = datePickerTo.getValue();

        try {
            LocalDate first = datePickerFrom.getConverter().fromString(datePickerFrom.getEditor().getText());
            LocalDate second = datePickerTo.getConverter().fromString(datePickerTo.getEditor().getText());
        } catch (Exception e) {
            filterDatePickerError.setText(ERROR_INVALID_START_DATE);
            return;
        }


        if ((from == null && to != null) || (from != null && to == null)) {
            setDateError(ERROR_DATE_BOTH_REQUIRED);
            return;
        }

        if (from != null && to != null && from.isAfter(to)) {
            setDateError(ERROR_START_AFTER_END);
            return;
        }

        CustomLinkedList<Room> filtered = new CustomLinkedList<>(rooms);

        filtered = applyRoomNumberFilter(filtered, numberText);
        if (filtered == null) return;

        if (!ACTION_ANY.equals(selectedType)) {
            filtered = filtered.filter(r -> r.getRoomType().equalsIgnoreCase(selectedType));
        }

        if (!ACTION_ANY.equals(selectedAction)) {
            boolean cancel = ACTION_CANCEL.equals(selectedAction);
            filtered = filterByDate(filtered, from, to, cancel);
        }

        updateTitle(selectedAction);
        showRooms(filtered);
        updateRoomCount(filtered.size());
    }

    private void setDateError(String message) {
        filterDatePickerError.setText(message);
    }

    private void updateRoomCount(int count) {
        cardSize.setText(" (" + count + ")");
        cardSize.getStyleClass().add("titleLabel");
    }


    private CustomLinkedList<Room> filterByDate(CustomLinkedList<Room> list, LocalDate from, LocalDate to, boolean cancel) {
        if (from == null || to == null || from.isAfter(to)) return list;

        return list.filter(room -> {
                for (DateRange r : room.getBookedDates()) {
                    LocalDate bookedFrom = toLocal(r.getStartDate());
                    LocalDate bookedTo = toLocal(r.getEndDate());

                    boolean overlaps = !to.isBefore(bookedFrom) && !from.isAfter(bookedTo);

                    if (cancel && overlaps) return true;
                    if (!cancel && overlaps) return false;
                }
                return !cancel;
            });
    }

    private CustomLinkedList<Room> applyRoomNumberFilter(CustomLinkedList<Room> list, String text) {
        if (text.isEmpty()) return list;
        try {
            int num = Integer.parseInt(text);
            if (num <= 0) return displayRoomError(ERROR_NEGATIVE_ROOM);
            return list.filter(r -> r.getRoomNumber() == num);
        } catch (NumberFormatException e) {
            return displayRoomError(ERROR_INVALID_ROOM);
        }
    }

    private CustomLinkedList<Room> displayRoomError(String error) {
        clearRoomNumberError();
        Label label = new Label(error);
        label.setStyle("-fx-text-fill: red");
        label.setId("roomNumberError");
        numberInputContainer.getChildren().add(label);
        return null;
    }

    private void clearRoomNumberError() {
        Node err = numberInputContainer.lookup("#roomNumberError");
        if (err != null) numberInputContainer.getChildren().remove(err);
    }

    private void updateTitle(String action) {
        switch (action) {
            case ACTION_BOOK -> availableOrCancelTitle.setText("Available rooms");
            case ACTION_CANCEL -> availableOrCancelTitle.setText("Booked rooms");
            default -> availableOrCancelTitle.setText("All rooms");
        }
    }

    public void clearFields() {
        roomType.setValue(ACTION_ANY);
        action.setValue(ACTION_ANY);
        roomNumber.clear();
        datePickerFrom.setValue(LocalDate.now());
        datePickerTo.setValue(LocalDate.now().plusDays(7));
        triggerSearch();
    }

    public void showRooms(CustomLinkedList<Room> list) {
        roomsContainer.getChildren().clear();
        for (Room room : list) {
            roomsContainer.getChildren().add(createRoomCard(room));
        }
    }

    private VBox createRoomCard(Room room) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(150, 220);
        card.setStyle("-fx-border-color: #ccc; -fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 10;");

        ImageView img = new ImageView(new Image(getClass().getResource("/com/example/finalproject/" + room.getImageName()).toString()));
        img.setFitHeight(220);
        img.setFitWidth(150);

        Label title = new Label(room.getRoomType() + " (" +room.getRoomNumber() + ")");
        title.setStyle("-fx-font-weight: bold;");
        Label price = new Label("$" + (int) room.getPriceWithTax() + " / night");
        price.setStyle("-fx-text-fill: #555;");
        Label include = new Label(room.getIncludes().length > 0 ? room.getIncludes()[0] : "No details");
        include.setStyle("-fx-text-fill: #777; -fx-font-size: 11px;");

        Tooltip tip = new Tooltip(room.toString());
        tip.setWrapText(true);
        tip.setMaxWidth(300);
        tip.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 15px;");
        Tooltip.install(card, tip);

        Button manageBtn = new Button("Manage");
        manageBtn.setPrefWidth(150);
        manageBtn.getStyleClass().add("manage");
        manageBtn.setOnAction(e -> openSelectDateDialog(room));

        card.getChildren().addAll(img, title, price, include, manageBtn);
        return card;
    }

    private void openSelectDateDialog(Room room) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Room " + room.getRoomNumber());
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().getStylesheets().add(HelloApplication.class.getResource("style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("modal");

        CustomLinkedList<DateRange> dateRanges = new CustomLinkedList<>(room.getBookedDates());
        DatePicker start = new DatePicker(), end = new DatePicker();

        // Cancel section
        VBox cancelBox = new VBox(10);
        Label cancelHeader = new Label("Cancel Reservations (click to cancel)");
        cancelHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        cancelBox.getChildren().add(cancelHeader);

        ScrollPane scroll = new ScrollPane(cancelBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(150);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Runnable[] refresh = new Runnable[1];
        refresh[0] = () -> {
            cancelBox.getChildren().setAll(cancelHeader);
            for (DateRange r : dateRanges) {
                Button b = new Button(r.toString());
                b.setOnAction(e -> {
                    dateRanges.remove(r);
                    refresh[0].run();
                    start.setDayCellFactory(makeDateCellFactory(dateRanges));
                    end.setDayCellFactory(makeDateCellFactory(dateRanges));
                });
                cancelBox.getChildren().add(b);
            }
        };
        refresh[0].run();

        start.setDayCellFactory(makeDateCellFactory(dateRanges));
        end.setDayCellFactory(makeDateCellFactory(dateRanges));

        // Booking section
        Label bookHeader = new Label("Book Reservations:");
        bookHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Start Date:"), start);
        grid.addRow(1, new Label("End Date:"), end);
        grid.setPadding(new Insets(5));

        Label err = new Label();
        err.setTextFill(Color.RED);

        Button add = new Button("Add Reservation");

        add.setOnAction(e -> {
            LocalDate s = start.getValue();
            LocalDate eD = end.getValue();

            if (s == null || eD == null) {
                err.setText(ERROR_DATE_BOTH_REQUIRED);
            } else if (eD.isBefore(s)) {
                err.setText(ERROR_START_AFTER_END);
            } else if (isRangeBooked(s, eD, dateRanges)) {
                err.setText(ERROR_EXISTING_BOOKINGS_OVERLAP);
            } else {
                dateRanges.add(new DateRange(
                        Date.from(s.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        Date.from(eD.atStartOfDay(ZoneId.systemDefault()).toInstant())
                ));
                start.setValue(null);
                end.setValue(null);
                err.setText("");
                refresh[0].run();
                start.setDayCellFactory(makeDateCellFactory(dateRanges));
                end.setDayCellFactory(makeDateCellFactory(dateRanges));
            }
        });

        start.valueProperty().addListener((obs, o, n) -> validate(start, end, dateRanges, err));
        end.valueProperty().addListener((obs, o, n) -> validate(start, end, dateRanges, err));

        VBox bookBox = new VBox(10, bookHeader, grid, err, add);
        bookBox.setPadding(new Insets(10));
        bookBox.setStyle("-fx-background-color: #fafafa; -fx-border-color: #ddd; -fx-border-radius: 5;");

        VBox layout = new VBox(20, scroll, new Separator(), bookBox);
        dialog.getDialogPane().setContent(layout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                room.setBookedDates(dateRanges);
                updateRoomInList(room);
                RoomInitializer.updateRooms(rooms);
            }
            return null;
        });

        dialog.showAndWait();
        updateRoomInList(room);
        triggerSearch();
    }

    private void validate(DatePicker start, DatePicker end, CustomLinkedList<DateRange> ranges, Label err) {
        LocalDate s = start.getValue(), e = end.getValue();
        if (s != null && e != null && !e.isBefore(s)) {
            err.setText(isRangeBooked(s, e, ranges) ? ERROR_EXISTING_BOOKINGS_OVERLAP : "");
        } else err.setText("");
    }

    private Callback<DatePicker, DateCell> makeDateCellFactory(CustomLinkedList<DateRange> ranges) {
        return dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now()) || isDateBooked(date, ranges)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        };
    }

    private boolean isDateBooked(LocalDate date, CustomLinkedList<DateRange> ranges) {
        for (DateRange r : ranges) {
            LocalDate s = toLocal(r.getStartDate()), e = toLocal(r.getEndDate());
            if (!date.isBefore(s) && !date.isAfter(e)) return true;
        }
        return false;
    }

    private boolean isRangeBooked(LocalDate start, LocalDate end, CustomLinkedList<DateRange> ranges) {
        for (DateRange r : ranges) {
            LocalDate s = toLocal(r.getStartDate()), eD = toLocal(r.getEndDate());
            if (!end.isBefore(s) && !start.isAfter(eD)) return true;
        }
        return false;
    }

    private LocalDate toLocal(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void updateRoomInList(Room updated) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomNumber() == updated.getRoomNumber()) {
                rooms.set(i, updated);
                break;
            }
        }
    }
}
