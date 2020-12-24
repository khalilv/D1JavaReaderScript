import com.thingmagic.Gen2;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;

public class ParamGet {
    public static void main(String[]args) {
        Reader reader = null;
        try{
            reader = Reader.create("tmr://192.168.0.102");
            int [] antennas = {1};
            reader.connect();
            if (Reader.Region.UNSPEC == (Reader.Region)reader.paramGet("/reader/region/id"))
            {
                reader.paramSet("/reader/region/id", Reader.Region.NA);
            }
            //add parameters to check here
            System.out.println("/reader/gen2/Tari : " + reader.paramGet("/reader/gen2/Tari"));
            System.out.println("/reader/gen2/q : " + reader.paramGet("/reader/gen2/q"));
            System.out.println("/reader/radio/readPower : " + reader.paramGet("/reader/radio/readPower"));
            System.out.println("/reader/gen2/session : " + reader.paramGet("/reader/gen2/session"));
            System.out.println("/reader/gen2/BLF : " + reader.paramGet("/reader/gen2/BLF"));
            System.out.println("/reader/gen2/tagEncoding : " + reader.paramGet("/reader/gen2/tagEncoding"));
            System.out.println("Finished");
        } catch (ReaderException e) {
            e.printStackTrace();
        }finally {
            reader.destroy();
        }
    }
}
