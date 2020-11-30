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
                while(!startListener()){ }
                System.out.println("Start signal received");
                ArrayList<String> epcs = new ArrayList<>();
                epcs.add("87983579835792357");
                epcs.add("980840352903675");
                toCSV(epcs);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static boolean startListener(){
        FutureTask task;
        D1SocketServer socket = null;
        try {
            socket = new D1SocketServer(8080, "/10.0.0.49");
            task = new FutureTask(socket);
            Thread t = new Thread(task);
            t.start();
            return (boolean) task.get(10,TimeUnit.SECONDS);
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

    public static void toCSV(ArrayList<String> epcs){
        try{
            FileWriter writer = new FileWriter("./results/" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".csv");
            writer.write(epcs.stream().collect(Collectors.joining(",")));
            writer.close();
        }catch (Exception e){
            masterLogger.err(e.getMessage());
        }
    }
}
