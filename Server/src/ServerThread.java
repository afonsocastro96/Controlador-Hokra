import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.io.*;
import java.net.Socket;

/**
 * Created by afonso on 02-06-2015.
 */
public class ServerThread extends Thread {
    int id;
    private Socket socket;
    private Server server;


    public ServerThread(Socket clientSocket, int id, Server server){
        this.socket = clientSocket;
        this.id = id;
        this.server = server;
    }

    public void run(){
        DataInputStream data;
        try {
            data = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            return;
        }
        while (true) {
            try {
                boolean actionButton = data.readBoolean();
                int xAxis = data.readInt();
                int yAxis = data.readInt();
                Packet packet = new Packet(actionButton,xAxis,yAxis);
                server.updatePacket(id, packet);
                System.out.println("Controller " + id + ": ActionButton: " + actionButton + " xAxisValue: " + xAxis + " yAxisValue: " + yAxis);
            } catch(EOFException eof) {
                try {
                    server.disconnectController(id);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            } catch(IOException e) {
                e.printStackTrace();
                return;
            }
        }

    }
}
