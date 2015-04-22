package net.macdidi.turtlecarmobilepi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoyStickView extends View {

    private Unit unit;

    private int sx;
    private int sy;

    private int parentWidth;
    private int parentHeight;

    private CallBack callBack;
    private boolean messageSended = false;

    private boolean enabled = true;

    public JoyStickView(Context context) {
        super(context);

        init(context);
    }

    public JoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.joy_stick_icon);
        unit = new Unit(bitmap, bitmap.getWidth(), bitmap.getHeight());
        setBackgroundResource(R.drawable.block_drawable);
        setFocusable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        unit.setCenterInParent(parentWidth, parentHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        unit.paint(canvas);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!enabled) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            sx = (int)event.getX();
            sy = (int)event.getY();
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            sx = 0;
            sy = 0;
            unit.setCenterInParent(parentWidth, parentHeight);

            if (messageSended) {
                messageSended = false;
                callBack.control(ControlType.STOP);
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            if (Math.abs(x - sx) > 10 || Math.abs(y - sy) > 10) {
                unit.setPosition(x, y);
                processControl(x, y);
            }
        }

        postInvalidate();

        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void processControl(int x, int y) {
        int halfImageX = unit.getWidth() / 2;
        int halfImageY = unit.getHeight() / 2;

        if (messageSended && touchControl(x, y, halfImageX, halfImageY)) {
            return;
        }

        if ((x + halfImageX) > parentWidth) {
            messageSended = true;
            callBack.control(ControlType.RIGHT);
        }
        else if ((y + halfImageY) > parentHeight) {
            messageSended = true;
            callBack.control(ControlType.BACKWARD);
        }
        else if ((x - halfImageX) < 0) {
            messageSended = true;
            callBack.control(ControlType.LEFT);
        }
        else if ((y - halfImageY) < 0) {
            messageSended = true;
            callBack.control(ControlType.FORWARD);
        }
        else {
            if (messageSended) {
                messageSended = false;
                callBack.control(ControlType.STOP);
            }
        }
    }

    private boolean touchControl(int x, int y, int halfImageX, int halfImageY) {
        return ((x + halfImageX) > parentWidth) ||
               ((y + halfImageY) > parentHeight) ||
               ((x - halfImageX) < 0) ||
               ((y - halfImageY) < 0);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        public void control(ControlType action);
    }

}
