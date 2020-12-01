import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

public class D1SocketServer implements Callable<Boolean> {
    private static final String key = "ZrRQju1Wz2NgHj5EJj2oOXP6Z3oBf3dF";
    private ServerSocket server;
    private String clientIP;
    public D1SocketServer(int port, String clientIP) throws IOException {
        this.server = new ServerSocket(port);
        this.clientIP = clientIP;
    }

    @Override
    public Boolean call() throws IOException {
        while(true) {
            Socket client = this.server.accept();
            if(!client.getInetAddress().toString().equals(clientIP)){
                this.server.close();
                return false;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while(true){
                if (in.ready()) {
                    String line = in.readLine(); //blocks here if esp crashed or restart
                    if (line != null) {
                        if (line.contains(key)) {
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
