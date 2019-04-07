package com.maxzxwd.easymulticutout;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class SelectorManager {
    private final SelectableTouchImageView selectedImage;
    private PointF firstPoint;
    private PointF secondPoint;

    private PointF lastFirstPoint;
    private PointF lastSecondPoint;

    public SelectorManager(SelectableTouchImageView selectedImage) {
        this.selectedImage = selectedImage;
        resetSelections();
    }

    public void resetSelections() {
        firstPoint = null;
        secondPoint = null;
        lastFirstPoint = null;
        lastSecondPoint = null;
    }

    public void switchPoints() {
        PointF b = firstPoint;
        firstPoint = secondPoint;
        secondPoint = b;

        b = lastFirstPoint;
        lastFirstPoint = lastSecondPoint;
        lastSecondPoint = b;
    }

    public void select(float touchX, float touchY) {
        lastFirstPoint = firstPoint;
        lastSecondPoint = secondPoint;
        if (firstPoint == null) {
            firstPoint = selectedImage.touchPositionToBitmapPosition(touchX, touchY);
            secondPoint = firstPoint;
        } else {
            secondPoint = selectedImage.touchPositionToBitmapPosition(touchX, touchY);
        }

        Bitmap bitmap = selectedImage.getBitmap();
        if (lastFirstPoint != null && lastSecondPoint != null) {
            int minX = (int) Math.min(lastFirstPoint.x, lastSecondPoint.x);
            int minY = (int) Math.min(lastFirstPoint.y, lastSecondPoint.y);
            int width = (int) Math.max(lastFirstPoint.x, lastSecondPoint.x) - minX + 1;
            int height = (int) Math.max(lastFirstPoint.y, lastSecondPoint.y) - minY + 1;

            int[] pixelsToChange = new int[width * height];
            bitmap.getPixels(pixelsToChange, 0, width, minX, minY, width, height);
            for (int i = 0; i < pixelsToChange.length; i++) {
                pixelsToChange[i] = AndroidUtil.inverseColor(pixelsToChange[i]);
            }
            bitmap.setPixels(pixelsToChange, 0, width, minX, minY, width, height);
        }

        int minX = (int) Math.min(firstPoint.x, secondPoint.x);
        int minY = (int) Math.min(firstPoint.y, secondPoint.y);
        int width = (int) Math.max(firstPoint.x, secondPoint.x) - minX + 1;
        int height = (int) Math.max(firstPoint.y, secondPoint.y) - minY + 1;

        int[] pixelsToChange = new int[width * height];
        bitmap.getPixels(pixelsToChange, 0, width, minX, minY, width, height);
        for (int i = 0; i < pixelsToChange.length; i++) {
            pixelsToChange[i] = AndroidUtil.inverseColor(pixelsToChange[i]);
        }
        bitmap.setPixels(pixelsToChange, 0, width, minX, minY, width, height);
        selectedImage.invalidate();
    }

    public Bitmap cutSelected() {
        if (firstPoint == null || secondPoint == null) {
            return null;
        }

        Bitmap bitmap = selectedImage.getBitmap();

        int minX = (int) Math.min(firstPoint.x, secondPoint.x);
        int minY = (int) Math.min(firstPoint.y, secondPoint.y);
        int width = (int) Math.max(firstPoint.x, secondPoint.x) - minX + 1;
        int height = (int) Math.max(firstPoint.y, secondPoint.y) - minY + 1;

        int[] pixelsToChange = new int[width * height];
        bitmap.getPixels(pixelsToChange, 0, width, minX, minY, width, height);
        for (int i = 0; i < pixelsToChange.length; i++) {
            pixelsToChange[i] = AndroidUtil.inverseColor(pixelsToChange[i]);
        }
        bitmap.setPixels(pixelsToChange, 0, width, minX, minY, width, height);
        selectedImage.invalidate();

        firstPoint = secondPoint = null;

        return Bitmap.createBitmap(bitmap, minX, minY, width, height);
    }
}
