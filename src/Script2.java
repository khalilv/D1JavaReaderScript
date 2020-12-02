import com.thingmagic.*;

import javax.swing.plaf.synth.Region;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;



public class Script2 {
    private static D1Logger masterLogger;
    private static D1ReadExceptionListener exceptionListener = new D1ReadExceptionListener();
    private static D1ReadListener readListener = new D1ReadListener();
    public static void main(String[]args){
        try{
            masterLogger = D1Logger.getInstance();
            Reader reader = setup("tmr://192.168.0.100");
            while(true){
                readListener.tags.clear();
                exceptionListener.exceptions.clear();
                masterLogger.log("Waiting to start...");
                while(!startListener(10)){ }
                System.out.println("Starting to read...");
                reader.startReading();
                while(!stopListener(10)){}
                reader.stopReading();
                saveToCSV(readListener.tags);
                System.out.println(readListener.tags);
                System.out.println(exceptionListener.exceptions);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Reader setup(String uri) throws Exception {
        Reader reader = Reader.create(uri);
        int [] antennas = {1,2};
        reader.connect();
        if (Reader.Region.UNSPEC == (Reader.Region)reader.paramGet("/reader/region/id"))
        {
            reader.paramSet("/reader/region/id", Reader.Region.NA);
        }
        reader.paramSet("/reader/radio/readPower", 3000);
        reader.paramSet("/reader/gen2/session", Gen2.Session.S0);
        reader.paramSet("/reader/gen2/Tari", Gen2.Tari.TARI_25US);
        reader.paramSet("/reader/gen2/Tari", Gen2.Tari.TARI_25US);
        reader.paramSet("/reader/gen2/BLF", 640);
        reader.paramSet("/reader/gen2/tagEncoding", Gen2.TagEncoding.FM0);
        reader.paramSet("/reader/gen2/tagEncoding", Gen2.TagEncoding.FM0);
        reader.paramSet("/reader/gen2/q", new Gen2.StaticQ(7));
        SimpleReadPlan plan = new SimpleReadPlan(antennas, TagProtocol.GEN2, null, null, 1000);
        reader.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
        reader.addReadListener(readListener);
        reader.addReadExceptionListener(exceptionListener);
        return reader;
    }

    public static boolean startListener(int timeout){
        FutureTask task;
        D1SocketServer socket = null;
        try {
            socket = new D1SocketServer(8081, "/192.168.0.103");
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
                    masterLogger.err(e.getMessage());
                }
                return false;
            }
        } catch (Exception e) {
            try {
                socket.close();
                return false;
            } catch (Exception e2) {
                if(e2.getMessage() != null){
                    masterLogger.err(e2.getMessage());
                }
                return false;
            }
        }
    }

    public static boolean stopListener(int timeout){
        FutureTask task;
        D1SocketServer socket = null;
        try {
            socket = new D1SocketServer(8081, "/192.168.0.103");
            task = new FutureTask(socket);
            Thread t = new Thread(task);
            t.start();
            return (boolean) task.get(timeout,TimeUnit.SECONDS);
        } catch (TimeoutException t){
            try {
                socket.close();
                return false;
            } catch (Exception e) {
                if(e.getMessage() != null){
                    masterLogger.err(e.getMessage());
                }
                return false;
            }
        } catch (Exception e) {
            try {
                socket.close();
                return false;
            } catch (Exception e2) {
                if(e2.getMessage() != null){
                    masterLogger.err(e2.getMessage());
                }
                return false;
            }
        }
    }

    public static void saveToCSV(ArrayList<D1Tag> tags){
        try{
            FileWriter writer = new FileWriter("./results/" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".csv");
            writer.write(tags.stream().map(d1Tag -> d1Tag.toString()).collect(Collectors.joining("\n")));
            writer.close();
        }catch (Exception e){
            masterLogger.err(e.getMessage());
        }
    }

    private static class D1ReadExceptionListener implements ReadExceptionListener
    {
        public ArrayList<ReaderException> exceptions = new ArrayList<>();
        public void tagReadException(com.thingmagic.Reader r, ReaderException re)
        {
            exceptions.add(re);
        }
    }

    private static class D1ReadListener implements ReadListener
    {
        public ArrayList<D1Tag> tags = new ArrayList<>();
        public void tagRead(Reader r, TagReadData tr)
        {
            Optional<D1Tag> existingTag = contains(tags, tr.epcString());
            if(!existingTag.isPresent()) {
                tags.add(new D1Tag(tr.getRssi(),tr.epcString()));
            }else if(existingTag.get().getRssi() < tr.getRssi()){
                existingTag.get().setRssi(tr.getRssi());
            }
        }
        private static Optional<D1Tag> contains(ArrayList<D1Tag> tags, String epc){
            return tags.stream().filter(o -> o.getEpc().equals(epc)).findFirst();
        }

    }

}
