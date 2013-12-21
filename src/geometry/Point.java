package geometry;

import android.graphics.Color;

public class Point {
    public int c;
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.c = Color.WHITE;
    }

    public Point(double x, double y, int c) {
        this.x = x;
        this.y = y;
        this.c = c;
    }

    public float fx() {
        return (float) x;
    }

    public float fy() {
        return (float) y;
    }
}
