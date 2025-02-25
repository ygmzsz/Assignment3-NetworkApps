import socket
import json
from datetime import datetime

#function that handles snding the log and stablishing the connection
def send_log():
    # Server configuration
    serverAddressPort = ("127.0.0.1", 8080)
    bufferSize = 1024
    
    # Get log message details
    logLevel = input("Enter log level (DEBUG/INFO/WARNING/ERROR): ").strip().upper()
    appName = input("Enter app name: ").strip()
    logMessage = input("Enter log message: ").strip()
    
    # Generate timestamp
    timestamp = datetime.now().strftime("%Y/%m/%d %H:%M:%S")

    # Create JSON log entry with formatted message
    log_entry = {
        "timestamp": timestamp,
        "log_level": logLevel,
        "app_name": appName,
        "message": logMessage
    }

    json_data = json.dumps(log_entry)  # Convert to JSON string
    bytesToSend = json_data.encode()

    # Create a TCP socket
    clientSocket = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
    
    try:
        # Connect to server
        print(f"Connecting to {serverAddressPort[0]}:{serverAddressPort[1]}...")
        clientSocket.connect(serverAddressPort)
        
        # Send JSON message to server
        clientSocket.sendall(bytesToSend)
        print(f"Sent: {json_data}")

        # Receive response from server
        clientSocket.settimeout(2)
        msgFromServer = clientSocket.recv(bufferSize)
        print(f"Response from server: {msgFromServer.decode().strip()}")

    except ConnectionRefusedError:
        print(f"Error: Cannot connect to server {serverAddressPort[0]}:{serverAddressPort[1]}")
    except Exception as e:
        print(f"Error: {e}")
    finally:
        # Close the socket
        clientSocket.close()

#main function, controls the user input
def main():
    print("Testing Client")
    print("---------------------")
    
    while True:
        print("\nOptions:")
        print("1. Send log message")
        print("2. Exit")
        
        choice = input("\nEnter choice (1-2): ")
        
        if choice == "1":
            send_log()
        elif choice == "2":
            print("Later Skater")
            break
        else:
            print("Invalid choice, try again.")

if __name__ == "__main__":
    main()