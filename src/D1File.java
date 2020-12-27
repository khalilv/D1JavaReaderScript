import java.io.*;
import java.util.ArrayList;

public class D1File {

    //add the full filepath from the Dynamic1/ folder including file extension. Will send the file to a node server to save to s3.
    public static void exportToS3(String filename) throws IOException {
        String command = "curl -X POST -F avatar=@" + filename + " https://protected-brook-30480.herokuapp.com/s3upload";
        Process process = Runtime.getRuntime().exec(command);
        DataInputStream input = new DataInputStream(process.getInputStream());
        String line;
        while((line = input.readLine()) != null){
            System.out.println(line);
        }
    }

    //add the full filepath from the Dynamic1/ folder including file extension and the String data to write to the file. Will write and save the file in the specified directory
    public static void exportToCSV(String filename, String data) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(data);
        writer.close();
    }

    //reads from input filename and parses file into Arraylist of D1Items
    public static ArrayList<D1Item> parseItemsFromCSV(String filename) throws IOException {
        BufferedReader br = null;
        String line;
        ArrayList<D1Item> items = new ArrayList<>();
        br = new BufferedReader(new FileReader(filename));
        br.readLine();
        while ((line = br.readLine()) != null) {
            String[] itemData = line.split(",");
            //System.out.println(itemData[0]  + "," + itemData[1] + "," +itemData[2] + "," + itemData[3] + "," + itemData[4] + "," + itemData[5]);
            items.add(new D1Item(itemData[0], itemData[1], itemData[2], itemData[3], itemData[4], Double.parseDouble(itemData[5])));
        }
        br.close();
        return items;
    }
}
