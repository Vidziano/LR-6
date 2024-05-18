package bulletinBoardService;

import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.io.IOException;
import javax.swing.JOptionPane;

public class MessanderImpl implements Messanger {
    private UITasks ui;
    private MulticastSocket group;
    private InetAddress addr;
    private int port;
    private String name;
    private boolean canceled = false;

    public MessanderImpl(InetAddress addr, int port, String name, UITasks ui) {
        this.ui = ui;
        this.addr = addr;
        this.port = port;
        this.name = name;
        try {
            group = new MulticastSocket(port);
            group.setTimeToLive(2);
            group.joinGroup(addr);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Не вдалося створити мультикаст-сокет: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        Thread t = new Receiver();
        t.start();
    }

    @Override
    public void stop() {
        cancel();
        try {
            group.leaveGroup(addr);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Помилка при від'єднанні: " + e.getMessage());
        } finally {
            if (group != null) {
                group.close();
            }
        }
    }

    @Override
    public void send() {
        new Sender().start();
    }

    private class Sender extends Thread {
        public void run() {
            try {
                String msg = name + ": " + ui.getMessage();
                byte[] out = msg.getBytes();
                DatagramPacket pkt = new DatagramPacket(out, out.length, addr, port);
                group.send(pkt);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Помилка при відправленні повідомлення: " + e.getMessage());
            }
        }
    }

    private class Receiver extends Thread {
        public void run() {
            byte[] in = new byte[512];
            DatagramPacket pkt = new DatagramPacket(in, in.length);
            while (!isCanceled()) {
                try {
                    group.receive(pkt);
                    ui.setText(new String(pkt.getData(), 0, pkt.getLength()));
                } catch (IOException e) {
                    if (!isCanceled()) {
                        JOptionPane.showMessageDialog(null, "Помилка при отриманні повідомлення: " + e.getMessage());
                        break;
                    }
                }
            }
        }
    }

    private synchronized boolean isCanceled() {
        return canceled;
    }

    private synchronized void cancel() {
        canceled = true;
    }
}
