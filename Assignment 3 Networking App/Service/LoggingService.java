import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingService {
    private static String LOG_FILE;
    private static int PORT;
    private static int RATE_LIMIT;
    private static final ConcurrentHashMap<String, AtomicInteger> IP_COUNT = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            loadConfig();
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket client = server.accept();
                new Thread(new ClientHandler(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void loadConfig() {
        try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            LOG_FILE = prop.getProperty("LOG_FILE");
            PORT = Integer.parseInt(prop.getProperty("PORT"));
            RATE_LIMIT = Integer.parseInt(prop.getProperty("RATE_LIMIT"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
            ) {
                String ip = clientSocket.getInetAddress().getHostAddress();
                IP_COUNT.putIfAbsent(ip, new AtomicInteger(0));
                int count = IP_COUNT.get(ip).incrementAndGet();

                if (count > RATE_LIMIT) {
                    out.write("Rate limit exceeded\n");
                    out.flush();
                    clientSocket.close();
                    return;
                }

                String message = in.readLine();
                logMessage(ip, message);
                out.write("Message logged\n");
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized void logMessage(String ip, String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String logMessage = dtf.format(now) + " " + ip + " " + message + "\n";
        System.out.print(logMessage);

        try (BufferedWriter log = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            log.write(logMessage);
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