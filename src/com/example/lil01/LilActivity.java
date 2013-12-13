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
        myview.wizard();

    }
}

class Point{
    float x;
    float y;
    public Point(float x, float y){
        this.x=x;
        this.y=y;
    }
}

class MyView extends View {
    Bitmap myWizard;

    Paint paint;
    ArrayList<Point> points = new ArrayList<Point>();
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
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("MyTag", "ACTION_MOVE");
                xWiz = event.getX();
                yWiz = event.getY();
                if (!isWizard)
                    points.add(new Point(xWiz, yWiz));
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
        int step = 0;
        invalidate();


        if (isStart){
            step = 2;
        }else{
            step = 0;
        }

        for(Point p : points){
            if (p != null){
                p.x = p.x+step;
                if (currPoint != null){
                    if (p.x<canvas.getWidth()){
                         canvas.drawLine(currPoint.x, currPoint.y, p.x, p.y, paint);
                    }else{
                        isStart=false;
                    }
                }
            }
            currPoint = p;
        }
        if (isWizard)
            canvas.drawBitmap(myWizard, xWiz, yWiz, null);
    }


    public void start() {
        isStart = !isStart;
        if (isStart) isWizard =false;
    }

    public void wizard() {
        if (!isStart){
            isWizard = !isWizard;
        }
    }
}