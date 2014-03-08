package geometry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Figure implements Serializable {
    private List<Point> fig;

    public Figure(Point p) {
        fig = new ArrayList<Point>();
        fig.add(p);
    }

    public Figure(List<Point> ps) {
        fig = ps;
    }

    public void add(Point p) {
        fig.add(p);
    }

    public Point get(int i) {
        return fig.get(i);
    }

    public List<Point> getPoints() {
        return fig;
    }

    public List<Point> clonePoints() {
        List<Point> res = new ArrayList<Point>();
        for (Point p : fig) {
            if (p != null)
                res.add(new Point(p.x, p.y, p.c, p.alpha));
            else
                res.add(null);
        }
        return res;
    }

    public List<Point> rotatePoints(double phi) {
        List<Point> res = new ArrayList<Point>();
        for (Point p : fig)
            if (p != null)
                res.add(rotatePoint(p, fig.get(0).x, fig.get(0).y, phi));
            else
                res.add(null);
        return res;
    }


    private Point rotatePoint(Point p, double cx, double cy, double phi) {
        Point pres = new Point(p.x, p.y, p.c, p.alpha);

        double dx = p.x - cx;
        double dy = p.y - cy;

        double dxn = (dx * Math.cos(phi) - dy * Math.sin(phi));
        double dyn = (dx * Math.sin(phi) + dy * Math.cos(phi));

        pres.x = cx + dxn;
        pres.y = cy + dyn;

        return pres;
    }

    public double getMostRemoteFromStartPoint(){
        Point startPoint = fig.get(0);
        double res = 0;
        for (Point p: fig){
            double length = startPoint.geLength(p);
            if ( length > res)
                res = length;
        }
        return res;
    }

    public boolean find(double x, double y) {
        for (Point p : fig){
            if (p.x == x && p.y ==y)
                return true;
        }
        return false;
    }
}