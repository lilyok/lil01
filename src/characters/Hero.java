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
    private int shift = 0;
    private int step = 0;
    private double phi = 0;
    private double dphi = 0.05;
    private Paint paint;
    private boolean isRight = true;

    private double front = 0;
    private double bottom = 0;
    private double top = 10000;
    private int alpha = 255;

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
        if (p != null)
            setBounds(p.x, p.y);
    }

    public void addPointToBody(double x, double y) {
        body.add(new Point(x, y, Color.rgb(0, 250, 250)));
        setBounds(x, y);
    }

    public void addPointToNewLeg(Point p) {
        legs.add(new Figure(p));
        if (p != null)
            setBounds(0, p.y);
    }

    public void addPointToNewLeg(double x, double y) {
        legs.add(new Figure(new Point(x, y, Color.rgb(250, 0, 250))));
        setBounds(0, y);

    }

    public void addPointToLastLeg(Point p) {
        Figure last = legs.get(legs.size() - 1);
        last.add(p);
        if (p != null)
            setBounds(0, last.get(0).y + last.getMostRemoteFromStartPoint());
    }

    public void addPointToLastLeg(double x, double y) {
        Figure last = legs.get(legs.size() - 1);
        last.add(new Point(x, y, Color.rgb(250, 0, 250)));
        setBounds(0, last.get(0).y + last.getMostRemoteFromStartPoint());
    }


    private void setBounds(double x, double y) {
        if (y > bottom) bottom = y;
        if (y < top) top = y;
        if (x > front) front = x;
    }

    public List<Double> getBounds() {
        List <Double> res = new ArrayList<Double>(){{
            add(front);
            add(top);
            add(bottom);
        }};
        return res;
    }

    public double getFront() {
        return front;
    }

    public double getBottom() {
        return bottom;
    }

    public double getTop() {
        return top;
    }

    public void move(boolean isMoving, Canvas canvas) {
        if (isMoving){
            this.shift += step;
            if (phi != 0) {
                if (this.phi >= 0.2 || this.phi <= -0.2)
                    dphi = -dphi;
                this.phi += dphi;
            } else if (step != 0){
                this.phi = 0.05;
            }
        }else{
            this.phi = 0;
        }

        onDraw(canvas);
    }

    private void onDraw(Canvas canvas) {
//        paint.setAlpha(alpha);
        geometry.Point currPoint = null;
        for (Point p : getPoints()) {
            if (p != null) {

                if (currPoint != null) {
                    if (p.x < canvas.getWidth()) {
                        paint.setColor(currPoint.c);
                        paint.setAlpha(alpha);
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
                r.x += shift;
        }
        return res;
    }

    public int getShift() {
        return shift;
    }

    public int getStep(){
        return step;
    }

    public void setStep(int step){
        this.step = step;
    }

    public void setDied() {
        if (alpha >0)
            alpha -= 51;
    }

    public int getAlpha(){
        return alpha;
    }

    public void fill(int color){
        for (Point p : body.getPoints()) {
            if (p != null) {
                p.c = color;
            }
        }

        for (Figure leg : legs){
            for (Point p : leg.getPoints()){
                if ( p != null)
                    p.c = color;
            }
        }
    }
}