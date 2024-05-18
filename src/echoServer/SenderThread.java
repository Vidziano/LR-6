package echoServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SenderThread extends Thread {
    private DatagramSocket socket;
    private InetAddress server;
    private int port;

    public SenderThread(DatagramSocket socket, InetAddress address, int port) {
        this.socket = socket;
        this.server = address;
        this.port = port;
    }

    @Override
    public void run() {
        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String theLine = userInput.readLine();
                if (theLine.equals(".")) {
                    break;
                }
                byte[] data = theLine.getBytes("UTF-8");
                DatagramPacket output = new DatagramPacket(data, data.length, server, port);
                socket.send(output);
            }
        } catch (IOException ex) {
            System.err.println("SenderThread IOException: " + ex.getMessage());
        } finally {
            if (!socket.isClosed()) {
                socket.close();
            }
        }
    }
}
