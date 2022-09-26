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
import android.widget.TextView;

import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/*
    Displays days of week with indicator circle on current day.

    Usage:
        Define in XML:
            <edu.wustl.arc.ui.WeekProgressView
                android:id="@+id/weekProgressView"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>

        Set cycle start date and current day index in Java:
            WeekProgressView weekProgressView = view.findViewById(R.id.weekProgressView);
            weekProgressView.setupView(DateTime startDate, int currentDay);

        Current day of the week will highlight itself
*/

public class WeekProgressView extends LinearProgressView {

    protected DateTime startDate;
    protected int currentDay = 0;
    private int days[] = new int[]{
            R.string.day_abbrev_sun,
            R.string.day_abbrev_mon,
            R.string.day_abbrev_tues,
            R.string.day_abbrev_weds,
            R.string.day_abbrev_thurs,
            R.string.day_abbrev_fri,
            R.string.day_abbrev_sat
    };

    public WeekProgressView(Context context) {
        super(context);
    }

    public WeekProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        backgroundBorderColor = R.color.weekProgressBorder;
        progressColor = R.color.weekProgressFill;
        indicatorColor = R.color.weekProgressFill;
        indicatorTextColor = R.color.white;
        indicatorWidth = ViewUtil.dpToPx(50);
        super.init(context);
    }

    @Override
    protected void initOnMeasure() {
        if(padding == null) {
            indicatorWidth = Math.max(indicatorWidth, unitWidth);
            padding = 0;
            if (indicatorWidth > unitWidth) {
                padding = (indicatorWidth - unitWidth) / 2;
            }
            backgroundLayout.setPadding(padding, 0, padding, 0);
        }

        buildProgressView();
        super.initOnMeasure();
    }

    private void buildProgressView() {
        if (padding == null) {
            return;
        }

        String overlaidProgressTag = "OverlaidProgressTag";
        DateTime day = startDate;

        List<View> viewsToReload = new ArrayList<>();
        for (int i = 0; i < progressLayout.getChildCount(); i++) {
            View v = progressLayout.getChildAt(i);
            if (v.getTag() != null && v.getTag() instanceof String) {
                if (overlaidProgressTag.equals(v.getTag())) {
                    viewsToReload.add(v);
                }
            }
        }
        for (View v : viewsToReload) {
            progressLayout.removeView(v);
        }

        for (int i = 0; i <= currentDay; i++) {
            int dayIndex = getDayIndex(day);
            day = day.plusDays(1);

            int width = backgroundLayout.getChildAt(i).getMeasuredWidth() - padding*2/7;
            LinearLayout.LayoutParams dayTextParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView dayTextView = new TextView(getContext());
            dayTextView.setTag(overlaidProgressTag);
            dayTextView.setLayoutParams(dayTextParams);
            dayTextView.setTypeface(Fonts.robotoBold);
            dayTextView.setTextSize(16);
            dayTextView.setTextColor(ViewUtil.getColor(getContext(), R.color.white));
            dayTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            dayTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            dayTextView.setText(ViewUtil.getString(days[dayIndex]));
            dayTextView.setPadding(0, ViewUtil.dpToPx(5), 0, ViewUtil.dpToPx(5));
            progressLayout.addView(dayTextView);
        }
        progressText = ViewUtil.getString(days[getDayIndex(startDate.plusDays(currentDay))]);
        progressLayout.setPadding(padding, 0, padding, 0);
        resetProgress(progress);
    }

    private void buildView() {
        buildBackgroundView();
        buildProgressView();
    }

    private void buildBackgroundView() {
        if (backgroundLayout.getChildCount() > 2) {
            return; // already built
        }
        backgroundLayout.removeAllViews();
        LinearLayout.LayoutParams dayTextParams =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        dayTextParams.weight = 1;
        DateTime day = startDate;
        for(int i=0;i<7;i++) {
            int dayIndex = getDayIndex(day);
            day = day.plusDays(1);

            TextView dayTextView = new TextView(getContext());
            dayTextView.setLayoutParams(dayTextParams);
            dayTextView.setTypeface(Fonts.robotoBold);
            dayTextView.setTextSize(16);
            dayTextView.setTextColor(ViewUtil.getColor(getContext(), R.color.text));
            dayTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            dayTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            dayTextView.setText(ViewUtil.getString(days[dayIndex]));
            dayTextView.setPadding(0, ViewUtil.dpToPx(5), 0, ViewUtil.dpToPx(5));
            backgroundLayout.addView(dayTextView);
        }
    }

    public void setupView(DateTime startDate, int currentDay) {
        this.startDate = startDate;
        this.currentDay = currentDay;
        this.progress = currentDay;
        this.maxValue = days.length-1;
        buildView();
    }

    private int getDayIndex(DateTime dateTime) {
        int dayIndex = dateTime.getDayOfWeek();
        if(dayIndex==7){
            dayIndex = 0;
        }
        return dayIndex;
    }

}
