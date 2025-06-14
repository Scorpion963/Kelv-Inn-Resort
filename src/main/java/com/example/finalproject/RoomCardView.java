package com.example.finalproject;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

// Creates a card that displays room information and a "Manage" button
// for opening the room management dialog.
public class RoomCardView extends VBox {
    private final ImageView img = new ImageView();
    private final Label title = new Label();
    private final Label price = new Label();
    private final Label include = new Label();
    private final Button manageBtn = new Button("Manage");

    // initializes the visual layout and styles
    public RoomCardView() {
        setAlignment(Pos.CENTER);
        setPrefSize(150, 220);
        getStyleClass().add("room-card");
        setSpacing(10);

        img.setFitWidth(150);
        img.setFitHeight(220);

        title.getStyleClass().add("room-title");
        price.getStyleClass().add("room-price");
        include.getStyleClass().add("room-include");

        manageBtn.setPrefWidth(150);
        manageBtn.getStyleClass().add("manage");

        getChildren().addAll(img, title, price, include, manageBtn);
    }

    // constructor that also binds a Room and its manage action
    public RoomCardView(Room room, Consumer<Room> onManage) {
        this();
        setRoom(room, onManage);
    }

    public void setRoom(Room room, Consumer<Room> onManage) {
        img.setImage(new Image(getClass().getResource("/com/example/finalproject/" + room.getImageName()).toString()));
        title.setText(room.getRoomType() + " (" + room.getRoomNumber() + ")");
        price.setText("$" + (int) room.getPriceWithTax() + " / night");
        include.setText((room.getIncludes() != null && room.getIncludes().length > 0) ? room.getIncludes()[0] : "No details");

        // triggers action when "Manage" is clicked
        manageBtn.setOnAction(e -> onManage.accept(room));

        // tooltip shows full room details on hover
        Tooltip tip = new Tooltip(room.toString());
        tip.setWrapText(true);
        tip.setMaxWidth(300);
        tip.getStyleClass().add("room-card-tooltip");
        Tooltip.install(this, tip);
    }
}