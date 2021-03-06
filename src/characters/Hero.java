package characters;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import geometry.Figure;
import geometry.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hero implements Serializable{
    private List<Figure> legs;
    private int shift = 0;
    private int step = 0;
    private double phi = 0;
    private double dphi = 0.1;
    private boolean isRight = true;


    private double backend  = 10000;
    private double front = 0;
    private double bottom = 0;
    private double top = 10000;
    private int alpha = 255;
    private boolean isMoving = false;
    private boolean isAnimated = false;

    private int numberOfBroken = 0;
    public int countDeadEnemies = 0;

    public Hero() {
        legs = new ArrayList<Figure>();
    }

    public Hero copy(){
        Hero h = new Hero();
        for (Figure leg : legs) {
            h.legs.add(new Figure(leg.clonePoints()));
        }
        h.bottom = this.bottom;
        h.top = this.top;
        h.front = this.front;
        h.backend = this.backend;
        return h;
    }

    public void addPointToNewLeg(double x, double y) {
        legs.add(new Figure(new Point(x, y, Color.rgb(250, 0, 250), 255)));
        setBounds(x, y);

    }

    public void addPointToLastLeg(double x, double y) {
        Figure last = legs.get(legs.size() - 1);
        last.add(new Point(x, y, Color.rgb(250, 0, 250), 255));
        setBounds(x, y);
    }


    private void setBounds(double x, double y) {
        if (y > bottom) bottom = y;
        if (y < top) top = y;
        if (x > front) front = x;
        if ( x < backend) backend = x;
    }

    public double getFront() {
        return front;
    }

    public double getBackend() {
        return backend;
    }

    public double getBottom() {
        return bottom;
    }

    public double getTop() {
        return top;
    }

    public void move(boolean isMoving, Canvas canvas) {
        this.isMoving = isMoving;
        if (isMoving){
            this.shift += step;
            if (step != 0){
                this.phi += dphi;

                if (this.phi >= 0.3 || this.phi <= 0)
                    if (this.phi >= 0.3) this.phi = 0.3;
                    else this.phi = 0;
                    dphi = -dphi;
            }
        }

        onDraw(canvas);
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);

        geometry.Point currPoint = null;
        for (Point p : getPoints()) {
            if (p != null) {

                if (currPoint != null) {
                    if (p.x < canvas.getWidth()) {
                        paint.setColor(currPoint.c);
                        paint.setAlpha(currPoint.alpha);
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
        int legIndex = 0;
        for (Figure leg : legs) {
            if (isRight && legIndex >= numberOfBroken)
                res.addAll(leg.rotatePoints(phi));
            else
                res.addAll(leg.clonePoints());
            res.add(null);

            isRight = !isRight;

            legIndex++;
        }
        if (isMoving && this.phi <= 0)
            isRight = isLeft;
        else
            isRight = !isLeft;


        for (Point r : res) {
            if (r != null){
                r.x += shift;
            }
        }
        return res;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public int getStep(){
        return step;
    }

    public void setStep(int step){
        this.step = step;
    }

    public void fill(int color){

        for (Figure leg : legs){
            for (Point p : leg.getPoints()){
                if ( p != null)
                    p.c = color;
            }
        }
    }

    public void deletePoint(double x, double y) {

        if (legs.size() > 0){
            int legsSize = legs.size();
            int lastLegSize = legs.get(legsSize-1).getPoints().size();
            if  (lastLegSize > 0){
                boolean res = legs.get(legsSize-1).find(x, y);
                if (res){
                    legs.remove(legsSize-1);
                    checkBounds(x, y);
                }
            }
        }


    }

    private void checkBounds(double x, double y) {
        if (front == x){
            front = 0;

            for (Figure leg : legs)
                for (Point point: leg.getPoints()){
                    if (point != null && point.x > front) front = point.x;
                }
        }
        if (backend == x){
            backend  = 10000;

            for (Figure leg : legs)
                for (Point point: leg.getPoints()){
                    if (point != null && point.x < backend) backend = point.x;
                }
        }
        if (top == y){
            top  = 10000;

            for (Figure leg : legs)
                for (Point point: leg.getPoints()){
                    if (point != null && point.y < top) top = point.y;
            }
        }
        if (bottom == y){
            bottom = 0;

            for (Figure leg : legs)
                for (Point point: leg.getPoints()){
                    if (point != null && point.y > bottom) bottom = point.y;
                }
        }
    }

    public boolean isDied(){
        if (numberOfBroken >= legs.size())
            return true;
        return false;
    }

    public void damage() {
        alpha -= 1;

        if (numberOfBroken < legs.size())  {
            for (Point p : legs.get(numberOfBroken).getPoints()){
                p.alpha = 51;
            }
        }
        if (alpha % 2 == 0)
            numberOfBroken++;

    }

    public void startAnimate() {
        isAnimated = true;
    }

    public boolean isAnimated() {
        return isAnimated;
    }
}