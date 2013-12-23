package geometry;

import java.util.ArrayList;
import java.util.List;

public class Figure {
    private ArrayList<Point> fig;

    public Figure() {
        fig = new ArrayList<Point>();
    }

    public Figure(Point p) {
        fig = new ArrayList<Point>();
        fig.add(p);
    }

    public Figure(ArrayList<Point> ps) {
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
                res.add(new Point(p.x, p.y, p.c));
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
        Point pres = new Point(p.x, p.y, p.c);

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
}