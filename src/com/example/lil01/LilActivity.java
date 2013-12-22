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
import characters.*;


import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;


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

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        myview = new MyView(this, height);

        setContentView(R.layout.main);
        TableLayout gl = (TableLayout) findViewById(R.id.gl);
        myview.setBackgroundColor(Color.BLACK);
        myview.requestFocus();
        gl.addView(myview);

        brbtn = (RadioButton) findViewById(R.id.rBody);
        lrbtn = (RadioButton) findViewById(R.id.rLegs);
    }

    public void startBtnClick(View view) {
        // выводим сообщение
//        Toast.makeText(this, "Зачем вы нажали?", Toast.LENGTH_SHORT).show();

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        myview.start();
    }


    public void wizardBtnClick(View view) {
        // Toast.makeText(this, Integer.toString(view.getLeft()), Toast.LENGTH_SHORT).show();

        myview.wizard();

    }

    public void legsRbtnClick(View view) {

        if (lrbtn.isChecked())
            brbtn.setChecked(false);

        myview.isLegs = true;

    }

    public void bodyRbtnClick(View view) {
        if (brbtn.isChecked())
            lrbtn.setChecked(false);

        myview.isLegs = false;
    }
}

class MyView extends View {
    Bitmap myWizard;
    Paint paint;

    private Deque<Hero> hero;
    private Deque<Enemy> enemy;
    public boolean isLegs = false;
    private boolean isStart = false;
    private boolean isWizard = false;
    private final int STEP = 10;

    public MyView(Context context, int height) {
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
        enemy = new LinkedList<Enemy>();
        createEnemies(height);
    }

    private void createEnemies(int height) {
        Random rnd = new Random();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.enemy1);
        int count = rnd.nextInt(height/(3*bmp.getHeight()));
        for (int i = 0; i < count; i++) {
            enemy.add(new Enemy(bmp));
            enemy.getLast().setTop(i);
            enemy.getLast().setStep(rnd.nextInt(5) + 5);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("MyTag", "ACTION_DOWN");

                if (!isWizard)
                    hero.add(new Hero());
                isWizard = true;
                if (isLegs) {
                    hero.getLast().addPointToNewLeg(event.getX(), event.getY());
                } else {
                    hero.getLast().addPointToBody(event.getX(), event.getY());
                }

                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("MyTag", "ACTION_MOVE");

                if (!isWizard)
                    hero.add(new Hero());
                isWizard = true;
                if (isLegs) {
                    hero.getLast().addPointToLastLeg(event.getX(), event.getY());
                } else {
                    hero.getLast().addPointToBody(event.getX(), event.getY());
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.i("MyTag", "ACTION_UP");
                if (isLegs) {
                    hero.getLast().addPointToLastLeg(null);
                } else {
                    hero.getLast().addPointToBody(null);
                }

                break;
        }

        return true;
    }


    private void getCollisionEnemy (int canvasWidth, ArrayList<Double> heroPosition){
        double dy = 0;
        int i = 0;
        int ic = 0;
        double heroStep = 0;
        for (Enemy e:enemy) {
            double dx = heroPosition.get(0) + STEP + e.getStep() - (canvasWidth - e.getShift());
            if (dx >= 0) {
                if ((e.getTop() <= heroPosition.get(1))&&(e.getBottom() >= heroPosition.get(1))&&
                    (e.getTop() <= heroPosition.get(2))&&(e.getBottom() >= heroPosition.get(2))){
                    //вернуть сколько шагов (STEP + e.getStep() - dx) герою, setStep(0) для врага
                } else if ((e.getTop() <= heroPosition.get(1))&&(e.getBottom() >= heroPosition.get(1))){
                    double dyTmp = heroPosition.get(1) - e.getBottom();
                    if (dyTmp > dy){
                        dy = dyTmp;
                        ic = i;
                        heroStep = dx;
                    }
                } else if ((e.getTop() <= heroPosition.get(2))&&(e.getBottom() >= heroPosition.get(2))){
                    double dyTmp = e.getTop() - heroPosition.get(2);
                    if (dyTmp > dy){
                        dy = dyTmp;
                        ic = i;
                        heroStep = dx;
                    }
                }
            }
            i++;
        }
        //вернуть сколько (STEP + e.getStep() - dx) шагов герою, setStep(0) для врага
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        invalidate();

        int canvasWidth = canvas.getWidth();
//        double d = 0;


        for (Hero h : hero)
            if (isStart) {
                //  d = h.getBounds().x +h.getStep()+ 2*STEP - canvasWidth+enemy.getStep();
                //  if (d >= 0)
                //      h.move((int)(STEP-d/2), 0.05, canvas);
                //  else
                h.move(STEP, 0.05, canvas);
            } else
                h.move(0, 0, canvas);

        for (Enemy e : enemy)
            if (isStart)
//            if (d >= 0)
//                enemy.move((int)(STEP-d/2), canvas);
//            else
                e.move(true, canvas);
            else
                e.move(false, canvas);
    }


    public void start() {
        isStart = !isStart;
    }

    public void wizard() {
        isWizard = false;
    }
}