package com.lil01;

import android.content.Context;
import android.graphics.*;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
//import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import characters.Enemy;
import characters.Hero;

import java.io.Serializable;
import java.util.*;

class MyView extends View {
    final List<Bitmap> enemyPics;
    private final int enemySideLength;

    private Bitmap bonusPic;
    private final int bonusSideLength;

    private Bitmap bombPic;
    private final int bombWidth;
    private final int bombHeight;
    private final int frameCount = 5;
    private int frameNum = 0;


    private Paint paint;
    private Button startBtn;
    private TextView scoreTextView;
    private Integer score = 0;
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
    private boolean hasBomb = false;
    private GestureDetector gestureDetector;

    private Random rnd;

    private Set<Hero> collisionHero = new HashSet<Hero>();

    private SoundPool sounds;
    private int sHit;
    private int sExplosion;
    private int sBreath;
    private int sSnarl;
    private int sBoxOpened;
    private int sSwing;
    private int sStep;
    private int sClap;
    private int sApplause;

    private final int countOfBitmaps = 5;

    public MyView(Context context, int height, TextView scoreTextView, Button startBtn) {
        super(context);
        rnd = new Random();

        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        sHit = sounds.load(context, R.raw.action, 1);
        sExplosion = sounds.load(context, R.raw.explosion, 1);
        sBreath = sounds.load(context, R.raw.breath, 1);
        sSnarl = sounds.load(context, R.raw.snarl, 1);
        sBoxOpened = sounds.load(context, R.raw.box, 1);
        sSwing = sounds.load(context, R.raw.bats, 1);
        sStep = sounds.load(context, R.raw.martian, 1);
        sClap = sounds.load(context, R.raw.clap, 1);
        sApplause = sounds.load(context, R.raw.applause, 1);

        bonusPic = BitmapFactory.decodeResource(getResources(), R.drawable.bonus);
        bombPic = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);

        enemySideLength = BitmapFactory.decodeResource(getResources(), R.drawable.enemy1).getHeight();
        bonusSideLength = bonusPic.getWidth();
        bombHeight = bombPic.getHeight();
        bombWidth = bombPic.getWidth() / frameCount;


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


