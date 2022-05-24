package com.laserinfinite.java;

import java.awt.*;
import java.util.ArrayList;

public class CollisionDetection {

    public static void playerBulletCollision(ArrayList<Player> players, ArrayList<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            for (Player player : players) {
                if (!player.getID().equals(ClientPanel.players.get(0).getID()))
                    if (Client.getDistanceBetweenPoints(player.getLocation(), new Point((int) bullet.getX(), (int) bullet.getY())) < 40) {
                        switch (bullet.getSize()) {
                            case 4:
                                bullet.explode();
                        }
                    }
            }
        }
    }

    public static void playerExplosionDetection(Player player, ArrayList<Effect> effects) {
        for (Effect explosion : effects) {
            if (explosion.isDamaging()) {
                if (Client.getDistanceBetweenPoints(player.getLocation(), new Point((int) explosion.getX(), (int) explosion.getY())) < explosion.getMaxR() + 30) {
                    player.lowerHealth(95);
                    explosion.nullify();
                }
            }
        }
    }

}
