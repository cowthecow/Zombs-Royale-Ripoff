package com.laserinfinite.java;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ClientHandler implements Runnable {


    //New algorithm
    //Afk detector for identities, because identities are now much more stable with bugs fixed
    //A map is used which maps identities to player index

    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private static int unusedIdentity = 100000;
    public static int lastUsedIdentity = 100000;
    public static int usedTries = 0;

    public static int identity = 100000;

    public ClientHandler(Socket socket, int newIdentity) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            clients.add(this);

            System.out.println(" A client handler is starting..");

            broadcastMessage("setidentity|" + String.valueOf(100000+newIdentity));
            lastUsedIdentity = 100000+newIdentity;

            playNoise("discordPing");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
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

    public static void setClientInfo(int identity) {
        lastUsedIdentity = identity;
    }

    public String getUsername() {
        return this.username;
    }

    public void broadcastMessage(String message) {
        try {
            for (ClientHandler clientHandler : clients) {
                if (clientHandler.equals(this)) {
                    if(message != null)
                        if(!message.contains("setidentity|"))continue;
                }
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
