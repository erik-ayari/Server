package Server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import javax.swing.*;

public class ChatroomGUI {

	//ArrayLists der Chatverlauf-Elemente/Nachrichteneingabe-Elemente/Scroll-Elemente
    ArrayList<JTextArea> chatHistorys;
    ArrayList<JScrollBar> scrollBars;
    
    //Tab-Element für Chatrooms
    JTabbedPane tabbedPane;
    
    //Liste der Chatrooms
    ArrayList<String> chatrooms;
    
    //Standardschrift
    Font font = new Font("Tahoma", Font.PLAIN, 40);

    //Konstruktor
    public ChatroomGUI(ArrayList<JTextArea> chatHistorys, ArrayList<JScrollBar> scrollBars, JTabbedPane tabbedPane, ArrayList<String> chatrooms) {
        this.chatHistorys = chatHistorys;
        this.scrollBars = scrollBars;
        this.tabbedPane = tabbedPane;
        this.chatrooms = chatrooms;
    }

    //Chatverlauf Element nach Index
    public JTextArea getChatHistory(int index) {
        return chatHistorys.get(index);
    }
    
    //ScrollBar nach Index
    public JScrollBar getScrollBar(int index) {
    	return scrollBars.get(index);
    }

    //Tab über Chatroom-Namen ermitteln
    public int findTabByName(String title)
    {
        int tabCount = tabbedPane.getTabCount();
        for (int i=0; i < tabCount; i++)
        {
            String tabTitle = tabbedPane.getTitleAt(i);
            if (tabTitle.equals(title)) return i;
        }
        return -1;
    }

    //Chatrooms in der GUI anzeigen
    public void addCurrentChatrooms(ArrayList<String> chats) {

    	//Counter
        int i = 0;
        for(String chat: chats) {
        	//Panel wird initialisiert/Layout zugewiesen
            JPanel panel = new JPanel(new BorderLayout());
            //Chatverlauf wird initilisiert/designt
            JTextArea chatHistory = new JTextArea();
            chatHistory.setFont(font);
            chatHistory.setEditable(false);
            //Chatverlauf wird der ArrayList hinzugefügt
            chatHistorys.add(chatHistory);
            JScrollPane jsp = new JScrollPane(chatHistorys.get(i));
            JScrollBar vertical = jsp.getVerticalScrollBar();
            //ScrollBar wird der ArrayList hinzugefügt
            scrollBars.add(vertical);
            
            //Ursprünglich Funktion, um Nutzer zu entfernen, jedoch zeitlich nicht mehr möglich gewesen
            /*JButton removeUser = new JButton();
            removeUser.setFont(font);
            removeUser.setText("Remove User");
            removeUser.addActionListener(new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // Remove User
					serverSession.removeUser(user);
                }

            });*/
            //Elemente werden dem Panel hinzugefügt
            panel.add(jsp, BorderLayout.CENTER);
            //panel.add(removeUser, BorderLayout.PAGE_END);
            tabbedPane.addTab(chat, panel);
            i++;
        }
    }

    //Ursprünglich Methode, um hinzugefügten Chatroom anzuzeigen
    public void addChatroom(String chatroom) {
        tabbedPane.removeAll();
        chatrooms.add(chatroom);
        addCurrentChatrooms(chatrooms);
    }

    //In jedem Chatroom etwas ausgeben
    public void printInEveryChatroom(String message) {
        for(JTextArea chatHistory : chatHistorys) {
            chatHistory.append(message + "\n");
        }
        for(JScrollBar scrollBar : scrollBars) {
        	scrollBar.setValue(scrollBar.getMaximum());
        }
    }
}