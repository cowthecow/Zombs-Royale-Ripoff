package com.laserinfinite.java;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static JFrame window = new JFrame("Zombs Royale Ripoff");

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    //This is only useful for keeping the player's ID
    private static Player player;

    private static int identity;
    private static boolean identitySet = false;
    public static StringBuilder username = new StringBuilder("");
    public static boolean usernameApproved = false;

    //Sussy amognsu algorithm
    //Each client sends the location of their player
    //When you recieve something copy the location to the player with that ID

    //Add a file which saves the latest ID you can use, static unreservedID variable is useless
    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            player = new Player(username.toString(), 300, 300, String.valueOf(Player.unreservedID), false);
            ClientPanel.players.add(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new ClientPanel());
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void connectSocket() throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost().getHostAddress(), 1234);
        Client clone = new Client(socket);

        clone.listenForMessages();
        clone.sendMessages();
    }

    public synchronized void sendMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        bufferedWriter.write(ClientPanel.players.get(0).toString(String.valueOf(identity)));
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        Thread.sleep(5);
                    }
                } catch (IOException | InterruptedException e) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public synchronized void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();

                        System.out.println(msgFromGroupChat);
                        if (msgFromGroupChat.contains("setidentity|")) {
                            if (!identitySet) {
                                String[] splitMessage = msgFromGroupChat.split("[|]");
                                identity = Integer.parseInt(splitMessage[1]);
                                ClientPanel.players.get(0).setIdentity(String.valueOf(identity));
                            }
                            identitySet = true;
                        } else {
                            String[] information = msgFromGroupChat.split("[|]");

                            if (msgFromGroupChat.length() > 0) {
                                boolean updated = false;
                                for (Player opponent : ClientPanel.players) {
                                    if (opponent.getID().equals(information[1])) {
                                        updated = true;
                                        opponent.setX(Double.parseDouble(information[3]));
                                        opponent.setY(Double.parseDouble(information[4]));
                                        opponent.setLeftFist(new Point((int) Double.parseDouble(information[5]), (int) Double.parseDouble(information[6])));
                                        opponent.setRightFist(new Point((int) Double.parseDouble(information[7]), (int) Double.parseDouble(information[8])));
                                        break;
                                    }
                                }

                                if (!updated && !information[1].equals("100000") && !information[1].equals("0")) {
                                    ClientPanel.players.add(new Player(information[2], Double.parseDouble(information[3]), Double.parseDouble(information[4]), information[1], true));
                                }
                            }

                        }
//                        for (Player opponent : ClientPanel.players) {
//                            if (opponent.getID().equals(coordinates[0])) {
//                                opponent.setX(Double.parseDouble(coordinates[1]));
//                                opponent.setY(Double.parseDouble(coordinates[2]));
//
//                                opponent.setLeftFist(new Point((int) Double.parseDouble(coordinates[3]), (int) Double.parseDouble(coordinates[4])));
//                                opponent.setRightFist(new Point((int) Double.parseDouble(coordinates[5]), (int) Double.parseDouble(coordinates[6])));
//                            }
//                        }

                    } catch (IOException e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public static double getAngleBetweenPoints(Point point1, Point point2) {
        double angle = Math.toDegrees(Math.atan2(point1.getY() - point2.getY(), point1.getX() - point2.getX()));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
