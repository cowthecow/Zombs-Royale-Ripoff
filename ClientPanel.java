package com.laserinfinite.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ClientPanel extends JPanel implements Runnable, KeyListener, MouseListener {

    //FIELDS
    public static int WIDTH = 1200;
    public static int HEIGHT = 800;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    private final int FPS = 155;
    private double averageFPS;

    public static ArrayList<Player> players = new ArrayList<>();

    public static ArrayList<Effect> effects = new ArrayList<>();
    public static ArrayList<Effect> effectsInQueue = new ArrayList<>();

    public static ArrayList<Bullet> bullets = new ArrayList<>();
    public static ArrayList<Bullet> bulletsInQueue = new ArrayList<>();

    public static boolean up = false, down = false, left = false, right = false;
    public static boolean punching = false;

    //CONSTRUCTOR
    public ClientPanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    //FUNCTIONS
    @Override
    public void addNotify() {
        super.addNotify();
        addMouseListener(this);
        addKeyListener(this);
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        running = true;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        long startTime;
        long URDTimeMillis;
        long waitTime;
        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = 155;

        long targetTime = 1000 / FPS;

        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //GAME LOOP
        while (running) {
            startTime = System.nanoTime();

            gameUpdate();
            gameRender();
            gameDraw();

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;

            waitTime = targetTime - URDTimeMillis;

            try {
                Thread.sleep(waitTime);
            } catch (Exception ignore) {
            }

            totalTime += System.nanoTime() - startTime;

            frameCount++;
            if (frameCount == maxFrameCount) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
            }

        }
    }

    public static void eraseAllPlayers() {
        while (players.size() >= 2) {
            players.remove(1);
        }
    }

    private void gameUpdate() {
        if (players.size() >= 2) {
            Client.lastMessageCD--;
            if (Client.lastMessageCD < 0) {
                Client.lastMessageCD = 20;
                eraseAllPlayers();
            }
        }
        try {

            if(players.size() > 0) CollisionDetection.playerExplosionDetection(players.get(0),effects);
            if(players.size() > 0) CollisionDetection.playerBulletCollision(players,bullets);

            ArrayList<Player> deletedPlayers = new ArrayList<>();

            int index = 0;
            for (Player player : players) {
                player.update();
                if (index != 0 && player.getStrikes() > 5)
                    deletedPlayers.add(player);
                index++;
            }

            ArrayList<Effect> deletedEffects = new ArrayList<>();

            for (Effect effect : effects) {
                effect.update();
                if (effect.shouldRemove()) deletedEffects.add(effect);
            }

            ArrayList<Bullet> deletedBullets = new ArrayList<>();

            for (Bullet bullet : bullets) {
                bullet.update();
                if(bullet.shouldRemove()) deletedBullets.add(bullet);
            }

            players.removeAll(deletedPlayers);
            effects.removeAll(deletedEffects);
            bullets.removeAll(deletedBullets);

        } catch (ConcurrentModificationException ignore) {

        }
    }

    private void gameRender() {
        try {
            g.setColor(new Color(64, 192, 64));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            if (!Client.usernameApproved) {
                g.setColor(new Color(0, 0, 0));
                g.setFont(new Font("Bahnschrift", Font.PLAIN, 100));
                g.drawString("ZOMBS ROYALE RIPOFF", 75, 150);
                g.setFont(new Font("Bahnschrift", Font.PLAIN, 50));
                g.drawString("ENTER YOUR NICKNAME", 325, 300);

                g.setColor(new Color(255, 255, 255, 128));
                g.fillRoundRect(325, 350, 550, 75, 15, 15);


                g.setColor(new Color(0, 0, 0, 128));
                g.setFont(new Font("Bahnschrift", Font.PLAIN, 45));
                double length = g.getFontMetrics().getStringBounds(Client.username.toString(), g).getWidth();
                g.drawString(Client.username.toString(), WIDTH / 2 - (int) (length / 2), 400);
            }

            int size = 30;

            for (Player player : players) {
                try {
                    player.draw(g);
                } catch (NullPointerException sus) {
                    System.out.println("opponents never intitilaiized");
                }
            }

            for (Effect effect : effects)
                effect.draw(g);

            for (Bullet bullet : bullets)
                bullet.draw(g);

            if (players.size() > 0) players.get(0).draw(g);

        } catch (ConcurrentModificationException ignore) {
            //This exists because the program dynamically inserts objects while repainting, cannot be fixed
        }

    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    public void toggleAll(KeyEvent e, boolean toggle) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            left = toggle;
        } else if (e.getKeyCode() == KeyEvent.VK_W) {
            up = toggle;
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            right = toggle;
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            down = toggle;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (Client.usernameApproved) {
            toggleAll(e, true);
        } else {
            if (e.getKeyChar() == '\n') {
                try {
                    Client.connectSocket();
                    Client.usernameApproved = true;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else if (e.getKeyChar() == '\b') {
                if (Client.username.length() > 0)
                    Client.username.delete(Client.username.length() - 1, Client.username.length());
            } else {
                Client.username.append(e.getKeyChar());
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (Client.usernameApproved) {
            toggleAll(e, true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (Client.usernameApproved) {
            toggleAll(e, false);
        } else {

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        punching = true;

        if (!players.get(0).getWeapon().equals("none")) {
            switch (players.get(0).getWeapon()) {
                case "rpg":
                    double weaponMouthX = players.get(0).getX() + Math.cos(players.get(0).getDirectionFacing() + Math.toRadians(50)) * 25;
                    double weaponMouthY = players.get(0).getY() + Math.sin(players.get(0).getDirectionFacing() + Math.toRadians(50)) * 25;

                    if(players.get(0).useAmmo(4)) {
                        Bullet sus = new Bullet(weaponMouthX, weaponMouthY, players.get(0).getDirectionFacing(), 4, 105, 72, 7, true);

                        bullets.add(sus);
                        bulletsInQueue.add(sus);
                    }

            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        punching = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}