package chatClient.ui.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ErrorAlertController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    AnchorPane mainPane;

    @FXML
    FontAwesomeIcon closeDialog;

    @FXML
    Label headText;

    @FXML
    Label contentText;

    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'ErrorAlert.fxml'.";
        assert closeDialog != null : "fx:id=\"closeDialog\" was not injected: check your FXML file 'ErrorAlert.fxml'.";
        assert contentText != null : "fx:id=\"contentText\" was not injected: check your FXML file 'ErrorAlert.fxml'.";
        assert headText != null : "fx:id=\"headText\" was not injected: check your FXML file 'ErrorAlert.fxml'.";

    }

}
