package com.example.lab5.Segmentation;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PolygonSegmentation {
    Point2D pMin, pMax;
    Point2D[] polygon;

    public PolygonSegmentation(Point2D pMin, Point2D pMax, Point2D[] polygon) {
        this.pMin = pMin;
        this.pMax = pMax;
        this.polygon = polygon;
    }

    public Point2D[] segment() {
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < polygon.length - 1; i++) {
            points.addAll(Arrays.stream(normalizeSegment(polygon[i], polygon[i + 1])).collect(Collectors.toList()));
        }
        return points.toArray(Point2D[]::new);
    }

    private Point2D[] normalizeSegment(Point2D begin, Point2D end) {
        if (!isLineVisible(begin, end)) {
            return new Point2D[]{};
        }
        var nearestBegin = getNearestVisible(begin, begin, end);
        var nearestEnd = getNearestVisible(end, begin, end);
        return new Point2D[]{nearestBegin, nearestEnd};
    }

    private Point2D getNearestVisible(Point2D p, Point2D begin, Point2D end) {
        var code = characteristicCode(p);
        var inside = Arrays.stream(code).allMatch(i -> i == 0);
        if (inside) {
            return p;
        }
        List<Point2D> intersections;
        for (int i = 0; i < 4; i++) {
            if (code[i] == 0) {
                continue;
            }
            var intersection = getIntersection(begin, end, i);
            if (intersection.getX() >= pMin.getX() && intersection.getX() <= pMax.getX() &&
                    intersection.getY() >= pMin.getY() && intersection.getY() <= pMax.getY()) {
                return intersection;
            }
        }
        return null;
    }

    private Point2D getIntersection(Point2D begin, Point2D end, int i) {
        var k = (end.getY() - begin.getY()) / (end.getX() - begin.getX());
        var b = begin.getY() - k * begin.getX();

        return switch (i) {
            case 0 -> new Point2D((pMax.getY() - b) / k, pMax.getY());
            case 1 -> new Point2D((pMin.getY() - b) / k, pMin.getY());
            case 2 -> new Point2D(pMax.getX(), k * pMax.getX() + b);
            case 3 -> new Point2D(pMin.getX(), k * pMin.getX() + b);
            default -> null;
        };
    }

    private boolean isLineVisible(Point2D begin, Point2D end) {
        var c1 = characteristicCode(begin);
        var c2 = characteristicCode(end);
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (c1[i] & c2[i]);
        }
        return result == 0;
    }

    private int[] characteristicCode(Point2D p) {
        return new int[]{
                p.getY() > pMax.getY() ? 1 : 0,
                p.getY() < pMin.getY() ? 1 : 0,
                p.getX() > pMax.getX() ? 1 : 0,
                p.getX() < pMin.getX() ? 1 : 0
        };
    }
}
