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
    private double dphi = 0.1;
    private Paint paint;
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
        body = new Figure();
        legs = new ArrayList<Figure>();

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
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

    public void addPointToBody(Point p) {
        body.add(p);
        if (p != null)
            setBounds(p.x, p.y);
    }

    public void addPointToBody(double x, double y) {
        body.add(new Point(x, y, Color.rgb(0, 250, 250), 255));
        setBounds(x, y);
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

        res.addAll(body.clonePoints());


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

//    public int getAlpha(){
//        return alpha;
//    }

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

    public void deletePoint(double x, double y) {
        int bodySize = body.getPoints().size();

        if (bodySize > 0){
            int i = 1;
            Point lastBodyPoint = body.get(bodySize-i);
            while (i < bodySize && lastBodyPoint == null){
                i++;
                lastBodyPoint = body.get(bodySize-i);
            }

            if (lastBodyPoint != null&&lastBodyPoint.x == x && lastBodyPoint.y == y){
                body.getPoints().remove(bodySize-i);
                checkBounds(x, y);
            }
        }

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
            for (Point point:body.getPoints())
                if (point != null && point.x > front) front = point.x;

            for (Figure leg : legs)
                for (Point point: leg.getPoints()){
                    if (point != null && point.x > front) front = point.x;
                }
        }
        if (backend == x){
            backend  = 10000;
            for (Point point:body.getPoints())
                if (point != null && point.x < backend) backend = point.x;

            for (Figure leg : legs)
                for (Point point: leg.getPoints()){
                    if (point != null && point.x < backend) backend = point.x;
                }
        }
        if (top == y){
            top  = 10000;
            for (Point point:body.getPoints())
                if (point != null && point.y < top) top = point.y;

            for (Figure leg : legs)
                for (Point point: leg.getPoints()){
                    if (point != null && point.y < top) top = point.y;
            }
        }
        if (bottom == y){
            bottom = 0;
            for (Point point:body.getPoints())
                if (point != null && point.y > bottom) bottom = point.y;

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