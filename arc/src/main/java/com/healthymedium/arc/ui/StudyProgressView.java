package com.healthymedium.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.healthymedium.arc.ui.base.RoundedDrawable;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class StudyProgressView extends LinearLayout {

    int currentWeek;
    int weekCount;

    int dp4;
    int dp8;
    int dp32;
    int dp42;

    public StudyProgressView(Context context) {
        super(context);
        init(null,0);
    }

    public StudyProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public StudyProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        Context context = getContext();
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        // init dummy values
        currentWeek = 4;
        weekCount = 12;

        if(!isInEditMode()){
            // Todo: actually get data
        }

        int dp1 = ViewUtil.dpToPx(1);
        int dp2 = ViewUtil.dpToPx(2);

        dp4 = ViewUtil.dpToPx(4);
        dp8 = ViewUtil.dpToPx(8);
        dp32 = ViewUtil.dpToPx(32);
        dp42 = ViewUtil.dpToPx(42);

        int color = ViewUtil.getColor(getContext(),R.color.secondaryAccent);

        for(int i=0;i<weekCount;i++){
            RoundedDrawable drawable = new RoundedDrawable();
            drawable.setRadius(dp4);
            drawable.setStrokeColor(color);
            drawable.setStrokeWidth(dp1);

            if(i<currentWeek){
                drawable.setFillColor(color);
            }

            View view = new View(context);
            view.setBackground(drawable);

            LayoutParams params;
            if(i==currentWeek-1){
                params = new LayoutParams(dp8,dp42);
            } else {
                params = new LayoutParams(dp8,dp32);
            }

            if(i==0){
                params.setMargins(0,0,dp4,0);
            } else if(i==weekCount-1){
                params.setMargins(dp4,0,0,0);
            } else {
                params.setMargins(dp4,0,dp4,0);
            }

            view.setLayoutParams(params);
            addView(view);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int blockWidth = ((width-((weekCount-1)*dp8))/weekCount);

        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View view = getChildAt(i);
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.width = blockWidth;
            view.setLayoutParams(params);
        }

        setMeasuredDimension(width,dp42);

    }

    public void refresh(){
        invalidate();
    }

}