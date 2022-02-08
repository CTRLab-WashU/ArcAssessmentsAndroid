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
package edu.wustl.arc.paths.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import edu.wustl.arc.hints.HintHighlighter;
import edu.wustl.arc.hints.HintPointer;
import edu.wustl.arc.hints.Hints;
import edu.wustl.arc.time.TimeUtil;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.wustl.arc.core.Application;
import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.paths.informative.ChangeAvailabilityScreen;
import edu.wustl.arc.study.TestDay;
import edu.wustl.arc.study.TestSession;
import edu.wustl.arc.ui.BottomNavigationView;
import edu.wustl.arc.ui.Button;

import edu.wustl.arc.assessments.R;
import edu.wustl.arc.study.Participant;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestCycle;
import edu.wustl.arc.utilities.Phrase;
import edu.wustl.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@SuppressLint("ValidFragment")
public class HomeScreen extends BaseFragment {

    public static final String HINT_TOUR = "HINT_TOUR";
    public static final String HINT_POST_BASELINE = "HINT_POST_BASELINE";
    public static final String HINT_POST_PAID_TEST = "HINT_POST_PAID_TEST";
    public static final String HINT_FIRST_TEST = "HINT_FIRST_TEST";


    String stringHeader;
    String stringSubheader;

    boolean isTestReady = false;

    protected LinearLayout landing_layout;
    protected TextView textViewHeader;
    protected TextView textViewSubheader;
    protected LinearLayout content;
    protected RelativeLayout rootView;
    protected TextView textViewAvailability;

    private View yellowBar;

    HintPointer tourHint;
    HintPointer beginTestHint;
    HintHighlighter beginTestHighlight;
    BottomNavigationView bottomNavigationView;

