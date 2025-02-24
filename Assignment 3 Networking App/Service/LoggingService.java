import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingService {
    private static String logFile;
    private static int port;
    private static final int RATE_LIMIT = 10;
    private static final ConcurrentHashMap<String, AtomicInteger> IP_COUNT = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java LoggingService <port> <log_file>");
            return;
        }
        try {
            port = Integer.parseInt(args[0]);
            logFile = args[1];
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket client = server.accept();
                new Thread(new ClientHandler(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Log messages with timestamp
    public static synchronized void logMessage(String message) {
        try (FileWriter writer = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(writer);
             PrintWriter out = new PrintWriter(bw)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            out.println("[" + timestamp + "] " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Rate limit based on IP
    public static boolean isRateLimited(String ip) {
        IP_COUNT.putIfAbsent(ip, new AtomicInteger(0));
        if (IP_COUNT.get(ip).incrementAndGet() > RATE_LIMIT) {
            return true;
        }
        return false;
    }
}

class ClientHandler implements Runnable {
    private Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

            String ip = client.getInetAddress().getHostAddress();
            if (LoggingService.isRateLimited(ip)) {
                out.println("Error: Too many requests. Try again later.");
                client.close();
                return;
            }

            String logMessage;
            while ((logMessage = in.readLine()) != null) {
                LoggingService.logMessage("Client (" + ip + "): " + logMessage);
                out.println("Logged: " + logMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
