package com.example.lil01;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;


import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class LilActivity extends Activity {
    MyView myview;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myview = new MyView(this);

        setContentView(R.layout.main);
        TableLayout gl = (TableLayout)findViewById(R.id.gl);
        myview.setBackgroundColor(Color.BLACK);
        myview.requestFocus();
        gl.addView(myview);
    }

    public void startBtnClick(View view){
        // выводим сообщение
        Toast.makeText(this, "Зачем вы нажали?", Toast.LENGTH_SHORT).show();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        myview.start();
    }


    public void wizardBtnClick(View view){
       // Toast.makeText(this, Integer.toString(view.getLeft()), Toast.LENGTH_SHORT).show();

        myview.wizard(view.getLeft(), view.getTop());

    }
}

class Point{
    int c;
    int id;
    float x;
    float y;

    public Point(float x, float y){
        this.x=x;
        this.y=y;
        this.c = Color.WHITE;
        this.id = -1;
    }

    public Point(float x, float y, int c, int id){
        this.x=x;
        this.y=y;
        this.c = c;
        this.id = id;
    }

}

class MyView extends View {
    Bitmap myWizard;

    Paint paint;

    ArrayList<Point> points = new ArrayList<Point>();
    int legCount = 0;
    ArrayList<Float> xc;
    ArrayList<Float> yc;
    int phi = 1;

    float xWiz;
    float yWiz;
    boolean isStart = false;
    boolean isWizard = false;

    public MyView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
        myWizard = BitmapFactory.decodeResource(getResources(), R.drawable.wizard);

        xc = new ArrayList<Float>();
        yc = new ArrayList<Float>();

    }

    public void fillSelectPoint(int color){

        for (Point p: points){
            if (p != null && Math.abs(p.x - xWiz)< 5 && Math.abs(p.y - yWiz)< 5){
                p.c = color;
                p.id = legCount;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("MyTag", "ACTION_DOWN");
                xWiz = event.getX();
                yWiz = event.getY();
                if (!isWizard)
                    points.add(new Point(xWiz, yWiz));
                else
                    fillSelectPoint(Color.YELLOW);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("MyTag", "ACTION_MOVE");
                xWiz = event.getX();
                yWiz = event.getY();
                if (!isWizard)
                    points.add(new Point(xWiz, yWiz));
                else
                    fillSelectPoint(Color.YELLOW);
                break;
            case MotionEvent.ACTION_UP:
                Log.i("MyTag", "ACTION_UP");
                if (isWizard) {
                    xWiz = event.getX();
                    yWiz = event.getY();
                }
                else
                    points.add(null);

                break;
        }

        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point currPoint = null;
        Point pres = null;
        int step;
        invalidate();

        if (isStart){
            step = 2;
        }else{
            step = 0;
        }

        for (Float c:xc){
            c += step;
        }

        for(Point p : points){
            if (p != null){
                p.x = p.x+step;

                pres = rotatePoints(p);
                if (currPoint != null){
                    if (p.x<canvas.getWidth()){
                         paint.setColor(currPoint.c);
                         canvas.drawLine(currPoint.x, currPoint.y, pres.x, pres.y, paint);
                    }else{
                        isStart=false;
                    }
                }
            }
            currPoint = pres;
        }

//        //Rotate
//        if (isStart)
//            for(Point p : points){
//                if ((p != null)&&(p.id > -1)&&(xc.size() > 0)){
//
//                    float d = (float) Math.sqrt(Math.pow(p.x - xc.get(p.id), 2) + Math.pow(p.y - yc.get(p.id), 2));
//                    p.x = (float) (p.x+d*Math.sin(0.3));
//                    p.y = (float) (p.y-d*Math.cos(0.3));
//                }
//            }


        if (isWizard)
            canvas.drawBitmap(myWizard, xWiz, yWiz, null);
    }

    public Point rotatePoints(Point p){
        Point pres = new Point(p.x, p.y, p.c, p.id);
        if (isStart&&(p.id > -1)&&(xc.size() > 0)){
            float dx = p.x - xc.get(p.id);
            float dy = p.y - yc.get(p.id);
            float dxn = (float) (dx * Math.cos(phi) - dy * Math.sin(phi));
            float dyn = (float) (dx * Math.sin(phi) + dy * Math.cos(phi));

//            float d = (float) Math.sqrt(Math.pow(p.x - xc.get(p.id), 2) + Math.pow(p.y - yc.get(p.id), 2));
            pres.x = xc.get(p.id)+dxn;
            pres.y = yc.get(p.id)+dyn;
        }

        return pres;
    }


    public void start() {
        isStart = !isStart;
        if (isStart) isWizard =false;
    }

    public void wizard(int left, int top) {
        if (!isStart){
            isWizard = !isWizard;
            if (isWizard){
                xWiz = left;
                yWiz = top;
            }else{
                xc.add(xWiz);
                yc.add(yWiz);
                fillSelectPoint(Color.RED);
                legCount += 1;
            }
        }
    }
}