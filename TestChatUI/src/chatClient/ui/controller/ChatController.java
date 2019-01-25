package chatClient.ui.controller;

import animatefx.animation.*;
import chatClient.main.Main;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

import java.io.*;
import java.net.URL;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import muc.User;

public class ChatController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    StackPane stackPane;

    @FXML
    private VBox parent;

    @FXML
    private Ellipse topMenu;

    @FXML
    private FontAwesomeIcon star;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    public FlowPane flow;

    @FXML
    private Ellipse botMenu;

    @FXML
    private HBox topBox;

    @FXML
    private HBox botBox;

    double xOffset = 0;
    double yOffset = 0;

    boolean isDragged = false;
    int temp = 0;

    @FXML
    public AnchorPane mainPane;

    private void makeStageDrageable() {
        parent.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        parent.setOnMouseDragged(event -> {
            Main.chatStage.setX(event.getScreenX() - xOffset);
            Main.chatStage.setY(event.getScreenY() - yOffset);
        });

    }

    @FXML
    void topMenu(MouseEvent event) {
        if (!isDragged) {
            if (!topBox.isDisable()) {
                new FadeOutDown(topBox).setSpeed(3).play();
                topBox.setDisable(true);
            } else {
                new FadeInUp(topBox).setSpeed(3).play();
                topBox.setVisible(true);
                topBox.setDisable(false);
            }
        }
    }

    @FXML
    void botMenu(MouseEvent event) {
        if (!isDragged) {
            if (!botBox.isDisable()) {
                new FadeOutUp(botBox).setSpeed(3).play();
                botBox.setDisable(true);
            } else {
                new FadeInDown(botBox).setSpeed(3).play();
                botBox.setVisible(true);
                botBox.setDisable(false);
            }
        }
    }

    @FXML
    void searchUser(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/SearchPanel.fxml"));
        loader.setController(Main.searchPanel);
        Parent root = loader.load();

        mainPane.getChildren().removeAll();
        mainPane.getChildren().setAll(Main.searchPanel.mainPane.getChildren());
        Main.mainChat.mainPane.setVisible(true);

    }

    @FXML
    void settings(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/Settings.fxml"));
        loader.setController(Main.settingsPanel);
        Parent root = loader.load();

        mainPane.getChildren().removeAll();
        mainPane.getChildren().setAll(Main.settingsPanel.mainPane.getChildren());
        Main.mainChat.mainPane.setVisible(true);
    }

    @FXML
    void question(MouseEvent event) {

    }

    @FXML
    void quitApp(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    void minimizeApp(MouseEvent event) {

    }

    @FXML
    void createGroupChat(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/createGroupChat.fxml"));
        loader.setController(Main.groupChat);
        Parent root = loader.load();

        mainPane.getChildren().removeAll();
        mainPane.getChildren().setAll(Main.groupChat.mainPane.getChildren());
        Main.mainChat.mainPane.setVisible(true);

    }

    public void addIcon(String[] msg){
        UserIconController userIconController = new UserIconController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
        loader.setController(userIconController);
        try {
            Parent root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (!msg[8].equals("null")) {
                userIconController.nameLabel.setText("");
                userIconController.imageCircle.setFill(new ImagePattern(SearchPanelController.decodeToImage(msg[8], 33)));
            } else {

                userIconController.nameLabel.setText((Character.toString(msg[5].charAt(0)) + Character.toString(msg[6].charAt(0))).toUpperCase());
                userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                System.out.println((Character.toString(msg[5].charAt(0)) + Character.toString(msg[6].charAt(0))).toUpperCase());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        userIconController.mainPane.setUserData(Integer.toString(SearchPanelController.numbPos++));
        userIconController.mainPane.setAccessibleText(msg[4] + " waitAnswer"); // username

        Main.contactlist.add(0, new muc.User(msg[4], msg[5], msg[6], null, null, msg[8], true));
        try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##contacts_user##")))) {
            serial.writeObject(Main.contactlist);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        Main.mainChat.flow.getChildren().add(0, userIconController.mainPane);
    }

    @FXML
    void initialize() throws ClassNotFoundException {
        assert parent != null : "fx:id=\"parent\" was not injected: check your FXML file 'Chat.fxml'.";
        assert star != null : "fx:id=\"star\" was not injected: check your FXML file 'Chat.fxml'.";
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected: check your FXML file 'Chat.fxml'.";
        assert flow != null : "fx:id=\"flow\" was not injected: check your FXML file 'Chat.fxml'.";
        assert topBox != null : "fx:id=\"topBox\" was not injected: check your FXML file 'Chat.fxml'.";
        assert botBox != null : "fx:id=\"botBox\" was not injected: check your FXML file 'Chat.fxml'.";
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'Chat.fxml'.";
        makeStageDrageable();

        ArrayList<User> list = null;

        try (ObjectInputStream serial = new ObjectInputStream(new FileInputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##contacts_user##")))) {
            list = (ArrayList<User>) serial.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null && !list.isEmpty()){
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUsername().charAt(0) == '#'){

                    UserIconController userIconController = new UserIconController();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
                    loader.setController(userIconController);
                    try {
                        Parent root = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    userIconController.nameLabel.setText((Character.toString(list.get(i).getUsername().charAt(0)) + Character.toString(list.get(i).getUsername().charAt(1))).toUpperCase());
                    userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                    if (list.get(i).getUnreadMessage() > 0) {
                        userIconController.countMsgLabel.setText(Integer.toString(list.get(i).getUnreadMessage()));
                        userIconController.countMsgLabel.setVisible(true);
                    }
                    System.out.println((Character.toString(list.get(i).getUsername().charAt(0)) + Character.toString(list.get(i).getUsername().charAt(1))).toUpperCase());



                    userIconController.mainPane.setUserData(Integer.toString(SearchPanelController.numbPos++));
                    userIconController.mainPane.setAccessibleText(list.get(i).getUsername()); // username

                    try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##contacts_user##")))) {
                        serial.writeObject(Main.contactlist);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Main.mainChat.flow.getChildren().add(userIconController.mainPane);

                    continue;
                }
                UserIconController userIconController = new UserIconController();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
                loader.setController(userIconController);
                try {
                    Parent root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    if (!list.get(i).getImage().equals("null")){
                        userIconController.nameLabel.setText("");
                        userIconController.imageCircle.setFill(new ImagePattern(SearchPanelController.decodeToImage(list.get(i).getImage(), 33)));
                    }
                    else {

                        userIconController.nameLabel.setText((Character.toString(list.get(i).getName().charAt(0)) + Character.toString(list.get(i).getSurname().charAt(0))).toUpperCase() );
                        userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                        System.out.println((Character.toString(list.get(i).getName().charAt(0)) + Character.toString(list.get(i).getSurname().charAt(0))).toUpperCase());
                    }
                    if (list.get(i).getUnreadMessage() > 0) {
                        userIconController.countMsgLabel.setText(Integer.toString(list.get(i).getUnreadMessage()));
                        userIconController.countMsgLabel.setVisible(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                userIconController.mainPane.setUserData(Integer.toString(SearchPanelController.numbPos++));
                if (list.get(i).isRemember()) userIconController.mainPane.setAccessibleText(list.get(i).getUsername() + " waitAnswer"); // username
                else userIconController.mainPane.setAccessibleText(list.get(i).getUsername());



                Main.mainChat.flow.getChildren().add(userIconController.mainPane);
            }
            Main.contactlist = list;
            try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##contacts_user##")))) {
                serial.writeObject(Main.contactlist);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (ObjectInputStream serial = new ObjectInputStream(new FileInputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##topics_user##")))) {
            Main.topicList = (LinkedHashMap<String, ArrayList<User>>) serial.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void inviteToGroup(String[] msg) {
        ArrayList<User> userArrayList = new ArrayList<>();
        String topicName = msg[4];
        String object = msg[5];

        try {
            byte bytes[] = Base64.getDecoder().decode(object);

            ByteArrayInputStream bis = null;
            ObjectInputStream ois = null;
            try {
                bis = new ByteArrayInputStream(bytes);
                ois = new ObjectInputStream(bis);
                userArrayList = (ArrayList<muc.User>) ois.readObject();
            } finally {
                if (bis != null) {
                    bis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            }

            for (int i = 0; i < userArrayList.size(); i++) {
                System.out.println( "user:"+i + " " + userArrayList.get(i).getUsername());
            }
            ArrayList<User> userNamesList = new ArrayList<>();
            userNamesList.addAll(userArrayList);
            if (Main.topicList.get(topicName) != null) userNamesList.addAll(Main.topicList.get(topicName));

            Main.topicList.put(topicName, Main.topicList.get(topicName) == null? userArrayList : userNamesList);
            try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##topics_user##")))) {
                serial.writeObject(Main.topicList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            UserIconController userIconController = new UserIconController();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
            loader.setController(userIconController);
            try {
                Parent root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            userIconController.nameLabel.setText((Character.toString(topicName.charAt(0)) + Character.toString(topicName.charAt(1))).toUpperCase());
            userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
            System.out.println((Character.toString(topicName.charAt(0)) + Character.toString(topicName.charAt(1))).toUpperCase());



            userIconController.mainPane.setUserData(Integer.toString(SearchPanelController.numbPos++));
            userIconController.mainPane.setAccessibleText(topicName); // username

            Main.contactlist.add(0, new User(topicName, null, null, null, null, null, false));
            try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##contacts_user##")))) {
                serial.writeObject(Main.contactlist);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Main.mainChat.flow.getChildren().add(0, userIconController.mainPane);

        } catch (ClassNotFoundException cfe) {
            cfe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void newMember(String[] msg){
        ArrayList<User> userArrayList = new ArrayList<>();
        String topicName = msg[4];
        String object = msg[5];

        try {
            byte bytes[] = Base64.getDecoder().decode(object);

            ByteArrayInputStream bis = null;
            ObjectInputStream ois = null;
            try {
                bis = new ByteArrayInputStream(bytes);
                ois = new ObjectInputStream(bis);
                userArrayList = (ArrayList<muc.User>) ois.readObject();
            } finally {
                if (bis != null) {
                    bis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            }

            if (Main.chatPanel.contactName.getAccessibleText().split(" ", 2)[0].equals(topicName)) {
                for (int i = 0; i < userArrayList.size(); i++) {
                    System.out.println("user:" + i + " " + userArrayList.get(i).getUsername());
                    Main.chatPanel.topicNamesList.put(userArrayList.get(i).getUsername(), userArrayList.get(i).getName() + " " + userArrayList.get(i).getSurname());
                }
            }
            ArrayList<User> userNamesList = new ArrayList<>();
            userNamesList.addAll(userArrayList);
            if (Main.topicList.get(topicName) != null) userNamesList.addAll(Main.topicList.get(topicName));

            Main.topicList.put(topicName, Main.topicList.get(topicName) == null? userArrayList : userNamesList);
            try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##topics_user##")))) {
                serial.writeObject(Main.topicList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (Main.chatPanel.contactName.getAccessibleText() != null && Main.chatPanel.contactName.getAccessibleText().equals(msg[4])){
                Main.chatPanel.statusContactLabel.setText(Integer.toString(Main.topicList.get(msg[4]).size()) + " members");
            }


        } catch (ClassNotFoundException cfe) {
            cfe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
