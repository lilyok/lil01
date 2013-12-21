package characters;

import android.graphics.*;
import com.example.lil01.R;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Paint {
    private Bitmap bmp;
    private int step = 0;
    private int numFrame = 0;
    private final int COL_COUNT = 3;
    private int width = 0;
    private int height = 0;

    private Rect src = new Rect();
    private Rect dst = new Rect();

    public Enemy(Bitmap b) {
        bmp = b;
        width = bmp.getWidth() / COL_COUNT;
        height = bmp.getHeight();
    }

    public void move(int step, Canvas canvas) {
        int srcX = numFrame * width;
        src = new Rect(srcX, 0, srcX + width, height);
        int canvasWidth = canvas.getWidth();
        dst = new Rect(canvasWidth - this.step, 10, canvasWidth - this.step + width, 10 + height);

        if (step > 0) {
            this.step += step;
            numFrame = ++numFrame % 3;
        }

        onDraw(canvas);
    }

    private void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, src, dst, null);
    }

    public Bitmap getBmp() {
        return bmp;
    }

}
