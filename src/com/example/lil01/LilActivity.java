package com.example.lil01;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import hero.*;


import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


public class LilActivity extends Activity {
    MyView myview;
    RadioButton brbtn;
    RadioButton lrbtn;
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

        brbtn = (RadioButton)findViewById(R.id.rBody);
        lrbtn = (RadioButton)findViewById(R.id.rLegs);
    }

    public void startBtnClick(View view){
        // выводим сообщение
//        Toast.makeText(this, "Зачем вы нажали?", Toast.LENGTH_SHORT).show();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        myview.start();
    }


    public void wizardBtnClick(View view){
       // Toast.makeText(this, Integer.toString(view.getLeft()), Toast.LENGTH_SHORT).show();

        myview.wizard();

    }

    public void legsRbtnClick(View view){

        if (lrbtn.isChecked())
            brbtn.setChecked(false);

        myview.isLegs = true;

    }

    public void bodyRbtnClick(View view){
        if (brbtn.isChecked())
            lrbtn.setChecked(false);

        myview.isLegs = false;
    }
}

class MyView extends View {
    Bitmap myWizard;
    Paint paint;

    Deque<Hero> hero;
    boolean isLegs = false;
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

        hero = new LinkedList<Hero>();
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("MyTag", "ACTION_DOWN");

                if (!isWizard)
                    hero.add(new Hero());
                isWizard = true;
                if (isLegs){
                    hero.getLast().addPointToNewLeg(event.getX(), event.getY());
                }else{
                    hero.getLast().addPointToBody(event.getX(), event.getY());
                }

                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("MyTag", "ACTION_MOVE");

                if (!isWizard)
                    hero.add(new Hero());
                isWizard = true;
                if (isLegs){
                    hero.getLast().addPointToLastLeg(event.getX(), event.getY());
                }else{
                    hero.getLast().addPointToBody(event.getX(), event.getY());
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.i("MyTag", "ACTION_UP");
                if (isLegs){
                    hero.getLast().addPointToLastLeg(null);
                }else{
                    hero.getLast().addPointToBody(null);
                }

                break;
        }

        return true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        hero.Point currPoint = null;
        invalidate();

        if (isStart){
            for(Hero h: hero)
                h.move(2, 0.05);
        }

        for(Hero h: hero)
            for (hero.Point p:h.getPoints()){
                if (p != null){

                    if (currPoint != null){
                        if (p.x<canvas.getWidth()){
                            paint.setColor(currPoint.c);
                            canvas.drawLine(currPoint.fx(), currPoint.fy(), p.fx(), p.fy(), paint);
                        }else{
                            isStart=false;
                        }
                    }
                }
                currPoint = p;
            }

    }




    public void start() {
        isStart = !isStart;
        if (isStart) isWizard =false;
    }

    public void wizard() {
        isWizard = false;
    }
}