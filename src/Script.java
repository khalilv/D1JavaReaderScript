


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class Script {
    private static final String key = "ZrRQju1Wz2NgHj5EJj2oOXP6Z3oBf3dF";
    private static D1Logger masterLogger;

    public static void main(String [] args) {
        try{
            masterLogger = D1Logger.getInstance();
            while(true){
                masterLogger.log("Waiting to start...");
                while(!StartListener("http://10.0.0.49/events")){
                   masterLogger.log("Invalid signal recieved");
                }
                System.out.println("Start signal received");
            }
            //do work here
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static boolean StartListener(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while (pingESP(url.getHost())) {
                if (in.ready()) {
                    String line = in.readLine(); //blocks here if esp crashed or restart
                    if (line != null) {
                        if (line.contains(key)) {
                            connection.disconnect();
                            in.close();
                            return true;
                        }
                    } else {
                        connection.disconnect();
                        in.close();
                        return false;
                    }
                }
            }
            return false;
        } catch (ProtocolException e) {
            masterLogger.err(e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            masterLogger.err(e.getMessage());
            return false;
        } catch (IOException e) {
            masterLogger.err(e.getMessage());
            return false;
        }catch(Exception e){
            masterLogger.err(e.getMessage());
            return false;
        }
    }

    public static boolean pingESP(String url){
        try {
            return InetAddress.getByName(url).isReachable(800); //Replace with your name
        } catch (Exception e) {
            masterLogger.err(e.getMessage());
            return false;
        }
    }
}
