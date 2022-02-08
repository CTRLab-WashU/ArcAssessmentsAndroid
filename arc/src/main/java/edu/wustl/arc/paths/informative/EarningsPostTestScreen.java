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
package edu.wustl.arc.paths.informative;

import android.os.Bundle;
import androidx.annotation.Nullable;
import edu.wustl.arc.ui.Button;
import edu.wustl.arc.ui.TotalEarningsView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.wustl.arc.api.models.EarningOverview;
import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.misc.TransitionSet;
import edu.wustl.arc.study.Earnings;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.ui.earnings.EarningsAchievementView;
import edu.wustl.arc.ui.earnings.EarningsGoalView;
import edu.wustl.arc.utilities.ViewUtil;

public class EarningsPostTestScreen extends BaseFragment {

    EarningOverview overview;

    TotalEarningsView weeklyTotal;
    TotalEarningsView studyTotal;
    LinearLayout goalLayout;
    LinearLayout achievementLayout;
    ImageView imageViewConfetti;

    public EarningsPostTestScreen() {
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings_post_test, container, false);

        imageViewConfetti = view.findViewById(R.id.imageViewConfetti);
        imageViewConfetti.setAlpha(0f);
        imageViewConfetti.animate().translationYBy(-200);

        goalLayout = view.findViewById(R.id.goalLayout);
        goalLayout.setAlpha(0f);

        TextView textViewHeader3 = view.findViewById(R.id.textViewHeader3);
        textViewHeader3.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_bonus_body)));

        Earnings earnings = Study.getParticipant().getEarnings();
        overview = earnings.getOverview();
        if(overview==null){
            overview = EarningOverview.getTestObject();
        }

        weeklyTotal = view.findViewById(R.id.weeklyTotal);
        weeklyTotal.setText(earnings.getPrevWeeklyTotal(),false);

        studyTotal = view.findViewById(R.id.studyTotal);
        studyTotal.setText(earnings.getPrevStudyTotal(),false);

        achievementLayout = view.findViewById(R.id.achievementLayout);
        for(EarningOverview.Achievement achievement : overview.new_achievements) {
            achievementLayout.addView(new EarningsAchievementView(getContext(),achievement));
        }

        for(EarningOverview.Goal goal : overview.goals) {
            // Hide the earnings for every test session, as they aren't true goals
            if (!goal.name.equals(Earnings.TEST_SESSION)) {
                boolean showCompletionCollapsed = (goal.completed && !overview.hasAchievementFor(goal));
                goalLayout.addView(new EarningsGoalView(getContext(), goal, overview.cycle, showCompletionCollapsed));
            }
        }

        Button button = view.findViewById(R.id.buttonNext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Study.openNextFragment();
            }
        });

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        weeklyTotal.setText(overview.cycle_earnings,true);
        studyTotal.setText(overview.total_earnings, true, new TotalEarningsView.Listener() {
            @Override
            public void onFinished() {
                goalLayout.animate().alpha(1.0f);
            }
        });
        imageViewConfetti.animate().translationYBy(200).setDuration(1000);
        imageViewConfetti.animate().alpha(1.0f).setDuration(1000);


    }
}
