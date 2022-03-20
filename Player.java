package com.laserinfinite.java;

import java.awt.*;

public class Player {


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

    public double health;
    public double shield;
    private boolean shadePlayer;

    public Player(String username, double x, double y, String identity, boolean shade) {
        this.username = username;
        this.x = x;
        this.y = y;
        this.health = 2;
        this.shield = 0;
        this.shadePlayer = shade;

        //No two players can have same ID
        this.identity = identity;
        Player.unreservedID++;
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

    public void setLeftFist(Point leftFist) {
        this.leftFist = leftFist;
    }

    public void setRightFist(Point rightFist) {
        this.rightFist = rightFist;
    }

    public Point getLocation() {
        return new Point((int) this.x, (int) this.y);
    }

    public void shiftLocation(int cx, int cy) {
        this.x += cx;
        this.y += cy;
    }

    public void update() {
        if(shadePlayer) return;

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
        leftFistX = this.x + (Math.cos(directionFacing - Math.PI / 5) * 30);
        leftFistY = this.y + (Math.sin(directionFacing - Math.PI / 5) * 30);

        leftFistX += Math.cos(directionFacing) * leftFistExtension;
        leftFistY += Math.sin(directionFacing) * leftFistExtension;

        leftFist = new Point((int) leftFistX, (int) leftFistY);

        double rightFistX, rightFistY;
        rightFistX = this.x + (Math.cos(directionFacing + Math.PI / 5) * 30);
        rightFistY = this.y + (Math.sin(directionFacing + Math.PI / 5) * 30);

        rightFistX += Math.cos(directionFacing) * rightFistExtension;
        rightFistY += Math.sin(directionFacing) * rightFistExtension;

        rightFist = new Point((int) rightFistX, (int) rightFistY);


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
        Point leftShoulder = new Point((int) (this.x + (Math.cos(directionFacing - Math.PI / 5) * 30)), (int) (this.y + (Math.sin(directionFacing - Math.PI / 5) * 30)));
        Point rightShoulder = new Point((int) (this.x + (Math.cos(directionFacing + Math.PI / 5) * 30)), (int) (this.y + (Math.sin(directionFacing + Math.PI / 5) * 30)));

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


        g.setColor(new Color(0, 0, 0));
        g.fillRoundRect(50, 20, 200, 20, 15, 15);
        g.fillRoundRect(50, 50, 200, 20, 15, 15);


        if(!shadePlayer) {
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
        g.drawString(username, (int)(x-30),(int)(y-40));
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
                + shield + "\n";
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
                + shield + "\n";
    }
}
