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
package edu.wustl.arc.ui.earnings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.utilities.ViewUtil;

public class EarningsTwentyOneSessionsView extends View {

    private float progress = 0;
    private String text = "?";

    private Paint basePaint;
    private Paint progressPaint;
    private Paint textPaint;

    private int dp18;
    private int dp6;
    private float textOffset;

    private int width;
    private int centerHeight;

    public EarningsTwentyOneSessionsView(Context context) {
        super(context);
        init(null,0);
    }

    public EarningsTwentyOneSessionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EarningsTwentyOneSessionsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        int baseColor = ViewUtil.getColor(getContext(),R.color.secondaryAccent);
        int progressColor = ViewUtil.getColor(getContext(),R.color.secondary);
        int white = ViewUtil.getColor(getContext(),R.color.white);

        dp18 = ViewUtil.dpToPx(18);
        dp6 = ViewUtil.dpToPx(6);

        // initialize member variables
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        basePaint.setStrokeCap(Paint.Cap.ROUND);
        basePaint.setStrokeWidth(ViewUtil.dpToPx(12));
        basePaint.setColor(baseColor);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(ViewUtil.dpToPx(12));
        progressPaint.setColor(progressColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTypeface(Fonts.robotoBold);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dp18);
        textPaint.setColor(white);
        textOffset = ((textPaint.descent() + textPaint.ascent()) / 2);

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        centerHeight = dp18;
        setMeasuredDimension(width,dp18*2);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // base
        basePaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(dp6,centerHeight,width-dp18,centerHeight,basePaint);
        basePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width-dp18,centerHeight,dp18,basePaint);

        // progress
        float x = ((width-(2*dp18))*(progress))+dp18;
        progressPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(dp6,centerHeight,x,centerHeight,progressPaint);
        progressPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x,centerHeight,dp18,progressPaint);

        float y = ((canvas.getHeight() / 2) - textOffset);
        canvas.drawText(text,x,y,textPaint);
    }

    public void setProgress(int value) {
        if(value<0){
            value = 0;
        }
        if (value>21){
            value = 21;
        }
        progress = value/21f;
        text = String.valueOf(value);
        invalidate();
    }

}
