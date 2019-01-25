package chatClient.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import chatClient.main.Main;
import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CreateGroupChatController {


    private LinkedHashSet<String> usersForGroup = new LinkedHashSet<>();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public
    AnchorPane mainPane;

    @FXML
    StackPane stackBoxPane;

    @FXML
    private TextField nameGroupField;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox scrollBox;

    @FXML
    void createGroupButton(MouseEvent event) throws IOException {
        if(nameGroupField.getText().isEmpty()){
            Main.mainLogin.createErrorAlertDialog(Main.groupChat.mainPane, Main.mainChat.stackPane, "Enter Name Group!", "Name Group label is Empty");
            return;
        }else if (nameGroupField.getText().charAt(0) != '#'){
            Main.mainLogin.createErrorAlertDialog(Main.groupChat.mainPane, Main.mainChat.stackPane, "Wrong name!", "The first character of the group name is not #");
            return;
        }else if(usersForGroup.isEmpty()){
            Main.mainLogin.createErrorAlertDialog(Main.groupChat.mainPane, Main.mainChat.stackPane, "Nothing selected!", "To continue, select at least one contact first.");
            return;
        }
        String msgBody = Main.mainUser.getUsername() + " ";

        Iterator<String> it = usersForGroup.iterator();
        while (it.hasNext()){
            String user = it.next();
            if (!it.hasNext()) msgBody += user;
            else msgBody += user + " ";
        }
        System.out.println("msgBody: " + msgBody);

        Main.mainClient.msg("topicAdd " + nameGroupField.getText() + " " + msgBody);
    }


    @FXML
    void initialize() throws IOException {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'createGroupChat.fxml'.";
        assert nameGroupField != null : "fx:id=\"nameGroupField\" was not injected: check your FXML file 'createGroupChat.fxml'.";
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected: check your FXML file 'createGroupChat.fxml'.";

        nameGroupField.setText("");

        usersForGroup.clear();

        for (int i = 0; i < Main.contactlist.size(); i++) {
            if (Main.contactlist.get(i).getUsername().equals(Main.mainUser.getUsername()) || Main.contactlist.get(i).getUsername().charAt(0) == '#') continue;

            UserIconController userIconController = new UserIconController();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
            loader.setController(userIconController);
            try {
                Parent root = loader.load();

                if (!Main.contactlist.get(i).getImage().equals("null")){
                    userIconController.nameLabel.setText("");
                    userIconController.imageCircle.setFill(new ImagePattern(SearchPanelController.decodeToImage(Main.contactlist.get(i).getImage(), 33)));
                }
                else {
                    userIconController.nameLabel.setText((Character.toString(Main.contactlist.get(i).getName().charAt(0)) + Character.toString(Main.contactlist.get(i).getSurname().charAt(0))).toUpperCase() );
                    userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                    System.out.println((Character.toString(Main.contactlist.get(i).getName().charAt(0)) + Character.toString(Main.contactlist.get(i).getSurname().charAt(0))).toUpperCase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            userIconController.mainPane.setOnMouseClicked(event2-> System.out.print(""));
            userIconController.mainPane.setOnMouseDragged(event2-> System.out.print(""));


            Label labelName = new Label(Main.contactlist.get(i).getName() + " " + Main.contactlist.get(i).getSurname());
            labelName.setFont(Font.font("ebrima", FontWeight.BOLD, 16));
            labelName.setTextFill(Color.valueOf("#d7d7d7"));

            JFXCheckBox checkBox = new JFXCheckBox("");
            checkBox.setCheckedColor(Paint.valueOf("#7129d1"));
            checkBox.setUserData(Main.contactlist.get(i).getUsername());

            checkBox.setOnMouseClicked(event2->{
                System.out.println(checkBox.isSelected() + " " + checkBox.getUserData());
                if (checkBox.isSelected()) usersForGroup.add(checkBox.getUserData().toString());
                else usersForGroup.remove(checkBox.getUserData().toString());

            });


            HBox hbox = new HBox(12);
            hbox.setOnMouseClicked(event2->{
                if (checkBox.isSelected()) {
                    checkBox.setSelected(false);
                    usersForGroup.remove(checkBox.getUserData().toString());
                }
                else {
                    checkBox.setSelected(true);
                    usersForGroup.add(checkBox.getUserData().toString());
                }
                System.out.println(checkBox.isSelected() + " " + checkBox.getUserData());
            });

            scrollBox.setAlignment(Pos.TOP_LEFT);
            hbox.setAlignment(Pos.CENTER_LEFT);


            hbox.getChildren().add(checkBox);
            hbox.getChildren().add(userIconController.mainPane);
            hbox.getChildren().add(labelName);
            hbox.getStyleClass().add("hbox");
            scrollBox.getChildren().addAll(hbox);
        }

    }

}
