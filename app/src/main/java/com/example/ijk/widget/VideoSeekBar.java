package com.example.ijk.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

import java.util.Locale;

public class VideoSeekBar extends AppCompatSeekBar {
    private int oldPaddingTop;

    private int oldPaddingLeft;

    private int oldPaddingRight;

    private int oldPaddingBottom;

    private boolean isMysetPadding = true;

    private boolean isShow = false;

    private String mText;

    private float mTextWidth;

    private float mImgWidth;

    private float mImgHei;

    private Paint mPaint;

    private Resources res;

    private Bitmap bm;

    private long  mDuration = 1;

    private int textsize = 13;

    private int textpaddingleft;

    private int textpaddingtop;

    private int imagepaddingleft;

    private int imagepaddingtop;

    private int increment = 1;
    private int maxMultiple = 10;
    private int c = 3;
    private int d = 2;


    public VideoSeekBar(Context context) {
        super(context);
        init();
    }

    public VideoSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * (非 Javadoc)
     *
     * @方法名: onTouchEvent
     * @描述: 不屏蔽屏蔽滑动
     * @日期: 2014-8-11 下午2:03:15
     * @param event
     * @return
     * @see android.widget.AbsSeekBar#onTouchEvent(MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    // 修改setpadding 使其在外部调用的时候无效
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (isMysetPadding) {
            super.setPadding(left, top, right, bottom);
        }
    }

    // 初始化
    private void init() {
        res = getResources();
        initBitmap();
        initDraw();
        setPadding();
    }

    private void initDraw() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTypeface(Typeface.DEFAULT);
        mPaint.setTextSize(textsize);
        mPaint.setColor(Color.BLACK);
    }

    private void initBitmap() {
        bm = null;
        if (bm != null) {
            mImgWidth = bm.getWidth();
            mImgHei = bm.getHeight();
        } else {
            mImgWidth = 60;
            mImgHei = 30;
        }
    }

    private void setIncrement() {
        this.c = (-1 + this.c);
        if (this.c <= 0) {
            if ((this.d <= maxMultiple) && (this.increment != 0)) {
                setKeyProgressIncrement(this.d * this.increment);
                this.d = (1 + this.d);
            }
            this.c = 3;
        }
    }

    @Override
    public void setMax(int max) {
        super.setMax(max);
        increment = max / 500;
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                setIncrement();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                this.c = 3;
                this.d = 2;
                setKeyProgressIncrement(this.increment);
                break;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    protected synchronized void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
            if(isShow) {
                mText = showTime((long) ((float)(getProgress() / 10000.0f)
                        * (mDuration / 1000.0f)));
                mTextWidth = mPaint.measureText(mText);
                Rect bounds = this.getProgressDrawable().getBounds();
                float xImg =
                        bounds.width() * getProgress() / getMax() + imagepaddingleft
                                + oldPaddingLeft;
                float yImg = imagepaddingtop + oldPaddingTop;
                float xText =
                        bounds.width() * getProgress() / getMax() + mImgWidth / 2
                                - mTextWidth / 2 + textpaddingleft + oldPaddingLeft;
                float yText =
                        yImg + textpaddingtop + mImgHei / 2 + getTextHei() / 4;
                //          canvas.drawBitmap(bm, xImg, yImg, mPaint);
                canvas.drawText(mText, xText, yText, mPaint);
            }else {
                // 不显示
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化padding 使其左右上 留下位置用于展示进度图片
    private void setPadding() {
        int top = getBitmapHeigh() + oldPaddingTop;
        int left = getBitmapWidth() / 2 + oldPaddingLeft;
        int right = getBitmapWidth() / 2 + oldPaddingRight;
        int bottom = oldPaddingBottom;
        isMysetPadding = true;
        setPadding(left, top, right, bottom);
        isMysetPadding = false;
    }

    /**
     * 设置展示进度背景图片
     *
     * @param resid
     */
    public void setBitmap(int resid) {
        bm = null;
        if (bm != null) {
            mImgWidth = bm.getWidth();
            mImgHei = bm.getHeight();
        } else {
            mImgWidth = 60;
            mImgHei = 30;
        }
        setPadding();
    }

    /**
     * 将长时间格式字符串转换为字符串 HH:mm:ss
     *
     * @return String
     */
    public String showTime(long time) {
        int minute = (int) time / 60;
        int hour = (int) minute / 60;
        int second = (int) time % 60;
        minute %= 60;
        return String.format(Locale.US,"%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * 替代setpadding
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setMyPadding(int left, int top, int right, int bottom) {
        oldPaddingTop = top;
        oldPaddingLeft = left;
        oldPaddingRight = right;
        oldPaddingBottom = bottom;
        isMysetPadding = true;
        setPadding(left + getBitmapWidth() / 2, top + getBitmapHeigh(), right
                + getBitmapWidth() / 2, bottom);
        isMysetPadding = false;
    }

    /**
     * 设置进度字体大小
     *
     * @param textsize
     */
    public void setTextSize(int textsize) {
        this.textsize = textsize;
        mPaint.setTextSize(textsize);
    }

    /**
     * 设置进度字体是否显示
     *
     */
    public void setTextVisiable(boolean isShow) {
        this.isShow = isShow;
    }

    /**
     * 设置进度大小
     *
     * @param mDuration
     */
    public void setDuration(long mDuration) {
        if(mDuration > 0) {
            this.mDuration = mDuration;
        }else {
            this.mDuration = 1;
        }
        if(mDuration >= 1 * 60 * 60 * 1000) {
            maxMultiple = 30;
            increment = getMax() / 500;
        }else if(mDuration >= 30 * 60 * 1000) {
            maxMultiple = 20;
            increment = getMax() / 500;
        }else if(mDuration >= 10 * 60 * 1000) {
            maxMultiple = 10;
            increment = getMax() / 100;
        }else {
            maxMultiple = 3;
            increment = getMax() / 50;
        }
    }



    /**
     * 设置进度字体颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 调整进度字体的位置 初始位置为图片的正中央
     *
     * @param top
     * @param left
     */
    public void setTextPadding(int top, int left) {
        this.textpaddingleft = left;
        this.textpaddingtop = top;
    }

    /**
     * 调整进图背景图的位置 初始位置为进度条正上方、偏左一半
     *
     * @param top
     * @param left
     */
    public void setImagePadding(int top, int left) {
        this.imagepaddingleft = left;
        this.imagepaddingtop = top;
    }

    private int getBitmapWidth() {
        return (int) Math.ceil(mImgWidth);
    }

    private int getBitmapHeigh() {
        return (int) Math.ceil(mImgHei);
    }

    private float getTextHei() {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.top) + 2;
    }

    public int getTextpaddingleft() {
        return textpaddingleft;
    }

    public int getTextpaddingtop() {
        return textpaddingtop;
    }

    public int getImagepaddingleft() {
        return imagepaddingleft;
    }

    public int getImagepaddingtop() {
        return imagepaddingtop;
    }

    public int getTextsize() {
        return textsize;
    }
}