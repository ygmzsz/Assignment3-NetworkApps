import socket
from datetime import datetime

def send_log():
    # Server configuration
    serverAddressPort = ("127.0.0.1", 8080)
    bufferSize = 1024
    
    # Get log message details
    logLevel = input("Enter log level (DEBUG/INFO/WARNING/ERROR): ").strip().upper()
    appName = input("Enter app name: ").strip()
    logMessage = input("Enter log message: ").strip()
    
    # Format the log message
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    msgToSend = f"[{logLevel}] [{appName}] {logMessage}\n"
    bytesToSend = msgToSend.encode()
    
    # Create a TCP socket
    clientSocket = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
    
    try:
        # Connect to server
        print(f"Connecting to {serverAddressPort[0]}:{serverAddressPort[1]}...")
        clientSocket.connect(serverAddressPort)
        
        # Send message to server
        clientSocket.sendall(bytesToSend)
        print(f"Sent: {msgToSend.strip()}")
        
        # Receive response from server
        clientSocket.settimeout(2)
        msgFromServer = clientSocket.recv(bufferSize)
        msg = f"Response from server: {msgFromServer.decode().strip()}"
        print(msg)
        
    except ConnectionRefusedError:
        print(f"Error: Cannot connect to server {serverAddressPort[0]}:{serverAddressPort[1]}")
    except Exception as e:
        print(f"Error: {e}")
    finally:
        # Close the socket
        clientSocket.close()

def main():
    print("Simple Logging Client")
    print("---------------------")
    
    while True:
        print("\nOptions:")
        print("1. Send log message")
        print("2. Exit")
        
        choice = input("\nEnter choice (1-2): ")
        
        if choice == "1":
            send_log()
        elif choice == "2":
            print("Goodbye!")
            break
        else:
            print("Invalid choice, try again.")

if __name__ == "__main__":
    main()