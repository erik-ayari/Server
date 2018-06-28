package Server;

import methods.ReceiveMessages;
import methods.ServerSession;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server {

    private static int port;
    ServerSession serverSession;
    JTabbedPane tabbedPane;
    ArrayList<JTextArea> chatHistorys;
    ArrayList<JScrollBar> scrollBars;
    ArrayList<String> chatrooms;
    ChatroomGUI cgi;
    private JFrame frame;

    public Server() throws IOException, JSONException {
        initialize();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //Port-Eingabe
                    JFrame frame = new JFrame("Welcome!");
                    port = Integer.parseInt(JOptionPane.showInputDialog(frame, "Please enter a port."));
                    //Server-Fenster
                    Server window = new Server();
                    window.frame.setVisible(true);
                    //serverSession = new ServerSession(port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize() throws IOException, JSONException {
        ////Größe des Fensters wird an Bildschirm angepasst
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("Server Administrator");
        frame.setBounds(100, 100, screen.width / 2, screen.height / 2);
        frame.setLocation(screen.width / 2 - frame.getSize().width / 2, screen.height / 2 - frame.getSize().height / 2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatHistorys = new ArrayList<JTextArea>();
        scrollBars = new ArrayList<JScrollBar>();

        //TabbedPane f�r Chatrooms
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 40));

        //Server wird gestartet
        serverSession = new ServerSession(port);

        //Aktuelle Chatrooms auslesen
        chatrooms = serverSession.getCurrentChatrooms();
        //ChatroomGUI - Chatrooms in GUI anzeigen
        cgi = new ChatroomGUI(chatHistorys, scrollBars, tabbedPane, chatrooms);
        cgi.addCurrentChatrooms(chatrooms);

        //Tabs dem Frame hinzuf�gen
        frame.setContentPane(tabbedPane);

        //Lokale IP-Ermitteln und auf allen Chatverlaufen zusammen mit Port anzeigen
        try {
            String localHost = InetAddress.getLocalHost().toString();
            String lh[] = localHost.split("/");
            cgi.printInEveryChatroom("Server wurde gestartet.\nIP: " + lh[1] + "\nPort: " + port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //Thread um Nachrichten zu empfangen starten
        ReceiveMessages rmsg = new ReceiveMessages(cgi, tabbedPane, serverSession, port, chatrooms);
        rmsg.start();
    }

}