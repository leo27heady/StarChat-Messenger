package chatClient.ui.controller;

import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOutUpBig;
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

public class RegistrationPanel {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label gotoLogin;

    @FXML
    public TextField signNameField;

    @FXML
    public TextField signLoginField;

    @FXML
    private JFXToggleButton checkRemember;

    @FXML
    private JFXButton regButton;

    @FXML
    private TextField signSurnameField;

    @FXML
    private PasswordField signConfirmPasswordField;

    @FXML
    public PasswordField signPasswordField;

    @FXML
    void openRegistration(MouseEvent event) throws IOException {
        Parent FXML = FXMLLoader.load(getClass().getResource("/chatClient/ui/fxml/loginPanel.fxml"));

        FXML.translateXProperty().set(Main.mainLogin.mainPane.getWidth());
        Main.mainLogin.mainPane.getChildren().add(FXML);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(FXML.translateXProperty(), 0, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.3), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(event1 -> Main.mainLogin.mainPane.getChildren().remove(this.mainPane));
        timeline.play();
        Main.mainLogin.textLogin.setText("LOGIN");
    }

    @FXML
    void regButton(MouseEvent event) throws IOException {
        if (signLoginField.getText().isEmpty() && signNameField.getText().isEmpty() && signSurnameField.getText().isEmpty() && signPasswordField.getText().isEmpty() && signConfirmPasswordField.getText().isEmpty()) {
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Fill in all the fields!", "You have not filled in the fields, fill them out and click the \"Register\" button");
        }else if(signNameField.getText().isEmpty()){
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Enter Name!", "Name label is Empty");
        }else if(signSurnameField.getText().isEmpty()){
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Enter Surname!", "Surname label is Empty");
        }else if(signLoginField.getText().isEmpty()){
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Enter Nickname!", "Nickname label is Empty");
        }else if(signPasswordField.getText().isEmpty()){
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Enter Password!", "Password label is Empty");
        }else if(signConfirmPasswordField.getText().isEmpty()){
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Enter Confirm Password!", "Confirm Password label is Empty");
        }else if(!signPasswordField.getText().equals(signConfirmPasswordField.getText())){
            Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane,"Passwords do not match!", "Password and Confirm Password is not Equals");
        }
        else{
            ChatClient client = new ChatClient(Main.getHost(), Main.getServerPort());

            if (client.connect()){
                client.registration(signLoginField.getText()    +
                        " " + signNameField.getText()    +
                        " " + signSurnameField.getText() +
                        " " + signPasswordField.getText());

                String line = client.bufferedIn.readLine();
                String[] tokens = line.split(" ");
                System.out.println(line);
                //client.logoff();

                if (tokens.length > 0) {

                    String cmd = tokens[0];
                    if ("msg".equals(cmd)) {
                        if (tokens[1].equals("Server")) {

                            if (tokens[2].equals("missing")) {
                                File mainDir = new File(System.getProperty("user.home") + "/Documents/StarChat");
                                if (!mainDir.exists()) {
                                    mainDir.mkdirs();
                                }
                                Main.mainUser = new muc.User(signLoginField.getText(), signNameField.getText(), signSurnameField.getText(),signPasswordField.getText(), null, null, checkRemember.isSelected());
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

                            }else if(tokens[2].equals("present")){
                                Main.mainLogin.createErrorAlertDialog(Main.mainLogin.mainPane, Main.mainLogin.stackBoxPane, "Nickname already used!", "User with the same nickname that you want to use is already present");

                                System.out.println("this nick is used");
                            }
                        }
                    }
                }
            }
        }
    }

    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'RegistrationPanel.fxml'.";
        assert signNameField != null : "fx:id=\"signNameField\" was not injected: check your FXML file 'RegistrationPanel.fxml'.";
        assert signLoginField != null : "fx:id=\"signNickNameField\" was not injected: check your FXML file 'RegistrationPanel.fxml'.";
        assert checkRemember != null : "fx:id=\"checkRemember\" was not injected: check your FXML file 'RegistrationPanel.fxml'.";
        assert regButton != null : "fx:id=\"regButton\" was not injected: check your FXML file 'RegistrationPanel.fxml'.";
        assert gotoLogin != null : "fx:id=\"gotoLogin\" was not injected: check your FXML file 'RegistrationPanel.fxml'.";
        assert signSurnameField != null : "fx:id=\"signSurnnameField\" was not injected: check your FXML file 'RegistrationPanel.fxml'.";
        assert signConfirmPasswordField != null : "fx:id=\"signConfirmPasswordField\" was not injected: check your FXML file 'RegistrationPanel.fxml'.";
        assert signPasswordField != null : "fx:id=\"signPasswordField\" was not injected: check your FXML file 'RegistrationPanel.fxml'.";

        signNameField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");
        signSurnameField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");
        signLoginField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");
        signPasswordField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");
        signConfirmPasswordField.setStyle("-fx-background-color: transparent; -fx-text-fill: #7129d1; -fx-prompt-text-fill: #7129d1;");

    }
}
