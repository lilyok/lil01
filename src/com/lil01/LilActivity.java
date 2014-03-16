package com.lil01;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
//import android.util.Log;
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
        helps.add(R.drawable.help7);
        helps.add(R.drawable.help8);
        helps.add(R.drawable.help9);

        helpTexts.add(getResources().getString(R.string.help1));//"1) Press 'Start' for start game, press backspace button for exit");
        helpTexts.add(getResources().getString(R.string.help2));
        helpTexts.add(getResources().getString(R.string.help3));
        helpTexts.add(getResources().getString(R.string.help4));
        helpTexts.add(getResources().getString(R.string.help5));
        helpTexts.add(getResources().getString(R.string.help6));
        helpTexts.add(getResources().getString(R.string.help7));
        helpTexts.add(getResources().getString(R.string.help8));
        helpTexts.add(getResources().getString(R.string.help9));


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        setContentView(R.layout.main);

        score = (TextView) findViewById(R.id.tvScore);
        Button start = (Button) findViewById(R.id.startBtn);

        myview = new MyView(this, min(height,width), score, start);

        TableLayout gl = (TableLayout) findViewById(R.id.gl);
        myview.setBackground(getResources().getDrawable(R.drawable.background));
        myview.requestFocus();

        gl.addView(myview);
    }

    public void onBackPressed() {
        int lastScore = readScore();
        myview.soundScore(lastScore);
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
                if (i < helps.size() - 1) {
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
        if (getResources().getString(R.string.startBtnText).equals(text)) {
            button.setText(R.string.pauseBtnText);
        } else {
            button.setText(R.string.startBtnText);
            Integer currentScore = recalculateScore(lastScore);
            showResultDialog(currentScore, max(currentScore, lastScore));
        }
        myview.start(lastScore);
    }

    private Integer recalculateScore(int lastScore) {
        Integer currentScore = Integer.valueOf(score.getText().toString());
        if (currentScore > lastScore) {
            writeScore(currentScore.toString());
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
            //Log.d("FileWriteLog", "Файл записан");
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

    @Override
    protected void onStop() {
        super.onStop();
        if (exitDialog != null) {
            exitDialog.dismiss();
            exitDialog = null;
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("enemy", myview.getState());
        outState.putString("scoreString",score.getText().toString());
        //Log.d("cmd", "onSaveInstanceState");
        super.onSaveInstanceState(outState);

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        myview.setState((State) savedInstanceState.getSerializable("enemy"));
        score.setText(savedInstanceState.getString("scoreString"));
        //Log.d("cmd", "onRestoreInstanceState");
    }


}