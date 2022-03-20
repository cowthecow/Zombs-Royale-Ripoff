package com.laserinfinite.java;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private static int unusedIdentity = 100000;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();

            clients.add(this);

            broadcastMessage("setidentity|" + unusedIdentity);
            playNoise("discordPing");
            unusedIdentity++;
        } catch (IOException e) {
            //broadcastMessage("SERVER:" + username + " got kicked out of the chat!");
            closeAll(socket, bufferedReader, bufferedWriter);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i = 0; i < 100; i++) {
                        Thread.sleep(1000);
                        unusedIdentity++;
                        broadcastMessage("setidentity|" + unusedIdentity);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("bitch, this is over");
            }
        }).start();
    }

    static void playNoise(String soundFile) {
        try {
            File f = new File("C:/Users/hongy/SoundFilesAU/" + soundFile + ".wav");
            AudioInputStream audioIn = null;
            audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
            Clip clip = null;
            clip = AudioSystem.getClip();
            clip.open(audioIn);

            if (soundFile.equals("towerDef")) clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();

                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void broadcastMessage(String message) {
        try {
            for (ClientHandler clientHandler : clients) {
                if (clientHandler.equals(this)) continue;
                try {
                    if (message != null) {
                        clientHandler.bufferedWriter.write(message);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                }
            }
        }catch (ConcurrentModificationException ignore) {

        }
    }

    public void removeClientHandler() {
        clients.remove(this);
        //broadcastMessage("SERVER:" + username + " has left the chat!");
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

//Commands:
//    /kick user (kicks someone out of group chat)
//    /mute user (mutes someone by disabling their textfield)
//    /
