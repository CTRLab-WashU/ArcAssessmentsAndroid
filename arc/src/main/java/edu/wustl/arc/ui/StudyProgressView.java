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
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import edu.wustl.arc.ui.base.RoundedDrawable;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.utilities.ViewUtil;
import edu.wustl.arc.study.Participant;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestCycle;


public class StudyProgressView extends LinearLayout {

    int currentWeek = 4;
    int weekCount = 12;

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

        Participant participant = Study.getParticipant();
        weekCount = participant.getState().testCycles.size();

        int currentWeek = weekCount;
        boolean isInCycle = false;

        TestCycle cycle = participant.getCurrentTestCycle();
        if(cycle != null) {
            isInCycle = (cycle.getActualStartDate().isBeforeNow() && cycle.getActualEndDate().isAfterNow());
            currentWeek = cycle.getId()+1;

            if(!isInCycle){
                currentWeek--;
            }
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
            if(i==currentWeek-1 && isInCycle){
                params = new LayoutParams(dp8,dp42);
            } else {
                params = new LayoutParams(dp8,dp32);
            }

            if(i==0){
                params.setMargins(0,0,dp2,0);
            } else if(i==weekCount-1){
                params.setMargins(dp2,0,0,0);
            } else {
                params.setMargins(dp2,0,dp2,0);
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
