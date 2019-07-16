package com.healthymedium.arc.custom;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class CircleProgressView extends View {

    private static final String PROPERTY_PROGRESS = "PROPERTY_PROGRESS";

    private ValueAnimator checkmarkAnimator;
    private Drawable checkmark;
    private int checkmarkAlpha;
    private float checkmarkY;

    private ValueAnimator progressAnimator;
    private int progress = 0;

    private float startAngle = 270F;
    private float sweepAngle;
    private float radius;
    private RectF rect;

    private Paint basePaint;
    private Paint shadowPaint;
    private Paint sweepPaint;
    private Paint fillPaint;

    private float strokeWidth;
    private boolean manualStrokeWidth;
    private int size;

    public CircleProgressView(Context context) {
        super(context);
        init(null,0);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        Context context = getContext();

        // initialize member variables
        manualStrokeWidth = false;
        rect = new RectF(0,0,0,0);

        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setStyle(Paint.Style.STROKE);
        basePaint.setColor(ViewUtil.getColor(context, R.color.secondary));

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setStrokeCap(Paint.Cap.ROUND);
        shadowPaint.setColor(ViewUtil.getColor(context, R.color.white));

        sweepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sweepPaint.setStyle(Paint.Style.STROKE);
        sweepPaint.setStrokeCap(Paint.Cap.ROUND);
        sweepPaint.setColor(ViewUtil.getColor(context, R.color.secondaryAccent));

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fillPaint.setColor(ViewUtil.getColor(context, R.color.secondaryAccent));

        progressAnimator = new ValueAnimator();
        progressAnimator.addUpdateListener(progressListener);
        progressAnimator.setDuration(400);

        checkmarkAnimator = new ValueAnimator();
        checkmarkAnimator.addUpdateListener(checkmarkListener);
        checkmarkAnimator.setDuration(200);

        checkmark = ViewUtil.getDrawable(context, R.drawable.ic_checkmark_blue);

        //
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressView, defStyle, 0);

        int baseColor = typedArray.getColor(R.styleable.CircleProgressView_baseColor,0);
        int sweepColor = typedArray.getColor(R.styleable.CircleProgressView_sweepColor,0);
        int shadowColor = typedArray.getColor(R.styleable.CircleProgressView_shadowColor,0);
        int checkmarkColor = typedArray.getColor(R.styleable.CircleProgressView_checkmarkColor,0);
        strokeWidth = (int) typedArray.getDimension(R.styleable.CircleProgressView_strokeWidth,0);

        typedArray.recycle();

        if(strokeWidth!=0){
            manualStrokeWidth = true;
            basePaint.setStrokeWidth(strokeWidth);
            shadowPaint.setStrokeWidth(strokeWidth);
            sweepPaint.setStrokeWidth(strokeWidth);
            fillPaint.setStrokeWidth(strokeWidth);
        }

        if(baseColor!=0) {
            basePaint.setColor(baseColor);
        }

        if(shadowColor!=0) {
            shadowPaint.setColor(shadowColor);
        }

        if(sweepColor!=0){
            sweepPaint.setColor(sweepColor);
            fillPaint.setColor(sweepColor);
        }

        if(checkmarkColor!=0){
            checkmark.setTint(checkmarkColor);
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        size = Math.min(width,height);

        if(!manualStrokeWidth) {
            strokeWidth = size * 0.0625f; // 1/16 of size
            basePaint.setStrokeWidth(strokeWidth);
            shadowPaint.setStrokeWidth(strokeWidth);
            sweepPaint.setStrokeWidth(strokeWidth);
            fillPaint.setStrokeWidth(strokeWidth);
        }

        // create a rect that's small enough that the stroke isn't cut off
        float offset = (strokeWidth/2);
        rect.set(offset,offset,size-offset,size-offset);
        radius = (size-strokeWidth)/2;

        int halfSize = size/2;
        int quarterSize = size/4;
        int checkmarkWidth = checkmark.getIntrinsicWidth();
        int checkmarkHeight = checkmark.getIntrinsicHeight();
        int newWidth = (int)(((float)checkmarkWidth / checkmarkHeight) * halfSize);
        int halfNewWidth = (int)((float)newWidth/2);

        checkmark.setBounds(halfSize-halfNewWidth,quarterSize,halfSize+halfNewWidth,3*quarterSize);
        checkmarkY = quarterSize;

        setMeasuredDimension(size,size);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(progress==0) {
            canvas.drawCircle(rect.centerX(),rect.centerY(),radius,basePaint);
            return;
        }

        if(progress<100) {
            canvas.drawCircle(rect.centerX(),rect.centerY(),radius,basePaint);
            canvas.drawArc(rect, startAngle, sweepAngle, false, shadowPaint);
            canvas.drawArc(rect, startAngle+1f, sweepAngle-2f, false, sweepPaint);
            return;
        }

        canvas.drawCircle(rect.centerX(),rect.centerY(),radius,fillPaint);
        checkmark.setAlpha(checkmarkAlpha);
        Rect rect = checkmark.getBounds();
        rect.top = (int) checkmarkY;
        rect.bottom = rect.top+(size/2);
        checkmark.setBounds(rect);
        checkmark.draw(canvas);
    }

    public void setProgress(int value, boolean animate) {
        if(animate) {
            progressAnimator.setValues(PropertyValuesHolder.ofInt(PROPERTY_PROGRESS, 0, value));
            progressAnimator.start();
        } else {
            progress = value;
            checkmarkAlpha = 255;
            invalidate();

        }
    }

    public void setStrokeWidth(int dp){
        manualStrokeWidth = true;
        strokeWidth = ViewUtil.dpToPx(dp); // 1/16 of size

        basePaint.setStrokeWidth(strokeWidth);
        shadowPaint.setStrokeWidth(strokeWidth);
        sweepPaint.setStrokeWidth(strokeWidth);
        fillPaint.setStrokeWidth(strokeWidth);
    }

    ValueAnimator.AnimatorUpdateListener progressListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            progress = (int) animation.getAnimatedValue(PROPERTY_PROGRESS);
            sweepAngle = (360f/100)*(progress); // scale for degress of circle
            invalidate();
            if(progress==100){
                checkmarkAlpha = 0;
                checkmarkAnimator.setValues(
                        PropertyValuesHolder.ofInt(View.ALPHA.getName(), 0,255),
                        PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,size/2,size/4));
                checkmarkAnimator.start();
            }
        }
    };

    ValueAnimator.AnimatorUpdateListener checkmarkListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            checkmarkY = (float) animation.getAnimatedValue(View.TRANSLATION_Y.getName());
            checkmarkAlpha = (int) animation.getAnimatedValue(View.ALPHA.getName());
            invalidate();
        }
    };

}
