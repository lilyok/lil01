package characters;

import android.graphics.*;

public class Enemy extends Paint {
    private Bitmap bmp;
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

    public int getHeight() {
        return height;
    }

    public int getBottom() {
        return bottom;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top * (height * 3 + 3);
        this.bottom = this.top + height * 3;
    }
    private Rect src = new Rect();
    private Rect dst = new Rect();

    public Enemy(Bitmap b) {
        bmp = b;
        width = bmp.getWidth() / COL_COUNT;
        height = bmp.getHeight();
    }

    public void move(boolean isMoving, Canvas canvas) {
        int srcX = numFrame * width;
        src = new Rect(srcX, 0, srcX + width, height);
        int canvasWidth = canvas.getWidth();
        dst = new Rect(canvasWidth - this.shift, top, canvasWidth - this.shift + 3 * width, bottom);

        if (isMoving) {
            this.shift += step;
            numFrame = ++numFrame % 3;
        }

        onDraw(canvas);

    }

    private void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setAlpha(alpha);
        canvas.drawBitmap(bmp, src, dst, paint);
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setDied(){
        if (alpha>0)
            alpha -= 51;
        else if (alpha == 0){
            alpha = 255;
            this.shift = 0;
            this.step = 0;
        }
    }

}
