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
package edu.wustl.arc.ui.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

import edu.wustl.arc.assessments.R;
import edu.wustl.arc.utilities.ViewUtil;

public class CircleRelativeLayout extends RelativeLayout {

    CircleDrawable background;

    public CircleRelativeLayout(Context context) {
        super(context);
        init(null,0);
    }

    public CircleRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        background = new CircleDrawable();
        setBackground(background);

        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleRelativeLayout, defStyle, 0);

        int fillColor = typedArray.getColor(R.styleable.CircleRelativeLayout_fillColor,0);
        int strokeColor = typedArray.getColor(R.styleable.CircleRelativeLayout_strokeColor,0);
        float dashLength = typedArray.getDimension(R.styleable.CircleRelativeLayout_dashLength,0);
        float dashSpacing = typedArray.getDimension(R.styleable.CircleRelativeLayout_dashSpacing,0);
        int strokeWidth = (int) typedArray.getDimension(R.styleable.CircleRelativeLayout_strokeWidth,0);

        int gradientEnum = (int) typedArray.getInt(R.styleable.CircleRelativeLayout_gradient,-1);
        int gradientColor0 = (int) typedArray.getColor(R.styleable.CircleRelativeLayout_gradientColor0,0);
        int gradientColor1 = (int) typedArray.getColor(R.styleable.CircleRelativeLayout_gradientColor1,0);

        typedArray.recycle();


        if(fillColor!=0){
            background.setFillColor(fillColor);
        }
        if(strokeColor!=0) {
            background.setStrokeColor(strokeColor);
        }
        background.setStrokeWidth(strokeWidth);

        if(dashLength!=0 && dashSpacing!=0){
            background.setStrokeDash(dashLength,dashSpacing);
        }

        if(gradientEnum!=-1 && gradientColor0!=0 && gradientColor1!=0){
            background.setGradient(gradientEnum,gradientColor0,gradientColor1);
        }
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
