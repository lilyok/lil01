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


import java.util.*;


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
    Map<Hero, Enemy> rival = new HashMap<Hero, Enemy>();

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
        int count = rnd.nextInt(height/(3*bmp.getHeight())-1)+1;
        for (int i = 0; i < count; i++) {
            enemy.add(new Enemy(bmp));
            enemy.getLast().setTop(i);
            enemy.getLast().setStep(rnd.nextInt(5) + 5);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isStart){
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
        }
        return true;
    }


    private void calculateRivals (Hero h){
        double dy = 0;
        Enemy resultEnemy = null;
        double heroTop = h.getTop();
        double heroBottom = h.getBottom();

        for (Enemy e:enemy) {
            if ((e.getTop() <= heroTop)&&(e.getBottom() >= heroTop)&&
                    (e.getTop() <= heroBottom)&&(e.getBottom() >= heroBottom)){
                //вернуть сколько шагов (STEP + e.getStep() - dx) герою, setStep(0) для врага
                rival.put(h, e);
            } else if ((e.getTop() >= heroTop)&&(e.getBottom() >= heroTop)&&
                    (e.getTop() <= heroBottom)&&(e.getBottom() <= heroBottom)){
                rival.put(h, e);
            } else if ((e.getTop() <= heroTop)&&(e.getBottom() >= heroTop)){
                double dyTmp = -heroTop + e.getBottom();
                if (dyTmp > dy){
                    dy = dyTmp;
                    resultEnemy = e;
                }
            } else if ((e.getTop() <= heroBottom)&&(e.getBottom() >= heroBottom)){
                double dyTmp = -e.getTop() + heroBottom;
                if (dyTmp > dy){
                    dy = dyTmp;
                    resultEnemy = e;
                }
            }
        }
        //вернуть сколько (STEP + e.getStep() - dx) шагов герою, setStep(0) для врага
        if (resultEnemy != null){
            rival.put(h, resultEnemy);
        }
    }

    private boolean isCollisionEnemy (int canvasWidth, Hero h){
        Enemy e = rival.get(h);
        if (e != null){
            double dx = h.getFront() + h.getStep() + STEP + e.getStep() - (canvasWidth - e.getShift());
            if (dx >= 0){
                e.setStep(0);
                return true;
            }
        }
        return false;
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        invalidate();

        int canvasWidth = canvas.getWidth();


        for (Hero h : hero)
            if (isStart) {
//                double heroStep = getCollisionEnemy(canvasWidth, h.getStep(), h.getBounds());
//                h.move((int)heroStep, 0.05, canvas);
                if(!isCollisionEnemy(canvasWidth, h))
                    h.move(STEP, 0.05, canvas);
                else
                    h.move(0, 0.05, canvas);
            } else
                h.move(0, 0, canvas);

        for (Enemy e : enemy)
            if (isStart)
                e.move(true, canvas);
            else
                e.move(false, canvas);
    }


    public void start() {
        isStart = !isStart;
        if (isStart){
            for (Hero h : hero)
                calculateRivals(h);

            Log.i("TagWiz", rival.toString());
        }

    }

    public void wizard() {
        isWizard = false;
    }
}