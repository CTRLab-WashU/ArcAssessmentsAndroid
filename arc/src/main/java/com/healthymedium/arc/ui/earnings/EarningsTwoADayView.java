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
package com.healthymedium.arc.ui.earnings;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.ui.CircleProgressView;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

import java.util.List;

public class EarningsTwoADayView extends LinearLayout {

    public EarningsTwoADayView(Context context, EarningOverview.Goal goal, int cycleIndex) {
        super(context);

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        int normalColor = ViewUtil.getColor(getContext(),R.color.white);
        int highlightColor = ViewUtil.getColor(getContext(),R.color.progressWeekBackground);

        int colors[] = new int[]{normalColor,highlightColor};
        boolean highlight = true;

        int abbreviations[] = new int[]{
                R.string.day_abbrev_sun,
                R.string.day_abbrev_mon,
                R.string.day_abbrev_tues,
                R.string.day_abbrev_weds,
                R.string.day_abbrev_thurs,
                R.string.day_abbrev_fri,
                R.string.day_abbrev_sat
        };

        TestCycle cycle = Study.getParticipant().getState().testCycles.get(cycleIndex);
        DateTime day = cycle.getActualStartDate();

        if(cycleIndex==0){
            day = day.plusDays(1);
        }

        List<Integer> components = goal.progress_components;
        int size = components.size();
        for(int i=0;i<abbreviations.length;i++){

            int dayOfWeek = day.getDayOfWeek();
            if(dayOfWeek==7){
                dayOfWeek = 0;
            }
            day = day.plusDays(1);
            String abbr = ViewUtil.getString(abbreviations[dayOfWeek]);

            int bgColor = colors[highlight ? 1:0];
            highlight = !highlight;

            int progress = 0;
            if(i<size){
                int testCount = components.get(i);
                progress = 50*testCount;
                if(progress>100) {
                    progress = 100;
                }
            }

            DayView dayView = new DayView(context,bgColor,progress,abbr);
            addView(dayView);
        }

    }

    public class DayView extends LinearLayout {

        public DayView(Context context, int bgColor, int progress, String abbr) {
            super(context);

            int dp8 = ViewUtil.dpToPx(8);
            int dp24 = ViewUtil.dpToPx(24);

            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,ViewUtil.dpToPx(72));
            params.weight = 1.0f;
            setLayoutParams(params);

            setBackgroundColor(bgColor);
            setGravity(Gravity.CENTER);
            setOrientation(VERTICAL);

            CircleProgressView progressView = new CircleProgressView(context);
            progressView.setLayoutParams(new LayoutParams(dp24,dp24));
            progressView.setProgress(progress,false);
            progressView.setMargin(dp8,0,dp8,dp8);
            progressView.setStrokeWidth(ViewUtil.dpToPx(1));
            progressView.setBaseColor(R.color.secondaryAccent);
            progressView.setCheckmarkColor(R.color.secondaryAccent);
            progressView.setSweepColor(R.color.secondary);
            addView(progressView);

            TextView textView = new TextView(context);
            textView.setTextColor(ViewUtil.getColor(R.color.secondaryDark));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);
            textView.setTypeface(Fonts.robotoBold);
            textView.setText(abbr);
            textView.setGravity(Gravity.CENTER);
            addView(textView);
        }
    }

}
