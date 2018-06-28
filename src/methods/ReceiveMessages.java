package methods;

import Server.ChatroomGUI;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class ReceiveMessages extends Thread {

    JTabbedPane tabbedPane;
    ChatroomGUI cgi;
    ServerSession serverSession;
    ArrayList<String> chatrooms;
    int port;

    public ReceiveMessages(ChatroomGUI cgi, JTabbedPane tabbedPane, ServerSession serverSession, int port, ArrayList<String> chatrooms) {
        this.cgi = cgi;
    	this.tabbedPane = tabbedPane;
        this.serverSession = serverSession;
        this.port = port;
        this.chatrooms = chatrooms;
    }

    public void run() {
        while (true) {
            try {

                ServerSocket serverSocket = new ServerSocket(port);

                //Eine Verbindung wird eingegangen
                Socket socket = serverSocket.accept();


                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                String[] input = (String[]) in.readObject();

                String type = input[0];


                serverSocket.close();

                switch (type) {
                    case "msg":
                        String message = input[1];
                        String sender = input[2];
                        String chatroom = input[3];

                        // an alle austeilen
                        int port = socket.getPort();

                        ArrayList<String> userIPS = new ArrayList<>();

                        String pfad = "data/users.json";
                        File file = new File(pfad);
                        String content = new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");

                        JSONObject json = new JSONObject(content);

                        for (Iterator it = json.keys(); it.hasNext(); ) {

                            String ip = (String) it.next();
                            userIPS.add(ip);

                        }

                        //System.out.println(userIPS);

                        for (String ip : userIPS) {
                            DistributeMessages temp = new DistributeMessages(new String[]{"msgDistribute", message, sender, chatroom}, ip, port);
                            temp.start();

                        }


                        //ALle Benutzer austeilen {msgDistribute, message, user, chatroom}
                        int index = cgi.findTabByName(chatroom);
                        JTextArea chatHistory = cgi.getChatHistory(index);
                        JScrollBar scrollBar = cgi.getScrollBar(index);
                        chatHistory.append(sender + ": " + message + "\n");
                        scrollBar.setValue(scrollBar.getMaximum());
                        break;

                    case "login":
                        String username = input[1];
                        String password = input[2];
                        String ip = socket.getInetAddress().toString().replace("/", "");

                        //login mit rückgabewerten (Mehr steht bei der Funktion)
                        int success = serverSession.login(username, password, ip);

                        Socket answer = new Socket(ip, 34568);
                        PrintWriter out = new PrintWriter(answer.getOutputStream(), true);
                        out.println(success);
                        answer.close();

                        cgi.printInEveryChatroom(username + " hat den Chat betreten.");
                        break;

                    case "getChatrooms":
                        //Alle aktuellen Chaträume bekommen
                        ArrayList<String> chatrooms = serverSession.getCurrentChatrooms();

                        System.out.println(chatrooms);

                        ip = socket.getInetAddress().toString().replace("/", "");

                        answer = new Socket(ip, 45450);
                        ObjectOutput objectOut = new ObjectOutputStream(answer.getOutputStream());
                        objectOut.writeObject(chatrooms);
                        answer.close();
                        break;

                    case "addNewChatroom":
                        //Zeit hat leider nicht gereicht

                        chatroom = input[1];

                        pfad = "data/chatrooms.json";
                        file = new File(pfad);
                        content = new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");

                        json = new JSONObject(content);

                        json.put(chatroom, "null");

                        FileWriter fw = new FileWriter(new File(pfad));
                        fw.write(json.toString());
                        fw.flush();
                        fw.close();

                        ip = socket.getInetAddress().toString().replace("/", "");
                        answer = new Socket(ip, 55555);
                        objectOut = new ObjectOutputStream(answer.getOutputStream());
                        objectOut.writeObject(new String[]{"chatroomadded", chatroom});
                        answer.close();

                        cgi.addChatroom(chatroom);
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}