        int count = (int) Math.round(height / (double) enemySideLength);
        for (int i = 0; i < count - 1; i++) {
            int typeOfDragon = rnd.nextInt(100) % 3;
            enemy.add(new Enemy(enemyPics.subList(typeOfDragon * countOfBitmaps, typeOfDragon * countOfBitmaps + countOfBitmaps)));
            enemy.getLast().setTop(i);
            enemy.getLast().setStep(i + 5);
        }

    }


    private int indexOfBonusPoint(double x, double y) {
        if (hasBomb)
            return -2;

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
                //Log.i("MyTag", "ACTION_DOWN");
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
                //Log.i("MyTag", "ACTION_MOVE");

                if (indexOfBonus == -1) {
                    if (!isWizard)
                        hero.add(new Hero());
                    isWizard = true;
                    ifNoHeroNewHero();
                    hero.getLast().addPointToLastLeg(x, y);

                }
                break;
            case MotionEvent.ACTION_UP:
                //Log.i("MyTag", "ACTION_UP");
                if (indexOfBonus >= 0) {
                    sounds.play(sBoxOpened, 1.0f, 1.0f, 0, 0, 1.5f);

                    listPosOfBonus.remove(indexOfBonus);
                    indexOfBonus = -1;

                    Hero lastNotAnimate = null;

                    if ((hero.size() > 0) && (!hero.getLast().isAnimated()))
                        lastNotAnimate = hero.removeLast();


                    hero.add(lastHero.copy());
                    animateHero();

                    if (lastNotAnimate != null)
                        hero.add(lastNotAnimate);
                } else if ((indexOfBonus == -2) && (frameNum == 0)) {
                    frameNum = 1;
                    sounds.play(sExplosion, 1.0f, 1.0f, 0, 0, 1.5f);
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
        if (isInfo) {
            clearAll();
        }

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
        if (hasBomb) {
            canvas.drawBitmap(bombPic, new Rect(bombWidth * frameNum, 0, (frameNum + 1) * bombWidth, bombHeight),
                    new Rect(0, 0, canvasWidth, canvas.getHeight()), paint);
        }

        if (isStart && elapsedTime > pauseTime) {
            if (frameNum > 0) {
                frameNum++;
                if (frameNum == frameCount) {
                    frameNum = 0;
                    hasBomb = false;

                    score += enemy.size();
                    scoreTextView.setText(String.valueOf(score));
                    clearActors();
                }
            }

            double dx;
            collisionHero.clear();
            //check collision
            if (isStart)
                sounds.play(sSwing, 0.1f, 0.1f, 0, 0, 0.5f);

            for (Enemy e : enemy) {
                boolean isCollision = false;

                for (Hero h : hero) {

                    if (h.isAnimated()) {
                        dx = isCollision(canvasWidth, h, e);

                        if (dx >= -1) { //fight
                            sounds.play(sHit, 1.0f, 1.0f, 0, 0, 1.5f);

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
                            sounds.play(sSnarl, 1.0f, 1.0f, 0, 0, 2.0f);
                            h.countDeadEnemies++;
                            score++;
                            int typeOfDragon = rnd.nextInt(100) % 3;
                            e.setBitmaps(enemyPics.subList(typeOfDragon * 5, typeOfDragon * 5 + 5));
                            scoreTextView.setText(score.toString());
                            break;
                        }

                    }

                }

                //if hero dead, enemy continue gone
                if (!isCollision && e.getStep() == 0)
                    e.randomizeStep();
            }

            //draw enemies
            for (Enemy e : enemy) {
                e.move(true, canvas);
                if (e.getShift() >= canvasWidth) {

                    isInfo = true;
                    startBtn.callOnClick();
                    break;
                }
            }

            //draw heroes
            if (isStart && hero.size() > 0 && hero.getFirst().isAnimated())
                sounds.play(sStep, 1.0f, 1.0f, 0, 0, 1.5f);

            for (Iterator<Hero> iterator = hero.iterator(); iterator.hasNext(); ) {
                Hero h = iterator.next();
                if ((h.countDeadEnemies >= 2) && (rnd.nextInt(100) == 0) && (listPosOfBonus.size() < 3)) {
                    if (rnd.nextInt(10) < 5 || hasBomb)
                        listPosOfBonus.add(new Point((int) (h.getShift() + h.getBackend()), rnd.nextInt(canvas.getHeight() - bonusSideLength)/*(int) h.getTop()*/));
                    else
                        hasBomb = true;
                    h.countDeadEnemies = 0;
                }
                if (!collisionHero.contains(h) && h.getStep() == 0 && h.isAnimated())
                    h.setStep(5);
                h.move(true, canvas);
                if (h.isDied()) {
                    sounds.play(sBreath, 1.0f, 1.0f, 0, 0, 1.5f);
                    iterator.remove();
                }
            }


        } else {  //draw stopped heroes and enemies
            for (Enemy e : enemy)
                e.move(false, canvas);

            for (Hero h : hero) {
                h.move(false, canvas);
            }
        }
    }

    public void start(int lastScore) {
        isStart = !isStart;
        if (!isStart)
            soundScore(lastScore);
    }

    public void soundScore(int lastScore) {
        if (lastScore >= score)
            sounds.play(sClap, 1.0f, 1.0f, 0, 0, 1.0f);
        else
            sounds.play(sApplause, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    private void clearAll() {
        if (isInfo) {
            clearActors();
            isInfo = false;
            score = 0;
            scoreTextView.setText(score.toString());
        }
    }

    private void clearActors() {
        hero.clear();
        for (Enemy e : enemy) {
            e.makeDied();
        }
    }

    public void doPause() {
        lastIsStart = isStart;
        isStart = false;
    }

    public void doContinue() {
        isStart = lastIsStart;
    }

    private void animateHero() {
        int heroSize = hero.size();
        int numOfHero = rnd.nextInt(255 - heroSize) + heroSize + 1;
        Hero h = hero.getLast();

        if (!h.isAnimated()) {
            lastHero = hero.getLast().copy();

            h.setStep(5);
            h.fill(Color.rgb((numOfHero % 3 + 1) * 89, (numOfHero % 2 + 1) * 78, numOfHero * 95));
            h.startAnimate();
            //Log.e("Vas", "before invalidate: " + Thread.currentThread().toString());
            invalidate();
        }
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

            if (hero.size() > 0 && !hasBomb) {
                hero.getLast().deletePoint(x, y);
                if (hero.getLast().getPoints().size() == 0) {
                    hero.removeLast();
                    isWizard = false;
                }

                Hero h = hero.getLast();
                if (h.getBackend() < x && h.getFront() > x && h.getTop() < y && h.getBottom() > y) {
                    isWizard = false;
                    animateHero();
                    //Log.e("Vas", "on double tap: " + Thread.currentThread().toString());
                }

                //Log.d("MyTag", "Double tapped at: (" + x + "," + y + ")");
            }
            return true;
        }
    }


    public void setState(State st) {
        this.score = st.score;
        this.hasBomb = st.hasBomb;
        if (st.lastHero != null)
            this.lastHero = st.lastHero.copy();
        for (int i = 0; i < st.listXofBonus.size(); i++){
            listPosOfBonus.add(new Point(st.listXofBonus.get(i), st.listYofBonus.get(i)));
        }
        hero.addAll(st.hero);
        int ind = 0;
        for (Enemy e : enemy) {
            e.setShift(st.shift.get(ind));
            e.setNumFrame(st.numFrame.get(ind));
            e.setAlpha(st.alpha.get(ind));
            e.setBmpIndex(st.bmpIndex.get(ind));
            e.setStep(st.step.get(ind));

            int i = st.bmpHash.get(ind);
            e.setBitmaps(enemyPics.subList(i, i + countOfBitmaps));
            ind++;
        }
    }

    public State getState() {
        return new State(score, enemy, hero, lastHero, listPosOfBonus, hasBomb, enemyPics, countOfBitmaps);
    }


}

class State implements Serializable {
    public List<Integer> shift = new ArrayList<Integer>();
    public List<Integer> numFrame = new ArrayList<Integer>();
    public List<Integer> bmpHash = new ArrayList<Integer>();
    public List<Integer> alpha = new ArrayList<Integer>();
    public List<Integer> bmpIndex = new ArrayList<Integer>();
    public List<Integer> step = new ArrayList<Integer>();

    public LinkedList<Hero> hero = new LinkedList<Hero>();
    public Hero lastHero = null;

    public List<Integer> listXofBonus = new ArrayList<Integer>();
    public List<Integer> listYofBonus = new ArrayList<Integer>();

    public boolean hasBomb = false;
    public int score = 0;

    private int calculateBitmap(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int hash = 0;
        for (int x = width/2-3; x < width/2+3; x++) {
            for (int y = height/2-3; y < height/2+3; y++) {
                hash += bmp.getPixel(x,y);
            }
        }
        return hash;
    }

    private int indexOfBitmap(int countOfBitmaps, List<Bitmap> enemyPics, int hashBmp) {
        for (int i = 0; i < enemyPics.size(); i += countOfBitmaps) {
            if (calculateBitmap(enemyPics.get(i)) == hashBmp)
                return i;
        }
        return -1;
    }

    public State(int score, Deque<Enemy> enemy, LinkedList<Hero> hero, Hero lastHero, List<Point> listPosOfBonus, boolean hasBomb, List<Bitmap> enemyPics, int countOfBitmaps) {
        if (lastHero != null)
            this.lastHero = lastHero.copy();
        this.score = score;
        for (Point p: listPosOfBonus){
            listXofBonus.add(p.x);
            listYofBonus.add(p.y);
        }
        this.hasBomb = hasBomb;

        this.hero.addAll(hero);
        for (Enemy e : enemy) {
            shift.add(e.getShift());
            numFrame.add(e.getNumFrame());
            step.add(e.getStep());
            bmpIndex.add(e.getBmpIndex());
            alpha.add(e.getAlpha());

            bmpHash.add(indexOfBitmap(countOfBitmaps, enemyPics, calculateBitmap(e.getFirstBitmap())));
        }
    }
}