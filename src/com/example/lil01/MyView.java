package com.example.lil01;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import characters.Enemy;
import characters.Hero;

import java.util.*;

class MyView extends View {
    private Paint paint;
    private Button startBtn;
    private TextView scoreTextView;
    private Integer score = 0;
    private Integer lastScore = 0;
    private int canvasWidth = 0;
    private LinkedList<Hero> hero;
    private Deque<Enemy> enemy;

    public boolean isLegs = false;
    private boolean isStart = false;
    private boolean isWizard = false;

    private boolean isInfo = false;
    private long prevTime;

    private GestureDetector gestureDetector;
    // private final int STEP = 10;

    public MyView(Context context, int height, TextView scoreTextView, Button startBtn) {
        super(context);

        gestureDetector = new GestureDetector(context, new GestureListener());

        setFocusable(true);
        setFocusableInTouchMode(true);

        this.scoreTextView = scoreTextView;
        this.startBtn = startBtn;

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(15);
        paint.setStyle(Paint.Style.STROKE);

        hero = new LinkedList<Hero>();
        enemy = new LinkedList<Enemy>();
        createEnemies(height);
    }

    private void createEnemies(double height) {
        Random rnd = new Random();
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.enemy1);
        int count = (int)Math.round(height/(double)bmp.getHeight());//3;//rnd.nextInt(height/(3*bmp.getHeight())-1)+1;
        for (int i = 0; i < count-1; i++) {
            enemy.add(new Enemy(bmp));
            enemy.getLast().setTop(i);
            enemy.getLast().setStep(rnd.nextInt(5) + 5);
        }

    }



    public boolean onTouchEvent(MotionEvent event) {
        // if (!isStart) {
        boolean result;
        result = gestureDetector.onTouchEvent(event);//return the double tap events
        if(result)
            return false;

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("MyTag", "ACTION_DOWN");

                if (!isWizard)
                    hero.add(new Hero());
                isWizard = true;
                if (isLegs) {
                    ifNoHeroNewHero();
                    hero.getLast().addPointToNewLeg(event.getX(), event.getY());
                } else {
                    ifNoHeroNewHero();
                    hero.getLast().addPointToBody(event.getX(), event.getY());
                }

                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("MyTag", "ACTION_MOVE");

                if (!isWizard)
                    hero.add(new Hero());
                isWizard = true;
                if (isLegs) {
                    ifNoHeroNewHero();
                    hero.getLast().addPointToLastLeg(event.getX(), event.getY());
                } else {
                    ifNoHeroNewHero();
                    hero.getLast().addPointToBody(event.getX(), event.getY());
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.i("MyTag", "ACTION_UP");
                if (!isLegs) {
                    ifNoHeroNewHero();
                    hero.getLast().addPointToBody(null);
                }

                break;
        }
        //    }
        return true;
    }

    private void ifNoHeroNewHero() {
        if (hero.size() == 0)
            hero.add(new Hero());
    }



    private double isCollision(int canvasWidth, Hero h, Enemy e) {
        double dx = -2*canvasWidth;
        final double heroTop = h.getTop();
        final double heroBottom = h.getBottom();
        final int enemyTop = e.getTop();
        boolean isCanCollision = false;
        final int enemyBottom = e.getBottom();
        if ((enemyTop <= heroTop) && (enemyBottom >= heroBottom)) {
            isCanCollision = true;
        } else if ((enemyTop >= heroTop) && (enemyBottom <= heroBottom)) {
            isCanCollision = true;
        } else if ((enemyTop <= heroTop) && (enemyBottom >= heroTop)) {
            double dyTmp = -heroTop + enemyBottom;
            if (dyTmp > (heroBottom - heroTop)/3) isCanCollision = true;

        } else if ((enemyTop <= heroBottom) && (enemyBottom >= heroBottom)) {
            double dyTmp = -enemyTop + heroBottom;
            if (dyTmp > (heroBottom - heroTop)/3) isCanCollision = true;
        }
        if (isCanCollision && h.getBackend() < canvasWidth - e.getShift() + e.getWidth()){
            double oldDx = h.getFront() + h.getShift() - (canvasWidth - e.getShift());
           // if (oldDx < 0)
                dx = oldDx + h.getStep() + e.getStep();
        }
        return dx;
    }


    protected void onDraw(Canvas canvas) {
        long now = System.currentTimeMillis();
        long elapsedTime = now - prevTime;
        int pauseTime = 100;
        if (elapsedTime > pauseTime)
            prevTime =  now;

        super.onDraw(canvas);
        invalidate();


        canvasWidth = canvas.getWidth();


        if (isStart && elapsedTime > pauseTime) {
            double dx = -2*canvasWidth;

            for (Enemy e: enemy){
                for (Hero h: hero){
                    dx = isCollision(canvasWidth, h, e);
                    if (dx >= -1){
                        e.setShift((int) (e.getShift() + e.getStep() -dx/2 + 1));
                        e.setStep(0);
                        h.setShift((int) (h.getShift() + h.getStep() - dx/2 + 1));
                        h.setStep(0);
                        h.damage();
                        e.damage();
                    }
                }
            }
            for (Enemy e : enemy) {
                e.move(true, canvas);
                if (e.getShift() >= canvasWidth) {
                    startBtn.callOnClick();
                    isInfo = true;
                    break;
                }
            }

            for (Iterator<Hero> iterator = hero.iterator(); iterator.hasNext(); ) {
                Hero h = iterator.next();
                h.move(true, canvas);
                if (h.getAlpha() <= 0)
                    iterator.remove();
            }


        } else {
            for (Enemy e : enemy)
                e.move(false, canvas);

            for (Hero h : hero ) {
                h.move(false, canvas);
            }
        }



        if (isInfo) {
            drawResult(canvas, canvasWidth);
        }
        drawWindow(canvas, canvasWidth);
    }


    private void drawResult(Canvas canvas, int canvasWidth) {
        int startX = canvasWidth / 2;
        int height = canvas.getHeight();
        paint.setStrokeWidth(3);
        paint.setTextSize(38);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0, 0, startX, height / 2, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        canvas.drawText("You killed " + score.toString() + " enemies", 30, height / 4, paint);

        Integer record = lastScore;
        if (score > lastScore) record = score;
        canvas.drawText("Score record: "+ record.toString() + " enemies", 30, height/4+45, paint);

    }

    private void drawWindow(Canvas canvas, int canvasWidth) {
        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(15);
        canvas.drawRect(canvas.getClipBounds(), paint);

        int startX = canvasWidth / 2 - 15;
        int startX1 = canvasWidth / 2 + 3;

        int height = canvas.getHeight();

        canvas.drawLine(startX, 0, startX, height, paint);
        canvas.drawLine(startX1, 0, startX1, height, paint);

        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(5);
        canvas.drawRect(startX - 7, height / 3, startX + 7, height / 3 + 40, paint);
        canvas.drawRect(startX1 - 7, height / 3, startX1 + 7, height / 3 + 40, paint);
    }

    public void start(int lastScore) {
        if (lastScore > 0 )
            this.lastScore = lastScore;
        isStart = !isStart;
        if (isInfo){
            hero.clear();
            for (Enemy e : enemy) {
                e.setShift(0);
                e.setStep(0);
            }
            isInfo = false;
            score = 0;
            scoreTextView.setText(score.toString());
        }
    }

    private boolean animateHero(){
        int heroSize = hero.size();
        if (heroSize > 0 && hero.getLast().getStep() == 0) {
            Hero h = hero.getLast();
            h.setStep(5);
            h.fill(Color.rgb((heroSize%3+1)*89, (heroSize%2+1)*78, heroSize*95));

            invalidate();
            return true;
        }
        return false;
    }


    private boolean animateHero(int numOfHero){

        Hero h = hero.get(numOfHero);
        if (h.getStep() == 0) {
            h.setStep(5);
            h.fill(Color.rgb((numOfHero % 3 + 1) * 89, (numOfHero % 2 + 1) * 78, numOfHero * 95));

            invalidate();
            return true;
        }
        return false;
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            hero.getLast().deletePoint(x, y);

            int i = 0;
            for (Hero h:hero){
                if (h.getBackend() < x && h.getFront() > x && h.getTop() < y && h.getBottom() > y) {
                    if (animateHero(i)) {
                        isWizard = false;
                        break;
                    }
                }
                i++;
            }
            Log.d("MyTag", "Double tapped at: (" + x + "," + y + ")");

            return true;
        }
    }
}