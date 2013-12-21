package characters;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import geometry.Figure;
import geometry.Point;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    private Figure body;
    private List<Figure> legs;
    private int step = 0;
    private double phi = 0;
    private double dphi = 0.05;
    private Paint paint;
    private boolean isRight = true;

    public Hero() {
        body = new Figure();
        legs = new ArrayList<Figure>();

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
    }

    public void addPointToBody(Point p) {
        body.add(p);
    }

    public void addPointToNewLeg(Point p) {
        legs.add(new Figure(p));
    }

    public void addPointToLastLeg(Point p) {
        legs.get(legs.size() - 1).add(p);
    }

    public void addPointToLastLeg(double x, double y) {
        legs.get(legs.size() - 1).add(new Point(x, y, Color.rgb(250, 0, 250)));
    }

    public void addPointToBody(double x, double y) {
        body.add(new Point(x, y, Color.rgb(0, 250, 250)));
    }

    public void addPointToNewLeg(double x, double y) {
        legs.add(new Figure(new Point(x, y, Color.rgb(250, 0, 250))));
    }

    public void move(int step, double phi, Canvas canvas) {
        this.step += step;
        if (phi != 0) {
            if (this.phi >= 0.2 || this.phi <= -0.2)
                dphi = -dphi;
            this.phi += dphi;
        } else {
            this.phi = phi;
        }

        onDraw(canvas);
    }

    private void onDraw(Canvas canvas) {
        geometry.Point currPoint = null;
        for (Point p : getPoints()) {
            if (p != null) {

                if (currPoint != null) {
                    if (p.x < canvas.getWidth()) {
                        paint.setColor(currPoint.c);
                        canvas.drawLine(currPoint.fx(), currPoint.fy(), p.fx(), p.fy(), paint);
                    }
                }
            }
            currPoint = p;
        }
    }


    public List<Point> getPoints() {
        List<Point> res = new ArrayList<Point>();
        boolean isLeft = !isRight;
        for (Figure leg : legs) {
            if (isRight)
                res.addAll(leg.rotatePoints(phi));
            else
                res.addAll(leg.clonePoints());
            res.add(null);

            isRight = !isRight;
        }

        isRight = isLeft;

        res.addAll(body.clonePoints());
        for (Point r : res) {
            if (r != null)
                r.x += step;
        }
        return res;
    }

}