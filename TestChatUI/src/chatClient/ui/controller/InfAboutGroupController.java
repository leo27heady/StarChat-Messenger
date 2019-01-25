package chatClient.ui.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class InfAboutGroupController {

    public LinkedHashSet<String> usersForGroup = new LinkedHashSet<>();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public
    AnchorPane mainPane;

    @FXML
    AnchorPane contactImagePane;

    @FXML
    public
    FontAwesomeIcon addUsers;

    @FXML
    public
    Circle contactImage;

    @FXML
    public
    Label textInsteadOfImage;

    @FXML
    public
    Label nameLabel;

    @FXML
    public
    Label statusContactLabel;

    @FXML
    public
    FontAwesomeIcon closeDialog;

    @FXML
    Label leaveGroup;

    @FXML
    ScrollPane scrollPane;

    @FXML
    public
    VBox scrollBox;

    @FXML
    public
    Label chooseLabel;

    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert contactImagePane != null : "fx:id=\"contactImagePane\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert contactImage != null : "fx:id=\"contactImage\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert textInsteadOfImage != null : "fx:id=\"textInsteadOfImage\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert nameLabel != null : "fx:id=\"nameLabel\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert statusContactLabel != null : "fx:id=\"statusContactLabel\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert closeDialog != null : "fx:id=\"closeDialog\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert leaveGroup != null : "fx:id=\"blockUser\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert scrollBox != null : "fx:id=\"scrollBox\" was not injected: check your FXML file 'infAboutGroup.fxml'.";
        assert chooseLabel != null : "fx:id=\"chooseLabel\" was not injected: check your FXML file 'infAboutGroup.fxml'.";

    }


}
