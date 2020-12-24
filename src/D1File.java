import java.io.*;
import java.util.ArrayList;

public class D1File {
    public static void exportToS3(String filename) throws IOException {
        String command = "curl -X POST -F avatar=@" + filename + " https://protected-brook-30480.herokuapp.com/s3upload";
        Process process = Runtime.getRuntime().exec(command);
        DataInputStream input = new DataInputStream(process.getInputStream());
        String line;
        while((line = input.readLine()) != null){
            System.out.println(line);
        }
    }

    public static void exportToCSV(String filename, String data) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(data);
        writer.close();
    }

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
