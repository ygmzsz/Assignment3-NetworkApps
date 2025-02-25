import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/*
File            : LoggingService.java
Project        : SENG2040 - Assignment #3
Programmer    : Dionisio Estupin and Ygnacio Maza Sanchez
File Version : 2025-02-24
Description    : // This class is a logging service that listens for incoming messages from clients and logs them to a file.
// The service has a rate limit of 10 messages per minute per IP address.
// The service reads configuration from a file named config.properties in the same directory.
*/
public class LoggingService {
    private static String LOG_FILE;
    private static int PORT;
    private static int RATE_LIMIT;
    private static final ConcurrentHashMap<String, AtomicInteger> IP_COUNT = new ConcurrentHashMap<>(); // IP address and message count//
    /*
Function: Main()
Description: This is the main method that starts the server and listens for incoming connections.
Parameters: String[] args
Return Values: N/A
*/
    public static void main(String[] args) {
        try {
            loadConfig();   // Load configuration from file//
            ServerSocket server = new ServerSocket(PORT);   // Start server//
            System.out.println("Server started on port " + PORT);   // Display server port//

            while (true) {  // Listen for incoming connections//
                Socket client = server.accept();    // Accept incoming connection//
                new Thread(new ClientHandler(client)).start();  // Start a new thread to handle the connection//
            }
        } catch (IOException e) {   // Handle exceptions//
            e.printStackTrace();    // Print exception stack trace//
            System.exit(1); // Exit program with error code 1//
        }
    }
/*
Function: loadConfig()
Description: This method loads configuration from a file named config.properties.
Parameters: N/A
Return Values: N/A
*/
    private static void loadConfig() {
        try (InputStream input = new FileInputStream("config.properties")) {    // Open config file//
            Properties prop = new Properties(); // Create properties object//
            prop.load(input);   // Load properties from file//
            LOG_FILE = prop.getProperty("LOG_FILE");    // Get log file path//
            PORT = Integer.parseInt(prop.getProperty("PORT"));  // Get server port//
            RATE_LIMIT = Integer.parseInt(prop.getProperty("RATE_LIMIT"));  // Get rate limit//
        } catch (IOException e) {
            e.printStackTrace();// Print exception stack trace//
            System.exit(1);
        }
    }
/*
Function: ClientHandler
Description: This class handles incoming connections from clients.
Parameters: N/A
Return Values: N/A
*/
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;  // Client socket//

        public ClientHandler(Socket clientSocket) { // Constructor//
            this.clientSocket = clientSocket;   // Set client socket//
        }

        @Override
        /*
Function: run
Description: This method reads a message from the client, logs it, and sends a response.
Parameters: N/A
Return Values: N/A
*/
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   // Open input stream//
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())) // Open output stream//
            ) {
                String ip = clientSocket.getInetAddress().getHostAddress(); // Get client IP address//
                IP_COUNT.putIfAbsent(ip, new AtomicInteger(0)); // Add IP address to count map//
                int count = IP_COUNT.get(ip).incrementAndGet(); // Increment message count//

                if (count > RATE_LIMIT) {// Check rate limit//
                    out.write("Rate limit exceeded\n");
                    out.flush();
                    clientSocket.close();
                    return;
                }

                String message = in.readLine();// Read message from client//
                logMessage(ip, message);// Log message//
                out.write("Message logged\n");
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
/*
Function: logMessage
Description: This method logs a message to a file.
Parameters: String ip, String message
Return Values: N/A
*/
    private static synchronized void logMessage(String ip, String message) {    // Synchronized
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();    // Get current date and time//
        String logMessage = dtf.format(now) + " " + ip + " " + message + "\n";  // Create log message//
        System.out.print(logMessage);   // Print log message to console//

        try (BufferedWriter log = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            log.write(logMessage);  // Write log message to file//
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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