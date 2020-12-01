import com.thingmagic.*;

import java.io.FileWriter;
import java.io.IOException;
import java.net.BindException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Script2 {
    private static D1Logger masterLogger;
    private static D1ReadExceptionListener exceptionListener = new D1ReadExceptionListener();
    private static D1ReadListener readListener = new D1ReadListener();
    public static void main(String[]args){
        try{
            masterLogger = D1Logger.getInstance();
            Reader reader = Reader.create("tmr://192.168.0.100");
            int [] antennas = {1,2};
            reader.connect();
            if (Reader.Region.UNSPEC == (Reader.Region)reader.paramGet("/reader/region/id"))
            {
                Reader.Region[] supportedRegions = (Reader.Region[])reader.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
                if (supportedRegions.length < 1)
                {
                    throw new Exception("Reader doesn't support any regions");
                }
                else
                {
                    reader.paramSet("/reader/region/id", supportedRegions[0]);
                }
            }
            SimpleReadPlan plan = new SimpleReadPlan(antennas, TagProtocol.GEN2, null, null, 1000);
            reader.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
            reader.addReadListener(readListener);
            reader.addReadExceptionListener(exceptionListener);
            while(true){
                masterLogger.log("Waiting to start...");
                while(!startListener(10)){ }
                System.out.println("Starting to read...");
                reader.startReading();
                while(!stopListener(10)){}
                reader.stopReading();
                System.out.println(readListener.tags);
                System.out.println(exceptionListener.exceptions);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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

    public static void saveToCSV(ArrayList<String> epcs){
        try{
            FileWriter writer = new FileWriter("./results/" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".csv");
            writer.write(epcs.stream().collect(Collectors.joining(",")));
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

    static class D1ReadListener implements ReadListener
    {
        public ArrayList<String> tags = new ArrayList<>();
        public void tagRead(Reader r, TagReadData tr)
        {
            if(!tags.contains(tr.toString())) {
                tags.add(tr.toString());
            }
        }

    }

}