    public HomeScreen() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_landing, container, false);

        rootView = view.findViewById(R.id.rootView);
        landing_layout = view.findViewById(R.id.landing_layout);
        content = view.findViewById(R.id.linearLayoutContent);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.robotoMedium);
        ViewUtil.setLineHeight(textViewHeader,32);

        yellowBar = view.findViewById(R.id.yellowBar);

        textViewSubheader = view.findViewById(R.id.textViewSubHeader);
        ViewUtil.setLineHeight(textViewSubheader,26);

        textViewAvailability = new TextView(getContext());
        textViewAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeAvailabilityScreen screen = new ChangeAvailabilityScreen();
                NavigationManager.getInstance().open(screen);
            }
        });
        textViewAvailability.setVisibility(View.GONE);
        textViewAvailability.setTextColor(ViewUtil.getColor(R.color.primary));
        textViewAvailability.setTypeface(Fonts.robotoBold);
        ViewUtil.underlineTextView(textViewAvailability);
        textViewAvailability.setText(ViewUtil.getString(R.string.availability_change_linked));

        RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        textViewLayoutParams.bottomMargin = ViewUtil.dpToPx(26);
        textViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textViewAvailability.setLayoutParams(textViewLayoutParams);
        rootView.addView(textViewAvailability);

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);

        //Font adjustments
        textViewHeader.setTypeface(Fonts.robotoMedium);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(tourHint!=null) {
            tourHint.setVisibility(View.GONE);
            tourHint.dismiss();
        }
        if(beginTestHint!=null) {
            beginTestHint.dismiss();
        }
        if(beginTestHighlight!=null) {
            beginTestHighlight.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        determineStrings();
        textViewHeader.setText(Html.fromHtml(stringHeader));
        textViewSubheader.setText(Html.fromHtml(stringSubheader));

        TestSession testSession = Study.getCurrentTestSession();
        if(testSession==null) {
            return;
        }

        boolean isTestReady = testSession.getScheduledTime().isBeforeNow();
        if (isTestReady) {
            Button button = new Button(getContext());
            button.setText(Application.getInstance().getResources().getString(R.string.button_begin));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    NavigationManager.getInstance().removeController();
                    Study.getCurrentTestSession().markStarted();
                    Study.getParticipant().save();
                    Study.getStateMachine().save();

                    if (!Hints.hasBeenShown(HINT_FIRST_TEST)) {
                        Hints.markShown(HINT_FIRST_TEST);
                        beginTestHint.dismiss();
                        beginTestHighlight.dismiss();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bottomNavigationView.setEnabled(true);
                                Study.openNextFragment();
                            }
                        },500);
                        return;
                    }

                    Study.openNextFragment();
                }
            });
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            buttonLayoutParams.bottomMargin = ViewUtil.dpToPx(8);
            button.setLayoutParams(buttonLayoutParams);
            content.addView(button);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            params.leftMargin = ViewUtil.dpToPx(32);
            params.rightMargin = ViewUtil.dpToPx(32);
            params.topMargin = ViewUtil.dpToPx(16);
            content.setLayoutParams(params);

            if (!Hints.hasBeenShown(HINT_FIRST_TEST)) {
                bottomNavigationView.setEnabled(false);


                beginTestHint = new HintPointer(getActivity(), button, true, false);
                beginTestHint.setText(ViewUtil.getString(R.string.popup_begin));
                beginTestHighlight = new HintHighlighter(getActivity());
                beginTestHighlight.addTarget(landing_layout, 10);

                beginTestHighlight.show();
                beginTestHint.show();
            }
        } else if(!Hints.hasBeenShown(HINT_FIRST_TEST)) {
            Hints.markShown(HINT_FIRST_TEST);
        }

        // The "tour" hints are the same for both post-baseline and post-paid test
        // The only difference is the first hint
        // HINT_TOUR - have the tour hints been shown at all?
        // HINT_POST_BASELINE - are we showing the post-baseline hints?
        // HINT_POST_PAID_TEST - are we showing the post-paid test hints?
        if (!Hints.hasBeenShown(HINT_TOUR) && Hints.hasBeenShown(HINT_FIRST_TEST)) {
            boolean isRightAfterPracticeTest = Study.getCurrentTestSession().getId()==1;
            if (isRightAfterPracticeTest) {
                showTourHints("", ViewUtil.getString(R.string.popup_tour), HINT_POST_BASELINE);
            } else {
                showTourHints(ViewUtil.getString(R.string.popup_nicejob), ViewUtil.getString(R.string.button_next), HINT_POST_PAID_TEST);
            }
        }

    }

    private void determineStrings() {

        Participant participant = Study.getParticipant();

        // No more tests, end of study
        if (!participant.isStudyRunning()) {
            stringHeader = ViewUtil.getString(R.string.home_header6);
            stringSubheader = ViewUtil.getString(R.string.home_body6);
            return;
        }

        // Default
        textViewAvailability.setVisibility(View.GONE);
        stringHeader = ViewUtil.getString(R.string.home_header1);
        stringSubheader = "";

        isTestReady = participant.shouldCurrentlyBeInTestSession();
        if(isTestReady){
            yellowBar.setVisibility(View.GONE);
            return;
        }

        TestCycle testCycle = participant.getCurrentTestCycle();
        if(testCycle==null) {
            stringHeader = ViewUtil.getString(R.string.home_header6);
            stringSubheader = ViewUtil.getString(R.string.home_body6);
            return;
        }

        TestDay testDay = participant.getCurrentTestDay();
        TestSession testSession = participant.getCurrentTestSession();

        DateTime cycleStartDate = testCycle.getActualStartDate();
        String startDateFmt = TimeUtil.format(cycleStartDate,R.string.format_date_long);

        DateTime cycleEndDate = testCycle.getActualEndDate().minusDays(1);
        String endDateFmt = TimeUtil.format(cycleEndDate,R.string.format_date_long);

        DateTime dayStartTime = testDay.getStartTime();

        // after
        if(testSession.getId() == 1 && dayStartTime.isAfterNow()) {
            stringHeader = ViewUtil.getString(R.string.home_header7);
            Phrase phrase = new Phrase(R.string.home_body7);
            phrase.replaceTimes(R.string.format_time, dayStartTime, testDay.getEndTime());
            stringSubheader = phrase.toString();
        }
        // After the cycle, one day before the start of the next session
        else if (testDay.getDayIndex()==0 && cycleStartDate.minusDays(1).isBeforeNow() && dayStartTime.isAfterNow()) {
            Phrase phrase = new Phrase(R.string.home_header5);
            phrase.replaceDate(endDateFmt);
            stringHeader = phrase.toString();

            Phrase phrase2 = new Phrase(R.string.home_body5);
            phrase2.replaceDates(startDateFmt,endDateFmt);
            stringSubheader = phrase2.toString();
        }
        // After the cycle before the start of the next session
        else if (cycleStartDate.isAfterNow()) {
            stringHeader = ViewUtil.getString(R.string.home_header4);

            Phrase phrase = new Phrase(R.string.home_body4);
            phrase.replaceDates(startDateFmt,endDateFmt);
            stringSubheader = phrase.toString();

            textViewAvailability.setVisibility(View.VISIBLE);
        }
        // After 4th test of the day
        else if (dayStartTime.isAfterNow()) {
            stringHeader = ViewUtil.getString(R.string.home_header3);
            Phrase phrase = new Phrase(R.string.home_body3);
            phrase.replaceTimes(R.string.format_time, testDay.getStartTime(), testDay.getEndTime());
            stringSubheader = phrase.toString();
        }
        // Open the app, no test, still in a cycle
        else if (testCycle.getNumberOfTestsLeft() > 0) {
            stringHeader = ViewUtil.getString(R.string.home_header2);
            stringSubheader = ViewUtil.getString(R.string.home_body2);
        }

    }

    public void setBottomNavigationView(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    private void showTourHints(String body, String btn, final String hint) {
        tourHint = new HintPointer(getActivity(), landing_layout, false, false);
        tourHint.addButton(btn, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tourHint.dismiss();

                // Mark the hint type as shown
                // Should be either HINT_POST_BASELINE or HINT_POST_PAID_TEST
                Hints.markShown(hint);

                // Once the tour has started, assume the user has seen it
                Hints.markShown(HINT_TOUR);
                bottomNavigationView.showHomeHint(getActivity());
            }
        });
        if (body.isEmpty()) {
            tourHint.hideText();
        } else {
            tourHint.setText(body);
            bottomNavigationView.setEnabled(false);
        }
        tourHint.show();
    }

}
