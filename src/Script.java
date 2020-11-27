


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Script {
    public static final String key = "ZrRQju1Wz2NgHj5EJj2oOXP6Z3oBf3dF";
    public static void main(String [] args) {
        try{
            System.out.println("Waiting for signal to start...");
            while(!StartListener("http://10.0.0.49/events")){
                System.out.println("Invalid signal received");
                TimeUnit.SECONDS.sleep(2); //sleep for 2 seconds
            }
            System.out.println("Start signal received");
            //do work here



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static boolean StartListener(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while(true){
                String line = in.readLine(); //blocks here if esp crashed or restarts
                if(line != null){
                    if(line.contains(key)){
                        connection.disconnect();
                        in.close();
                        return true;
                    }
                }else{
                    connection.disconnect();
                    in.close();
                    return false;
                }
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
