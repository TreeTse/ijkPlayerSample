package com.example.ijk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class VolumeSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {
    private int mProgress = 100;
    private int c = 3;
    private int d = 2;
    private int increment = 1;
    private int maxMultiple = 10;
    private int targetIncrement = 1;
    private IVolumeCallback mCallback;

    public VolumeSeekBar(Context context) {
        super(context);
    }

    public VolumeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VolumeSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldw, oldh);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
    }

    public void setCallback(IVolumeCallback callback) {
        mCallback = callback;
    }

    private void setIncrement() {
        this.c = (-1 + this.c);
        if (this.c <= 0) {
            if ((this.d <= maxMultiple) && (this.increment != 0)) {
                targetIncrement = this.d * this.increment;
                this.d = (1 + this.d);
            }
            this.c = 3;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                setIncrement();
                mProgress = mProgress - targetIncrement;
                if (mProgress < 0)
                    mProgress = 0;
                setProgress(mProgress);
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if (mCallback != null)
                    mCallback.onProgress(mProgress);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                setIncrement();
                mProgress = mProgress + targetIncrement;
                if (mProgress > 200)
                    mProgress = 200;
                setProgress(mProgress);
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if (mCallback != null)
                    mCallback.onProgress(mProgress);
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                this.c = 3;
                this.d = 2;
                targetIncrement = this.increment;
                mCallback.onVolumeChange(mProgress);
                break;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mProgress = getMax() - (int) (getMax() * event.getY() / getHeight());
                if (mProgress > 200)
                    mProgress = 200;
                if (mProgress < 0)
                    mProgress = 0;
                setProgress(mProgress);
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if (mCallback != null) {
                    mCallback.onProgress(mProgress);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCallback != null) {
                    mCallback.onVolumeChange(mProgress);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

}