package com.example.lab5.Segmentation;

import javafx.geometry.Point2D;

public class LiangBarski {
    double tIn = 0;
    double tOut = 1;
    Point2D pMin, pMax, p1, p2;
    double[] t = new double[4];
    double[] q = new double[4];
    double[] s = new double[4];

    public LiangBarski(Point2D pMin, Point2D pMax, Point2D p1, Point2D p2) {
        this.pMin = pMin;
        this.pMax = pMax;
        this.p1 = p1;
        this.p2 = p2;
        t[0] = (pMin.getY() - p1.getY()) / (p2.getY() - p1.getY());
        t[1] = (pMin.getX() - p1.getX()) / (p2.getX() - p1.getX());
        t[2] = (pMax.getY() - p1.getY()) / (p2.getY() - p1.getY());
        t[3] = (pMax.getX() - p1.getX()) / (p2.getX() - p1.getX());
        var dx = p2.getX() - p1.getX();
        var dy = p2.getY() - p1.getY();
        s[0] = -dy;
        s[1] = -dx;
        s[2] = dy;
        s[3] = dx;
        q[0] = p1.getY() - pMin.getY();
        q[1] = p1.getX() - pMin.getX();
        q[2] = pMax.getY() - p1.getY();
        q[3] = pMax.getX() - p1.getX();
    }

    public boolean execute() {
        for (int i = 0; i < 4; i++) {
            if (s[i] > 0) {
                tOut = Math.min(q[i] / s[i], tOut);
            }
            if (s[i] < 0) {
                tIn = Math.max(q[i] / s[i], tIn);
            }
            if (s[i] == 0) {
                if (q[i] < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public Point2D getInPoint() {
        return new Point2D(p1.getX() + tIn * (p2.getX() - p1.getX()),
                p1.getY() + tIn * (p2.getY() - p1.getY()));
    }

    public Point2D getOutPoint() {
        return new Point2D(p1.getX() + tOut * (p2.getX() - p1.getX()),
                p1.getY() + tOut * (p2.getY() - p1.getY()));
    }
}
