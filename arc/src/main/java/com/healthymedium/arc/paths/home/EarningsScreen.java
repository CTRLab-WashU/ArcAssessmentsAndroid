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
package com.healthymedium.arc.paths.home;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.api.models.EarningOverview;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.informative.EarningsDetailsScreen;
import com.healthymedium.arc.paths.informative.FAQScreen;
import com.healthymedium.arc.study.Earnings;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.ParticipantState;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.study.TestDay;
import com.healthymedium.arc.study.TestSession;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.earnings.EarningsGoalView;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class EarningsScreen extends BaseFragment {

    SwipeRefreshLayout refreshLayout;

    TextView headerText;
    TextView weeklyTotal;
    TextView studyTotal;
    TextView lastSync;
    LinearLayout goalLayout;

    TextView weeklyTotalLabel;
    TextView studyTotalLabel;

    TextView earningsBody1;
    Button viewDetailsButton;
    TextView bonusHeader;
    TextView bonusBody;
    Button viewFaqButton;

    public EarningsScreen() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);

        Participant participant = Study.getParticipant();
        TestCycle testCycle = participant.getCurrentTestCycle();
        TestDay testDay = participant.getCurrentTestDay();
        TestSession testSession = participant.getCurrentTestSession();

        ParticipantState state = participant.getState();
        int sessionIndex = state.currentTestSession-1;
        int dayIndex = state.currentTestDay;
        int cycleIndex = state.currentTestCycle;

        boolean isPractice = false;

        if(testCycle != null) {
            if (testDay.getStartTime().isAfterNow()) {
                if (sessionIndex < 0) {
                    dayIndex--;
                    if (dayIndex < 0) {
                        cycleIndex--;
                        testCycle = state.testCycles.get(cycleIndex);
                        dayIndex = testCycle.getNumberOfTestDays() - 1;
                    }
                    testDay = testCycle.getTestDay(dayIndex);
                    sessionIndex = testDay.getNumberOfTests() - 1;
                    testSession = testDay.getTestSession(sessionIndex);
                }
            }
            isPractice = (dayIndex==0 && sessionIndex==0 && cycleIndex==0);
        }

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnabled(false);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        String body = new String();
        if(isPractice){
            body += ViewUtil.getString(R.string.earnings_body0);
        }else {
            body += ViewUtil.getString(R.string.earnings_body1);
        }

        headerText = view.findViewById(R.id.textViewHeader);
        headerText.setTypeface(Fonts.robotoMedium);
        headerText.setText(ViewUtil.getString(R.string.resources_nav_earnings));

        earningsBody1 = view.findViewById(R.id.earningsBody1);
        earningsBody1.setText(Html.fromHtml(body));

        viewDetailsButton = view.findViewById(R.id.viewDetailsButton);
        viewDetailsButton.setText(ViewUtil.getString(R.string.button_viewdetails));
        viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EarningsDetailsScreen earningsDetailsScreen = new EarningsDetailsScreen();
                NavigationManager.getInstance().open(earningsDetailsScreen);
            }
        });

        bonusHeader = view.findViewById(R.id.bonusGoalsHeader);
        bonusHeader.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_bonus_header)));
        bonusHeader.setTypeface(Fonts.robotoMedium);

        bonusBody = view.findViewById(R.id.bonusBody);
        bonusBody.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_bonus_body)));
        ViewUtil.setLineHeight(bonusBody,26);

        viewFaqButton = view.findViewById(R.id.viewFaqButton);
        viewFaqButton.setText(ViewUtil.getString(R.string.button_viewfaq));
        viewFaqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQScreen faqScreen = new FAQScreen();
                NavigationManager.getInstance().open(faqScreen);
            }
        });

        goalLayout = view.findViewById(R.id.goalLayout);
        weeklyTotal = view.findViewById(R.id.weeklyTotal);
        weeklyTotal.setTypeface(Fonts.robotoMedium);
        studyTotal = view.findViewById(R.id.studyTotal);
        studyTotal.setTypeface(Fonts.robotoMedium);
        lastSync = view.findViewById(R.id.textViewLastSync);

        weeklyTotalLabel = view.findViewById(R.id.weeklyTotalLabel);
        studyTotalLabel = view.findViewById(R.id.studyTotalLabel);

        weeklyTotalLabel.setText(ViewUtil.getString(R.string.earnings_weektotal));
        studyTotalLabel.setText(ViewUtil.getString(R.string.earnings_studytotal));

        if(Study.getParticipant().getEarnings().hasCurrentOverview()){
            populateViews();
        } else {
            refresh();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
        refreshLayout.setProgressViewOffset(true, 0,top+ViewUtil.dpToPx(16));
    }

    private void refresh() {
        if (!Study.getRestClient().isUploadQueueEmpty()){
            refreshLayout.setRefreshing(false);
            if(getContext()!=null) {
                populateViews();
            }
            return;
        }

        refreshLayout.setRefreshing(true);

        Study.getParticipant().getEarnings().refreshOverview(new Earnings.Listener() {
            @Override
            public void onSuccess() {
                if(refreshLayout!=null) {
                    refreshLayout.setRefreshing(false);
                    Study.getParticipant().save();
                    if(getContext()!=null) {
                        populateViews();
                    }
                }
            }

            @Override
            public void onFailure() {
                if(refreshLayout!=null) {
                    refreshLayout.setRefreshing(false);
                    if(getContext()!=null) {
                        populateViews();
                    }
                }
            }
        });
    }

    private void populateViews() {
        EarningOverview overview = Study.getParticipant().getEarnings().getOverview();
        if(overview==null){
            return;
        }

        weeklyTotal.setText(overview.cycle_earnings);
        studyTotal.setText(overview.total_earnings);
        lastSync.setVisibility(View.GONE);

        goalLayout.removeAllViews();
        for(EarningOverview.Goal goal : overview.goals) {
            // Hide the earnings for every test session, as they aren't true goals
            if (!goal.name.equals(Earnings.TEST_SESSION)) {
                goalLayout.addView(new EarningsGoalView(getContext(),
                        goal, overview.cycle, false));
            }
        }
    }

}
