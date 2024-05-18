package bulletinBoardService;

import java.net.*;
import java.io.*;

public class MulticastSenderReceiver {
    private String name;
    private InetAddress addr;
    private int port = 3456;
    private MulticastSocket group;

    public MulticastSenderReceiver(String name) {
        this.name = name;
        try {
            addr = InetAddress.getByName("224.0.0.1");
            group = new MulticastSocket(port);
            group.joinGroup(addr);
            System.out.println("Joined the group as: " + name);
            System.out.println("\n" );
            new Receiver().start();
            new Sender().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Sender extends Thread {
        public void run() {
            try {
                BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String userInput = fromUser.readLine();
                    if (userInput != null && !userInput.trim().isEmpty()) {
                        String msg = name + ": " + userInput;
                        byte[] out = msg.getBytes();
                        DatagramPacket pkt = new DatagramPacket(out, out.length, addr, port);
                        group.send(pkt);
                       // System.out.println( msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Receiver extends Thread {
        public void run() {
            try {
                byte[] in = new byte[256];
                DatagramPacket pkt = new DatagramPacket(in, in.length);
                while (true) {
                    group.receive(pkt);
                    String received = new String(pkt.getData(), 0, pkt.getLength());
                    System.out.println( received);
                    System.out.println("\n" );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java bulletinBoardService.MulticastSenderReceiver <name>");
            return;
        }
        new MulticastSenderReceiver(args[0]);
    }
}
