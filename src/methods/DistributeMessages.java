package methods;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DistributeMessages extends Thread {

    private String[] message;
    private String ip;
    private int port;

    public DistributeMessages(String[] message, String ip, int port) {
        this.message = message;
        this.port = port;
        this.ip = ip;
    }

    public void run() {

        try {

            //veruschen, das Objekt zu senden, da paralleler Thread nicht schlimm wenn es einen Fehler wirft
            Socket s = new Socket(this.ip, 55555);
            ObjectOutput out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(this.message);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
