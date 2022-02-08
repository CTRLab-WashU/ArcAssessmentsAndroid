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
package edu.wustl.arc.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;

import edu.wustl.arc.assessments.R;
import edu.wustl.arc.ui.base.RoundedLinearLayout;
import edu.wustl.arc.utilities.ViewUtil;

public class Grid2BoxView extends RoundedLinearLayout {

    int defaultWidth = ViewUtil.dpToPx(60);
    int defaultHeight = ViewUtil.dpToPx(80);

    ImageView imageView;
    int imageResource = 0;

    boolean selected = false;
    boolean selectable = true;
    long timestampSelect = 0;
    long timestampImage = 0;

    int fillColorNormal = R.color.gridNormal;
    int fillColorSelected = R.color.white;

    int strokeColorNormal = R.color.grid2NormalBorder;
    int strokeWidthNormal = 1;

    int strokeColorSelected = R.color.grid2SelectedBorder;
    int strokeWidthSelected = 4;

    Listener listener;

    public Grid2BoxView(Context context) {
        super(context);
        init(context);
    }

    public Grid2BoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Grid2BoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setGravity(Gravity.CENTER);
        setRadius(8);

        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(imageView);

        showSelectedState(false);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public long getTimestampSelect() {
        return timestampSelect;
    }

    public long getTimestampImage() {
        return timestampImage;
    }

    public void setImage(@DrawableRes int id){
        timestampImage = System.currentTimeMillis();

        Drawable drawable = ViewUtil.getDrawable(id);
        imageView.setImageDrawable(drawable);
        imageResource = id;
    }

    public void removeImage(){
        timestampImage = 0;

        imageView.setImageDrawable(null);
        imageResource = 0;
    }

    public int getImage(){
        return imageResource;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if(this.selected==selected) {
            return;
        }
        this.selected = selected;
        timestampSelect = selected ? System.currentTimeMillis():0;
        showSelectedState(selected);
    }

    private void showSelectedState(boolean selected) {
        if(selected) {
            setFillColor(fillColorSelected);
            setStrokeColor(strokeColorSelected);
            setStrokeWidth(strokeWidthSelected);
            setElevation(ViewUtil.dpToPx(2));
        } else {
            setFillColor(fillColorNormal);
            setStrokeColor(strokeColorNormal);
            setStrokeWidth(strokeWidthNormal);
            setElevation(0);
        }
        if(isAttachedToWindow()) {
            refresh();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN && selectable) {
            if(listener!=null) {
                listener.onSelected(this);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int width;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.max(defaultWidth, widthSize);
        } else {
            width = defaultWidth;
        }

        int height = (int) (width*1.3333);

        setMeasuredDimension(width,height);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);

        int childCount = getChildCount();
        for(int i=0;i<childCount;i++) {
            getChildAt(i).measure(widthMeasureSpec,heightMeasureSpec);
        }
    }

    public interface Listener {
        void onSelected(Grid2BoxView view);
    }

}
