package geometry;

import android.graphics.Color;

import java.io.Serializable;

public class Point implements Serializable {
    public int c;
    public int alpha = 255;
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        this.c = Color.WHITE;
    }

    public Point(double x, double y, int c, int a) {
        this.x = x;
        this.y = y;
        this.c = c;
        this.alpha = a;
    }

    public float fx() {
        return (float) x;
    }

    public float fy() {
        return (float) y;
    }

    public double geLength(Point nextPoint){
      return Math.sqrt((nextPoint.x - this.x)*(nextPoint.x - this.x)+(nextPoint.y - this.y)*(nextPoint.y - this.y));
    }
}
