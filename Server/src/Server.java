import java.io.*;
import java.net.*;
import java.util.Enumeration;

/**
 * Created by afonso on 28-05-2015.
 */
public class Server {
    private static final int numberControllers = 4;
    private ServerSocket myServer;
    private Socket myClient;
    private BufferedReader in;
    private PrintWriter out;
    private int portNumber = 25294;
    private Packet[] packets = {null, null, null, null};

    private String getIPAdress() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String ipAddress = inetAddress.getHostAddress().toString();
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("Socket exception in getting IP Adress" + ex.toString());
        }
        return null;
    }

    private void createServer() throws IOException {
        try {
            myServer = new ServerSocket();
            myServer.bind(new InetSocketAddress(getIPAdress(), portNumber));
            System.out.println(myServer.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() {
        System.out.println("Waiting for clients...");
        while(true) {
            try {
                myClient = myServer.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* NEW CONTROLLER HYPE */
            boolean full = true;
            for(int i = 1; i <= numberControllers; ++i){
                if(packets[i] == null) {
                    System.out.println("New controller connected! ID: " + i);
                    packets[i] = new Packet();
                    new ServerThread(myClient, i,this).start();
                    full = false;
                    break;
                }
            }
            if(full){
                System.out.println("No room left for controllers!");
            }
        }
    }

    public void disconnectController(int id){
        System.out.println("Controller " + id + " closed.");
        packets[id] = null;
    }

    public void updatePacket(int id, Packet packet){
        packets[id] = packet;
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.createServer();
            server.connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
