package characters;

import android.graphics.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy extends Paint {
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private int bmpIndex = 0;
//    private Bitmap bmp;
    private int shift = 0;
    private int step = 0;
    private int numFrame = 0;
    private final int COL_COUNT = 3;
    private int width = 0;
    private int height = 0;
    private int top = 0;
    private int bottom = 0;
    private int alpha = 255;


    public int getShift() {
        return shift;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getWidth() {
        return width;
    }

    public int getBottom() {
        return bottom;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top * (height + 10) + 10;
        this.bottom = this.top + height;
    }
    private Rect src = new Rect();
    private Rect dst = new Rect();

    public Enemy(List<Bitmap> b) {
        bitmaps.addAll(b);
        Bitmap bmp = b.get(0);
        width = bmp.getWidth() / COL_COUNT;
        height = bmp.getHeight();
    }

    public void move(boolean isMoving, Canvas canvas) {
        if (isDied()){
            bmpIndex = 0;
            this.shift = 0;
            this.alpha = 255;
            randomizeStep();
        }

        int srcX = numFrame * width;
        src = new Rect(srcX, 0, srcX + width, height);
        int canvasWidth = canvas.getWidth();
        dst = new Rect(canvasWidth - this.shift, top, canvasWidth - this.shift + width, bottom);

        if (isMoving) {
            this.shift += step;
            numFrame = ++numFrame % 3;
        }

        onDraw(canvas);

    }

    public void randomizeStep() {
        Random rnd = new Random();
        this.step = rnd.nextInt(5) + 5;
    }

    private void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setAlpha(alpha);
        canvas.drawBitmap(bitmaps.get(bmpIndex), src, dst, paint);
    }


    public boolean isDied() {
        if (bmpIndex >= bitmaps.size())
            return true;
        return false;
    }

    public void makeDied(){
        bmpIndex = bitmaps.size();
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public void damage() {
        alpha -= 1;
        if (bmpIndex > bitmaps.size()/2)
            alpha -= 50;
      //  if (alpha % 2 == 0)
            bmpIndex++;
    }


    public void setBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public int getNumFrame() {
        return numFrame;
    }

    public Bitmap getFirstBitmap() {
        return bitmaps.get(0);
    }

    public void setNumFrame(Integer numFrame) {
        this.numFrame = numFrame;
    }

    public int getBmpIndex() {
        return bmpIndex;
    }

    public void setBmpIndex(Integer bmpIndex) {
        this.bmpIndex = bmpIndex;
    }
}
