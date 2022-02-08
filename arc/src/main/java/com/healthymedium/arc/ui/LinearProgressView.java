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

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.ui.base.ChipFrameLayout;
import com.healthymedium.arc.ui.base.ChipLinearLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class LinearProgressView extends RelativeLayout {

    protected int progress = 0;
    protected int indicatorWidth;
    protected Integer padding;
    protected int totalWidth;
    protected int unitWidth;
    protected int maxValue;

    protected ChipLinearLayout backgroundLayout;
    protected ChipLinearLayout progressLayout;
    protected ChipFrameLayout indicatorLayout;
    protected TextView indicatorTextView;

    protected int backgroundBorderColor;
    protected Integer backgroundFillColor;
    protected int progressColor;
    protected int indicatorColor;
    protected int indicatorTextColor;
    protected String progressText;

    protected boolean initialized = false;

    public LinearProgressView(Context context) {
        super(context);
        init(context);
    }

    public LinearProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LinearProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        RelativeLayout.LayoutParams matchWrapParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams wrapWrapParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        matchWrapParams.addRule(RelativeLayout.CENTER_VERTICAL);
        wrapWrapParams.addRule(RelativeLayout.CENTER_VERTICAL);

        backgroundLayout = new ChipLinearLayout(context);
        backgroundLayout.setLayoutParams(matchWrapParams);
        backgroundLayout.setOrientation(LinearLayout.HORIZONTAL);
        backgroundLayout.setStrokeColor(backgroundBorderColor);
        backgroundLayout.setStrokeWidth(1);
        if(backgroundFillColor != null) {
            backgroundLayout.setFillColor(backgroundFillColor);
        }

        progressLayout = new ChipLinearLayout(context);
        progressLayout.setLayoutParams(wrapWrapParams);
        progressLayout.setOrientation(LinearLayout.HORIZONTAL);
        progressLayout.setFillColor(progressColor);
        progressLayout.setStrokeColor(progressColor);
        progressLayout.setStrokeWidth(1);

        addView(backgroundLayout);

        indicatorLayout = new ChipFrameLayout(context);
        indicatorLayout.setFillColor(indicatorColor);

        indicatorTextView = new TextView(context);
        indicatorTextView.setTypeface(Fonts.robotoBold);
        indicatorTextView.setTextSize(16);
        indicatorTextView.setTextColor(ViewUtil.getColor(getContext(), indicatorTextColor));
        indicatorTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        indicatorTextView.setGravity(Gravity.CENTER_VERTICAL);

        indicatorLayout.addView(indicatorTextView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalWidth = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            totalWidth = widthSize;
        }

        if(totalWidth > 0 && !initialized) {
            unitWidth = backgroundLayout.getChildAt(0).getMeasuredWidth();
            initOnMeasure();
        }
    }

    protected void initOnMeasure(){
        addView(progressLayout);

        if(progress >= 0) {
            float percent = ((float)progress/(float)maxValue);
            float centerX = totalWidth * percent;
            centerX -= indicatorWidth * percent;

            FrameLayout.LayoutParams indicatorParams = new FrameLayout.LayoutParams(indicatorWidth, indicatorWidth);
            indicatorLayout.setLayoutParams(indicatorParams);
            indicatorTextView.setLayoutParams(indicatorParams);
            indicatorTextView.setText(progressText);
            indicatorLayout.setX(centerX);
            addView(indicatorLayout);
        }

        initialized = true;
    }
}
