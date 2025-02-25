package com.example.galleryapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class HelloApplication extends Application {
    // Path to the images folder
    private static final String IMAGE_DIR = "src/main/resources/images";
    // List to store image paths
    private static final List<String> IMAGE_PATHS = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        // Load all images from the folder
        loadImagesFromFolder();

        // Create the main layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Create the thumbnail grid view and pass the root BorderPane
        ThumbnailGridView thumbnailGridView = new ThumbnailGridView(root);
        root.setCenter(thumbnailGridView);

//
        Scene scene = new Scene(root, 800, 450);
        // Link to external CSS file
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("LIKHALE's Rich Internet Gallery App!");

        stage.setScene(scene);
//        it will set the stage to full screen size
        stage.setFullScreen(true);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
    // Load all images from the folder
    private void loadImagesFromFolder() {
        File folder = new File(IMAGE_DIR);
        if (folder.exists() && folder.isDirectory()) {
//            specifying type of file extension to load
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
            if (files != null) {
                for (File file : files) {
                    IMAGE_PATHS.add("/images/" + file.getName()); // Add image path to the list
                }
            }
        } else {
            System.err.println("Image folder not found: " + IMAGE_DIR);
        }
    }
    // Thumbnail Grid View
    static class ThumbnailGridView extends ScrollPane {
        // Reference to the root BorderPane
        private final BorderPane root;
        public ThumbnailGridView(BorderPane root) {
            // Store the reference
            this.root = root;
            // Create the flow pane for thumbnails
            FlowPane thumbnailFlowPane = new FlowPane();
            thumbnailFlowPane.setHgap(20);
            thumbnailFlowPane.setVgap(20);
            thumbnailFlowPane.setPadding(new Insets(20));
            // Load thumbnail images
            for (String imagePath : IMAGE_PATHS) {
                ImageView thumbnail = createThumbnail(imagePath);
                if (thumbnail != null) {
                    thumbnailFlowPane.getChildren().add(thumbnail);
                }
            }
            // Set up the scroll pane
            this.setContent(thumbnailFlowPane);
            this.setFitToWidth(true);
            this.setFitToHeight(true);
            this.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
        private ImageView createThumbnail(String imagePath) {
            // Check if the resource exists
            if (getClass().getResource(imagePath) == null) {
                System.err.println("Image not found: " + imagePath);
                // It will Skip image if is not there
                return null;
            }
            // Load the images
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView thumbnail = new ImageView(image);
            thumbnail.setFitWidth(150);
            thumbnail.setFitHeight(150);
            thumbnail.getStyleClass().add("thumbnail");
            // Add click event to show full-size image
            thumbnail.setOnMouseClicked(event -> {
                FullImageView fullImageView = new FullImageView(image);
                // Use the stored BorderPane reference
                root.setCenter(fullImageView);
            });

            return thumbnail;
        }
    }
    // Full Image View
    static class FullImageView extends BorderPane {
        public FullImageView(Image image) {
            // Create the full-size image view
            ImageView fullImage = new ImageView(image);
            fullImage.setFitWidth(600);
            fullImage.setFitHeight(400);
            fullImage.getStyleClass().add("full-image");

            // Create navigation buttons
            Button backButton = new Button("Back to Thumbnails");
//            setting class/id for the button to modified in css
            backButton.getStyleClass().add("nav-button");

            backButton.setOnAction(event -> {
                ThumbnailGridView thumbnailGridView = new ThumbnailGridView((BorderPane) this.getParent());
                ((BorderPane) this.getParent()).setCenter(thumbnailGridView);
            });

            // Add components to the layout
            HBox buttonBox = new HBox(backButton);
            buttonBox.getStyleClass().add("button-box");
            buttonBox.setPadding(new Insets(20));

            this.setCenter(fullImage);
            this.setBottom(buttonBox);
        }
    }
}
