package chatClient.ui.controller;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

import chatClient.main.Main;
import com.jfoenix.controls.JFXButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import javax.imageio.ImageIO;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.imgscalr.Scalr;

import java.io.IOException;

public class SearchPanelController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public AnchorPane mainPane;

    @FXML
    private TextField searchField;

    @FXML
    private HBox userPlace;

    public static int numbPos = 1;

    @FXML
    void searchField(ActionEvent event) {
        if (!searchField.getText().isEmpty()) {
            Main.mainClient.msg("findUser " + searchField.getText());
            //Platform.runLater(() -> {

                String line = null;
                while(true){
                    if (Main.msgServer != null){
                        line = Main.msgServer;
                        Main.msgServer = null;
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String[] tokens = line.split(" ");
                System.out.println(line);
                if (tokens.length > 0) {

                        String cmd = tokens[0];
                        if ("msg".equals(cmd)) {
                            if (tokens[1].equals("Server")) {
                                if (tokens[2].equals("notFound")) {
                                    userPlace.getChildren().removeAll();
                                    Label label = new Label("Not Found!");
                                    label.setFont(Font.font("ebrima", FontWeight.BOLD, 16));
                                    label.setTextFill(Color.valueOf("#7129d1"));
                                    userPlace.getChildren().setAll(label);
                                    System.out.println("notFound");
                                } else if (tokens[2].equals("found")) {
                                    System.out.println("found");

                                    UserIconController userIconController = new UserIconController();

                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
                                    loader.setController(userIconController);
                                    try {
                                        Parent root = loader.load();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    userPlace.getChildren().removeAll();


                                    System.out.println(tokens[7]);

                                    try {
                                        if (!tokens[7].equals("null")){
                                            userIconController.nameLabel.setText("");
                                            userIconController.imageCircle.setFill(new ImagePattern(SearchPanelController.decodeToImage(tokens[7], 33)));
                                        }
                                        else {

                                            userIconController.nameLabel.setText((Character.toString(tokens[4].charAt(0)) + Character.toString(tokens[5].charAt(0))).toUpperCase() );
                                            userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                                            System.out.println((Character.toString(tokens[4].charAt(0)) + Character.toString(tokens[5].charAt(0))).toUpperCase());
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Label label = new Label(tokens[4] + " " + tokens[5]);
                                    label.setFont(Font.font("ebrima", FontWeight.BOLD, 16));
                                    label.setTextFill(Color.valueOf("#7129d1"));

                                    JFXButton button = new JFXButton("add");
                                    button.setPrefWidth(45);
                                    button.setPrefHeight(25);
                                    button.setFont(Font.font("ebrima", FontWeight.BOLD, 14));
                                    button.setStyle("-fx-background-color: #7129d1; -fx-text-fill: #dedce0;");


//                                ДОДЕЛАТЬ

                                    userPlace.getChildren().setAll(button);
                                    userPlace.getChildren().add(userIconController.mainPane);
                                    userPlace.getChildren().add(label);


                                    button.setOnMouseClicked(event1 -> {
                                        System.out.println("click");

                                        button.setStyle("-fx-background-color: #dedce0; -fx-text-fill: #7129d1;");
                                        button.setDisable(true);


                                        Main.mainClient.msg("msg " + tokens[3] + " userAddRequest " + Main.mainUser.getUsername() + " " + Main.mainUser.getName() + " " + Main.mainUser.getSurname() + " " + Main.mainUser.getStatus() + " " + Main.mainUser.getImage());

                                        if (!tokens[3].equals(Main.mainUser.getUsername())) {
                                            userIconController.mainPane.setUserData(Integer.toString(numbPos++));
                                            userIconController.mainPane.setAccessibleText(tokens[3]); // username

                                            Main.mainChat.flow.getChildren().add(0, userIconController.mainPane);

                                            Main.contactlist.add(0, new muc.User(tokens[3], tokens[4], tokens[5], null, null, tokens[7], false));
                                            try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##contacts_user##")))) {
                                                serial.writeObject(Main.contactlist);
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                if (tokens[6].equals("null")) System.out.println("NULL");
                            }
                        }
                    }
                }

            //});
        }
    }

    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\"manePane\" was not injected: check your FXML file 'SearchPanel.fxml'.";
        assert searchField != null : "fx:id=\"signNameField\" was not injected: check your FXML file 'SearchPanel.fxml'.";
        Main.searchPanel.searchField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: rgba(113, 41, 209, 0.65);");
    }

    public static Image decodeToImage(String imageString, int circleRadius) throws IOException {

        BufferedImage bufImage = null;
        byte[] imageByte;
        try {
            imageByte = Base64.getDecoder().decode(imageString.getBytes());
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            bufImage = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return SwingFXUtils.toFXImage(Scalr.resize(bufImage, circleRadius*2), null );
    }

}
