package echoServer;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoClient {
    public static final int PORT = 7;

    public static void main(String[] args) {
        String hostname = "localhost";
        DatagramSocket socket = null;
        try {
            InetAddress ia = InetAddress.getByName(hostname);
            socket = new DatagramSocket();
            Thread sender = new SenderThread(socket, ia, PORT);
            sender.start();
            Thread receiver = new ReceiverThread(socket);
            receiver.start();

            sender.join();
            receiver.interrupt();
        } catch (Exception ex) {
            System.err.println("UDPEchoClient Exception: " + ex.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}

