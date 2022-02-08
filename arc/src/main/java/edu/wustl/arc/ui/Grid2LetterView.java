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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.ui.base.CircleFrameLayout;
import edu.wustl.arc.utilities.ViewUtil;

public class Grid2LetterView extends CircleFrameLayout {

    boolean selected = false;

    String text = "E";
    float textHeight;
    float textSize;

    int defaultSize = ViewUtil.dpToPx(48);
    int viewSize;

    Paint paint;

    public Grid2LetterView(Context context) {
        super(context);
        init(null,0);
    }

    public Grid2LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Grid2LetterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setFillColor(R.color.white);

        paint = new Paint();
        paint.setColor(ViewUtil.getColor(getContext(), R.color.primaryButtonDark));
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Fonts.georgia);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
    }

    public void setF(){
        text = "F";
        invalidate();
    }

    public boolean isF(){
        return text.equals("F");
    }

    public boolean isSelected(){
        return selected;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            viewSize = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            viewSize = Math.max(defaultSize, widthSize);
        } else {
            viewSize = defaultSize;
        }

        setMeasuredDimension(viewSize,viewSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        textHeight = fm.bottom - fm.top + fm.leading;

        textSize = 0.75f*viewSize;
        paint.setTextSize(textSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
           return true;
        }
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN) {
            selected = !selected;
            setFillColor(R.color.primaryButtonDark);
            paint.setColor(ViewUtil.getColor(getContext(), R.color.white));
            invalidate();
        }
        if(action == MotionEvent.ACTION_UP) {
            int color = selected ? R.color.accent : R.color.white;
            setFillColor(color);
            paint.setColor(ViewUtil.getColor(getContext(), R.color.primaryButtonDark));
            invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = canvas.getClipBounds();
        canvas.drawText(text,rect.centerX(),rect.centerY()*1.5f,paint);
    }

}
