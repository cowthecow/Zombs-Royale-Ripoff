package com.laserinfinite.java;

import java.awt.*;
import java.util.ArrayList;

public class Player {

    //rewrite ideas:
    //abstract class "Entity" and abstract class "Effect"
    //bullets have health but don't give crap about collision detection
    //explosion and other crap extends "Effect"
    //players and bullets and more are all entities

    //bug: since shades don't have angles, the "shoulder" for drawing arms is cursed and sticks out of the back of the player's body

    private final int size = 30;
    private String identity;
    public static int unreservedID = 100000;

    private String username;
    private double x;
    private double y;
    private double directionFacing;

    private Point leftFist = new Point(0, 0);
    private Point rightFist = new Point(0, 0);
    private double leftFistExtension = 0;
    private double rightFistExtension = 0;
    private boolean leftPunching = false;
    private boolean rightPunching = false;
    private boolean leftRetract = false;
    private boolean rightRetract = false;
    private int previousPunch = 0;
    private int cooldown = 0;
    private int bloodCooldown = 0;
    private int amountOfFists = 0;

    public double health;
    public double shield;
    public double expiryDate = 10;
    private boolean shadePlayer;
    private String weapon;
    private int strikes = 0;
    private int rpgStatus = 0;

    //4 Types of ammo
    //Size 1 ammo
    //Size 2 ammo
    //Size 3 ammo
    //Large (Size 4) ammo
    //Special ammo (For admins only)
    //Shotty ammo

    private int size4Ammo = 2;

