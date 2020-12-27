import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;

public class D1SocketServer implements Callable<Boolean> {
    private static final boolean test = false; //uncomment to accept start signals from anyone
    private static final String key = "ZrRQju1Wz2NgHj5EJj2oOXP6Z3oBf3dF"; //esp auth key to send
    private static final Set<String> trustedClients = new HashSet<>(Arrays.asList("/192.168.0.103", "/192.168.0.102")); //list of valid ip addresses to accept start signals from
    private ServerSocket server;

    public D1SocketServer(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    //open socket on specified port. If client sends a signal from a trusted IP and sends the auth key then return true, otherwise return false.
    @Override
    public Boolean call() throws IOException {
        while(true) {
            Socket client = this.server.accept();
            if(!(test || trustedClients.contains(client.getInetAddress().toString()))){
                System.out.println("Received start signal from unexpected source: " + client.getInetAddress().toString() + " Rejecting.");
                this.server.close();
                return false;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while(true){
                if (in.ready()) {
                    String line = in.readLine();
                    if (line != null) {
                        if (!test) {
                            if(line.contains(key)){
                                this.server.close();
                                in.close();
                                return true;
                            }
                        }else{
                            this.server.close();
                            in.close();
                            return true;
                        }
                    } else {
                        this.server.close();
                        in.close();
                        return false;
                    }
                }
            }
        }
    }

    public void close() throws IOException {
        this.server.close();
    }

}
