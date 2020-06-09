package com.healthymedium.arc.ui.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class RoundedLinearLayout extends LinearLayout {

    RoundedDrawable background;

    public RoundedLinearLayout(Context context) {
        super(context);
        init(null,0);
    }

    public RoundedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RoundedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        background = new RoundedDrawable();
        setBackground(background);

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedLinearLayout, defStyle, 0);

        int fillColor = typedArray.getColor(R.styleable.RoundedLinearLayout_fillColor,0);
        int strokeColor = typedArray.getColor(R.styleable.RoundedLinearLayout_strokeColor,0);
        float dashLength = typedArray.getDimension(R.styleable.RoundedLinearLayout_dashLength,0);
        float dashSpacing = typedArray.getDimension(R.styleable.RoundedLinearLayout_dashSpacing,0);
        int strokeWidth = (int) typedArray.getDimension(R.styleable.RoundedLinearLayout_strokeWidth,0);
        
        int radius = (int) typedArray.getDimension(R.styleable.RoundedLinearLayout_radius,0);
        int radiusTopLeft = (int) typedArray.getDimension(R.styleable.RoundedLinearLayout_radiusTopLeft,0);
        int radiusTopRight = (int) typedArray.getDimension(R.styleable.RoundedLinearLayout_radiusTopRight,0);
        int radiusBottomLeft = (int) typedArray.getDimension(R.styleable.RoundedLinearLayout_radiusBottomLeft,0);
        int radiusBottomRight = (int) typedArray.getDimension(R.styleable.RoundedLinearLayout_radiusBottomRight,0);

        int gradientEnum = (int) typedArray.getInt(R.styleable.RoundedLinearLayout_gradient,-1);
        int gradientColor0 = (int) typedArray.getColor(R.styleable.RoundedLinearLayout_gradientColor0,0);
        int gradientColor1 = (int) typedArray.getColor(R.styleable.RoundedLinearLayout_gradientColor1,0);

        typedArray.recycle();


        if(fillColor!=0){
            background.setFillColor(fillColor);
        }
        if(strokeColor!=0) {
            background.setStrokeColor(strokeColor);
        }
        background.setStrokeWidth(strokeWidth);

        if(radius!=0){
            background.setRadius(radius);
        } else {
            background.setRadius(radiusTopLeft,radiusTopRight,radiusBottomLeft,radiusBottomRight);
        }

        if(dashLength!=0 && dashSpacing!=0){
            background.setStrokeDash(dashLength,dashSpacing);
        }

        if(gradientEnum!=-1 && gradientColor0!=0 && gradientColor1!=0){
            background.setGradient(gradientEnum,gradientColor0,gradientColor1);
        }
    }

    public void setRadius(int dp) {
        background.setRadius(ViewUtil.dpToPx(dp));
    }

    public void setFillColor(@ColorRes int color) {
        background.setFillColor(ViewUtil.getColor(getContext(),color));
    }

    public void setFillColor(String colorString) {
        background.setFillColor(Color.parseColor(colorString));
    }

    public void setStrokeColor(@ColorRes int color) {
        background.setStrokeColor(ViewUtil.getColor(getContext(),color));
    }

    public void setStrokeColor(String colorString) {
        background.setStrokeColor(Color.parseColor(colorString));
    }

    public void setStrokeWidth(int dp) {
        background.setStrokeWidth(ViewUtil.dpToPx(dp));
    }

    public void setStrokeDash(int dpLength, int dpSpacing) {
        int length = ViewUtil.dpToPx(dpLength);
        int spacing = ViewUtil.dpToPx(dpSpacing);
        background.setStrokeDash(length,spacing);
    }

    public void setHorizontalGradient(@ColorRes int colorLeft, @ColorRes int colorRight) {
        int left = ViewUtil.getColor(getContext(),colorLeft);
        int right = ViewUtil.getColor(getContext(),colorRight);
        background.setGradient(SimpleGradient.LINEAR_HORIZONTAL,left,right);
    }

    public void setVerticalGradient(@ColorRes int colorTop, @ColorRes int colorBottom) {
        int top = ViewUtil.getColor(getContext(),colorTop);
        int bottom = ViewUtil.getColor(getContext(),colorBottom);
        background.setGradient(SimpleGradient.LINEAR_VERTICAL,top,bottom);
    }

    public void removeStrokeDash() {
        background.removeStrokeDash();
    }

    public void refresh(){
        background.invalidate();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setOutlineProvider(background.getOutlineProvider());
    }

    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return background.getOutlineProvider();
    }

}
