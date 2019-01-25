package muc;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerWorker extends Thread implements Serializable {


    private transient Socket clientSocket;

    private transient Server server;

    private User user = new User();
    private transient OutputStream outputStream;
    private transient BufferedWriter bufferedOut;

    private LinkedHashSet<String> topicSet = new LinkedHashSet<>();
    private LinkedHashSet<String> userContacts = new LinkedHashSet<>();
    private HashSet<String> blockedContacts = new HashSet<>();


    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (SocketException sex) {
            try {
                handleLogoff();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        bufferedOut = new BufferedWriter(new OutputStreamWriter(this.outputStream, StandardCharsets.UTF_8));

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(" ");

            if (tokens != null && tokens.length > 0) {

                String cmd = tokens[0];


                if ("logoff".equals(cmd) || "quit".equals(cmd)) {
                    //handleLogoff();
                    break;
                } else if ("login".equals(cmd)) {

                    String[] userData = line.split(" ");
                    String message = handleLogin(userData);

                    //outputStream.write(("msg Server " + message + '\n').getBytes());
                    bufferedOut.write("msg Server " + message + '\n');
                    bufferedOut.flush();

                    if (!message.equals("wrongPassword") && !message.equals("missing")) {
                        handleNewUser(userData[1]);
                    } else {
                        server.removeWorker(this);
                        clientSocket.close();
                    }

                } else if ("registration".equals(cmd)) {

                    String[] userData = line.split(" ");
                    if (handleRegistration(userData)) {
                        //outputStream.write("msg Server missing\n".getBytes());
                        bufferedOut.write("msg Server missing\n");
                        bufferedOut.flush();

                        try (ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File("AllUserData")))) {
                            serial.writeObject(Server.userList);
                        }

                    } else {
                        //outputStream.write("msg Server present\n".getBytes());
                        bufferedOut.write("msg Server present\n");
                        bufferedOut.flush();
                        server.removeWorker(this);

                        clientSocket.close();
                    }
                } else if ("findUser".equals(cmd)) {

                    //outputStream.write(("msg Server " + handleFindUser(tokens[1]) + "\n").getBytes());
                    bufferedOut.write("msg Server " + handleFindUser(tokens[1]) + "\n");
                    bufferedOut.flush();

                } else if("setImage".equals(cmd)){
                    handleSetImage(tokens[1]);


                }else if ("block".equals(cmd)){

                    this.userContacts.remove(tokens[1]);
                    this.blockedContacts.add(tokens[1]);
                    handleMessage(new String[]{"msg", tokens[1], dateToString() + " blocked"});

                }else if ("msg".equals(cmd)) {
                    System.out.println("MSG: " + line);
                    for (int i = 0; i < tokens[2].length(); i++) {
                        System.out.println("    CHAR[" + i+"] = " + (int)tokens[2].charAt(i));
                    }
                    if (tokens.length > 2) {
                        System.out.println("uuu: " + tokens[1] + " " + user.getUsername());
                        if (tokens[2].equals("userAddRequest")) {
                            userContacts.add(tokens[1]);
                            Server.userList.get(tokens[1]).userContacts.add(user.getUsername());
                            handleNewUser(this.user.getUsername());
                        }
                        String[] tokenMsg = line.split(" ", 3);

                        tokenMsg[2] = dateToString() + " " + tokenMsg[2];


                        if (tokenMsg[1].charAt(0) == '#') handleTopicMessage(tokenMsg);
                        else handleMessage(tokenMsg);
                    }
                } else if ("topicAdd".equals(cmd)){ // topicAdd #topic leo bodya dron ...
                    String[] msg = line.split(" ");
                    handleTopicAdd(msg);
                }else if ("join".equals(cmd)) {
                    handleJoin(tokens[1], this.user.getUsername()); // join #team
                } else if ("leave".equals(cmd)) {
                    handleLeave(tokens[1], this.user.getUsername());
                } else {
                    String msg = "unknown " + cmd + "\n";
                    //outputStream.write(msg.getBytes());
                    bufferedOut.write(msg);
                    bufferedOut.flush();
                }
            }
            System.out.println("\n\nusers: " + Server.userList.size() + "\n\n");
            System.out.println("\n\nonline: " + server.getWorkerList().size() + "\n\n");
            sleep(100);
        }
        System.out.println(user.getUsername() + " closed");
        handleLogoff();
    }

    private void handleTopicAdd(String[] msg) throws IOException{
        ArrayList<User> userArrayListMini = new ArrayList<>();
        HashSet<String> userNamesMini = new HashSet<>();
        ArrayList<User> userArrayListMax = new ArrayList<>();
        String serializedObjectMini = "";
        String serializedObjectMax = "";

        for (int i = 2; i < msg.length; i++) {
            User user = Server.userList.get(msg[i]).user.clone();
            user.setPassword(null);
            userArrayListMini.add(user);
            userNamesMini.add(user.getUsername());
            handleJoin(msg[1], msg[i]);
        }
        for (String userName : Server.topicList.get(msg[1]).split(" ")) {
            User user = Server.userList.get(userName).user.clone();
            user.setPassword(null);
            userArrayListMax.add(user);
        }
        System.out.println(userArrayListMax);

        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(userArrayListMax);
            oos.flush();
            bytes = bos.toByteArray();

            serializedObjectMax = Base64.getEncoder().encodeToString(bytes);

            for (int i = 2; i < msg.length; i++)
                handleMessage(new String[]{"msg", msg[i], dateToString() + " inviteToGroup " + msg[1] + " " + serializedObjectMax});

        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }


        if (Server.topicList.get(msg[1]) != null && !Server.topicList.get(msg[1]).isEmpty()) {
            byte[] bytes1 = null;
            ByteArrayOutputStream bos1 = null;
            ObjectOutputStream oos1 = null;
            try {
                bos1 = new ByteArrayOutputStream();
                oos1 = new ObjectOutputStream(bos1);
                oos1.writeObject(userArrayListMini);
                oos1.flush();
                bytes1 = bos1.toByteArray();

                serializedObjectMini = Base64.getEncoder().encodeToString(bytes1);

                for (int i = 0; i < userArrayListMax.size(); i++)
                    if (!userNamesMini.contains(userArrayListMax.get(i).getUsername())) handleMessage(new String[]{"msg", userArrayListMax.get(i).getUsername(), dateToString() + " newMember " + msg[1] + " " + serializedObjectMini});

            } finally {
                if (oos1 != null) {
                    oos1.close();
                }
                if (bos1 != null) {
                    bos1.close();
                }
            }
        }
    }

    private String dateToString(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy_HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        System.out.println("Report Date: " + reportDate);
        return reportDate;
    }

    private void handleSetImage(String imageString) throws IOException {
        System.out.println("userImage");

        Iterator<String> it = userContacts.iterator();

        while (it.hasNext()){
            String user = it.next();
            if (Server.userList.get(user) != null){
                handleMessage(new String[]{"msg", Server.userList.get(user).user.getUsername(), dateToString() + " userImage " + imageString});
                if (Server.userList.get(user).user.getUsername().equals(this.getUser().getUsername()) ) Server.userList.get(user).user.setImage(imageString);
            }
        }

        System.out.println("TOPIC SET: " + Server.userList.get(this.user.getUsername()).topicSet);
        String[] topics =  Server.userList.get(this.user.getUsername()).topicSet.toArray(new String[Server.userList.get(this.user.getUsername()).topicSet.size()]);
        for (String topic : topics){
            System.out.println("USER LIST: " + Server.topicList.get(topic));
            //String[] usersTopic = Server.topicList.get(topic).split(" ");

            handleTopicMessage(new String[]{"msg", topic, dateToString() + " userImage " + imageString});

        }

    }

    private String handleFindUser(String userName) throws IOException {
        if (Server.userList.get(userName) != null) {
            return "found " + Server.userList.get(userName).user.getUsername()
                    + " " + Server.userList.get(userName).user.getName()
                    + " " + Server.userList.get(userName).user.getSurname()
                    + " " + Server.userList.get(userName).user.getStatus()
                    + " " + Server.userList.get(userName).user.getImage();
        }
        else return "notFound";
    }

    private void handleNewUser(String userName) throws IOException {

        List<ServerWorker> workerList = server.getWorkerList();

        // send current user all other online logins
        for(ServerWorker worker : workerList) {
            if (worker.getUser().getUsername() != null && this.userContacts.contains(worker.getUser().getUsername())) {
                if (!userName.equals(worker.getUser().getUsername())) {
                    String msg2 = "online " + worker.getUser().getUsername() + "\n";
                    send(msg2);
                }
            }
        }

        // send other online users current user's status
        String onlineMsg = "online " + userName + "\n";
        for(ServerWorker worker : workerList) {
            if (!userName.equals(worker.getUser().getUsername()) && this.userContacts.contains(worker.getUser().getUsername())) {
                worker.send(onlineMsg);
            }
        }

        for (int i = 0; i < Server.unreadMessage.size(); i++) {
            if(Server.unreadMessage.get(i).get(0).equals(userName)){
                if (Server.unreadMessage.get(i).size() > 1) {
                    for (int j = 1; j < Server.unreadMessage.get(i).size(); j++) send(Server.unreadMessage.get(i).get(j));
                    while (Server.unreadMessage.get(i).size() > 1) Server.unreadMessage.get(i).remove(1);

                }
                break;
            }
        }




    }

    private String handleLogin(String[] userData) throws IOException {

        //this.user.setUsername(userData[1]);
        String nickName = userData[1];

        if (Server.userList.get(nickName) != null){
            System.out.println("found username");
            if (Server.userList.get(nickName).user.getPassword().equals(userData[2])) {
                this.user.setUsername(userData[1]);
                this.userContacts = Server.userList.get(nickName).userContacts;
                this.blockedContacts = Server.userList.get(nickName).blockedContacts;
                System.out.println("okayLogin " + Server.userList.get(nickName).user.getName() + " " + Server.userList.get(nickName).user.getSurname()+ " " + Server.userList.get(nickName).user.getStatus() + " " + Server.userList.get(nickName).user.getImage());
                return "okayLogin " + Server.userList.get(nickName).user.getName() + " " + Server.userList.get(nickName).user.getSurname()+ " " + Server.userList.get(nickName).user.getStatus() + " " + Server.userList.get(nickName).user.getImage();
            }
            else {
                System.out.println("wrongPassword");
                return "wrongPassword";
            }
        }

        System.out.println("not found username");
        return "missing";
    }

    private boolean handleRegistration(String[] userData) throws IOException {

        String nickName = userData[1];

        if (Server.userList.get(nickName) != null){
            System.out.println("found username");
            System.out.println(Arrays.toString(Server.userList.get(nickName).topicSet.toArray(new String[Server.userList.get(nickName).topicSet.size()])));
            return false;
        }

        this.user.setUsername(userData[1]);
        this.user.setName(userData[2]);
        this.user.setSurname(userData[3]);
        this.user.setPassword(userData[4]);

        System.out.println("not found username");
        Server.userList.put(this.user.getUsername(), this);

        return true;
    }

    private void handleLeave(String topicName, String userName) {
        Server.userList.get(userName).topicSet.remove(topicName);


        String[] users = Server.topicList.get(topicName).split(" ");
        String newUsers = "";
        for (int i = 0; i < users.length; i++) {
            if (users[i].equals(userName)) continue;
            if (i == users.length - 1 || (i == users.length - 2 && users[i + 1].equals(userName))) newUsers += users[i];
            else newUsers += users[i] + " ";
        }
        Server.topicList.put(topicName, newUsers);

    }

    /*public boolean isMemberOfTopic(String topic){
        return topicSet.contains(topic);
    }*/

    private void handleJoin(String topicName, String userName) {

        Server.userList.get(userName).topicSet.add(topicName);

        Server.topicList.put(topicName, Server.topicList.get(topicName) == null? userName : Server.topicList.get(topicName) + " " + userName);
        System.out.println("topicSet: " + Server.userList.get(userName).topicSet);
        System.out.println("topicList: " + Server.topicList.get(topicName));

    }



    // format: "msg" "login" msg...
    private void handleMessage(String[] tokens) throws IOException {

        String  sendTo = tokens[1];
        String body = tokens[2];

        boolean userIsOnline = false;

        for (ServerWorker worker : server.getWorkerList()){
            System.out.println(worker.user.getUsername());
            if (sendTo.equals(worker.getUser().getUsername())){
                String outMsg = "msg " + user.getUsername() + " " + body + "\n";
                worker.send(outMsg);
                userIsOnline = true;
                break;
            }

        }

        if(!userIsOnline) {
            int size = Server.unreadMessage.size();

            boolean foundUser = false;

            String outMsg = "msg " + user.getUsername() + " " + body + "\n";
            if(size > 0) for (int i = 0; i < size; i++) {
                if(Server.unreadMessage.get(i).get(0).equals(sendTo)){
                    Server.unreadMessage.get(i).add(outMsg);
                    foundUser = true;
                }
            }

            if(!foundUser) {
                Server.unreadMessage.add(new ArrayList<String>());
                size = Server.unreadMessage.size();
                Server.unreadMessage.get(size - 1).add(sendTo);
                Server.unreadMessage.get(size - 1).add(outMsg);
            }


        }
    }

    public boolean isMemberOfTopic(String topic){
        return topicSet.contains(topic);
    }

    // format: "msg" "#topic" body...
    private void handleTopicMessage(String[] tokens) throws IOException, ClassCastException {
        System.out.println(tokens[2]);
        String  sendTo = tokens[1];
        String body = tokens[2];
        String outMsg = "msg " + sendTo + " " + user.getUsername() + " " + body + "\n";

        /*Iterator<String> it = topicSet.iterator();
        while (it.hasNext()){
            String topic = it.next();

        }*/
        System.out.println("LIST: " + Server.topicList.get(sendTo));
        HashSet<String> set = new HashSet<>(Arrays.asList(Server.topicList.get(sendTo).split(" ")));
        /*System.out.println("set: " + set);
        System.out.println("topicList: " + Server.topicList.get(sendTo));*/

        for (ServerWorker worker : server.getWorkerList()) {
            if (Server.userList.get(worker.user.getUsername()).isMemberOfTopic(sendTo)){
                System.out.println("member: " + worker.user.getUsername());
                worker.send(outMsg);
                set.remove(worker.user.getUsername());
            }
        }

        if(!set.isEmpty()) {
            System.out.println("notEmpty: " + set);
            for (int i = 0; i < Server.unreadMessage.size(); i++) {

                if(set.contains(Server.unreadMessage.get(i).get(0))){
                    Server.unreadMessage.get(i).add(outMsg);
                    set.remove(Server.unreadMessage.get(i).get(0));
                }
            }

            if(!set.isEmpty()) {
                String[] mass = set.toArray(new String[set.size()]);
                for (int i = 0; i < mass.length; i++) {
                    Server.unreadMessage.add(new ArrayList<String>());
                    Server.unreadMessage.get(Server.unreadMessage.size() - 1).add(mass[i]);
                    Server.unreadMessage.get(Server.unreadMessage.size() - 1).add(outMsg);
                }
            }


        }



    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        //List<ServerWorker> workerList = server.getWorkerList();

        // send other online users current user's status

        String onlineMsg = "offline " + user.getUsername() + "\n";
        for (ServerWorker worker : server.getWorkerList()) {
            if (!user.getUsername().equals(worker.getUser().getUsername()) && this.userContacts.contains(worker.getUser().getUsername())) {
                worker.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    /*public String getLogin() {
        return login;
    }*/

    public User getUser(){
        return user;
    }
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void setServer(Server server) {
        this.server = server;
    }


    private void send(String msg) throws IOException {
        if (user.getUsername() != null) {
            //outputStream.write(msg.getBytes());
            bufferedOut.write(msg);
            bufferedOut.flush();
        }
    }
}