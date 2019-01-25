package chatClient.ui.controller;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import chatClient.main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class UserIconController {

    public UserIconController(){}

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public AnchorPane mainPane;

    @FXML
    public Circle imageCircle;

    @FXML
    public Label nameLabel;

    public UserIconController(AnchorPane mainPane, Circle imageCircle, Label nameLabel) {
        this.mainPane = mainPane;
        this.imageCircle = imageCircle;
        this.nameLabel = nameLabel;
    }

    @FXML
    public
    Label countMsgLabel;

    @FXML
    void userIconPressed(MouseEvent event) {
        Main.mainChat.xOffset = event.getSceneX();
        Main.mainChat.yOffset = event.getSceneY();
        Main.mainChat.isDragged = false;
    }

    @FXML
    void userIconDragged(MouseEvent event) {
        Main.chatStage.setX(event.getScreenX() - Main.mainChat.xOffset);
        Main.chatStage.setY(event.getScreenY() - Main.mainChat.yOffset);
        Main.mainChat.isDragged = true;
    }

    @FXML
    void userIconClicked(MouseEvent event) throws IOException {
        if (!Main.mainChat.isDragged) {

            for (int i = 0; i < Main.contactlist.size(); i++) {
                if (Main.contactlist.get(i).getUsername().equals(mainPane.getAccessibleText().split(" ", 2)[0])){
                    countMsgLabel.setVisible(false);
                    countMsgLabel.setText("0");
                    Main.contactlist.get(i).setUnreadMessage(0);
                    try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##contacts_user##")))) {
                        serial.writeObject(Main.contactlist);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }


            //Main.chatPanel.msgBox.
            //Main.chatMsgBoxList.add(Main.chatPanel.msgBox);

            Main.mainChat.mainPane.setAccessibleText(mainPane.getAccessibleText().split(" ", 2)[0]);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/chatPanel.fxml"));

            loader.setController(Main.chatPanel);
            Parent root = loader.load();

            Main.chatPanel.mainPane.setUserData(mainPane.getAccessibleText().split(" ", 2)[0]);
            System.out.println("FOUND: " + Main.chatPanel.mainPane.getUserData());
            Main.mainChat.mainPane.getChildren().removeAll();
            Main.mainChat.mainPane.getChildren().setAll(Main.chatPanel.mainPane.getChildren());

            System.out.println("size: " + mainPane.getAccessibleText().split(" ", 2).length);
            if (mainPane.getAccessibleText().split(" ", 2).length > 1? mainPane.getAccessibleText().split(" ", 2)[1].equals("waitAnswer"):false) {
                Main.chatPanel.addContactButton.setVisible(true);
                Main.chatPanel.blockContactButton.setVisible(true);
            }
            //else Main.chatPanel.answerAddUser.setVisible(false);
            if (mainPane.getAccessibleText().split(" ", 2)[0].charAt(0) == '#'){
                Main.chatPanel.contactName.setText(mainPane.getAccessibleText().split(" ", 2)[0]);
                Main.chatPanel.statusContactLabel.setText(Integer.toString(Main.topicList.get(mainPane.getAccessibleText().split(" ", 2)[0]).size()) + " members");
                Main.chatPanel.contactName.setAccessibleText(mainPane.getAccessibleText().split(" ", 2)[0]);
            }else {
                for (int i = 0; i < Main.contactlist.size(); i++) {

                    if (Main.contactlist.get(i).getUsername().equals(mainPane.getAccessibleText().split(" ", 2)[0])) {
                        Main.chatPanel.contactName.setText(Main.contactlist.get(i).getName() + " " + Main.contactlist.get(i).getSurname());
                        Main.chatPanel.contactName.setAccessibleText(mainPane.getAccessibleText().split(" ", 2)[0]);
                        if (Main.onlineUsers.contains(Main.chatPanel.contactName.getAccessibleText())) Main.chatPanel.statusContactLabel.setText("online");
                        else Main.chatPanel.statusContactLabel.setText("offline");
                        break;
                    }
                }
            }
            Main.mainChat.mainPane.setVisible(true);




            ObservableList<Node> workingCollection = FXCollections.observableArrayList(
                    Main.mainChat.flow.getChildren()
            );
            System.out.println("size: " + workingCollection.size());

            int numb = 0;

            for (Node u :
                    workingCollection) {
                if (event.getSource().hashCode() == u.hashCode()) {
                    System.out.println("event: " + event.getSource().hashCode() + " u: " + u.hashCode());
                    System.out.println("this is: " + u.getUserData());
                    System.out.println("str: " + u.getAccessibleText());
                    numb = Integer.parseInt(u.getUserData().toString());
                    System.out.println("numb: " + numb);
                    continue;
                }
                System.out.println(u.getUserData());

            }

            //*workingCollection.remove(numb);
            //Main.mainChat.flow.getChildren().setAll(workingCollection);
        }
    }

    @FXML
    void initialize() {
        assert mainPane != null : "fx:id=\"mainPain\" was not injected: check your FXML file 'userIcon.fxml'.";
        assert imageCircle != null : "fx:id=\"imageCircle\" was not injected: check your FXML file 'userIcon.fxml'.";
        assert nameLabel != null : "fx:id=\"nameLabel\" was not injected: check your FXML file 'userIcon.fxml'.";

    }
}
