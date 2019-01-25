package chatClient.ui.controller;

import chatClient.main.Main;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class SettingsController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public AnchorPane mainPane;

    @FXML
    private Circle settingsUserImage;

    @FXML
    private Label textInsteadOfImage;

    @FXML
    private AnchorPane userNamePane;

    @FXML
    private Label settingsUserName;

    @FXML
    private FontAwesomeIcon editPencilUserName;

    @FXML
    private AnchorPane namePane;

    @FXML
    private Label settingsName;

    @FXML
    private FontAwesomeIcon editPencilName;

    @FXML
    private AnchorPane passwordPane;

    @FXML
    private Label settingsPassword;

    @FXML
    private FontAwesomeIcon editPencilPassword;

    @FXML
    private AnchorPane statusPane;

    @FXML
    private Label settingsStatus;

    @FXML
    private FontAwesomeIcon editPencilStatus;

    @FXML
    private JFXButton settingsCancelButton;

    @FXML
    private JFXButton settingsSaveButton;

    private void setExtFilters(FileChooser chooser){
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.JPG"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
    }

    File imageName = null;

    @FXML
    void userImagePane(MouseEvent event) throws IOException {

        Stage stage = (Stage) Main.chatStage.getScene().getWindow();

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")  + "/Desktop"));


        setExtFilters(fileChooser);
        imageName = fileChooser.showOpenDialog(stage);
        if (imageName != null) {

            BufferedImage img = ImageIO.read(new File(imageName.getAbsolutePath()));
            String imgString = imageToString(img, typeFile(imageName.getName()));

            Main.mainUser.setImage(imgString);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(System.getProperty("user.home") + "/Documents/StarChat" + "/##end_user##"))) {
                oos.writeObject(new muc.User(Main.mainUser.getUsername(), Main.mainUser.getName(), Main.mainUser.getSurname(),Main.mainUser.getPassword(), null, imgString, Main.mainUser.isRemember()));
            }

            textInsteadOfImage.setText("");
            settingsUserImage.setFill(new ImagePattern(SearchPanelController.decodeToImage( imgString, 100)));
            Main.mainClient.msg("setImage " + imgString);
            System.out.println(imageName.getAbsolutePath());
        }

    }

    private String typeFile(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }

        return extension;
    }

    public static String imageToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = new String(Base64.getEncoder().encode(imageBytes));

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }



    @FXML
    void userNamePane(MouseEvent event) {

    }

    @FXML
    void namePane(MouseEvent event) {

    }

    @FXML
    void passwordPane(MouseEvent event) {

    }

    @FXML
    void statusPane(MouseEvent event) {

    }

    @FXML
    void cancelButton(MouseEvent event) {

    }

    @FXML
    void saveButton(MouseEvent event) {

    }

    @FXML
    void initialize() throws IOException {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'Settings.fxml'.";
        assert settingsUserImage != null : "fx:id=\"settingsUserImage\" was not injected: check your FXML file 'Settings.fxml'.";
        assert textInsteadOfImage != null : "fx:id=\"textInsteadOfImage\" was not injected: check your FXML file 'Settings.fxml'.";
        assert userNamePane != null : "fx:id=\"userNamePane\" was not injected: check your FXML file 'Settings.fxml'.";
        assert settingsUserName != null : "fx:id=\"settingsUserName\" was not injected: check your FXML file 'Settings.fxml'.";
        assert editPencilUserName != null : "fx:id=\"editPencilUserName\" was not injected: check your FXML file 'Settings.fxml'.";
        assert namePane != null : "fx:id=\"namePane\" was not injected: check your FXML file 'Settings.fxml'.";
        assert settingsName != null : "fx:id=\"settingsName\" was not injected: check your FXML file 'Settings.fxml'.";
        assert editPencilName != null : "fx:id=\"editPencilName\" was not injected: check your FXML file 'Settings.fxml'.";
        assert passwordPane != null : "fx:id=\"passwordPane\" was not injected: check your FXML file 'Settings.fxml'.";
        assert settingsPassword != null : "fx:id=\"settingsPassword\" was not injected: check your FXML file 'Settings.fxml'.";
        assert editPencilPassword != null : "fx:id=\"editPencilPassword\" was not injected: check your FXML file 'Settings.fxml'.";
        assert statusPane != null : "fx:id=\"statusPane\" was not injected: check your FXML file 'Settings.fxml'.";
        assert settingsStatus != null : "fx:id=\"settingsStatus\" was not injected: check your FXML file 'Settings.fxml'.";
        assert editPencilStatus != null : "fx:id=\"editPencilStatus\" was not injected: check your FXML file 'Settings.fxml'.";
        assert settingsCancelButton != null : "fx:id=\"settingsCancelButton\" was not injected: check your FXML file 'Settings.fxml'.";
        assert settingsSaveButton != null : "fx:id=\"settingsSaveButton\" was not injected: check your FXML file 'Settings.fxml'.";

        settingsUserName.setText(Main.mainUser.getUsername());
        settingsName.setText(Main.mainUser.getName() + " " + Main.mainUser.getSurname());
        if (Main.mainUser.getStatus() == null || Main.mainUser.getStatus().equals("null")) settingsStatus.setText("(not have status)");
        else settingsStatus.setText(Main.mainUser.getStatus());
        if (Main.mainUser.getImage() == null || Main.mainUser.getImage().equals("null")) {
            textInsteadOfImage.setText((Character.toString(Main.mainUser.getName().charAt(0)) + Character.toString(Main.mainUser.getSurname().charAt(0))).toUpperCase() );
            settingsUserImage.setFill(javafx.scene.paint.Color.valueOf("#7129d1"));
            System.out.println((Character.toString(Main.mainUser.getName().charAt(0)) + Character.toString(Main.mainUser.getSurname().charAt(0))).toUpperCase());
        }
        else{
            textInsteadOfImage.setText("");
            settingsUserImage.setFill(new ImagePattern(SearchPanelController.decodeToImage( Main.mainUser.getImage(), 100)));
        }
    }
}