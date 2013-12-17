package hero;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Hero{
    private Figure body;
    private List<Figure> legs;
    int step = 0;
    double phi =0;
    double dphi = 0.05;

    private boolean isRight = true;

    public Hero(){
        body = new Figure();
        legs = new ArrayList<Figure>();
    }

    public void addPointToBody(Point p){
        body.add(p);
    }

    public void addPointToNewLeg(Point p){
        legs.add(new Figure(p));
    }

    public void addPointToLastLeg(Point p){
        legs.get(legs.size()-1).add(p);
    }

    public void addPointToLastLeg(double x, double y){
        legs.get(legs.size()-1).add(new Point(x,y, Color.rgb(250, 0, 250)));
    }

    public void addPointToBody(double x, double y){
        body.add(new Point(x,y,Color.rgb(0,250,250)));
    }

    public void addPointToNewLeg(double x, double y){
        legs.add(new Figure(new Point(x,y,Color.rgb(250,0,250))));
    }

    public void move(int step, double phi){
        this.step += step;
        if (phi != 0){
            if (this.phi >= 0.2 || this.phi <= -0.2)
                dphi = -dphi;
            this.phi += dphi;
        } else {
            this.phi = phi;
        }

    }

    public List<Point> getPoints(){
        List<Point> res = new ArrayList<Point>();
      //  bo i = 1;
        boolean isLeft = !isRight;
        for (Figure leg: legs){
            if (isRight)
                res.addAll(leg.rotatePoints(phi));
            else
                res.addAll(leg.rotatePoints(0));
            res.add(null);

            isRight = !isRight;
     //       i=-i;
        }

        isRight = isLeft;

        res.addAll(body.clonePoints());
        for (Point r : res){
            if (r != null)
                r.x += step;
        }
        return res;
    }

}