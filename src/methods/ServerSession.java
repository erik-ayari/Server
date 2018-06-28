package methods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerSession {

    private int serverPort;

    public ServerSession(int serverPort) {
        this.serverPort = serverPort;
    }

    public static ArrayList<String> getCurrentChatrooms() throws IOException, JSONException {

        //JsonObjekt erzeugen
        //String pfad = "src/methods/data/chatrooms.json";
        String pfad = "data/chatrooms.json";
        File file = new File(pfad);
        String content = new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");

        JSONObject json = new JSONObject(content);

        //Leere ArrayList erzeugen
        ArrayList<String> chatrooms = new ArrayList<>();

        //Durch die json scrapen
        for (Iterator it = json.keys(); it.hasNext(); ) {

            String name = (String) it.next();
            chatrooms.add(name);

        }

        //Alle Chatrooms zurückgeben
        return chatrooms;

    }

    public int login(String username, String password, String ip) throws IOException, JSONException {

        //Zugewiesener Nutzername der IP ändern
        changeIPAddress(ip, username);

        //String pfad = "src/methods/data/passwords.json";

        String pfad = "data/passwords.json";
        File file = new File(pfad);
        String content = new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");

        JSONObject json = new JSONObject(content);

        for (Iterator it = json.keys(); it.hasNext(); ) {

            String jsonUsername = (String) it.next();

            if (jsonUsername.equals(username)) {
                //Username ist schon vergeben
                if (json.get(jsonUsername).equals(password)) {
                    //Passwort korrekt

                    return 2;

                }
                //password falsch
                return 1;

            }

        }

        registerUser(username, password);
        //Neuer User wird registiert
        return 0;

    }

    public void changeIPAddress(String ip, String username) throws IOException, JSONException {

        String pfad = "data/users.json";
        File file = new File(pfad);
        String content = new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");

        JSONObject json = new JSONObject(content);

        //Neuen namen in die JSON schreiben

        json.put(ip, username);
        FileWriter fw = new FileWriter(new File(pfad));
        fw.write(json.toString());
        fw.flush();
        fw.close();

    }

    public void registerUser(String username, String password) throws JSONException, IOException {

        String pfad = "data/passwords.json";
        File file = new File(pfad);
        String content = new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");

        JSONObject json = new JSONObject(content);

        //Neuen Namen mit password in die JSON schreiben

        json.put(username, password);
        FileWriter fw = new FileWriter(new File(pfad));
        fw.write(json.toString());
        fw.flush();
        fw.close();

    }

    public String[] getAllUsers() throws IOException, JSONException {

        //Alle registrierten User als Array zurückgeben

        String pfad = "data/users.json";
        File file = new File(pfad);
        String content = new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");

        JSONObject json = new JSONObject(content);

        ArrayList<String> users = new ArrayList<>();

        for (Iterator it = json.keys(); it.hasNext(); ) {

            String ip = (String) it.next();
            users.add((String) json.get(ip));

        }

        String[] out = new String[users.size()];
        users.toArray(out);
        return out;


    }

    public String getUserByIP(String ip) throws IOException, JSONException {
        //Nutzer anhand einer IP zurückgeben

        String pfad = "data/users.json";
        File file = new File(pfad);
        String content = new String(Files.readAllBytes(Paths.get(file.toURI())), "UTF-8");

        JSONObject json = new JSONObject(content);

        return (String) json.get(ip);


    }


}