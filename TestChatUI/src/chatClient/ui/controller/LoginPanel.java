package chatClient.ui.controller;

import animatefx.animation.*;
import chatClient.main.Main;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import muc.client.ChatClient;

public class LoginPanel {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label registerField;

    @FXML
    private TextField signLoginField;

    @FXML
    private PasswordField signPasswordField;

    @FXML
    private Label forgotField;

    @FXML
    private JFXToggleButton checkRemember;

    @FXML
    private JFXButton signButton;

    @FXML
    private TextField signNameField;

    @FXML
    private TextField signSurnameField;

    @FXML
    private TextField signConfirmPasswordField;

    @FXML
    void openForgPass(MouseEvent event) {

    }

    @FXML
    void openRegistration(MouseEvent event) throws IOException {

        Parent FXML = FXMLLoader.load(getClass().getResource("/chatClient/ui/fxml/RegistrationPanel.fxml"));

        FXML.translateXProperty().set(Main.mainLogin.mainPane.getWidth());
        Main.mainLogin.mainPane.getChildren().add(FXML);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(FXML.translateXProperty(), 0, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.3), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(event1 -> {
            Main.mainLogin.mainPane.getChildren().remove(this.mainPane);
        });
        timeline.play();
        Main.mainLogin.textLogin.setText("REGISTER");

    }

    @FXML
    void signButton() throws IOException {
        if (signLoginField.getText().isEmpty() && signPasswordField.getText().isEmpty()) {
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Enter Login and Password!", "Login and Password label is Empty");
        }else if(signLoginField.getText().isEmpty()){
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Enter Login!", "Login label is Empty");
        }else if(signPasswordField.getText().isEmpty()){
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Enter Password!", "Password label is Empty");
        }else{

            ChatClient client = new ChatClient(Main.getHost(), Main.getServerPort());

            if (client.connect()){
                client.login(signLoginField.getText() + " " + signPasswordField.getText());

                String line = client.bufferedIn.readLine();
                String[] tokens = line.split(" ");
                System.out.println(line);
                //client.logoff();

                if (tokens.length > 0) {

                    String cmd = tokens[0];
                    if ("msg".equals(cmd)) {
                        if (tokens[1].equals("Server")) {

                            if (tokens[2].equals("missing")) {
                                Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Not Found!", "Your profile has not been found");
                                System.out.println("missing");

                            }else if(tokens[2].equals("wrongPassword")){
                                Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Wrong Password!", "Your password does not match the real password");
                                System.out.println("wrongPassword");

                            }else if(tokens[2].equals("okayLogin")){

                                File mainDir = new File(System.getProperty("user.home") + "/Documents/StarChat");
                                if (!mainDir.exists()) {
                                    mainDir.mkdirs();
                                }

                                Main.mainUser = new muc.User(signLoginField.getText(), tokens[3], tokens[4], signPasswordField.getText(), tokens[5], tokens[6], checkRemember.isSelected());
                                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mainDir.getAbsolutePath() + "/##end_user##"))) {
                                    oos.writeObject(Main.mainUser);
                                }

                                File userDir = new File(mainDir.getAbsolutePath() + "/" + signLoginField.getText());
                                if(!userDir.exists()) {
                                    userDir.mkdirs();
                                }


                                new FadeOutUpBig(mainPane).setSpeed(1.85).play();
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/Chat.fxml"));
                                loader.setController(Main.mainChat);
                                Parent root = loader.load();
                                Main.stage.close();

                                Scene scene = new Scene(root);
                                Main.chatStage = new Stage();
                                scene.setFill(Color.TRANSPARENT);
                                Main.chatStage.setScene(scene);
                                Main.chatStage.initStyle(StageStyle.TRANSPARENT);
                                Main.chatStage.setAlwaysOnTop(true);
                                Main.chatStage.show();
                                new FadeInUp(root).setSpeed(1.85).play();
                                Main.mainClient = client;
                                new Thread(() ->Main.mainClient.readMessageLoop()).start();
                            }
                        }
                    }
                }
            }
        }
    }

    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'loginPanel.fxml'.";
        assert registerField != null : "fx:id=\"registerField\" was not injected: check your FXML file 'loginPanel.fxml'.";
        assert signLoginField != null : "fx:id=\"signLoginField\" was not injected: check your FXML file 'loginPanel.fxml'.";
        assert signPasswordField != null : "fx:id=\"signPasswordField\" was not injected: check your FXML file 'loginPanel.fxml'.";
        assert forgotField != null : "fx:id=\"forgotField\" was not injected: check your FXML file 'loginPanel.fxml'.";
        assert checkRemember != null : "fx:id=\"checkRemember\" was not injected: check your FXML file 'loginPanel.fxml'.";
        assert signButton != null : "fx:id=\"signButton\" was not injected: check your FXML file 'loginPanel.fxml'.";

        signNameField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");
        signSurnameField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");
        signLoginField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");
        signPasswordField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");
        signConfirmPasswordField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");


    }
}
