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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.wustl.arc.api.models.EarningDetails;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.time.TimeUtil;
import edu.wustl.arc.ui.base.RoundedFrameLayout;
import edu.wustl.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class EarningsDetailedCycleView extends LinearLayout {

    TextView textViewTitle;
    TextView textViewDates;

    LinearLayout goalLayout;
    RoundedFrameLayout ongoingLayout;

    TextView cycleTotal;


    public EarningsDetailedCycleView(Context context, EarningDetails.Cycle cycle) {
        super(context);
        View view = inflate(context, R.layout.custom_earnings_cycle_details,this);

        DateTime start = new DateTime(cycle.start_date*1000L);
        DateTime end = new DateTime(cycle.end_date*1000L);

        boolean isCurrent = (end.isAfterNow()&&start.isBeforeNow());

        textViewTitle = view.findViewById(R.id.textViewTitle);
        if(isCurrent){
            textViewTitle.setText(ViewUtil.getString(R.string.earnings_details_subheader1));
            ongoingLayout = view.findViewById(R.id.ongoingLayout);
            ongoingLayout.setVisibility(VISIBLE);
        }

        String startString = TimeUtil.format(start,R.string.format_date_lo);
        String endString = TimeUtil.format(end,R.string.format_date_lo);
        String dates = ViewUtil.getString(R.string.earnings_details_dates);
        dates = ViewUtil.replaceToken(dates,R.string.token_date1,startString);
        dates = ViewUtil.replaceToken(dates,R.string.token_date2,endString);

        textViewDates = view.findViewById(R.id.textViewDates);
        textViewDates.setText(dates);

        goalLayout = view.findViewById(R.id.goalLayout);
        boolean highlight = false;

        for(EarningDetails.Goal goal : cycle.details) {
            EarningsDetailedGoalView goalView = new EarningsDetailedGoalView(context,goal);
            highlight = !highlight;
            if(highlight){
                goalView.highlight();
            }
            goalLayout.addView(goalView);
        }

        cycleTotal = view.findViewById(R.id.cycleTotal);
        cycleTotal.setText(cycle.total);

    }

}
