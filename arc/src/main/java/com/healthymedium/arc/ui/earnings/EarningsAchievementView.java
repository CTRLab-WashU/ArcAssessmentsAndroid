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
import android.view.View;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.RoundedRelativeLayout;
import com.healthymedium.arc.utilities.ViewUtil;

import static com.healthymedium.arc.study.Earnings.FOUR_OUT_OF_FOUR;
import static com.healthymedium.arc.study.Earnings.TEST_SESSION;
import static com.healthymedium.arc.study.Earnings.TWENTY_ONE_SESSIONS;
import static com.healthymedium.arc.study.Earnings.TWO_A_DAY;

public class EarningsAchievementView extends RoundedRelativeLayout {

    TextView textViewName;
    TextView textViewValue;

    public EarningsAchievementView(Context context, EarningOverview.Achievement achievement) {
        super(context);
        View view = inflate(context, R.layout.custom_earnings_achievement,this);

        textViewName = view.findViewById(R.id.textViewName);
        String name = new String();
        switch (achievement.name) {
            case TEST_SESSION:
                name = ViewUtil.getString(R.string.progress_earnings_status1);
                break;
            case TWENTY_ONE_SESSIONS:
                name = ViewUtil.getString(R.string.progress_earnings_status2);
                break;
            case TWO_A_DAY:
                name = ViewUtil.getString(R.string.progress_earnings_status4);
                break;
            case FOUR_OUT_OF_FOUR:
                name = ViewUtil.getString(R.string.progress_earnings_status3);
                break;
        }
        textViewName.setText(name);

        textViewValue = view.findViewById(R.id.textViewValue);
        textViewValue.setText(ViewUtil.getString(R.string.earnings_symbol)+achievement.amount_earned);
    }

}