    public Player(String username, double x, double y, String identity, boolean shade) {
        this.username = username;
        this.x = x;
        this.y = y;
        this.health = 2;
        this.shield = 0;
        this.shadePlayer = shade;
        this.weapon = "rpg";

        //No two players can have same ID
        this.identity = identity;
        Player.unreservedID++;

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    size4Ammo++;
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    rpgStatus++;
                    rpgStatus %= 2


                    ;
                }
            }
        }).start();
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    public String getUsername() {
        return username;
    }

    public String getID() {
        return this.identity;
    }

    public void setIdentity(String newID) {
        this.identity = newID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void strike() {
        this.strikes++;
    }

    public void resetStrikes() {
        this.strikes = 0;
    }

    public int getStrikes() {
        return this.strikes;
    }

    public Point getLeftFist() {
        return this.leftFist;
    }

    public Point getRightFist() {
        return this.rightFist;
    }

    public int getPreviousPunch() {
        return previousPunch;
    }

    public void setPreviousPunch(int previousPunch) {
        this.previousPunch = previousPunch;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void lowerHealth(int amount) {
        this.health -= amount;
    }

    public void setLeftFist(Point leftFist) {
        this.leftFist = leftFist;
    }

    public void setRightFist(Point rightFist) {
        this.rightFist = rightFist;
    }

    public Point getLocation() {
        return new Point((int) this.x, (int) this.y);
    }

    public double getDirectionFacing() {
        return directionFacing;
    }

    public void setDirectionFacing(double directionFacing) {
        this.directionFacing = directionFacing;
    }

    public void shiftLocation(int cx, int cy) {
        this.x += cx;
        this.y += cy;
    }

    public ArrayList<Effect> feelPunches(ArrayList<Point> fistLocations) {
        ArrayList<Effect> effects = new ArrayList<>();
        for (Point fist : fistLocations) {
            if (Client.getDistanceBetweenPoints(fist, new Point((int) this.x, (int) this.y)) <= 40) {
                this.health -= 1;
                this.shield -= 1;

                if (bloodCooldown == 0) {
                    Effect susBlood = new Effect(fist.getX(), fist.getY(), 70, 0.3, 0, new Color(255, 64, 0));
                    ClientPanel.effects.add(susBlood);
                    effects.add(susBlood);
                    bloodCooldown = 10;
                } else {
                    bloodCooldown--;
                }

            }
        }
        amountOfFists = fistLocations.size();
        return effects;
    }

    public void leftRetract() {
        this.leftRetract = true;
        this.leftPunching = false;
    }

    public void rightRetract() {
        this.rightRetract = true;
        this.rightPunching = false;
    }

    public void setAmmo(int type1, int type2, int type3, int type4, int shotty) {
        this.size4Ammo = type4;
    }

    public boolean useAmmo(int type) {
        switch (type) {
            case 4:
                if (size4Ammo > 0) size4Ammo--;
                else return false;
                break;
        }
        return true;
    }

    public void update() {
        if (shadePlayer) return;

        if (health < 100)
            health += 0.2;

        if (shield < 100)
            shield += 0.2;

        if (cooldown > 0) cooldown--;

        if (ClientPanel.up) {
            this.y -= 2;
        }
        if (ClientPanel.down) {
            this.y += 2;
        }
        if (ClientPanel.left) {
            this.x -= 2;
        }
        if (ClientPanel.right) {
            this.x += 2;
        }

        if (ClientPanel.punching) {
            if (previousPunch == 0 && !rightPunching && !rightRetract && !leftPunching && !leftRetract && cooldown == 0) {
                leftPunch();
                setPreviousPunch(1);
                cooldown = 75;
            } else if (previousPunch == 1 && !rightPunching && !rightRetract && !leftPunching && !leftRetract && cooldown == 0) {
                rightPunch();
                setPreviousPunch(0);
                cooldown = 75;
            }
        }

        double mouseX = (MouseInfo.getPointerInfo().getLocation().x - Client.window.getLocation().getX());
        double mouseY = (MouseInfo.getPointerInfo().getLocation().y - Client.window.getLocation().getY() - 30);
        directionFacing = Math.toRadians(Client.getAngleBetweenPoints(new Point((int) (mouseX), (int) mouseY), new Point((int) x, (int) y)));

        double leftFistX, leftFistY;
        double rightFistX, rightFistY;

        if (weapon.equals("none")) {

            leftFistX = this.x + (Math.cos(directionFacing - Math.PI / 5) * 30);
            leftFistY = this.y + (Math.sin(directionFacing - Math.PI / 5) * 30);

            leftFistX += Math.cos(directionFacing) * leftFistExtension;
            leftFistY += Math.sin(directionFacing) * leftFistExtension;

            leftFist = new Point((int) leftFistX, (int) leftFistY);

            rightFistX = this.x + (Math.cos(directionFacing + Math.PI / 5) * 30);
            rightFistY = this.y + (Math.sin(directionFacing + Math.PI / 5) * 30);

            rightFistX += Math.cos(directionFacing) * rightFistExtension;
            rightFistY += Math.sin(directionFacing) * rightFistExtension;

            rightFist = new Point((int) rightFistX, (int) rightFistY);
        } else if (weapon.equals("rpg")) {
            leftFistX = this.x + (Math.cos(directionFacing + 0.21) * 45);
            leftFistY = this.y + (Math.sin(directionFacing + 0.21) * 45);

            leftFist = new Point((int) leftFistX, (int) leftFistY);

            rightFistX = this.x + (Math.cos(directionFacing + Math.PI / 5) * 30);
            rightFistY = this.y + (Math.sin(directionFacing + Math.PI / 5) * 30);

            rightFist = new Point((int) rightFistX, (int) rightFistY);
        }

        if (this.x - this.size < 0) {
            this.x = this.size;
        }

        if (this.x + this.size > ClientPanel.WIDTH) {
            this.x = ClientPanel.WIDTH - this.size;
        }

        if (this.y - this.size < 0) {
            this.y = this.size;
        }

        if (this.y + this.size > ClientPanel.HEIGHT) {
            this.y = ClientPanel.HEIGHT - this.size;
        }

        if (leftPunching) {
            leftFistExtension += 4;
            if (leftFistExtension > 40) {
                leftPunching = false;
                leftRetract = true;
            }
        }
        if (leftRetract) {
            leftFistExtension -= 3;
            if (leftFistExtension <= 0) {
                leftFistExtension = 0;
                leftRetract = false;
            }
        }

        if (rightPunching) {
            rightFistExtension += 4;
            if (rightFistExtension > 40) {
                rightPunching = false;
                rightRetract = true;
            }
        }
        if (rightRetract) {
            rightFistExtension -= 3;
            if (rightFistExtension <= 0) {
                rightFistExtension = 0;
                rightRetract = false;
            }
        }
    }

    public void leftPunch() {
        leftPunching = true;
    }

    public void rightPunch() {
        rightPunching = true;
    }

    public void draw(Graphics2D g) {
        //sus amogus baka
        //Here is a paragraph on the constant e.
        //E is a stupid useless mathematical constant. It is so stupid and useless because it is so overrated and literally no one uses it for mathematical proofs. The end.

        Point leftShoulder = new Point(), rightShoulder = new Point();

        if (this.weapon.equals("none")) {
            leftShoulder = new Point((int) (this.x + (Math.cos(directionFacing - Math.PI / 5) * 30)), (int) (this.y + (Math.sin(directionFacing - Math.PI / 5) * 30)));
            rightShoulder = new Point((int) (this.x + (Math.cos(directionFacing + Math.PI / 5) * 30)), (int) (this.y + (Math.sin(directionFacing + Math.PI / 5) * 30)));
        } else if (this.weapon.equals("rpg")) {
            leftShoulder = new Point((int) (this.x + (Math.cos(directionFacing) * 30)), (int) (this.y + (Math.sin(directionFacing) * 30)));
            rightShoulder = new Point((int) (this.x + (Math.cos(directionFacing + Math.PI / 5) * 30)), (int) (this.y + (Math.sin(directionFacing + Math.PI / 5) * 30)));
        }

        if (!username.equals("Laser Infinite")) {
            g.setStroke(new BasicStroke(3));
            g.setColor(new Color(255, 192, 128));
            g.fillOval(leftFist.x - 10, leftFist.y - 10, 20, 20);
            g.fillOval(rightFist.x - 10, rightFist.y - 10, 20, 20);
            g.setColor(new Color(0, 0, 0));
            g.drawOval(leftFist.x - 10, leftFist.y - 10, 20, 20);
            g.drawOval(rightFist.x - 10, rightFist.y - 10, 20, 20);

            g.setStroke(new BasicStroke(10));
            g.setColor(new Color(255, 192, 128));
            g.drawLine(leftShoulder.x, leftShoulder.y, leftFist.x, leftFist.y);
            g.drawLine(rightShoulder.x, rightShoulder.y, rightFist.x, rightFist.y);

            g.setStroke(new BasicStroke(3));
            g.setColor(new Color(255, 192, 128));
            g.fillOval((int) this.x - size, (int) this.y - size, size * 2, size * 2);
            g.setColor(new Color(0, 0, 0));
            g.drawOval((int) this.x - size, (int) this.y - size, size * 2, size * 2);

        } else {
            g.setStroke(new BasicStroke(5));
            g.setColor(new Color(225, 225, 225, 128));
            g.fillOval(leftFist.x - 10, leftFist.y - 10, 20, 20);
            g.fillOval(rightFist.x - 10, rightFist.y - 10, 20, 20);
            g.setColor(new Color(0, 128, 128, 128));
            g.drawOval(leftFist.x - 10, leftFist.y - 10, 20, 20);
            g.drawOval(rightFist.x - 10, rightFist.y - 10, 20, 20);

            g.setStroke(new BasicStroke(5));
            g.setColor(new Color(225, 225, 225, 128));
            g.fillOval((int) this.x - size, (int) this.y - size, size * 2, size * 2);
            g.setColor(new Color(0, 128, 128, 128));
            g.drawOval((int) this.x - size, (int) this.y - size, size * 2, size * 2);
        }

        if (!this.weapon.equals("none")) {
            switch (this.weapon) {
                case "rpg":
                    Weapon.drawEquippedWeapon(g, this, "rpg", size4Ammo > 0);
                    break;
            }
        }

        g.setColor(new Color(0, 0, 0));
        g.fillRoundRect(50, 20, 200, 20, 15, 15);
        g.fillRoundRect(50, 50, 200, 20, 15, 15);

        if (!shadePlayer) {
            g.setColor(new Color(64, 255, 64, 128));
            g.fillRoundRect(50, 20, (int) health * 2, 20, 15, 15);

            g.setColor(new Color(64, 255, 255, 128));
            g.fillRoundRect(50, 50, (int) shield * 2, 20, 15, 15);

            g.setStroke(new BasicStroke(3));
            g.setColor(new Color(0, 0, 0));
            g.drawRoundRect(50, 50, 200, 20, 15, 15);
            g.drawRoundRect(50, 20, 200, 20, 15, 15);
        }

        g.setStroke(new BasicStroke(1));
        g.setFont(new Font("Bahnschrift", Font.PLAIN, 20));
        g.drawString(username, (int) (x - 30), (int) (y - 40));
    }


    @Override
    public String toString() {
        return "|" + identity + "|"
                + username + "|"
                + x + "|"
                + y + "|"
                + leftFist.x + "|"
                + leftFist.y + "|"
                + rightFist.x + "|"
                + rightFist.y + "|"
                + health + "|"
                + shield + "|"
                + weapon + "|"
                + directionFacing + "|"
                + size4Ammo + "\n";
    }

    public String toString(String customID) {
        return "|" + customID + "|"
                + username + "|"
                + x + "|"
                + y + "|"
                + leftFist.x + "|"
                + leftFist.y + "|"
                + rightFist.x + "|"
                + rightFist.y + "|"
                + health + "|"
                + shield + "|"
                + weapon + "|"
                + directionFacing + "|"
                + size4Ammo + "\n";
    }
}
