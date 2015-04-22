package net.macdidi.turtlecarmobilepi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Unit {

    private int x;
    private int y;
    private Bitmap bitmap;
    private int width;
    private int height;
    private int parentWidth;
    private int parentHeight;

    public Unit(Bitmap bitmap, int width, int height) {
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
    }

    public void move(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void setPosition(int x, int y) {
        this.x = x - (width / 2);
        this.y = y - (height / 2);
    }

    public void paint(Canvas canvas) {
        Rect dest = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bitmap, null, dest, null);
    }

    public void setCenterInParent(int parentWidth, int parentHeight) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        setPosition(parentWidth / 2, parentHeight / 2);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
