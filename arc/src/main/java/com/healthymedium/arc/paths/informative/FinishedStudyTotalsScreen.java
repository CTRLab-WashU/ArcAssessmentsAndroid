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
package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.api.models.StudySummary;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.utilities.ViewUtil;

public class FinishedStudyTotalsScreen extends BaseFragment {

    public static boolean DEBUG_SHOW_TOTAL = true;

    ProgressBar progressBar;

    LinearLayout linearLayoutResults;
    TextView textViewTotalEarned;
    TextView textViewTotalTests;
    TextView textViewTotalDays;
    TextView textViewTotalGoals;

    TextView textViewError;
    Button button;

    public FinishedStudyTotalsScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finished_study_totals, container, false);

        // get inflated views ----------------------------------------------------------------------
        TextView header = view.findViewById(R.id.textViewHeader);
        header.setTypeface(Fonts.robotoMedium);

        TextView textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        ViewUtil.setLineHeight(textViewSubHeader,26);

        progressBar = view.findViewById(R.id.progressBar);

        linearLayoutResults = view.findViewById(R.id.linearLayoutResults);
        textViewTotalEarned = view.findViewById(R.id.textViewTotalEarned);
        textViewTotalTests = view.findViewById(R.id.textViewTotalTests);
        textViewTotalDays = view.findViewById(R.id.textViewTotalDays);
        textViewTotalGoals = view.findViewById(R.id.textViewTotalGoals);

        textViewError = view.findViewById(R.id.textViewError);
        ViewUtil.setLineHeight(textViewError,26);

        button = view.findViewById(R.id.button);

        // -----------------------------------------------------------------------------------------

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
        progressBar.animate().alpha(1.0f).setDuration(300);

        if(Config.REST_BLACKHOLE) {
            if(DEBUG_SHOW_TOTAL){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StudySummary summary = new StudySummary();
                        summary.days_tested = 168;
                        summary.goals_met = 13;
                        summary.tests_taken = 302;
                        summary.total_earnings = "$115.75";
                        showTotals(summary);
                    }
                },2000);
            }
            else{
                showError();
            }

        }

        Study.getRestClient().getStudySummary(new RestClient.Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                if(!response.successful) {
                    showError();
                }
                StudySummary summary = response.getOptionalAs("summary", StudySummary.class);
                if(summary!=null) {
                    showTotals(summary);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(RestResponse response) {
                showError();
            }
        });

    }

    private void showTotals(StudySummary summary) {
        textViewTotalEarned.setText(summary.total_earnings);
        textViewTotalTests.setText(String.valueOf(summary.tests_taken));
        textViewTotalDays.setText(String.valueOf(summary.days_tested));
        textViewTotalGoals.setText(String.valueOf(summary.goals_met));

        progressBar.setVisibility(View.GONE);
        linearLayoutResults.setVisibility(View.VISIBLE);
        linearLayoutResults.animate().alpha(1.0f).setDuration(300);
        button.setEnabled(true);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        textViewError.setVisibility(View.VISIBLE);
        textViewError.animate().alpha(1.0f).setDuration(300);
        button.setEnabled(true);
    }

}
