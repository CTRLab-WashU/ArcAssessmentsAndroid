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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.ChipFrameLayout;
import com.healthymedium.arc.utilities.ViewUtil;

public class SessionsProgressView extends LinearProgressView {
    public SessionsProgressView(Context context) {
        super(context);
    }

    public SessionsProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SessionsProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        backgroundBorderColor = R.color.sessionProgressBackground;
        backgroundFillColor = R.color.sessionProgressBackground;
        progressColor = R.color.weekProgressFill;
        indicatorColor = R.color.weekProgressFill;
        indicatorTextColor = R.color.white;
        indicatorWidth = ViewUtil.dpToPx(36);

        RelativeLayout.LayoutParams indicatorParams = new RelativeLayout.LayoutParams(indicatorWidth, indicatorWidth);
        indicatorParams.addRule(ALIGN_PARENT_END);
        ChipFrameLayout endNode = new ChipFrameLayout(context);
        endNode.setFillColor(backgroundFillColor);
        endNode.setLayoutParams(indicatorParams);
        addView(endNode);

        super.init(context);
        backgroundLayout.setMinimumHeight(ViewUtil.dpToPx(12));
        progressLayout.setMinimumHeight(ViewUtil.dpToPx(12));
    }

    @Override
    protected void initOnMeasure() {
        progressText = String.valueOf(progress);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(unitWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        for(int i=0; i<=progress; i++) {
            View view = new View(getContext());
            view.setLayoutParams(params);
            progressLayout.addView(view);
        }

        super.initOnMeasure();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        for(int i=0; i<=maxValue; i++) {
            View view = new View(getContext());
            view.setLayoutParams(params);
            backgroundLayout.addView(view);
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
