import socket

msgFromClient = "Hello From <<Your Registered Name Here>>"

serverAddress = "10.xx.xx.xx"
serverPort = 20001
bufferSize = 1024

# Create a TCP socket
TCPClientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
    # Connect to the server
    TCPClientSocket.connect((serverAddress, serverPort))

    # Send message
    TCPClientSocket.sendall(msgFromClient.encode())

    # Receive response
    msgFromServer = TCPClientSocket.recv(bufferSize)
    print("Message from Server:", msgFromServer.decode())

finally:
    # Close the connection
    TCPClientSocket.close()
