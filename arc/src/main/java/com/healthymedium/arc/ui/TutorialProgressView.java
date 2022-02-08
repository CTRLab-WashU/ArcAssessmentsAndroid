/*
  Copyright (c) 2022 Washington University in St. Louis

  Washington University in St. Louis hereby grants to you a non-transferable,
  non-exclusive, royalty-free license to use and copy the computer code
  provided here (the "Software").  You agree to include this license and the
  above copyright notice in all copies of the Software.  The Software may not
  be distributed, shared, or transferred to any third party.  This license does
  not grant any rights or licenses to any other patents, copyrights, or other
  forms of intellectual property owned or controlled by
  Washington University in St. Louis.

  YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED
  "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING
  WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR
  PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER
  THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON
  UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR
  CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE,
  THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT
  OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
*/
package com.healthymedium.arc.ui;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class TutorialProgressView extends View {

    private static final String PROPERTY_PROGRESS = "PROPERTY_PROGRESS";
    private static final String PROPERTY_RADIUS = "PROPERTY_RADIUS";

    private ValueAnimator dotAnimator;
    private ValueAnimator progressAnimator;
    private int progress = 0;

    private Paint baseLinePaint;
    private Paint baseDotPaint;
    private Paint progressLinePaint;
    private Paint progressDotPaint;

    private int doneDotRadius;
    private int doneDotAlpha;
    private int dp16;
    private int dp8;
    private int dp4;

    private int white;
    private int yellow;

    private int width;
    private int height;
    private int centerHeight;

    public TutorialProgressView(Context context) {
        super(context);
        init(null,0);
    }

    public TutorialProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TutorialProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        white = ViewUtil.getColor(getContext(),R.color.white);
        yellow = ViewUtil.getColor(getContext(),R.color.hintDark);

        dp16 = ViewUtil.dpToPx(16);
        dp8 = ViewUtil.dpToPx(8);
        dp4 = ViewUtil.dpToPx(4);

        // initialize member variables
        baseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseLinePaint.setStyle(Paint.Style.STROKE);
        baseLinePaint.setStrokeCap(Paint.Cap.ROUND);
        baseLinePaint.setStrokeWidth(ViewUtil.dpToPx(2));
        baseLinePaint.setColor(white);

        baseDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseDotPaint.setStyle(Paint.Style.FILL);
        baseDotPaint.setColor(white);

        progressLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressLinePaint.setStyle(Paint.Style.FILL);

        progressDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressDotPaint.setStyle(Paint.Style.FILL);
        progressDotPaint.setColor(yellow);

        progressAnimator = new ValueAnimator();
        progressAnimator.addUpdateListener(progressListener);
        progressAnimator.setDuration(400);

        dotAnimator = new ValueAnimator();
        dotAnimator.addUpdateListener(dotListener);
        dotAnimator.setDuration(200);

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        centerHeight = height/2;

        progressLinePaint.setShader(new LinearGradient(0, 0, width, 0, white, yellow, Shader.TileMode.CLAMP));

        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // base
        canvas.drawLine(dp8+dp4,centerHeight,width-dp16,centerHeight,baseLinePaint);
        canvas.drawCircle(width-dp16,centerHeight,dp4,baseDotPaint);

        // progress
        float x = ((width-dp16)*((float)progress/100))+dp8;
        canvas.drawRoundRect(dp8,centerHeight-dp4,x,centerHeight+dp4,dp8,dp8,progressLinePaint);

        if(progress>=100) {
            progressDotPaint.setAlpha(doneDotAlpha);
            canvas.drawCircle(width-dp16,centerHeight,doneDotRadius,progressDotPaint);
        }
    }

    public void setProgress(int value, boolean animate) {
        if(value<0){
            value = 0;
        }
        if (value>100){
            value = 100;
        }
        if(animate) {
            progressAnimator.setValues(PropertyValuesHolder.ofInt(PROPERTY_PROGRESS, progress, value));
            progressAnimator.start();
        } else {
            progress = value;
            doneDotAlpha = 255;
            invalidate();
        }
    }

    ValueAnimator.AnimatorUpdateListener progressListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            progress = (int) animation.getAnimatedValue(PROPERTY_PROGRESS);
            invalidate();
            if(progress==100){
                int radius = ViewUtil.dpToPx(8);
                doneDotAlpha = 0;
                dotAnimator.setValues(
                        PropertyValuesHolder.ofInt(View.ALPHA.getName(), 0,255),
                        PropertyValuesHolder.ofInt(PROPERTY_RADIUS,radius*2,radius));
                dotAnimator.start();
            }
        }
    };

    ValueAnimator.AnimatorUpdateListener dotListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            doneDotRadius = (int) animation.getAnimatedValue(PROPERTY_RADIUS);
            doneDotAlpha = (int) animation.getAnimatedValue(View.ALPHA.getName());
            invalidate();
        }
    };

}
