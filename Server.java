package com.laserinfinite.java;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server extends JPanel {

    private ServerSocket serverSocket;

    private static JFrame frame = new JFrame("Server GUI");

    private double startAngle = 0;
    private double arcAngle = 0;
    private boolean angleIncreasing = true;
    public static int playerCount = 0;

    public Server(ServerSocket serverSocket) throws UnknownHostException {
        this.serverSocket = serverSocket;

        frame.getContentPane().add(this);
        frame.setBackground(new Color(64, 64, 64));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }

    public void startServer() {
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                try {
                    while (!serverSocket.isClosed()) {
                        Socket socket = serverSocket.accept();
                        System.out.println("A new player has joined! ");
                        playerCount++;
                        ClientHandler clientHandler = new ClientHandler(socket, playerCount);
                        new Thread(clientHandler).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();

        while(true) {
            frame.repaint();
        }
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if(angleIncreasing) {
            arcAngle += 0.25;
            if(arcAngle == 360) {
                angleIncreasing = false;
            }
        }else {
            startAngle += 0.25;
            if(startAngle == 360) {
                startAngle = 0;
                arcAngle = 0;
                angleIncreasing = true;
            }
        }
        g2d.setStroke(new BasicStroke(14));
        g2d.setColor(new Color(192,255,192));
        g2d.drawArc(100,100,300,300,0,(int)arcAngle);

        g2d.setStroke(new BasicStroke(18));
        g2d.setColor(new Color(64,64,64));
        g2d.drawArc(100,100,300,300,0,(int)startAngle);

        g2d.setColor(new Color(192,192,192));
        g2d.setFont(new Font("Bahnschrift", Font.PLAIN, 40));
        g2d.drawString("SERVER CONNECTED", 250-(int)(g2d.getFontMetrics().getStringBounds("SERVER CONNECTED",g2d).getWidth()/2), 250);
    }
}
