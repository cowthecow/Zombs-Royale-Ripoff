package com.laserinfinite.java;

import java.awt.*;

public class Effect {

    private double x;
    private double y;
    private double currentR;
    private double maxR;

    private double fadeSpeed;
    private Color color;
    private boolean shouldRemove;
    private int isDamaging;
    private int cooldown = 5;

    public Effect(double x, double y, double maxR, double fadeSpeed, int isDamaging, Color color) {
        this.x = x;
        this.y = y;
        this.isDamaging = isDamaging;
        this.currentR = 1;
        this.maxR = maxR;
        this.fadeSpeed = fadeSpeed;
        this.color = color;
        this.shouldRemove = false;
    }

    public Effect(double x, double y, double startingR, double maxR, double fadeSpeed, int isDamaging, Color color) {
        this.x = x;
        this.y = y;
        this.isDamaging = isDamaging;
        this.currentR = startingR;
        this.maxR = maxR;
        this.fadeSpeed = fadeSpeed;
        this.color = color;
        this.shouldRemove = false;
    }

    public Effect(String effectString) {
        String[] split = effectString.split("[|]");

        this.x = Double.parseDouble(split[0]);
        this.y = Double.parseDouble(split[1]);
        this.maxR = Double.parseDouble(split[2]);
        this.fadeSpeed = Double.parseDouble(split[3]);
        this.isDamaging = Integer.parseInt(split[4]);
        this.color = new Color(Integer.parseInt(split[5]), Integer.parseInt(split[6]), Integer.parseInt(split[7]));
    }

    public boolean shouldRemove() {
        return shouldRemove;
    }

    public void update() {
        if (currentR < maxR) {
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 - (255 * (currentR / maxR))));
            currentR += fadeSpeed;
        } else {
            this.shouldRemove = true;
        }

        if (isDamaging == 1 || isDamaging == 2) {
            cooldown--;
            if (cooldown == 0) {
                cooldown = 15;
                double randomColor = Math.random();

                System.out.println("werer");
                Effect sus = new Effect(this.x + (int) (Math.random() * 100) - (int) (Math.random() * 50), this.y + (int) (Math.random() * 100) - (int) (Math.random() * 50), 10, 70, 0.5, 0, (randomColor < 0.75) ? new Color(255, 92, 0) : new Color(64, 64, 64));
                ClientPanel.effects.add(sus);
            }
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(color);

        int integerR = (int) currentR;
        g.fillOval((int) x - integerR, (int) y - integerR, integerR * 2, integerR * 2);
    }

    @Override
    public String toString() {
        return x + "|" + y + "|" + maxR + "|" + fadeSpeed + "|" + isDamaging + "|" + color.getRed() + "|" + color.getGreen() + "|" + color.getBlue();
    }

    public boolean isDamaging() {
        return isDamaging == 1;
    }

    public void nullify() {
        isDamaging = 2;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMaxR() {
        return maxR;
    }
}
