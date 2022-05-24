package com.laserinfinite.java;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Weapon {

    //Types of guns to add:
    //AK47 (Assault)
    //RPG
    //Sniper (random sniper thingy you can find online)
    //Pistol

    //If weapon is equipped it is invisible
    //Weapon only updates when it is dropped, where it turns visible and changes location

    private String type;
    private double x;
    private double y;
    private boolean equipped;

    public Weapon(String type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.equipped = false;
    }

    public void pickUp() {
        this.equipped = true;
    }

    public void drop() {
        this.equipped = false;
    }

    public void draw(Graphics2D g) {
        if(!equipped) {
            switch (type) {
                // case "rpg": ....
            }
        }
    }

    public static void drawEquippedWeapon(Graphics2D g, Player player, String weaponType, boolean loaded) {
        int x = (int) player.getX()+18, y = (int) player.getY();

        //RPG: Width 10-20 Height 50-60
        if(weaponType.equals("rpg")) {
            AffineTransform affineTransform = new AffineTransform();
            affineTransform.rotate(player.getDirectionFacing()+Math.PI/2, player.getX(), player.getY());
            Polygon rpg = new Polygon(new int[]{x-20,x+20,x+10,x+10,x+14,x-14,x-10,x-10}, new int[]{y-60,y-60,y-50,y+35,y+50,y+50,y+35,y-50}, 8);
            Polygon stripe = new Polygon(new int[]{x-7,x+7,x+7,x-7}, new int[]{y-40,y-40,y-35,y-35},4);
            Polygon stripe1 = new Polygon(new int[]{x-7,x+7,x+7,x-7}, new int[]{y-30,y-30,y-25,y-25},4);
            Polygon stripe2 = new Polygon(new int[]{x-7,x+7,x+7,x-7}, new int[]{y-20,y-20,y-15,y-15},4);
            Polygon bullet = new Polygon(new int[]{x-8,x+8,x+8,x-8}, new int[]{y-68,y-68,y-60,y-60},4);

            if(loaded) {
                g.setColor(new Color(128, 64, 0));
                g.fill(affineTransform.createTransformedShape(bullet));
            }

            g.setColor(new Color(0,128,0));
            g.fill(affineTransform.createTransformedShape(rpg));
            g.fill(affineTransform.createTransformedShape(stripe));
            g.fill(affineTransform.createTransformedShape(stripe1));
            g.fill(affineTransform.createTransformedShape(stripe2));
            g.setColor(new Color(0,0,0));
            if(loaded) g.draw(affineTransform.createTransformedShape(bullet));
            g.draw(affineTransform.createTransformedShape(rpg));
            g.fill(affineTransform.createTransformedShape(stripe));
            g.fill(affineTransform.createTransformedShape(stripe1));
            g.fill(affineTransform.createTransformedShape(stripe2));
        }
    }
}
