package muc.client;

import chatClient.main.Main;
import javafx.application.Platform;
import muc.Decrypt;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatClient {

    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    public BufferedReader bufferedIn;
    public BufferedWriter bufferedOut;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();


    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public void msg(String msg) {
        String cmd = msg + "\n";
        try {
            bufferedOut.write(cmd);
            bufferedOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logoff() {
        String cmd = "logoff\n";
        try {
            //serverOut.write(cmd.getBytes());
            bufferedOut.write(cmd);
            bufferedOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String login) {

        String cmd = "login " + login + '\n';
        try {
            bufferedOut.write(cmd);
            bufferedOut.flush();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }

    public boolean registration(String login) {

        String cmd = "registration " + login + '\n';
        try {
            bufferedOut.write(cmd);
            bufferedOut.flush();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }

    public void startMessageReader() {

        readMessageLoop();
    }

    public void readMessageLoop(){
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens != null && tokens.length > 0) {
                    System.out.println(line);
                    String cmd = tokens[0];
                    if ("online".equals(cmd)) {
                        handleOnline(tokens);
                        Main.onlineUsers.add(tokens[1]);
                        System.out.println("add to list: " + tokens[1]);
                        Platform.runLater(()-> {
                            if (Main.chatPanel.contactName.getAccessibleText() != null && Main.chatPanel.contactName.getAccessibleText().equals(tokens[1])){
                                Main.chatPanel.statusContactLabel.setText(cmd);
                            }
                        });

                    }else if ("offline".equals(cmd)){
                        handleOffline(tokens);

                        Main.onlineUsers.remove(tokens[1]);
                        System.out.println("remove from list: " + tokens[1]);
                        Platform.runLater(()-> {
                            if (Main.chatPanel.contactName.getAccessibleText() != null && Main.chatPanel.contactName.getAccessibleText().equals(tokens[1])){
                                Main.chatPanel.statusContactLabel.setText(cmd);
                            }
                        });

                    }else if ("msg".equals(cmd)) {
                        System.out.println("message");
                        if (tokens[1].equals("Server")){
                            Main.msgServer = line;

                        }
                        else if (tokens[3].equals("userAddRequest")){
                            System.out.println("request");


                            String[] msg = line.split(" ", 9);
                            Platform.runLater(()-> {
                                Main.mainChat.addIcon(msg);
                            });

                        }else if(tokens[3].equals("userImage")){


                            System.out.println("have user image: " + line);
                            String[] msg = line.split(" ", 5);

                            Platform.runLater(() -> {

                                Main.chatPanel.newIconContact(msg);
                            });


                        }else if(tokens.length > 4 ? tokens[4].equals("userImage") : false){



                            System.out.println("topic user image: " + line);
                            String[] msg = line.split(" ", 6);

                            Platform.runLater(() -> {

                                Main.chatPanel.newTopicIconContact(msg);
                            });


                        }else if(tokens[3].equals("inviteToGroup")){
                            System.out.println("invite To Group: " + line);
                            String[] msg = line.split(" ", 6);

                            Platform.runLater(()-> {

                                Main.mainChat.inviteToGroup(msg);
                            });

                        }else if (tokens[3].equals("newMember")){

                            System.out.println("newMember: " + line);
                            String[] msg = line.split(" ", 6);

                            Platform.runLater(()-> {
                                Main.mainChat.newMember(msg);
                            });

                        }else{
                            String[] msg;
                            String body = "";
                            line = new String(StandardCharsets.UTF_8.encode(line).array(), StandardCharsets.UTF_8);
                            if (tokens[1].charAt(0) == '#') {
                                msg = line.split(" ", 5);
                                body = msg[4];
                                if (tokens[2].equals(Main.mainUser.getUsername())) continue;
                            }
                            else {
                                msg = line.split(" ", 4);
                                body = msg[3];
                                if (tokens[1].equals(Main.mainUser.getUsername())) continue;
                            }


                            //body = new Decrypt().decrypt(body);

                            String newLine = "";
                            for (int i = body.length() - 1; i >= 0 ; i--){
                                if(body.charAt(i) == ' ') {
                                    body = Decrypt.removeCharAt(body, i);
                                } else break;
                            }
                            for (int i = 0; i < msg.length; i++) newLine += msg[i] + (i == (msg.length - 1)? "" : " ");

                            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.home") + "/Documents/StarChat/" + Main.mainUser.getUsername() + "/" + tokens[1], true))) {
                                bos.write((newLine + '\n').getBytes());
                            }

                            Platform.runLater(()->{
                                Main.chatPanel.handleNewMessage(msg);

                            });

                        }

                        System.out.println(line);

                        String[] tokensMsg = line.split(" ", 3);
                        handleMessage(tokensMsg);
                    }
                }
                Thread.sleep(100);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for(MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners){
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners){
            listener.online(login);
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is: " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn, StandardCharsets.UTF_8));
            this.bufferedOut = new BufferedWriter(new OutputStreamWriter(serverOut, StandardCharsets.UTF_8));
            return true;
        }catch (SocketException sex){
          sex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener){
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener){
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener){
        messageListeners.remove(listener);
    }

}
