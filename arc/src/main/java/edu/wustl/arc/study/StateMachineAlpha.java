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
package edu.wustl.arc.study;

import android.util.Log;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.core.LoadingDialog;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.paths.questions.QuestionSignature;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class StateMachineAlpha extends StateMachine {

    public static final int PATH_SETUP_PARTICIPANT = 0;         //
    public static final int PATH_COMMITMENT = 9;
    public static final int PATH_COMMITMENT_REBUKED = 10;
    public static final int PATH_NOTIFICATIONS_OVERVIEW = 11;
    public static final int PATH_BATTERY_OPTIMIZATION_OVERVIEW = 12;

    public static final int PATH_SETUP_AVAILABILITY = 1;        //

    public static final int PATH_TEST_FIRST_OF_BASELINE = 2;    // first test of the baseline
    public static final int PATH_TEST_BASELINE = 3;             // every other test in the baseline

    public static final int PATH_TEST_NONE = 4;                 // no tests available
    public static final int PATH_TEST_FIRST_OF_VISIT = 5;       // first test of a visit, this trumps the first day path
    public static final int PATH_TEST_FIRST_OF_DAY = 6;         // first test on a given day
    public static final int PATH_TEST_OTHER = 7;                // every test that doesn't listed above

    public static final int PATH_STUDY_OVER = 8;                //

    public static final int LIFECYCLE_INIT = 0;                 //
    public static final int LIFECYCLE_BASELINE = 1;             //
    public static final int LIFECYCLE_IDLE = 3;                 //
    public static final int LIFECYCLE_ARC = 4;                  //
    public static final int LIFECYCLE_OVER = 5;                 //

    @Override
    public void initialize() {
        super.initialize();
        state.lifecycle = LIFECYCLE_INIT;
        state.currentPath = PATH_SETUP_PARTICIPANT;
    }

    // deciding paths ------------------------------------------------------------------------------

    @Override
    public void decidePath(){
        Log.i("StateMachine", "decidePath");
        Log.i("StateMachine", "lifecycle = "+getLifecycleName(state.lifecycle));

        switch (state.lifecycle) {
            case LIFECYCLE_INIT:
                decidePathInit();
                break;
            case LIFECYCLE_BASELINE:
                decidePathBaseline();
                break;
            case LIFECYCLE_ARC:
                decidePathArc();
                break;
            case LIFECYCLE_IDLE:
                decidePathIdle();
                break;
            case LIFECYCLE_OVER:
                decidePathOver();
                break;
        }
    }

    public void decidePathInit(){
        cache.segments.clear();
        Participant participant = Study.getParticipant();

        if(!participant.hasId()){
            state.currentPath = PATH_SETUP_PARTICIPANT;
            return;
        }

        if(participant.hasRebukedCommitmentToStudy()){
            state.currentPath = PATH_COMMITMENT_REBUKED;
            return;
        }

        if(!participant.hasCommittedToStudy()){
            state.currentPath = PATH_COMMITMENT;
            return;
        }

        if(!participant.hasBeenShownNotificationOverview()){
            state.currentPath = PATH_NOTIFICATIONS_OVERVIEW;
            return;
        }

        if(!participant.hasBeenShownBatteryOptimizationOverview()){
            state.currentPath = PATH_BATTERY_OPTIMIZATION_OVERVIEW;
            return;
        }

        if(!participant.hasSchedule()){
            state.currentPath = PATH_SETUP_AVAILABILITY;
            return;
        }

        if(participant.getState().currentTestCycle == 0){
            Log.i("StateMachine", "init finished, setting lifecycle to baseline");
            state.lifecycle = LIFECYCLE_BASELINE;
        } else if(participant.getCurrentTestCycle() == null) {
            Log.i("StateMachine", "init finished, setting lifecycle to over");
            state.lifecycle = LIFECYCLE_OVER;
        } else {
            Log.i("StateMachine", "init finished, setting lifecycle to idle");
            state.lifecycle = LIFECYCLE_IDLE;

        }
        decidePath();
    }

    public void decidePathBaseline(){
        Participant participant = Study.getInstance().getParticipant();

        if(participant.getCurrentTestCycle() == null) {
            state.lifecycle = LIFECYCLE_OVER;
            decidePath();
            return;
        }

        if (participant.getCurrentTestSession().isOngoing()) {
            Log.i("StateMachine", "loading in the middle of an indexed test, marking it abandoned");
            abandonTest();
            decidePath();
            return;
        }

        cache.segments.clear();

        TestCycle cycle = participant.getCurrentTestCycle();
        if (shouldRestrictTestBasedOnCurrentTime(cycle, participant, true)) {
            return;
        }

        currentlyInTestPath = true;

        if (!hasThereBeenAFinishedTestInCycle(participant.getCurrentTestCycle())) {
            Log.i("StateMachine", "setting path for first of baseline");
            state.currentPath = PATH_TEST_FIRST_OF_BASELINE;
            return;
        }

        if (!hasThereBeenAFinishedTestOnDay(participant.getCurrentTestDay())) {
            Log.i("StateMachine", "setting path for first of day");
            state.currentPath = PATH_TEST_FIRST_OF_DAY;
            return;
        }

        Log.i("StateMachine", "setting path for baseline test");
        state.currentPath = PATH_TEST_BASELINE;
    }

    public void decidePathArc(){
        Participant participant = Study.getInstance().getParticipant();

        if(participant.getCurrentTestCycle() == null) {
            state.lifecycle = LIFECYCLE_OVER;
            decidePath();
            return;
        }

        TestCycle cycle = participant.getCurrentTestCycle();
        TestDay day = participant.getCurrentTestDay();

        if(!participant.isStudyRunning()){
            Log.i("StateMachine", "study isn't running, setting lifecycle to over");
            state.lifecycle = LIFECYCLE_OVER;
            decidePath();
            return;
        }

        if (participant.getCurrentTestSession().isOngoing()) {
            abandonTest();
            decidePath();
            return;
        }

        cache.segments.clear();

        if (shouldRestrictTestBasedOnCurrentTime(cycle, participant, false)) {
            return;
        }

        currentlyInTestPath = true;

        if (!hasThereBeenAFinishedTestInCycle(cycle)) {
            Log.i("StateMachine", "setting path for first of cycle");
            state.currentPath = PATH_TEST_FIRST_OF_VISIT;
            return;
        }

        if (!hasThereBeenAFinishedTestOnDay(day)) {
            Log.i("StateMachine", "setting path for first of day");
            state.currentPath = PATH_TEST_FIRST_OF_DAY;
            return;
        }

        Log.i("StateMachine", "setting path for test");
        state.currentPath = PATH_TEST_OTHER;
    }

    protected boolean hasThereBeenAFinishedTestInCycle(TestCycle cycle) {
        return cycle.hasThereBeenAFinishedTest();
    }

    protected boolean hasThereBeenAFinishedTestOnDay(TestDay day) {
        return day.hasThereBeenAFinishedTest();
    }

    protected boolean shouldRestrictTestBasedOnCurrentTime(
            TestCycle cycle, Participant participant, boolean loadTestDataFromCache) {

        if(cycle.getActualStartDate().isAfterNow()){
            Log.i("StateMachine", "indexed cycle hasn't started, setting lifecycle to idle");
            state.lifecycle = LIFECYCLE_IDLE;
            decidePath();
            return true;
        }

        if (participant.getCurrentTestSession().getScheduledTime().minusMinutes(5).isAfterNow()) {
            Log.i("StateMachine", "indexed test hasn't started, do nothing");
            state.currentPath = PATH_TEST_NONE;
            return true;
        }

        if (participant.getCurrentTestSession().getExpirationTime().isBeforeNow()) {
            Log.i("StateMachine", "indexed test has expired, marking it as such");
            participant.getCurrentTestSession().markMissed();

            if (loadTestDataFromCache) {
                loadTestDataFromCache();
                cache.data.clear();
            }

            submitTest(participant.getCurrentTestSession());
            participant.moveOnToNextTestSession(true);

            if (loadTestDataFromCache) {
                save();
            } else {
                participant.save();
            }

            decidePath();
            return true;
        }

        return false;
    }

    public void decidePathIdle() {
        TestCycle cycle = Study.getCurrentTestCycle();

        if (cycle.getActualStartDate().isBeforeNow()) {
            state.lifecycle = LIFECYCLE_ARC;
            decidePath();
        } else {
            state.currentPath = PATH_TEST_NONE;
        }
    }

    public void decidePathOver() {
        state.currentPath = PATH_STUDY_OVER;
    }

    // setting up paths ----------------------------------------------------------------------------

//    @Override
//    protected void setupPath(){
//
//    }

    @Override
    protected void endOfPath(){
        Log.i("StateMachine", "endOfPath");
        Log.i("StateMachine", "lifecycle = "+getLifecycleName(state.lifecycle));
        Log.i("StateMachine", "path = "+getPathName(state.currentPath));

        switch (state.lifecycle) {
            case LIFECYCLE_INIT:
                switch (state.currentPath){
                    case PATH_SETUP_PARTICIPANT:
                        break;
                    case PATH_SETUP_AVAILABILITY:
                        break;
                }
                break;
            case LIFECYCLE_BASELINE:
            case LIFECYCLE_ARC:
                switch (state.currentPath){
                    case PATH_TEST_NONE:
                        break;
                    case PATH_NOTIFICATIONS_OVERVIEW:
                        break;
                    default:
                        Log.i(tag, "gather data from test");
                        // set up a loading dialog in case this takes a bit
                        LoadingDialog dialog = new LoadingDialog();
                        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");

                        TestSession currentTest = Study.getCurrentTestSession();
                        currentTest.markCompleted();
                        if(Study.getCurrentTestDay().getNumberOfTestsAvailableNow()==0){
                            setTestCompleteFlag(true);
                        }
                        loadTestDataFromCache();

                        submitTest(Study.getCurrentTestSession());
                        save();

                        dialog.dismiss();

                        moveOnToNextSessionAfterTestCompleted();

                        break;
                }
                break;
            case LIFECYCLE_IDLE:
                break;
            case LIFECYCLE_OVER:
                break;
        }
        currentlyInTestPath = false;
    }

    protected void moveOnToNextSessionAfterTestCompleted() {
        Study.getParticipant().moveOnToNextTestSession(true);
    }

    // state machine helpers ---------------------------------------------------------------------

    public void addWelcome() {
//        List<BaseFragment> fragments = new ArrayList<>();
//
//        if(Config.IS_REMOTE) {
//            // I commit or I'm not able to commit
//            fragments.add(new QuestionRemoteStudyCommitment(
//                    true,
//                    ViewUtil.getString(R.string.testing_commitment),
//                    ViewUtil.getString(R.string.onboarding_body),
//                    ViewUtil.getString(R.string.radio_commit),
//                    ViewUtil.getString(R.string.radio_nocommit)
//            ));
//
//        } else {
//            // I understand
//            fragments.add(new QuestionSingleButton(
//                    false,
//                    ViewUtil.getString(R.string.onboarding_header),
//                    ViewUtil.getString(R.string.onboarding_body),
//                    ViewUtil.getString(R.string.button_continue),
//                    ViewUtil.getString(R.string.radio_understand)));
//        }
//
//        PathSegment segment = new PathSegment(fragments);
//        enableTransition(segment,true);
//        cache.segments.add(segment);
    }

    public void checkForSignaturePage(boolean allowHelp){
        if(Config.ENABLE_SIGNATURES) {
            addSignaturePage(allowHelp);
        }
    }

    public void addSignaturePage(boolean allowHelp){
        List<ArcBaseFragment> fragments = new ArrayList<>();
        fragments.add(new QuestionSignature(
                false,
                allowHelp,
                ViewUtil.getString(R.string.idverify_header),
                ViewUtil.getString(R.string.testing_id_header)));
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    // --------------------------------------------------------------------------

    public void setPathFirstOfBaseline(){
        checkForSignaturePage(true);
        addChronotypeSurvey();
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathBaselineTest(){
        checkForSignaturePage(true);
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }


    public void setPathNoTests(){
        //  leave empty for now
    }

    public void setPathTestFirstOfVisit(){
        checkForSignaturePage(true);
        addChronotypeSurvey();
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathTestFirstOfDay(){
        checkForSignaturePage(true);
        addWakeSurvey();
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathTestOther(){
        checkForSignaturePage(true);
        addContextSurvey();
        addTests();
        addInterruptedPage();
        checkForSignaturePage(false);
    }

    public void setPathOver(){
        setPathNoTests();
    }


    // utility functions ---------------------------------------------------------------------------

    @Override
    public String getLifecycleName(int lifecycle){
        switch (lifecycle){
            case LIFECYCLE_INIT:
                return "INIT";
            case LIFECYCLE_BASELINE:
                return "BASELINE";
            case LIFECYCLE_IDLE:
                return "IDLE";
            case LIFECYCLE_ARC:
                return "ARC";
            case LIFECYCLE_OVER:
                return "OVER";
            default:
                return "INVALID";
        }
    }

    @Override
    public String getPathName(int path){
        switch (path){
            case PATH_SETUP_PARTICIPANT:
                return "SETUP_PARTICIPANT";
            case PATH_SETUP_AVAILABILITY:
                return "SETUP_AVAILABILITY";
            case PATH_TEST_FIRST_OF_BASELINE:
                return "TEST_FIRST_OF_BASELINE";
            case PATH_TEST_BASELINE:
                return "TEST_BASELINE";
            case PATH_TEST_NONE:
                return "TEST_NONE";
            case PATH_TEST_FIRST_OF_VISIT:
                return "TEST_FIRST_OF_VISIT";
            case PATH_TEST_FIRST_OF_DAY:
                return "TEST_FIRST_OF_DAY";
            case PATH_TEST_OTHER:
                return "TEST_OTHER";
            case PATH_STUDY_OVER:
                return "STUDY_OVER";
            case PATH_COMMITMENT:
                return "PATH_COMMITMENT";
            case PATH_COMMITMENT_REBUKED:
                return "PATH_COMMITMENT_REBUKED";
            case PATH_NOTIFICATIONS_OVERVIEW:
                return "PATH_NOTIFICATIONS_OVERVIEW";
            case PATH_BATTERY_OPTIMIZATION_OVERVIEW:
                return "PATH_BATTERY_OPTIMIZATION_OVERVIEW";
            default:
                return "INVALID";
        }
    }

    @Override
    public void loadTestDataFromCache() {
        loadCognitiveTestFromCache();
    }
}
