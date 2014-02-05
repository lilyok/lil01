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
import android.view.*;
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
    private final List<String> helpTexts = new ArrayList<String>();

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

        helpTexts.add("You can draw heroes on window:\n1) Select 'Body' if it was not selected.");
        helpTexts.add("2) Draw body, head.\n");
        helpTexts.add("3) Select 'Legs' and\ndraw legs, tails, wings.");
        helpTexts.add("4) Press wizard button\nfor animate hero.");
        helpTexts.add("5) Then draw and animate\nother hero.");
        helpTexts.add("6) When all heroes is ready press Start button.\nPress Pause button for paused game");


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
            public void onClick(DialogInterface dialog, int which) {
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


    public void helpBtnClick(final View view) {
        Toast.makeText(this, "тут будет справка", Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder alertHelpBuilder = new AlertDialog.Builder(this);
        // alertHelpBuilder.setTitle("Help");


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
                if (i > 0) {
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
                if (i < 5) {
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
//        Toast.makeText(this, "Зачем вы нажали?", Toast.LENGTH_SHORT).show();

        Button button = (Button) view;
        CharSequence text = button.getText();
        int lastScore = readScore();
        if ("Start".equals(text)) {
            button.setText("Pause");
        } else {
            button.setText("Start");
            recalculateScore(lastScore);
        }
        myview.start(lastScore);
    }

    private void recalculateScore(int lastScore) {
        Integer currentScore = Integer.valueOf(score.getText().toString());
        if (currentScore > lastScore) {
            writeScore(currentScore.toString());
            Toast.makeText(this, "Новый рекорд " + currentScore.toString(), Toast.LENGTH_SHORT).show();
        }
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
            try {
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