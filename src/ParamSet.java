import com.thingmagic.*;

public class ParamSet {

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
            //add parameters to set here
            reader.paramSet("/reader/gen2/Tari", Gen2.Tari.TARI_6_25US);
            reader.paramSet("/reader/gen2/BLF", Gen2.LinkFrequency.LINK640KHZ);
            reader.paramSet("/reader/gen2/q", new Gen2.StaticQ(7));
            reader.paramSet("/reader/radio/readPower", 3000);
            reader.paramSet("/reader/gen2/session", Gen2.Session.S0);

            reader.paramSet("/reader/gen2/tagEncoding", Gen2.TagEncoding.FM0);
            System.out.println("Parameters set successfully");
        } catch (ReaderException e) {
            e.printStackTrace();
        }finally {
            reader.destroy();
        }
    }
}
