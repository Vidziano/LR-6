package echoServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
public class ReceiverThread extends Thread {
    private DatagramSocket socket;
    private volatile boolean stopped = false;

    public ReceiverThread(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (!stopped) {
            byte[] buffer = new byte[65507];
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(dp);
                String received = new String(dp.getData(), 0, dp.getLength(), "UTF-8");
                System.out.println(received);
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }

    public void halt() {
        stopped = true;
    }
}

