package com.example.ce881lab1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class HelloView extends View implements View.OnClickListener {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private static final String TAG = "HelloView";

    //Declared Variables
    private static int backGround = Color.BLACK;
    private static int mixGround = Color.CYAN;
    private static int textColor = Color.YELLOW;
    private static Paint.Style fillStyle =  Paint.Style.FILL;
    private static String helloText = "Hello World";
    private static int textSize = 100;

    public void onClick(View view)
    {
        Log.i(TAG, "clicked");
        Random rand = new Random();
        backGround = Color.rgb(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
        mixGround = Color.rgb(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
        textColor = Color.rgb(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
        postInvalidate();
    }


    public HelloView(Context context) {
        super(context);
        //init(null, 0);
        Log.i(TAG, "invoked constructor 1");
        setOnClickListener(this);
    }

    public HelloView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(attrs, 0);
        Log.i(TAG, "invoked constructor 2");
        setOnClickListener(this);
    }

    public HelloView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //init(attrs, defStyle);
        Log.i(TAG, "invoked constructor 3");
        setOnClickListener(this);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.HelloView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.HelloView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.HelloView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.HelloView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.HelloView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.HelloView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        //fill view with background color
        Paint bg = new Paint();
        bg.setColor(backGround);
        bg.setStyle(fillStyle);
        bg.setShader(new RadialGradient(contentWidth / 2f, contentHeight / 2f, 50f, backGround, mixGround, Shader.TileMode.MIRROR));
        canvas.drawRect(0,0, getWidth(),getHeight(),bg);

        //draw text
        Paint textPaint = new Paint();
        textPaint.setColor(textColor);

        textPaint.setTextSize(Math.max(canvas.getHeight(), canvas.getWidth())*0.1f);
        textPaint.setAntiAlias(true);
       // textPaint.setTypeface(resou);

        mTextWidth = textPaint.measureText(helloText);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;

        float x = paddingLeft + (contentWidth - mTextWidth) /2;
        float y =  paddingTop + (contentHeight + mTextHeight) / 2;

        canvas.drawText(helloText, x,y,textPaint);

        /*// TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }*/
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
