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
import edu.wustl.arc.utilities.ViewUtil;

import edu.wustl.arc.study.Earnings;

public class EarningsDetailedGoalView extends LinearLayout {

    TextView textViewName;
    TextView textViewDesc;
    TextView textViewValue;
    LinearLayout linearLayout;

    public EarningsDetailedGoalView(Context context, EarningDetails.Goal goal) {
        super(context);
        View view = inflate(context, R.layout.custom_earnings_cycle_details_goal,this);
        linearLayout = view.findViewById(R.id.linearLayout);

        textViewName = view.findViewById(R.id.textViewName);
        textViewDesc = view.findViewById(R.id.textViewDesc);
        textViewValue = view.findViewById(R.id.textViewValue);

        String name = new String();
        switch (goal.name) {
            case Earnings.TEST_SESSION:
                name = ViewUtil.getString(R.string.earnings_details_complete_test_session);
                break;
            case Earnings.TWENTY_ONE_SESSIONS:
                name = ViewUtil.getString(R.string.earnings_details_21sessions);
                break;
            case Earnings.TWO_A_DAY:
                name = ViewUtil.getString(R.string.earnings_details_2aday);
                break;
            case Earnings.FOUR_OUT_OF_FOUR:
                name = ViewUtil.getString(R.string.earnings_details_4of4);
                break;
        }
        textViewName.setText(name);
        textViewValue.setText(goal.amount_earned);
        textViewDesc.setText(goal.value);
        if(goal.count_completed>1){
            String times = ViewUtil.getString(R.string.earnings_details_number_sessions);
            times = ViewUtil.replaceToken(times,R.string.token_number,String.valueOf(goal.count_completed));
            textViewDesc.append(" " + times);
        }

    }

    public void highlight() {
        linearLayout.setBackgroundColor(ViewUtil.getColor(R.color.earningsDetailsBlue));
    }

}
