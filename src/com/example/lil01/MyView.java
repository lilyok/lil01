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
    private final int enemySideLength;
    private final int bonusSideLength;
    private Paint paint;
    private Button startBtn;
    private TextView scoreTextView;
    private Integer score = 0;
    private Integer lastScore = 0;
    private LinkedList<Hero> hero;
    private Deque<Enemy> enemy;

    private boolean isStart = false;
    private boolean lastIsStart = false;
    private boolean isWizard = false;

    private boolean isInfo = false;
    private int indexOfBonus = -1;
    private Hero lastHero = null;

    private long prevTime;
    private List<Point> listPosOfBonus = new ArrayList<Point>();
    private GestureDetector gestureDetector;

    final List<Bitmap> enemyPics;
    private Random rnd;
    private Bitmap bonusPic;
    private Set<Hero> collisionHero = new HashSet<Hero>();

    public MyView(Context context, int height, TextView scoreTextView, Button startBtn) {
        super(context);
        rnd = new Random();


        enemySideLength = BitmapFactory.decodeResource(getResources(), R.drawable.enemy1).getHeight();
        bonusSideLength = BitmapFactory.decodeResource(getResources(), R.drawable.christmasgifticon).getWidth();

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

        bonusPic = BitmapFactory.decodeResource(getResources(), R.drawable.bonus);
        hero = new LinkedList<Hero>();
        enemy = new LinkedList<Enemy>();
        enemyPics = new ArrayList<Bitmap>();
        createEnemies(height);
    }

    private Bitmap intToBmp(int res) {
        return BitmapFactory.decodeResource(getResources(), res);
    }

    private void createEnemies(double height) {
        enemyPics.add(intToBmp(R.drawable.enemy1));
        enemyPics.add(intToBmp(R.drawable.enemy1a));
        enemyPics.add(intToBmp(R.drawable.enemy1b));
        enemyPics.add(intToBmp(R.drawable.enemy1c));
        enemyPics.add(intToBmp(R.drawable.enemy1d));

        enemyPics.add(intToBmp(R.drawable.enemy2));
        enemyPics.add(intToBmp(R.drawable.enemy2a));
        enemyPics.add(intToBmp(R.drawable.enemy2b));
        enemyPics.add(intToBmp(R.drawable.enemy2c));
        enemyPics.add(intToBmp(R.drawable.enemy2d));

        enemyPics.add(intToBmp(R.drawable.enemy3));
        enemyPics.add(intToBmp(R.drawable.enemy3a));
        enemyPics.add(intToBmp(R.drawable.enemy3b));
        enemyPics.add(intToBmp(R.drawable.enemy3c));
        enemyPics.add(intToBmp(R.drawable.enemy3d));


        int count = (int) Math.round(height / (double) enemySideLength);//bmp.getHeight());//3;//rnd.nextInt(height/(3*bmp.getHeight())-1)+1;
        for (int i = 0; i < count - 1; i++) {
            int typeOfDragon = rnd.nextInt(100) % 3;
            enemy.add(new Enemy(enemyPics.subList(typeOfDragon * 5, typeOfDragon * 5 + 5)));
            enemy.getLast().setTop(i);
            enemy.getLast().setStep(i + 5);
        }

    }


    private int indexOfBonusPoint(double x, double y) {
        for (int i = 0; i < listPosOfBonus.size(); i++) {
            final int x1 = listPosOfBonus.get(i).x;
            if ((x1 < x) && (x1 + bonusSideLength > x)) {
                final int y1 = listPosOfBonus.get(i).y;
                if ((y1 < y) && (y1 + bonusSideLength > y))
                    return i;
            }
        }
        return -1;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result;
        result = gestureDetector.onTouchEvent(event);//return the double tap events
        if (result)
            return false;

        int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("MyTag", "ACTION_DOWN");
                indexOfBonus = indexOfBonusPoint(x, y);
                if (indexOfBonus == -1) {
                    if (!isWizard)
                        hero.add(new Hero());
                    isWizard = true;

                    ifNoHeroNewHero();
                    hero.getLast().addPointToNewLeg(x, y);

                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("MyTag", "ACTION_MOVE");

                if (indexOfBonus == -1) {
                    if (!isWizard)
                        hero.add(new Hero());
                    isWizard = true;
                    ifNoHeroNewHero();
                    hero.getLast().addPointToLastLeg(x, y);

                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i("MyTag", "ACTION_UP");
                if (indexOfBonus == -1) {
                    ifNoHeroNewHero();
                    hero.getLast().addPointToBody(null);
                } else {
                    listPosOfBonus.remove(indexOfBonus);
                    indexOfBonus = -1;

                    Hero lastNotAnimate = null;

                    if ((hero.size() > 0) && (!hero.getLast().isAnimated()))
                        lastNotAnimate = hero.removeLast();


                    hero.add(lastHero.copy());
                    animateHero();

                    if (lastNotAnimate != null)
                        hero.add(lastNotAnimate);
                }
                break;
        }
        return true;
    }

    private void ifNoHeroNewHero() {
        if (hero.size() == 0)
            hero.add(new Hero());
    }


    private double isCollision(int canvasWidth, Hero h, Enemy e) {
        double dx = -2 * canvasWidth;
        final double heroTop = h.getTop();
        final double heroBottom = h.getBottom();
        final int enemyTop = e.getTop();
        boolean isCanCollision = false;
        final int enemyBottom = e.getBottom();
        if ((enemyTop <= heroTop) && (enemyBottom >= heroTop)) {
            isCanCollision = true;
        } else if ((enemyTop <= heroBottom) && (enemyBottom >= heroBottom)) {
            isCanCollision = true;
        } else if ((enemyTop >= heroTop) && (enemyBottom <= heroBottom)) {
            isCanCollision = true;
        }

        if (isCanCollision && h.getBackend() < canvasWidth - e.getShift() + e.getWidth()) {
            double oldDx = h.getFront() + h.getShift() - (canvasWidth - e.getShift());
            dx = oldDx + h.getStep() + e.getStep();
        }
        return dx;
    }


    protected void onDraw(Canvas canvas) {
        long now = System.currentTimeMillis();
        long elapsedTime = now - prevTime;
        int pauseTime = 100;
        if (elapsedTime > pauseTime)
            prevTime = now;

        super.onDraw(canvas);
        invalidate();


        int canvasWidth = canvas.getWidth();

        //draw Bonus
        for (Point p : listPosOfBonus) {
            canvas.drawBitmap(bonusPic, p.x, p.y, paint);
        }

        if (isStart && elapsedTime > pauseTime) {
            double dx;
            collisionHero.clear();
            //check collision
            for (Enemy e : enemy) {
                boolean isCollision = false;

                for (Hero h : hero) {

                    if (h.isAnimated()) {
                        dx = isCollision(canvasWidth, h, e);

                        if (dx >= -1) { //fight
                            e.setShift((int) (e.getShift() + e.getStep() - dx / 2 + 1));
                            e.setStep(0);
                            h.setShift((int) (h.getShift() + h.getStep() - dx / 2 + 1));
                            h.setStep(0);
                            h.damage();
                            e.damage();
                            isCollision = true;
                            collisionHero.add(h);
                        }
                        if (e.isDied()) {
                            h.countDeadEnemies++;
                            break;
                        }

                    }

                }

                //if hero dead, enemy continue gone
                if (!isCollision && e.getStep() == 0)
                    e.randomizeStep();
                //if enemy dead
                if (e.isDied()) {
                    score++;
                    int typeOfDragon = rnd.nextInt(100) % 3;
                    e.setBitmaps(enemyPics.subList(typeOfDragon * 5, typeOfDragon * 5 + 5));
                    scoreTextView.setText(score.toString());

                }
            }

            //draw enemies
            for (Enemy e : enemy) {
                e.move(true, canvas);
                if (e.getShift() >= canvasWidth) {
                    startBtn.callOnClick();
                    isInfo = true;
                    break;
                }
            }

            //draw heroes
            for (Iterator<Hero> iterator = hero.iterator(); iterator.hasNext(); ) {
                Hero h = iterator.next();
                if ((h.countDeadEnemies >= 2) && (rnd.nextInt(100) == 0)) {
                    listPosOfBonus.add(new Point((int) (h.getShift() + h.getBackend()), (int) h.getTop()));
                    h.countDeadEnemies = 0;
                }
                if (!collisionHero.contains(h) && h.getStep() == 0 && h.isAnimated())
                    h.setStep(5);
                h.move(true, canvas);
                if (h.isDied())
                    iterator.remove();
            }


        } else {  //draw stopped heroes and enemies
            for (Enemy e : enemy)
                e.move(false, canvas);

            for (Hero h : hero) {
                h.move(false, canvas);
            }
        }


        if (isInfo) {
            drawResult(canvas, canvasWidth);
        }
    }


    private void drawResult(Canvas canvas, int canvasWidth) {
        int startX = canvasWidth / 2;
        int height = canvas.getHeight();
        paint.setStrokeWidth(3);
        paint.setTextSize(38);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        canvas.drawRect(0, 0, startX, height / 2, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        canvas.drawText("You killed " + score.toString() + " enemies", 30, height / 4, paint);

        Integer record = lastScore;
        if (score > lastScore) record = score;
        canvas.drawText("Score record: " + record.toString() + " enemies", 30, height / 4 + 45, paint);

    }

    public void start(int lastScore) {
        if (lastScore > 0)
            this.lastScore = lastScore;
        isStart = !isStart;
        if (isInfo) {
            hero.clear();
            for (Enemy e : enemy) {
                e.makeDied();
            }
            isInfo = false;
            score = 0;
            scoreTextView.setText(score.toString());
        }
    }

    public void doPause(){
        lastIsStart = isStart;
        isStart = false;
    }
    public void doContinue(){
        isStart = lastIsStart;
    }

    private boolean animateHero() {
        int heroSize = hero.size();
        int numOfHero = rnd.nextInt(255 - heroSize) + heroSize + 1;
        Hero h = hero.getLast();
        if (!h.isAnimated()) {
            lastHero = hero.getLast().copy();

            h.setStep(5);
            h.fill(Color.rgb((numOfHero % 3 + 1) * 89, (numOfHero % 2 + 1) * 78, numOfHero * 95));
            h.startAnimate();
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

            Hero h = hero.getLast();
            if (h.getBackend() < x && h.getFront() > x && h.getTop() < y && h.getBottom() > y) {
                if (animateHero()) {
                    isWizard = false;
                }
            }


            Log.d("MyTag", "Double tapped at: (" + x + "," + y + ")");

            return true;
        }
    }
}