package com.maxzxwd.easymulticutout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.ortiz.touchview.TouchImageView;

public class SelectableTouchImageView extends TouchImageView {
    private GestureDetector gestureDetector;
    private LongPressListener longPressListener;
    private ChangeDrawableListener changeDrawableListener;

    public SelectableTouchImageView(Context context) {
        super(context);
        init(context);
    }

    public SelectableTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SelectableTouchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
                if (longPressListener != null) {
                    longPressListener.onLongPress(e);
                }
            }
        });
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (changeDrawableListener != null) {
            super.setImageDrawable(changeDrawableListener.onChangeDrawable(drawable));
        } else {
            super.setImageDrawable(drawable);
        }
    }

    public Bitmap getBitmap() {
        if (!(getDrawable() instanceof BitmapDrawable)) {
            throw new IllegalStateException("Drawable should be BitmapDrawable");
        }

        return ((BitmapDrawable) getDrawable()).getBitmap();
    }

    public void setLongPressListener(LongPressListener longPressListener) {
        this.longPressListener = longPressListener;
    }

    public void setChangeDrawableListener(ChangeDrawableListener changeDrawableListener) {
        this.changeDrawableListener = changeDrawableListener;
    }

    public PointF touchPositionToBitmapPosition(float x, float y) {
        Bitmap bitmap = getBitmap();
        float[] m = new float[9];
        getImageMatrix().getValues(m);

        float imageWidth = m[Matrix.MSCALE_X] * getDrawable().getIntrinsicWidth();
        float imageHeight = m[Matrix.MSCALE_Y] * getDrawable().getIntrinsicHeight();

        float origW = bitmap.getWidth();
        float origH = bitmap.getHeight();
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        float finalX = ((x - transX) * origW) / imageWidth;
        float finalY = ((y - transY) * origH) / imageHeight;
        finalX = Math.min(Math.max(finalX, 0), origW);
        finalY = Math.min(Math.max(finalY, 0), origH);

        return new PointF(finalX, finalY);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    public interface LongPressListener {
        void onLongPress(MotionEvent event);
    }

    public interface ChangeDrawableListener {
        Drawable onChangeDrawable(Drawable drawable);
    }
}
