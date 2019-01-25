package chatClient.main;

import animatefx.animation.*;
import chatClient.ui.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import muc.client.ChatClient;
import muc.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;


public class Main extends Application {

    private static String host = "localhost";
    private static int serverPort = 8818;
    public static String msgServer = null;
    public static ChatClient mainClient = null;

    public static User mainUser = null;

    public static Login mainLogin = new Login();
    public static ChatController mainChat = new ChatController();
    public static ChatPanelController chatPanel = new ChatPanelController();
    public static ErrorAlertController errorAlert = new ErrorAlertController();
    public static InfAboutContactController contactInf = new InfAboutContactController();
    public static InfAboutGroupController groupInf = new InfAboutGroupController();
    public static SearchPanelController searchPanel = new SearchPanelController();
    public static SettingsController settingsPanel = new SettingsController();
    public static CreateGroupChatController groupChat = new CreateGroupChatController();

    public static ArrayList<User> contactlist = new ArrayList<>();
    public static LinkedHashMap<String, ArrayList<User>> topicList = new LinkedHashMap<>();
    public static HashSet<String> onlineUsers = new HashSet<>();

    public static Stage chatStage;
    public static Stage stage = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        if (!isRemember()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/Login.fxml"));
            loader.setController(mainLogin);
            Parent root = loader.load();
            primaryStage.setTitle("Hello World");
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setAlwaysOnTop(true);
            this.stage = primaryStage;
            stage.getIcons().add(new Image("chatClient/ui/util/images/icon1.png"));
            primaryStage.show();
            new FadeInUp(root).setSpeed(1.85).play();
        }

    }

    private boolean isRemember() throws IOException {
        User signUser = null;

        try {
            FileInputStream fis = new FileInputStream(System.getProperty("user.home") + "/Documents/StarChat/##end_user##");
            ObjectInputStream ois = new ObjectInputStream(fis);
            signUser = (User)ois.readObject();
            Main.mainUser = signUser;
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (signUser != null){
            if (signUser.isRemember()) {
                ChatClient client = new ChatClient(Main.host, Main.serverPort);

                if (client.connect()){

                    client.login(signUser.getUsername() + " " + signUser.getPassword());

                    String line = client.bufferedIn.readLine();
                    String[] tokens = line.split(" ");
                    System.out.println(line);
                    //client.logoff();

                    if (tokens.length > 0) {

                        String cmd = tokens[0];
                        if ("msg".equals(cmd)) {
                            if (tokens[1].equals("Server")) {

                                if (tokens[2].equals("missing")) {
                                    System.out.println("missing");
                                    return false;

                                }else if(tokens[2].equals("wrongPassword")){

                                    System.out.println("wrongPassword");
                                    return false;
                                }else if(tokens[2].equals("okayLogin")){

                                    File mainDir = new File(System.getProperty("user.home") + "/Documents/StarChat");
                                    if (!mainDir.exists()) {
                                        mainDir.mkdirs();
                                    }

                                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mainDir.getAbsolutePath() + "/##end_user##"))) {
                                        oos.writeObject(new User(signUser.getUsername(), signUser.getName(), signUser.getSurname(),signUser.getPassword(), tokens[5], tokens[6], signUser.isRemember()));
                                    }

                                    File userDir = new File(mainDir.getAbsolutePath() + "/" + signUser.getUsername());
                                    if(!userDir.exists()) {
                                        userDir.mkdirs();
                                    }

                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatClient/ui/fxml/Chat.fxml"));
                                    loader.setController(Main.mainChat);
                                    Parent root = loader.load();

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
                                }
                            }
                        }
                    }
                    return true;
                }
                else {
                    System.out.println("connection error");
                    return false;
                }
            }
            else return false;
        }
        else return  false;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String getHost() {
        return host;
    }
    public static int getServerPort() {
        return serverPort;
    }

    public static void setHost(String host) {
        Main.host = host;
    }
    public static void setServerPort(int serverPort) {
        Main.serverPort = serverPort;
    }
}
