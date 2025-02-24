import java.io.*;
import java,net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
impport java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingService{
    private static final String LOG_FILE = "log.txt";
    private static final int PORT = 8080;
    private static final int RATE_LIMIT= 10;
    private static final ConcurrentHashMap<String, AtomicInteger> IP_COUNT = new ConcurrentHashMap<>();

    public static void main(String[] args){
        try{
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while(true){
              rt();  Socket client = server.accept();
                new Thread(new ClientHandler(client)).start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}

private static class ClientHandler {
    
}
// Set up logging//
//The service will store logging information in plain text files.//
//the service will not need UI//
//You must use a config file or command line arguments – no-hard coded paths or addresses.//
//You will have to research what features a good logging service should support.
// You will create a client tool to test your service.//
//o This must be developed in a language different than the logging service or a penalty will apply.//
//o This tool should allow for manual testing of your service.//
//o This tool should be able to run a thorough set of automated tests on your logging service.//
// You must adhere to SET standards//
// Demonstration is REQUIRED or you will not receive a grade//