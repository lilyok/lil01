package com.example.lil01;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import characters.Enemy;
import characters.Hero;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class LilActivity extends Activity {
    private MyView myview;
    private RadioButton brbtn;
    private RadioButton lrbtn;
    private TextView score;
    private final String FILENAME = "score.log";
    private final List<Integer> helps = new ArrayList<Integer>();
    private final List <String> helpTexts = new ArrayList<String>();
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helps.add(R.drawable.help1);
        helps.add(R.drawable.help2);
        helps.add(R.drawable.help3);
        helps.add(R.drawable.help4);
        helps.add(R.drawable.help5);
        helps.add(R.drawable.help6);

        helpTexts.add("You would have battle with dragons and\nyou can draw heroes on window:\n1) Select 'Body' if it was not selected");
        helpTexts.add("2) Draw body, head or\nother body parts\nwhich moving in a straight line");
        helpTexts.add("3) Select 'Legs' and\ndraw legs, tail,\nwings, hair, if you want");
        helpTexts.add("4) Press wizard button\nfor animate hero\nhero must change color");
        helpTexts.add("5) Then draw and animate\nother hero, if you want\n");
        helpTexts.add("6) When all heroes is ready press\nStart button. Press Pause button for\npaused game or for draw new Hero");


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        setContentView(R.layout.main);
        brbtn = (RadioButton) findViewById(R.id.rBody);
        lrbtn = (RadioButton) findViewById(R.id.rLegs);
        score = (TextView) findViewById(R.id.tvScore);
        Button start = (Button) findViewById(R.id.startBtn);

        myview = new MyView(this, height, score, start);

        TableLayout gl = (TableLayout) findViewById(R.id.gl);
        myview.setBackground(getResources().getDrawable(R.drawable.background));
        myview.requestFocus();

        gl.addView(myview);
    }

    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exit?");

        alertDialog.setMessage("Do you want exit?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                int lastScore = readScore();

                recalculateScore(lastScore);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
        return;

    }



    public void helpBtnClick(final View view){
        Toast.makeText(this, "тут будет справка", Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder alertHelpBuilder = new AlertDialog.Builder(this);
        alertHelpBuilder.setTitle("Help");


        final View helpView = new View(this);
        final AtomicInteger picNum = new AtomicInteger(0);
        final TextView helpText = new TextView(this);
        helpText.setText(helpTexts.get(0));
        helpText.setTextSize(14);

        helpView.setBackground(getResources().getDrawable(helps.get(0)));


        alertHelpBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        final Button backBtn = new Button(this);
        backBtn.setText("back");
        backBtn.setEnabled(false);

        backBtn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

        final Button nextBtn = new Button(this);
        nextBtn.setText("next");
        nextBtn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

        backBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int i = picNum.get();
                if (i > 0){
                    i = picNum.decrementAndGet();
                    if (i == 4)
                        nextBtn.setEnabled(true);
                    else if (i == 0)
                        v.setEnabled(false);
                    helpView.setBackground(getResources().getDrawable(helps.get(i)));
                    helpText.setText(helpTexts.get(i));
                }
            }

        });


        nextBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int i = picNum.get();
                if (i < 5){
                    i = picNum.incrementAndGet();
                    if (i == 1)
                        backBtn.setEnabled(true);
                    else if (i == 5)
                        v.setEnabled(false);

                    helpView.setBackground(getResources().getDrawable(helps.get(i)));
                    helpText.setText(helpTexts.get(i));
                }
            }

        });


        LinearLayout layoutHorizontal = new LinearLayout(this);
        layoutHorizontal.setOrientation(0);
        layoutHorizontal.setGravity(Gravity.FILL);
        layoutHorizontal.addView(backBtn);
        layoutHorizontal.addView(helpText);
        layoutHorizontal.addView(nextBtn);


        LinearLayout layoutVertical = new LinearLayout(this);
        layoutVertical.setOrientation(1);
        layoutVertical.setGravity(Gravity.CENTER);

        layoutVertical.addView(layoutHorizontal);
        layoutVertical.addView(helpView);

        alertHelpBuilder.setView(layoutVertical);


        alertHelpBuilder.show();
    }


    public void startBtnClick(View view) {
        // выводим сообщение
        Toast.makeText(this, "Зачем вы нажали?", Toast.LENGTH_SHORT).show();

        Button button = (Button) view;
        CharSequence text = button.getText();
        int lastScore = readScore();
        if ("Start".equals(text)){
            button.setText("Pause");
        } else {
            button.setText("Start");
            recalculateScore(lastScore);
        }
        myview.start(lastScore);
    }

    private void recalculateScore(int lastScore) {
        Integer currentScore = Integer.valueOf(score.getText().toString());
        if (currentScore > lastScore){
            writeScore(currentScore.toString());
            Toast.makeText(this, "Новый рекорд "+currentScore.toString(), Toast.LENGTH_SHORT).show();
        }
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

    void writeScore(String currentScore) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            // пишем данные
            bw.write(currentScore);
            // закрываем поток
            bw.close();
            Log.d("FileWriteLog", "Файл записан");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int readScore() {
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String str = "";
            // читаем содержимое
            str = br.readLine();
            try{
                int res = Integer.valueOf(str.trim());
                return res;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

}

class MyView extends View {
    private Bitmap myWizard;
    private Paint paint;
    private Button startBtn;
    private TextView scoreTextView;
    private Integer score = 0;
    private Integer lastScore = 0;
    private int canvasWidth = 0;
    private LinkedList<Hero> hero;
    private Deque<Enemy> enemy;
    private Map<Hero, Enemy> rival = new HashMap<Hero, Enemy>();

    public boolean isLegs = false;
    private boolean isStart = false;
    private boolean isWizard = false;

    private boolean isInfo = false;
    // private final int STEP = 10;

    public MyView(Context context, int height, TextView scoreTextView, Button startBtn) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.scoreTextView = scoreTextView;
        this.startBtn = startBtn;

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
        int count = height/bmp.getHeight();//3;//rnd.nextInt(height/(3*bmp.getHeight())-1)+1;
        for (int i = 0; i < count; i++) {
            enemy.add(new Enemy(bmp));
            enemy.getLast().setTop(i);
            enemy.getLast().setStep(rnd.nextInt(2) + 1);
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
            //if (!rival.containsValue(e)) {
            if (h.getFront() < canvasWidth - e.getShift()){
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
      //  }
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

        canvasWidth = canvas.getWidth();


        for (Iterator<Hero> iterator = hero.iterator(); iterator.hasNext(); ) {
            Hero h = iterator.next();

            if (isStart) {
                double tmpStep = isCollisionEnemy(canvasWidth, h);
                if (tmpStep < 0)
                    h.move(true, canvas);
                else {
                    if (h.getStep() > 0) {
                        h.setStep((int) tmpStep);
                        score++;
                        scoreTextView.setText(score.toString());
                    }
                    h.move(true, canvas);

                    h.setStep(0);



                    if (h.getAlpha() == 0) {

                        rival.remove(h);
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
                if (e.getShift() >= canvasWidth) {
                    startBtn.callOnClick();
                    isInfo = true;
                    break;
                }
            } else
                e.move(false, canvas);

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

        canvas.drawRect(0, 0, startX, height/2, paint);
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

        canvas.drawLine(startX, 0, startX, height,paint);
        canvas.drawLine(startX1, 0, startX1, height,paint);

        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(5);
        canvas.drawRect(startX - 7, height/3, startX + 7, height/3+40, paint);
        canvas.drawRect(startX1 - 7, height/3, startX1 + 7, height/3+40, paint);
    }

    public void start(int lastScore) {
        if (lastScore > 0 )
            this.lastScore = lastScore;
        isStart = !isStart;
        if (isInfo){
            hero.clear();
            rival.clear();
            for (Enemy e : enemy) {
                e.setShift(0);
                e.setStep(0);
            }
            isInfo = false;
            score = 0;
            scoreTextView.setText(score.toString());
        }
        if (isStart && rival.size() == 0) {

            Log.i("TagWiz", rival.toString());
        }

    }

    private void animateHero(){
        int heroSize = hero.size();
        if (heroSize > 0 && hero.getLast().getStep() == 0) {
            Hero h = hero.getLast();
            h.setStep(3);
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