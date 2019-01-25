package chatClient.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class InfAboutContactController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public
    AnchorPane mainPane;

    @FXML
    public
    FontAwesomeIcon closeDialog;

    @FXML
    private AnchorPane contactImagePane;

    @FXML
    public
    Circle contactImage;

    @FXML
    public
    Label textInsteadOfImage;

    @FXML
    private AnchorPane userNamePane;

    @FXML
    public
    Label userName;

    @FXML
    private AnchorPane statusPane;

    @FXML
    public
    Label aboutStatus;

    @FXML
    public
    Label nameLabel;

    @FXML
    public
    Label statusContactLabel;

    @FXML
    Label blockUser;


    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'infAboutContact.fxml'.";
        assert contactImagePane != null : "fx:id=\"contactImagePane\" was not injected: check your FXML file 'infAboutContact.fxml'.";
        assert contactImage != null : "fx:id=\"contactImage\" was not injected: check your FXML file 'infAboutContact.fxml'.";
        assert textInsteadOfImage != null : "fx:id=\"textInsteadOfImage\" was not injected: check your FXML file 'infAboutContact.fxml'.";
        assert userNamePane != null : "fx:id=\"userNamePane\" was not injected: check your FXML file 'infAboutContact.fxml'.";

    }
}


