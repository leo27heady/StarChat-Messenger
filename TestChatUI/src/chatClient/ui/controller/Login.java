package chatClient.ui.controller;

import chatClient.main.Main;
import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;

public class Login {

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isDragged = false;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane parent;

    @FXML
    private Ellipse starEllipse;

    @FXML
    private FontAwesomeIcon star;

    @FXML
    StackPane stackBoxPane;

    @FXML
    private Ellipse powerEllipse;

    @FXML
    private FontAwesomeIcon power;

    @FXML
    public Label textLogin;

    @FXML
    AnchorPane mainPane;

    @FXML
    void initialize() throws IOException {
        assert parent != null : "fx:id=\"parent\" was not injected: check your FXML file 'Login.fxml'.";
        assert starEllipse != null : "fx:id=\"starEllipse\" was not injected: check your FXML file 'Login.fxml'.";
        assert star != null : "fx:id=\"star\" was not injected: check your FXML file 'Login.fxml'.";
        assert powerEllipse != null : "fx:id=\"powerEllipse\" was not injected: check your FXML file 'Login.fxml'.";
        assert textLogin != null : "fx:id=\"textLogin\" was not injected: check your FXML file 'Login.fxml'.";



        power.setOnMouseClicked(event -> {if(!isDragged)System.exit(0);});

        textLogin.setOnMousePressed(event2 -> {
            xOffset = event2.getSceneX();
            yOffset = event2.getSceneY();
            isDragged = false;
        });
        textLogin.setOnMouseDragged(event3 -> {
            Main.stage.setX(event3.getScreenX() - xOffset);
            Main.stage.setY(event3.getScreenY() - yOffset);
            isDragged = true;
        });

        parent.setOnMousePressed(event2 -> {
            xOffset = event2.getSceneX();
            yOffset = event2.getSceneY();
            isDragged = false;
        });
        parent.setOnMouseDragged(event3 -> {
            Main.stage.setX(event3.getScreenX() - xOffset);
            Main.stage.setY(event3.getScreenY() - yOffset);
            isDragged = true;
        });

        mainPane.setOnMousePressed(event2 -> {
            xOffset = event2.getSceneX();
            yOffset = event2.getSceneY();
            isDragged = false;
        });
        mainPane.setOnMouseDragged(event3 -> {
            Main.stage.setX(event3.getScreenX() - xOffset);
            Main.stage.setY(event3.getScreenY() - yOffset);
            isDragged = true;
        });

        Parent FXML = FXMLLoader.load(getClass().getResource("/chatClient/ui/fxml/loginPanel.fxml"));
        mainPane.getChildren().removeAll();
        mainPane.getChildren().setAll(FXML);

    }

    void createErrorAlertDialog(AnchorPane contentPane, StackPane stackPane, String headText, String contentText) throws IOException {
        System.out.println("Error Alert");

        BoxBlur blur = new BoxBlur(5, 5, 1);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/ErrorAlert.fxml"));

        loader.setController(Main.errorAlert);
        loader.load();

        Main.errorAlert.headText.setText(headText);
        Main.errorAlert.contentText.setText(contentText);

        JFXDialog dialog = new JFXDialog(stackPane, Main.errorAlert.mainPane, JFXDialog.DialogTransition.CENTER);

        Main.errorAlert.closeDialog.setOnMouseClicked(event1 -> dialog.close());

        dialog.show();
        contentPane.setEffect(blur);

        dialog.setOnDialogClosed(event2 -> contentPane.setEffect(null));
    }

}
