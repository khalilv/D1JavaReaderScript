import com.thingmagic.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;



public class Script2 {
    private static ArrayList items;
    public static void main(String[]args) {
        Reader reader = null;
        ArrayList<D1Tag> d1Tags = new ArrayList<>();
        try {
            items = D1File.parseItemsFromCSV("./items.csv"); //load items from csv
            int readTime = args.length > 0 ? Integer.parseInt(args[0]) : 15000; //set read time (default is 15)
            while (true) {
                System.out.println(getDate() + " Waiting for start signal.");
                while(!startListener(10)){ } //wait for start signal
                reader = Reader.create("tmr://192.168.0.102"); //connect to reader
                int[] antennas = {1};
                reader.connect();
                if (Reader.Region.UNSPEC == (Reader.Region) reader.paramGet("/reader/region/id")) {
                    reader.paramSet("/reader/region/id", Reader.Region.NA);
                }
                System.out.println(getDate() + " Connected to reader successfully.");
                SimpleReadPlan plan = new SimpleReadPlan(antennas, TagProtocol.GEN2, null, null, 1000);
                reader.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
                System.out.println(getDate() + " Starting to read.");
                TagReadData[] tagsRead = reader.read(readTime); //read synchronously
                for (TagReadData tag : tagsRead) {
                    d1Tags.add(new D1Tag(tag.getRssi(), tag.epcString())); //make tag objects
                }
                System.out.println(getDate() + " Finished Reading");
                saveResults(d1Tags); //save results
                d1Tags.clear(); //clear arraylist
                reader.destroy(); //release reader
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(reader != null){
                reader.destroy();
            }
        }
    }


    public static boolean startListener(int timeout){
        FutureTask task;
        D1SocketServer socket = null;
        try {
            socket = new D1SocketServer(8081); //port to listen on
            task = new FutureTask(socket);
            Thread t = new Thread(task);
            t.start();
            return (boolean) task.get(timeout, TimeUnit.SECONDS);
        }catch (TimeoutException t){
            try {
                socket.close();
                return false;
            } catch (Exception e) {
                if(e.getMessage() != null){
                    System.out.println(getDate() + " " + e.getMessage());
                }
                return false;
            }
        } catch (Exception e) {
            try {
                socket.close();
                return false;
            } catch (Exception e2) {
                if(e2.getMessage() != null){
                    System.out.println(getDate() + " "+ e2.getMessage());
                }
                return false;
            }
        }
    }

    private static void saveResults(ArrayList<D1Tag> tags){
        String filename = "./results/" + getDate() + ".csv";
        try {
            D1File.exportToCSV(filename, tags.stream().map(d1Tag -> d1Tag.toString(findItem(d1Tag))).collect(Collectors.joining("\n"))); //save to csv locally
            System.out.println(getDate() + " Saved results to csv file.");
            D1File.exportToS3(filename); //export csv to s3
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Optional<D1Item> findItem(D1Tag tag){
        return items.stream().filter(x -> ((D1Item)x).getEpc().equals(tag.getEpc().substring(0,18))).findAny();
    }

    private static String getDate(){
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
    }
}
