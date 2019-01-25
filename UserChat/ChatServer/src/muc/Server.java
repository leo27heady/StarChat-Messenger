package muc;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread implements Serializable{

    private final int serverPort;

    private ArrayList<ServerWorker> workerList = new ArrayList<>();

    static HashMap<String, ServerWorker> userList = new HashMap<>();

    static HashMap<String, String> topicList = new HashMap<>();

    static List<List<String>> unreadMessage = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ServerWorker> getWorkerList(){
        return workerList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(true) {

                System.out.println("About to accept client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }
    public void addWorker(ServerWorker serverWorker) {
        workerList.add(serverWorker);
    }
}
