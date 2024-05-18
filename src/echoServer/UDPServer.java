package echoServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public abstract class UDPServer implements Runnable {
    private final int bufferSize;
    private final int port;
    private volatile boolean isShutDown = false;

    public UDPServer(int port, int bufferSize) {
        this.bufferSize = bufferSize;
        this.port = port;
    }
    public UDPServer(int port) {
        this(port, 8192);
    }

 public UDPServer() {
        this(12345, 8192);
    }

    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            socket.setSoTimeout(10000);

            while (!isShutDown) {
                byte[] buffer = new byte[bufferSize];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(incoming);
                    respond(socket, incoming);
                } catch (SocketTimeoutException e) {
                    if (isShutDown) break;
                } catch (IOException e) {
                    System.err.println("Server Exception: " + e.getMessage());
                    if (isShutDown) break;
                }
            }
        } catch (SocketException e) {
            System.err.println("Could not bind to port: " + port + "\n" + e);
        }
    }



    public void shutDown() {
        this.isShutDown = true;
    }

    public abstract void respond(DatagramSocket socket, DatagramPacket request) throws IOException;


    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            ReceiverThread receiver = new ReceiverThread(socket);
            receiver.start();

            Thread.sleep(10000);
            receiver.halt();
        } catch (SocketException | InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }
}


