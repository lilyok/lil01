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
        myview.setBackground(getResources().getDrawable(R.drawable.background));
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

    private LinkedList<Hero> hero;
    private Deque<Enemy> enemy;
    Map<Hero, Enemy> rival = new HashMap<Hero, Enemy>();

    public boolean isLegs = false;
    private boolean isStart = false;
    private boolean isWizard = false;
    // private final int STEP = 10;

    public MyView(Context context, int height) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(15);
        paint.setStyle(Paint.Style.STROKE);

        myWizard = BitmapFactory.decodeResource(getResources(), R.drawable.wizard);

        hero = new LinkedList<Hero>();
        enemy = new LinkedList<Enemy>();
        createEnemies(height);
    }

    private void createEnemies(int height) {
        Random rnd = new Random();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.enemy1);
        int count = 3;//rnd.nextInt(height/(3*bmp.getHeight())-1)+1;
        for (int i = 0; i < count; i++) {
            enemy.add(new Enemy(bmp));
            enemy.getLast().setTop(i);
            enemy.getLast().setStep(rnd.nextInt(5) + 5);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isStart) {
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


    private void calculateRivals(Hero h) {
        double dy = 0;
        Enemy resultEnemy = null;
        double heroTop = h.getTop();
        double heroBottom = h.getBottom();

        for (Enemy e : enemy) {
            if (!rival.containsValue(e)) {
                if ((e.getTop() <= heroTop) && (e.getBottom() >= heroTop) &&
                        (e.getTop() <= heroBottom) && (e.getBottom() >= heroBottom)) {
                    rival.put(h, e);
                    break;
                } else if ((e.getTop() >= heroTop) && (e.getBottom() >= heroTop) &&
                        (e.getTop() <= heroBottom) && (e.getBottom() <= heroBottom)) {
                    rival.put(h, e);
                    break;
                } else if ((e.getTop() <= heroTop) && (e.getBottom() >= heroTop)) {
                    double dyTmp = -heroTop + e.getBottom();
                    if (dyTmp > dy) {
                        dy = dyTmp;
                        resultEnemy = e;
                    }
                } else if ((e.getTop() <= heroBottom) && (e.getBottom() >= heroBottom)) {
                    double dyTmp = -e.getTop() + heroBottom;
                    if (dyTmp > dy) {
                        dy = dyTmp;
                        resultEnemy = e;
                    }
                }
            }
        }
        if (resultEnemy != null) {
            rival.put(h, resultEnemy);
        }
    }

    private double isCollisionEnemy(int canvasWidth, Hero h) {
        Enemy e = rival.get(h);
        if (e != null) {
            double dx = h.getFront() + h.getShift() + h.getStep() + e.getStep() - (canvasWidth - e.getShift());
            if (dx >= -1) {
                int lastStep = e.getStep();
                e.setStep(0);
                e.setDied();
                h.setDied();
                return h.getStep() + lastStep - dx;
            }
        }
        return -1;
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        invalidate();

        int canvasWidth = canvas.getWidth();


        for (Iterator<Hero> iterator = hero.iterator(); iterator.hasNext(); ) {
            Hero h = iterator.next();

            if (isStart) {
                double tmpStep = isCollisionEnemy(canvasWidth, h);
                if (tmpStep < 0)
                    h.move(true, canvas);
                else {
                    if (h.getStep() > 0)
                        h.setStep((int) tmpStep);
                    h.move(true, canvas);

                    h.setStep(0);

                    Enemy e = rival.get(h);
                    if (e.getStep() == 0 && e.getShift() == 0 && h.getAlpha() == 0) {
//                        Map<Hero, Enemy> temp = new HashMap<Hero, Enemy>();
//                        temp.putAll(rival);
                        int size = rival.size();
                        rival.remove(h);
//                        if (size - rival.size() > 1) {
//                            Log.d("", "");
//                        }
                        iterator.remove();
                    }
                }
            } else {
                h.move(false, canvas);
            }
        }

        for (Enemy e : enemy)
            if (isStart) {
                e.move(true, canvas);
            } else
                e.move(false, canvas);


        drawWindow(canvas, canvasWidth);
    }

    private void drawWindow(Canvas canvas, int canvasWidth) {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(15);
        canvas.drawRect(canvas.getClipBounds(), paint);

        int startX = canvasWidth / 2 - 15;
        int startX1 = canvasWidth / 2 + 3;

        int height = canvas.getHeight();

        canvas.drawLine(startX, 0, startX, height,paint);
        canvas.drawLine(startX1, 0, startX1, height,paint);

        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(5);
        canvas.drawRect(startX - 7, height/3, startX + 7, height/3+40, paint);
        canvas.drawRect(startX1 - 7, height/3, startX1 + 7, height/3+40, paint);
    }


    public void start() {
        isStart = !isStart;
        if (isStart && rival.size() == 0) {
//            for (Hero h : hero){
//                if (h.getStep() != 0)
//                    calculateRivals(h);
//            }

            Log.i("TagWiz", rival.toString());
        }

    }

    private void animateHero(){
        int heroSize = hero.size();
        if (heroSize > 0 && hero.getLast().getStep() == 0) {
            Hero h = hero.getLast();
            h.setStep(10);
            h.fill(Color.rgb((heroSize%3+1)*89, (heroSize%2+1)*78, heroSize*95));

            calculateRivals(h);

            invalidate();
        }
    }

    public void wizard() {
        isWizard = false;
        animateHero();
    }
}