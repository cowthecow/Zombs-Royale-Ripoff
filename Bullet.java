package com.laserinfinite.java;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Bullet {


    private int coolDown = 5;
    private double x;
    private double y;
    private double directionFacing;
    private int size;
    private int damage;
    private int constantSpeed;

    private boolean shouldRemove = false;
    private boolean isExplosive;
    private int bulletRange;

    public Bullet(double x, double y, double directionFacing, int size, int range, int damage, int constantSpeed, boolean isExplosive) {
        this.x = x;
        this.y = y;
        this.directionFacing = directionFacing;
        this.size = size;
        this.bulletRange = range;
        this.damage = damage;
        this.constantSpeed = constantSpeed;
        this.isExplosive = isExplosive;
    }

    public Bullet(String bulletString) {
        String[] components = bulletString.split("[|]");
        this.x = Double.parseDouble(components[0]);
        this.y = Double.parseDouble(components[1]);
        this.directionFacing = Double.parseDouble(components[2]);
        this.size = Integer.parseInt(components[3]);
        this.bulletRange = Integer.parseInt(components[4]);
        this.damage = Integer.parseInt(components[5]);
        this.constantSpeed = Integer.parseInt(components[6]);
    }

    public boolean shouldRemove() {
        return shouldRemove;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public void explode() {
        if (isExplosive) {
            shouldRemove = true;
            bulletRange = 1000;
            Effect sus = new Effect(this.x, this.y, 10, 100, 1, 1, new Color(255, 92, 0));
            ClientPanel.effects.add(sus);
            ClientPanel.effectsInQueue.add(sus);
        }
    }

    public void update() {
        this.x += Math.cos(directionFacing) * constantSpeed;
        this.y += Math.sin(directionFacing) * constantSpeed;

        if (isExplosive) {
            coolDown--;
            if (coolDown == 0) {
                coolDown = 5;
                Effect sus = new Effect(this.x, this.y, 10, 30, 0.5, 0, new Color(64, 64, 64));
                ClientPanel.effects.add(sus);
                ClientPanel.effectsInQueue.add(sus);
            }
        }
        bulletRange--;
        if (bulletRange < 0) {
            shouldRemove = true;
            Effect sus = new Effect(this.x, this.y, 10, 100, 1, 1, new Color(255, 92, 0));
            ClientPanel.effects.add(sus);
            ClientPanel.effectsInQueue.add(sus);
        }
    }

    public void draw(Graphics2D g) {
        //small size: assault rifle bullets
        //small-medium size: pistol bullets
        //medium size: sniper bullets
        //big size: rpg grenades

        int x = (int) this.x, y = (int) this.y;
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(directionFacing + Math.PI / 2, this.x, this.y);

        if (this.size == 1) {
            Point radialCenter = new Point((int) (this.x + Math.cos(directionFacing) * 10), (int) (this.y + Math.sin(directionFacing) * 10));

            Color[] colors;

            colors = new Color[]{Color.WHITE, new Color(255, 200, 120), new Color(167, 135, 20)};

            Point2D center = new Point2D.Float((int) radialCenter.x, (int) radialCenter.y);
            float radius = 15;
            Point2D focus = new Point2D.Float((int) radialCenter.x, (int) radialCenter.y);
            float[] dist = {0.0f, 0.25f, 1.0f};

            g.setPaint(new RadialGradientPaint(center, radius, focus,
                    dist, colors,
                    MultipleGradientPaint.CycleMethod.NO_CYCLE.NO_CYCLE));

            Polygon bullet = new Polygon(new int[]{x - 8, x - 6, x + 6, x + 8, x + 8, x - 8}, new int[]{y - 6, y - 14, y - 14, y - 6, y + 8, y + 8}, 6);
            g.fill(affineTransform.createTransformedShape(bullet));
        } else if (this.size == 2) {

        } else if (this.size == 3) {

        } else if (this.size == 4) {

            Point radialCenter = new Point((int) (this.x + Math.cos(directionFacing) * 10), (int) (this.y + Math.sin(directionFacing) * 10));

            Color[] colors;

            colors = new Color[]{new Color(205, 127, 50), new Color(205, 127, 50), new Color(102, 64, 25)};

            Point2D center = new Point2D.Float((int) radialCenter.x, (int) radialCenter.y);
            float radius = 15;
            Point2D focus = new Point2D.Float((int) radialCenter.x, (int) radialCenter.y);
            float[] dist = {0.0f, 0.25f, 1.0f};

            g.setPaint(new RadialGradientPaint(center, radius, focus,
                    dist, colors,
                    MultipleGradientPaint.CycleMethod.NO_CYCLE.NO_CYCLE));

            Polygon bullet = new Polygon(new int[]{x - 8, x - 6, x + 6, x + 8, x + 8, x - 8}, new int[]{y - 6, y - 14, y - 14, y - 6, y + 8, y + 8}, 6);
            g.fill(affineTransform.createTransformedShape(bullet));
        }


    }

    @Override
    public String toString() {
        return x + "|" + y + "|" + directionFacing + "|" + size + "|" + bulletRange + "|" + damage + "|" + constantSpeed;
    }
}

