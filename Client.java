package com.laserinfinite.java;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Queue;

public class Client {
    public static JFrame window = new JFrame("Zombs Royale Ripoff");

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    //This is only useful for keeping the player's ID
    public static Player player;
    private static Queue<String> lastIdentitiesFound = new ArrayDeque<>();

    private static int identity;
    private static boolean identitySet = false;
    public static StringBuilder username = new StringBuilder("");
    public static boolean usernameApproved = false;

    private int strikeCooldown = 1;
    private int strikeStatus = 0;
    public static int lastMessageCD = 200;

    //Sussy amognsu algorithm
    //Each client sends the location of their player
    //When you recieve something copy the location to the player with that ID

    //Add a file which saves the latest ID you can use, static unreservedID variable is useless


    //algorithm
    //have an arraylist that is the same length as the player
    //if a client responds, their number goes down
    //the entire arraylist gets their numbers raised every update
    //every player with a number above 40 gets deleted and their number gets deleted too
    //players start with 0 as a number

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
        Socket socket = new Socket("127.0.0.1", 1234);
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
                        Thread.sleep(1);
                        try {
                            if (ClientPanel.bulletsInQueue.size() != 0) addObjects("bullets");
                            if (ClientPanel.effectsInQueue.size() != 0) addObjects("effects");
                        } catch (ConcurrentModificationException ignore) {

                        }
                    }
                } catch (IOException | InterruptedException e) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public synchronized void addObjects(String objectType) {
        try {
            if (socket.isConnected()) {
                switch (objectType) {
                    case "bullets":
                        for (Bullet bullet : ClientPanel.bulletsInQueue) {
                            bufferedWriter.write("addbullet|" + bullet.toString());
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                        ClientPanel.bulletsInQueue.clear();
                        break;

                    case "effects":
                        for (Effect effect : ClientPanel.effectsInQueue) {
                            bufferedWriter.write("addeffect|" + effect.toString());
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                        ClientPanel.effectsInQueue.clear();
                        break;
                }
            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void notifyClient(ArrayList<Effect> broadcastEffects) {
        try {
            for (Effect effect : broadcastEffects) {
                bufferedWriter.write("addeffect|" + effect.toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void increaseStrikeCooldown() {
        strikeCooldown++;
    }

    public synchronized void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        lastMessageCD = 20;

                        if (msgFromGroupChat == null) continue;

                        if (msgFromGroupChat.contains("setidentity|")) {
                            if (!identitySet) {
                                String[] splitMessage = msgFromGroupChat.split("[|]");
                                identity = Integer.parseInt(splitMessage[1]);
                                ClientPanel.players.get(0).setIdentity(String.valueOf(identity));
                                player.setIdentity(String.valueOf(identity));
                            }
                            identitySet = true;
                            ClientHandler.setClientInfo(identity);
                        } else if (msgFromGroupChat.contains("addbullet|")) {
                            String cutString = msgFromGroupChat.substring(10);
                            ClientPanel.bullets.add(new Bullet(cutString));
                        } else if (msgFromGroupChat.contains("addeffect|")) {
                            String cutString = msgFromGroupChat.substring(10);
                            ClientPanel.effects.add(new Effect(cutString));
                        } else {
                            String[] information = msgFromGroupChat.split("[|]");

                            if (msgFromGroupChat.length() > 0) {
                                boolean updated = false;

                                ArrayList<Point> fistLocations = new ArrayList<>();

                                int index = 0;
                                for (Player opponent : ClientPanel.players) {
                                    if (opponent.getID().equals(information[1])) {
                                        updated = true;
                                        opponent.resetStrikes();
                                        strikeStatus++;

                                        opponent.setX(Double.parseDouble(information[3]));
                                        opponent.setY(Double.parseDouble(information[4]));
                                        opponent.setLeftFist(new Point((int) Double.parseDouble(information[5]), (int) Double.parseDouble(information[6])));
                                        opponent.setRightFist(new Point((int) Double.parseDouble(information[7]), (int) Double.parseDouble(information[8])));
                                        opponent.setDirectionFacing((Double.parseDouble(information[12])));
                                        opponent.setAmmo(0, 0, 0, Integer.parseInt(information[13]), 0);

                                        if (!opponent.getID().equals(player.getID())) {
                                            if (getDistanceBetweenPoints(opponent.getLeftFist(), opponent.getLocation()) > 35) {
                                                if (opponent.getWeapon().equals("none"))
                                                    fistLocations.add(opponent.getLeftFist());
                                            }

                                            if (getDistanceBetweenPoints(opponent.getRightFist(), opponent.getLocation()) > 35) {
                                                if (opponent.getWeapon().equals("none"))
                                                    fistLocations.add(opponent.getRightFist());
                                            }
                                            opponent.setWeapon(information[11]);
                                        }
                                        break;
                                    }
                                    index++;
                                }


                                if (strikeStatus > strikeCooldown) {
                                    strikeStatus = 0;
                                    for (Player player : ClientPanel.players) {
                                        if (ClientPanel.players.indexOf(player) != 0)
                                            player.strike();
                                    }
                                }

                                ArrayList<Effect> broadcastEffects = ClientPanel.players.get(0).feelPunches(fistLocations);
                                fistLocations.clear();

                                if (!updated && !information[1].equals("100000") && !information[1].equals("0")) {
                                    System.out.println("player was added");
                                    ClientPanel.players.add(new Player(information[2], Double.parseDouble(information[3]), Double.parseDouble(information[4]), information[1], true));
                                    strikeCooldown++;
                                }

                                notifyClient(broadcastEffects);
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

    public static double getDistanceBetweenPoints(Point point1, Point point2) {
        int xDist = Math.abs(point1.x - point2.x);
        int yDist = Math.abs(point1.y - point2.y);
        return Math.sqrt(xDist * xDist + yDist * yDist);
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
