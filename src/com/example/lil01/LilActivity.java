package com.example.lil01;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import static java.lang.Math.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LilActivity extends Activity implements View.OnClickListener {
    private MyView myview;

    private TextView score;
    private final String FILENAME = "score.log";
    private final List<Integer> helps = new ArrayList<Integer>();
    private final List<String> helpTexts = new ArrayList<String>();
    private Dialog helpDialog;
    private Dialog resultDialog;
    private Dialog exitDialog;

    private final AtomicInteger picNum = new AtomicInteger(0);
    private View helpView;
    private TextView helpText;
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

        helpTexts.add("You can draw heroes on window: 1) Select 'Body' if it was not selected. проверка переноста тест тест тетс прпрар впппы ппппы рррррыры ппапап рррккр рррра рррра рррра");
        helpTexts.add("2) Draw body, head.\n");
        helpTexts.add("3) Select 'Legs' and\ndraw legs, tails, wings.");
        helpTexts.add("4) Press wizard button\nfor animate hero.");
        helpTexts.add("5) Then draw and animate\nother hero.");
        helpTexts.add("6) When all heroes is ready press Start button.\nPress Pause button for paused game");


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        setContentView(R.layout.main);

        score = (TextView) findViewById(R.id.tvScore);
        Button start = (Button) findViewById(R.id.startBtn);

        myview = new MyView(this, height, score, start);

        TableLayout gl = (TableLayout) findViewById(R.id.gl);
        myview.setBackground(getResources().getDrawable(R.drawable.background));
        myview.requestFocus();

        gl.addView(myview);
    }

    public void onBackPressed() {
        int lastScore = readScore();
        Integer currentScore = recalculateScore(lastScore);
        showExitDialog(currentScore, max(currentScore, lastScore));
    }

    public void showExitDialog(Integer currentScore, Integer maxScore) {
        myview.doPause();
        exitDialog = new Dialog(this,android.R.style.Theme_Translucent);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setContentView(R.layout.exit_dialog);
        exitDialog.setCancelable(true);
        Button btnReturn = (Button) exitDialog.findViewById(R.id.btncancelexit);
        btnReturn.setOnClickListener(this);

        Button btnExit = (Button) exitDialog.findViewById(R.id.btnexit);
        btnExit.setOnClickListener(this);

        EditText killedEnemy = (EditText) exitDialog.findViewById(R.id.killedEnemy);
        killedEnemy.setText(currentScore.toString());

        EditText recordKilledEnemy = (EditText) exitDialog.findViewById(R.id.recordKilledEnemy);
        recordKilledEnemy.setText(maxScore.toString());

        exitDialog.show();
    }

    public void showResultDialog(Integer currentScore, Integer maxScore) {
        resultDialog = new Dialog(this,android.R.style.Theme_Translucent);
        resultDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resultDialog.setContentView(R.layout.result_dialog);
        resultDialog.setCancelable(true);
        Button btnReturn = (Button) resultDialog.findViewById(R.id.btnreturn);
        btnReturn.setOnClickListener(this);

        EditText killedEnemy = (EditText) resultDialog.findViewById(R.id.killedEnemy);
        killedEnemy.setText(currentScore.toString());

        EditText recordKilledEnemy = (EditText) resultDialog.findViewById(R.id.recordKilledEnemy);
        recordKilledEnemy.setText(maxScore.toString());

        resultDialog.show();
    }

    public void helpBtnClick(final View view) {
        myview.doPause();
        Toast.makeText(this, "тут будет справка", Toast.LENGTH_SHORT).show();
        picNum.set(0);

        helpDialog = new Dialog(this,android.R.style.Theme_Translucent);
        helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        helpDialog.setContentView(R.layout.help_dialog);
        helpDialog.setCancelable(true);
        Button btnCancel = (Button) helpDialog.findViewById(R.id.btncancel);
        btnCancel.setOnClickListener(this);

        helpText = (TextView) helpDialog.findViewById(R.id.tv);
        helpText.setText(helpTexts.get(0));

        helpView = helpDialog.findViewById(R.id.helpView);
        helpView.setBackgroundResource(helps.get(0));

        Button nextBtn = (Button) helpDialog.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(this);
        Button backBtn = (Button) helpDialog.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        helpDialog.show();
    }

    @Override
    public void onClick(View v) {
        int i = 0;
        switch (v.getId()) {
            case R.id.btncancel:
                myview.doContinue();
                helpDialog.dismiss();
                break;
            case R.id.btnreturn:
                resultDialog.dismiss();
                break;
            case R.id.btncancelexit:
                myview.doContinue();
                exitDialog.dismiss();
                break;
            case R.id.btnexit:
                finish();
                break;
            case R.id.nextBtn:
                i = picNum.get();
                if (i < 5) {
                    i = picNum.incrementAndGet();

                    helpView.setBackgroundResource(helps.get(i));
                    helpText.setText(helpTexts.get(i));
                }
                break;
            case R.id.backBtn:
                i = picNum.get();
                if (i > 0) {
                    i = picNum.decrementAndGet();
                    helpView.setBackgroundResource(helps.get(i));
                    helpText.setText(helpTexts.get(i));
                }
                break;
            default:
                break;
        }

    }


    public void startBtnClick(View view) {
        // выводим сообщение

        Button button = (Button) view;
        CharSequence text = button.getText();
        int lastScore = readScore();
        if ("Start".equals(text)) {
            button.setText("Pause");
        } else {
            button.setText("Start");
            Integer currentScore = recalculateScore(lastScore);
            showResultDialog(currentScore, max(currentScore, lastScore));
        }
        myview.start();
    }

    private Integer recalculateScore(int lastScore) {
        Integer currentScore = Integer.valueOf(score.getText().toString());
        if (currentScore > lastScore) {
            writeScore(currentScore.toString());
            Toast.makeText(this, "Новый рекорд " + currentScore.toString(), Toast.LENGTH_SHORT).show();
        }
        return currentScore;
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