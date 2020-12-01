import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Script2 {
    private static D1Logger masterLogger;
    public static void main(String[]args){
        try{
            masterLogger = D1Logger.getInstance();
            while(true){
                masterLogger.log("Waiting to start...");
                while(!startListener(10)){ }
                System.out.println("Start signal received");
                ArrayList<String> epcs = new ArrayList<>();
                epcs.add("87983579835792357");
                epcs.add("980840352903675");
                saveToCSV(epcs);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static boolean startListener(int timeout){
        FutureTask task;
        D1SocketServer socket = null;
        try {
            socket = new D1SocketServer(8080, "/192.168.10.135");
            task = new FutureTask(socket);
            Thread t = new Thread(task);
            t.start();
            return (boolean) task.get(timeout,TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            try {
                socket.close();
                return false;
            } catch (IOException ioException) {
                return false;
            }
        } catch (Exception e){
            masterLogger.err(e.getMessage());
            return false;
        }
    }

    public static boolean stopListener(int timeout){
        FutureTask task;
        D1SocketServer socket = null;
        try {
            socket = new D1SocketServer(8080, "/192.168.10.135");
            task = new FutureTask(socket);
            Thread t = new Thread(task);
            t.start();
            return (boolean) task.get(timeout,TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            try {
                socket.close();
                return false;
            } catch (IOException ioException) {
                return false;
            }
        } catch (Exception e){
            masterLogger.err(e.getMessage());
            return false;
        }
    }

    public static void saveToCSV(ArrayList<String> epcs){
        try{
            FileWriter writer = new FileWriter("./results/" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".csv");
            writer.write(epcs.stream().collect(Collectors.joining(",")));
            writer.close();
        }catch (Exception e){
            masterLogger.err(e.getMessage());
        }
    }
}
