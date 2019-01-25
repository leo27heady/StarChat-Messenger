package chatClient.ui.controller;

import chatClient.Crypt;
import chatClient.main.Main;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import muc.User;

public class ChatPanelController {

    public HashMap<String, String> topicNamesList = new HashMap<>();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public AnchorPane mainPane;

    @FXML
    public VBox msgBox;

    @FXML
    private ScrollPane msgScroll;

    @FXML
    private AnchorPane botPanel;

    @FXML
    private TextField chatMsgField;

    @FXML
    StackPane stackBoxPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private FontAwesomeIcon sendMsgIcon;

    @FXML
    JFXButton addContactButton;

    @FXML
    Label blockContactButton;

    @FXML
    public Label contactName;

    @FXML
    public Label statusContactLabel;

    public void handleNewMessage(String[] msg){

        String sender, body;
        if(msg[1].charAt(0) == '#') {
            sender = msg[2];
            body = msg[4];
        } else {
            sender = msg[1];
            body = msg[3];
        }

        if (Main.chatPanel.mainPane != null && msg[1].equals(String.valueOf(Main.chatPanel.mainPane.getUserData()))) {
                System.out.println("sender: " + sender);


                Text text = new Text(body);

                text.setFill(Color.WHITE);
                text.getStyleClass().add("message");
                System.out.println(text.getStyle());
                TextFlow tempFlow = new TextFlow();
                if (msg[1].charAt(0) == '#') {
                    Text txtName = new Text(topicNamesList.get(sender) + "\n");
                    txtName.getStyleClass().add("txtName");
                    tempFlow.getChildren().add(txtName);
                }

                tempFlow.getChildren().add(text);
                tempFlow.setMaxWidth(200);

                TextFlow flow = new TextFlow(tempFlow);

                HBox hbox = new HBox(12);


                flow.getStyleClass().add("textFlowFlipped");
                Main.chatPanel.msgBox.setAlignment(Pos.TOP_LEFT);
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.getChildren().add(flow);
                hbox.getStyleClass().add("hbox");
                Main.chatPanel.msgBox.getChildren().addAll(hbox);
            }
        else{

            ObservableList<Node> workingCollection = FXCollections.observableArrayList(Main.mainChat.flow.getChildren());
            System.out.println("size: " + workingCollection.size());

            for (int j = 0; j < workingCollection.size(); j++) {
                System.out.println(workingCollection.get(j).getAccessibleText().split(" ", 2)[0]);
                if (msg[1].equals(workingCollection.get(j).getAccessibleText().split(" ", 2)[0])) {

                    for (int i = 0; i < Main.contactlist.size(); i++) {
                        if (Main.contactlist.get(i).getUsername().equals(msg[1])){

                            System.out.println("FOUND");
                            UserIconController userIconController = new UserIconController();

                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
                            loader.setController(userIconController);
                            try {
                                Parent root = loader.load();

                                if (msg[1].charAt(0) == '#'){
                                    userIconController.nameLabel.setText((Character.toString(msg[1].charAt(0)) + Character.toString(msg[1].charAt(1))).toUpperCase());
                                    userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                                }
                                else {
                                    if (Main.contactlist.get(i).getImage() != null && !"null".equals(Main.contactlist.get(i).getImage())) {
                                        userIconController.nameLabel.setText("");
                                        userIconController.imageCircle.setFill(new ImagePattern(SearchPanelController.decodeToImage(Main.contactlist.get(i).getImage(), 33)));
                                    } else {

                                        System.out.println((Character.toString(Main.contactlist.get(i).getName().charAt(0)) + Character.toString(Main.contactlist.get(i).getSurname().charAt(0))).toUpperCase());
                                        userIconController.nameLabel.setText((Character.toString(Main.contactlist.get(i).getName().charAt(0)) + Character.toString(Main.contactlist.get(i).getSurname().charAt(0))).toUpperCase());
                                        userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                                    }
                                }
                                Main.contactlist.get(i).setUnreadMessage(Main.contactlist.get(i).getUnreadMessage() + 1);
                                userIconController.countMsgLabel.setText(Integer.toString(Main.contactlist.get(i).getUnreadMessage()));
                                User user = Main.contactlist.get(i);
                                Main.contactlist.remove(i);
                                Main.contactlist.add(0, user);

                                userIconController.countMsgLabel.setVisible(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            userIconController.mainPane.setUserData(workingCollection.get(j).getUserData());
                            userIconController.mainPane.setAccessibleText(workingCollection.get(j).getAccessibleText());


                            Main.mainChat.flow.getChildren().remove(j);
                            Main.mainChat.flow.getChildren().add(0, userIconController.mainPane);

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

                    break;
                }
            }

        }
    }

    @FXML
    void addContactButton(MouseEvent event) {
        addContactButton.setVisible(false);
        blockContactButton.setVisible(false);

        ObservableList<Node> workingCollection = FXCollections.observableArrayList(Main.mainChat.flow.getChildren());
        System.out.println("size: " + workingCollection.size());

        for (Node u : workingCollection) {
            if (contactName.getAccessibleText().equals(u.getAccessibleText().split(" ", 2)[0])) {
                System.out.println(contactName.getAccessibleText());
                u.setAccessibleText(u.getAccessibleText().split(" ", 2)[0]);
                Main.mainChat.flow.getChildren().setAll(workingCollection);
                break;
            }
        }

        for (int i = 0; i < Main.contactlist.size(); i++) {
            if (Main.contactlist.get(i).getUsername().equals(contactName.getAccessibleText())){

                Main.contactlist.get(i).setRemember(false);

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
    }

    @FXML
    void blockContactButton(MouseEvent event) {
        Main.mainClient.msg("block " + contactName.getAccessibleText());

        addContactButton.setVisible(false);
        blockContactButton.setVisible(false);

        ObservableList<Node> workingCollection = FXCollections.observableArrayList(Main.mainChat.flow.getChildren());
        System.out.println("size: " + workingCollection.size());

        for (Node u : workingCollection) {
            if (contactName.getAccessibleText().equals(u.getAccessibleText().split(" ", 2)[0])) {
                System.out.println(contactName.getAccessibleText());
                u.setAccessibleText(u.getAccessibleText().split(" ", 2)[0]);
                Main.mainChat.flow.getChildren().setAll(workingCollection);
                break;
            }
        }

        for (int i = 0; i < Main.contactlist.size(); i++) {
            if (Main.contactlist.get(i).getUsername().equals(contactName.getAccessibleText())){

                Main.contactlist.get(i).setRemember(false);

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


    }

    public void newIconContact(String[] msg){
        ObservableList<Node> workingCollection = FXCollections.observableArrayList(Main.mainChat.flow.getChildren());
        System.out.println("size: " + workingCollection.size());

        for (int i = 0; i < workingCollection.size(); i++) {
            System.out.println(workingCollection.get(i).getAccessibleText().split(" ", 2)[0]);
            if (msg[1].equals(workingCollection.get(i).getAccessibleText().split(" ", 2)[0])) {



                UserIconController userIconController = new UserIconController();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
                loader.setController(userIconController);
                try {
                    Parent root = loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (!msg[4].equals("null")){
                        userIconController.nameLabel.setText("");
                        userIconController.imageCircle.setFill(new ImagePattern(SearchPanelController.decodeToImage(msg[4], 33)));
                    }
                    else {

                        userIconController.nameLabel.setText((Character.toString(contactName.getText().split(" ", 2)[0].charAt(0)) + Character.toString(contactName.getText().split(" ", 2)[1].charAt(0))).toUpperCase() );
                        userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                        System.out.println((Character.toString(contactName.getText().split(" ", 2)[0].charAt(0)) + Character.toString(contactName.getText().split(" ", 2)[1].charAt(0))).toUpperCase());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                userIconController.mainPane.setUserData(workingCollection.get(i).getUserData());
                userIconController.mainPane.setAccessibleText(workingCollection.get(i).getAccessibleText());
                workingCollection.set(i, userIconController.mainPane);
                Main.mainChat.flow.getChildren().setAll(workingCollection);
                break;
            }
        }

        for (int i = 0; i < Main.contactlist.size(); i++) {
            if (Main.contactlist.get(i).getUsername().equals(msg[1])){

                Main.contactlist.get(i).setImage(msg[4]);

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
    }

    public void newTopicIconContact(String[] msg) {
        System.out.println("HELLO WORLD");
        if (!msg[5].equals("null")) {
            for (int i = 0; i < Main.topicList.size(); i++) {
                if (Main.topicList.get(msg[1]).get(i).getUsername().equals(msg[2])) {
                    Main.topicList.get(msg[1]).get(i).setImage(msg[5]);
                    break;
                }
            }
            try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/##topics_user##")))) {
                serial.writeObject(Main.topicList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void sendMsg(ActionEvent event) {
        if(chatMsgField.getText().isEmpty()) return;

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy_HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);

        String body = chatMsgField.getText();
        //body = new Crypt().crypt(body + "  ");
        //body = new String(Charset.forName("Cp866").encode(body).array());

        String msg = "msg " + mainPane.getUserData() + " " + body;
        msg = new String(StandardCharsets.UTF_8.encode(msg).array(), StandardCharsets.UTF_8);
        Main.mainClient.msg(msg);
        createOwnMsgBubble(chatMsgField.getText());
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/" + mainPane.getUserData(), true))) {
            bos.write(("msg " + Main.mainUser.getUsername() + " " + reportDate + " " + chatMsgField.getText() + '\n').getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        chatMsgField.setText("");

    }

    @FXML
    void contactPanelClicked(MouseEvent event) throws IOException {

        if (this.contactName.getAccessibleText().charAt(0) == '#') {

            System.out.println("group panel clicked");

            BoxBlur blur = new BoxBlur(5, 5, 1);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/infAboutGroup.fxml"));

            loader.setController(Main.groupInf);
            Parent root = loader.load();

            Main.groupInf.textInsteadOfImage.setText((Character.toString(this.contactName.getAccessibleText().charAt(0)) + Character.toString(this.contactName.getAccessibleText().charAt(1))).toUpperCase());
            Main.groupInf.contactImage.setFill(Color.valueOf("#7129d1")); // make random numb from the username
            System.out.println((Character.toString(this.contactName.getAccessibleText().charAt(0)) + Character.toString(this.contactName.getAccessibleText().charAt(1))).toUpperCase());


            Main.groupInf.nameLabel.setText(this.contactName.getText());
            Main.groupInf.statusContactLabel.setText(this.statusContactLabel.getText());

            JFXDialog dialog = new JFXDialog(stackBoxPane, Main.groupInf.mainPane, JFXDialog.DialogTransition.CENTER);

            Main.groupInf.addUsers.setOnMouseClicked(event1 -> {
                if (Main.groupInf.usersForGroup.size() != 0){
                    String msgBody = "";

                    Iterator<String> it = Main.groupInf.usersForGroup.iterator();
                    while (it.hasNext()){
                        String user = it.next();
                        if (!it.hasNext()) msgBody += user;
                        else msgBody += user + " ";
                    }
                    System.out.println(msgBody);

                    Main.mainClient.msg("topicAdd " + this.contactName.getText() + " " + msgBody);

                    dialog.close();
                }
                Main.groupInf.chooseLabel.setVisible(true);
                Main.groupInf.scrollBox.getChildren().setAll();

                HashSet<String> groupUsers = new HashSet<>();
                for (int i = 0; i < Main.topicList.get(this.contactName.getText()).size(); i++) {
                    groupUsers.add(Main.topicList.get(this.contactName.getText()).get(i).getUsername());
                }

                for (int i = 0; i < Main.contactlist.size(); i++) {
                    if (Main.contactlist.get(i).getUsername().equals(Main.mainUser.getUsername()) || Main.contactlist.get(i).getUsername().charAt(0) == '#' || groupUsers.contains(Main.contactlist.get(i).getUsername()) ) continue;

                    UserIconController userIconController = new UserIconController();

                    FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
                    loader1.setController(userIconController);
                    try {
                        Parent root1 = loader1.load();

                        if (Main.contactlist.get(i).getImage() != null && !Main.contactlist.get(i).getImage().equals("null")){
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
                        if (checkBox.isSelected()) Main.groupInf.usersForGroup.add(checkBox.getUserData().toString());
                        else Main.groupInf.usersForGroup.remove(checkBox.getUserData().toString());

                    });


                    HBox hbox = new HBox(12);
                    hbox.setOnMouseClicked(event2->{
                        if (checkBox.isSelected()) {
                            checkBox.setSelected(false);
                            Main.groupInf.usersForGroup.remove(checkBox.getUserData().toString());
                        }
                        else {
                            checkBox.setSelected(true);
                            Main.groupInf.usersForGroup.add(checkBox.getUserData().toString());
                        }
                        System.out.println(checkBox.isSelected() + " " + checkBox.getUserData());
                    });

                    Main.groupInf.scrollBox.setAlignment(Pos.TOP_LEFT);
                    hbox.setAlignment(Pos.CENTER_LEFT);


                    hbox.getChildren().add(checkBox);
                    hbox.getChildren().add(userIconController.mainPane);
                    hbox.getChildren().add(labelName);
                    hbox.getStyleClass().add("hbox");
                    Main.groupInf.scrollBox.getChildren().addAll(hbox);
                }
            });

            for (int i = 0; i < Main.topicList.get(this.contactName.getAccessibleText()).size(); i++) {
                //if (Main.topicList.get(this.contactName.getAccessibleText()).get(i).getUsername().equals(Main.mainUser.getUsername()) || Main.topicList.get(this.contactName.getAccessibleText()).get(i).getUsername().charAt(0) == '#') continue;

                UserIconController userIconController = new UserIconController();

                FXMLLoader Loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/userIcon.fxml"));
                Loader.setController(userIconController);
                try {
                    Parent Root = Loader.load();

                    if (Main.topicList.get(this.contactName.getAccessibleText()).get(i).getImage() != null){
                        userIconController.nameLabel.setText("");
                        userIconController.imageCircle.setFill(new ImagePattern(SearchPanelController.decodeToImage(Main.topicList.get(this.contactName.getAccessibleText()).get(i).getImage(), 33)));
                    }
                    else {
                        userIconController.nameLabel.setText((Character.toString(Main.topicList.get(this.contactName.getAccessibleText()).get(i).getName().charAt(0)) + Character.toString(Main.topicList.get(this.contactName.getAccessibleText()).get(i).getSurname().charAt(0))).toUpperCase() );
                        userIconController.imageCircle.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                        System.out.println((Character.toString(Main.topicList.get(this.contactName.getAccessibleText()).get(i).getName().charAt(0)) + Character.toString(Main.topicList.get(this.contactName.getAccessibleText()).get(i).getSurname().charAt(0))).toUpperCase());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                userIconController.mainPane.setOnMouseClicked(event2-> System.out.print(""));
                userIconController.mainPane.setOnMouseDragged(event2-> System.out.print(""));


                Label labelName = new Label(Main.topicList.get(this.contactName.getAccessibleText()).get(i).getName() + " " + Main.topicList.get(this.contactName.getAccessibleText()).get(i).getSurname());
                labelName.setFont(Font.font("ebrima", FontWeight.BOLD, 16));
                labelName.setTextFill(Color.valueOf("#d7d7d7"));




                HBox hbox = new HBox(12);


                Main.groupInf.scrollBox.setAlignment(Pos.TOP_LEFT);
                hbox.setAlignment(Pos.CENTER_LEFT);


                hbox.getChildren().add(userIconController.mainPane);
                hbox.getChildren().add(labelName);
                hbox.getStyleClass().add("hbox");
                Main.groupInf.scrollBox.getChildren().addAll(hbox);
            }




            Main.groupInf.closeDialog.setOnMouseClicked(event1 -> dialog.close());

            dialog.show();
            contentPane.setEffect(blur);

            dialog.setOnDialogClosed(event2 -> {
                Main.groupInf.usersForGroup.clear();
                contentPane.setEffect(null);
            });

        }else{
            System.out.println("contact panel clicked");

            BoxBlur blur = new BoxBlur(5, 5, 1);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/infAboutContact.fxml"));

            loader.setController(Main.contactInf);
            Parent root = loader.load();

            for (int i = 0; i < Main.contactlist.size(); i++) {

                if (Main.contactlist.get(i).getUsername().equals(this.contactName.getAccessibleText())) {
                    if (!Main.contactlist.get(i).getImage().equals("null")) {
                        Main.contactInf.textInsteadOfImage.setText("");
                        Main.contactInf.contactImage.setFill(new ImagePattern(SearchPanelController.decodeToImage(Main.contactlist.get(i).getImage(), (int) (Main.contactInf.contactImage.getRadius() * 2))));
                    } else {

                        Main.contactInf.textInsteadOfImage.setText((Character.toString(Main.contactlist.get(i).getName().charAt(0)) + Character.toString(Main.contactlist.get(i).getSurname().charAt(0))).toUpperCase());
                        Main.contactInf.contactImage.setFill(Color.valueOf("#7129d1")); // make random numb from the username
                        System.out.println((Character.toString(Main.contactlist.get(i).getName().charAt(0)) + Character.toString(Main.contactlist.get(i).getSurname().charAt(0))).toUpperCase());
                    }

                    Main.contactInf.nameLabel.setText(this.contactName.getText());
                    Main.contactInf.statusContactLabel.setText(this.statusContactLabel.getText());
                    Main.contactInf.userName.setText(this.contactName.getAccessibleText());
                    if (Main.contactlist.get(i).getStatus() == null || Main.contactlist.get(i).getStatus().equals("null"))
                        Main.contactInf.aboutStatus.setText("(not have status)");
                    else Main.contactInf.aboutStatus.setText(Main.contactlist.get(i).getStatus());


                    break;
                }
            }

            JFXDialog dialog = new JFXDialog(stackBoxPane, Main.contactInf.mainPane, JFXDialog.DialogTransition.CENTER);

            Main.contactInf.closeDialog.setOnMouseClicked(event1 -> dialog.close());

            dialog.show();
            contentPane.setEffect(blur);

            dialog.setOnDialogClosed(event2 -> contentPane.setEffect(null));
        }
    }

    @FXML
    void initialize()  {
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'chatPanel.fxml'.";
        assert msgScroll != null : "fx:id=\"msgScroll\" was not injected: check your FXML file 'chatPanel.fxml'.";
        assert botPanel != null : "fx:id=\"botPanel\" was not injected: check your FXML file 'chatPanel.fxml'.";
        assert chatMsgField != null : "fx:id=\"chatMsgField\" was not injected: check your FXML file 'chatPanel.fxml'.";
        assert sendMsgIcon != null : "fx:id=\"sendMsgIcon\" was not injected: check your FXML file 'chatPanel.fxml'.";
        msgScroll.vvalueProperty().bind(msgBox.heightProperty());

        topicNamesList.clear();
        System.out.println("contactName: " + Main.mainChat.mainPane.getAccessibleText().split(" ", 2)[0]);
        if (Main.mainChat.mainPane.getAccessibleText().split(" ", 2)[0].charAt(0) == '#') for (int i = 0; i < Main.topicList.get(Main.mainChat.mainPane.getAccessibleText().split(" ", 2)[0]).size(); i++) topicNamesList.put(Main.topicList.get(Main.mainChat.mainPane.getAccessibleText().split(" ", 2)[0]).get(i).getUsername(), Main.topicList.get(Main.mainChat.mainPane.getAccessibleText().split(" ", 2)[0]).get(i).getName() + " " + Main.topicList.get(Main.mainChat.mainPane.getAccessibleText().split(" ", 2)[0]).get(i).getSurname());


        try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/" + Main.mainChat.mainPane.getAccessibleText()))) {
            while(true) {
                String msg = br.readLine();
                System.out.println(msg);
                if (msg == null) {
                    break;
                }
                String[] tokens = msg.split(" ", 4);
                if (tokens[1].charAt(0) != '#') {
                    if (tokens[1].equals(Main.mainUser.getUsername())) {
                        createOwnMsgBubble(tokens[3]);
                    } else {
                        createAnotherMsgBubble("NULL", tokens[1], tokens[3]);
                    }
                }else{

                    tokens = msg.split(" ", 5);
                    if (tokens[2].equals(Main.mainUser.getUsername())) {
                        createOwnMsgBubble(tokens[4]);
                    } else {
                        createAnotherMsgBubble(tokens[1], tokens[2], tokens[4]);
                    }



                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void createOwnMsgBubble(String content){
        Text text = new Text(content);

        text.setFill(Color.WHITE);
        text.getStyleClass().add("message");
        System.out.println(text.getStyle());
        TextFlow tempFlow=new TextFlow();
        /*Text txtName = new Text(Main.mainUser.getUsername() + "\n");
        txtName.setTextAlignment(TextAlignment.RIGHT);
        txtName.getStyleClass().add("txtName");
        tempFlow.getChildren().add(txtName);*/

        tempFlow.getChildren().add(text);
        tempFlow.setMaxWidth(200);

        TextFlow flow = new TextFlow(tempFlow);

        HBox hbox = new HBox(12);

        flow.getStyleClass().add("textFlow");
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(flow);
        hbox.getStyleClass().add("hbox");
        msgBox.getChildren().addAll(hbox);
    }

    void createAnotherMsgBubble(String topic, String sender, String content){
        Text text = new Text(content);

        text.setFill(Color.WHITE);
        text.getStyleClass().add("message");
        System.out.println(text.getStyle());
        TextFlow tempFlow=new TextFlow();
        if (topic.charAt(0) == '#') {
            Text txtName = new Text(topicNamesList.get(sender) + "\n");
            txtName.getStyleClass().add("txtName");
            tempFlow.getChildren().add(txtName);
        }

        tempFlow.getChildren().add(text);
        tempFlow.setMaxWidth(200);

        TextFlow flow=new TextFlow(tempFlow);

        HBox hbox = new HBox(12);



        flow.getStyleClass().add("textFlowFlipped");
        msgBox.setAlignment(Pos.TOP_LEFT);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(flow);
        hbox.getStyleClass().add("hbox");
        msgBox.getChildren().addAll(hbox);
    }


}
