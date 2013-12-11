package com.example.lil01;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


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
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll);

        myview.setBackgroundColor(Color.BLACK);
        myview.requestFocus();
        ll.addView(myview);
    }

    public void startBtnClick(View view) throws InterruptedException {
        // выводим сообщение
        Toast.makeText(this, "Зачем вы нажали?", Toast.LENGTH_SHORT).show();
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        myview.Start();
    //    myview.invalidate();

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

    Paint paint;
    ArrayList<Point> points = new ArrayList<Point>();
    boolean isStart = false;

    public MyView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5);
    }


    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("MyTag", "ACTION_DOWN");
                points.add(new Point(event.getX(), event.getY()));
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("MyTag", "ACTION_MOVE");
                points.add(new Point(event.getX(), event.getY()));
                break;
            case MotionEvent.ACTION_UP:
                Log.i("MyTag", "ACTION_UP");
                points.add(null);
                break;
        }

  //      invalidate();
        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point currPoint = null;
        int step = 0;
        invalidate();

        //invalidate();
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
//            invalidate();
       // } while (dm != null &&isIn);

    }


    public void Attack(){
        for(Point p : points){
            if (p != null){
                p.x = p.x+2;
            }
        }
    }

    public void Start() {
        isStart = !isStart;
    }
}