import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class D1Logger {

    private static D1Logger instance = null;
    private Logger logger;
    private D1Logger() throws IOException {
        FileHandler handler = new FileHandler("./logs/d1logs.log", true);
        this.logger = Logger.getLogger("D1Logger");
        logger.addHandler(handler);
    }

    public static D1Logger getInstance() throws IOException {
        if (instance == null) {
            instance = new D1Logger();
        }
        return instance;
    }
    public void log(String message)  {
        try{
            logger.info(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
