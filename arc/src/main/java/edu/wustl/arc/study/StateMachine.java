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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import edu.wustl.arc.api.tests.CognitiveTest;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.core.SplashScreen;
import edu.wustl.arc.path_data.Grid2TestPathData;
import edu.wustl.arc.paths.templates.StateInfoTemplate;
import edu.wustl.arc.paths.templates.TestInfoTemplate;
import edu.wustl.arc.paths.tests.Grid2Letters;
import edu.wustl.arc.paths.tests.Grid2Study;
import edu.wustl.arc.paths.tests.Grid2Test;
import edu.wustl.arc.paths.tests.TestIntro;
import edu.wustl.arc.paths.tests.TestProgress;
import edu.wustl.arc.time.TimeUtil;

import edu.wustl.arc.api.tests.data.BaseData;
import edu.wustl.arc.core.Application;
import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.core.SimplePopupScreen;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.misc.TransitionSet;
import edu.wustl.arc.path_data.ChronotypePathData;
import edu.wustl.arc.path_data.ContextPathData;
import edu.wustl.arc.path_data.GridTestPathData;
import edu.wustl.arc.path_data.PriceTestPathData;
import edu.wustl.arc.path_data.SymbolsTestPathData;
import edu.wustl.arc.path_data.WakePathData;
import edu.wustl.arc.paths.questions.QuestionCheckBoxes;
import edu.wustl.arc.paths.questions.QuestionDuration;
import edu.wustl.arc.paths.questions.QuestionInteger;
import edu.wustl.arc.paths.questions.QuestionPolar;
import edu.wustl.arc.paths.questions.QuestionRadioButtons;
import edu.wustl.arc.paths.questions.QuestionRating;
import edu.wustl.arc.paths.questions.QuestionTime;
import edu.wustl.arc.paths.tests.GridLetters;
import edu.wustl.arc.paths.tests.GridStudy;
import edu.wustl.arc.paths.tests.GridTest;
import edu.wustl.arc.paths.tests.PriceTestCompareFragment;
import edu.wustl.arc.paths.tests.PriceTestMatchFragment;
import edu.wustl.arc.paths.tests.QuestionInterrupted;
import edu.wustl.arc.paths.tests.SymbolTest;
import edu.wustl.arc.paths.tests.TestBegin;
import edu.wustl.arc.utilities.CacheManager;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.utilities.PreferencesManager;
import edu.wustl.arc.utilities.PriceManager;
import edu.wustl.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StateMachine {

    public static final String TAG_STUDY_STATE_CACHE = "StudyStateCache";
    public static final String TAG_STUDY_STATE = "StudyState";
    public static final String TAG_TEST_COMPLETE = "TestCompleteFlag";
    public static int AUTOMATED_TESTS_RANDOM_SEED = -1;

    protected String tag = getClass().getSimpleName();

    protected State state;
    protected StateCache cache;
    protected boolean currentlyInTestPath = false;

    public StateMachine() {

    }

    public void showSplashScreen() {
        NavigationManager.getInstance().open(new SplashScreen());
    }

    protected void enableTransition(PathSegment segment, boolean animateEntry){
        int size = segment.fragments.size();
        if(size==0){
            return;
        }

        segment.fragments.get(0).setTransitionSet(TransitionSet.getSlidingDefault(animateEntry));
        for(int i=1;i<size;i++){
            segment.fragments.get(i).setTransitionSet(TransitionSet.getSlidingDefault());
        }
    }

    protected void enableTransitionGrids(PathSegment segment, boolean animateEntry){
        if (animateEntry) {
            segment.fragments.get(1).setTransitionSet(TransitionSet.getSlidingDefault());
            segment.fragments.get(2).setTransitionSet(TransitionSet.getSlidingDefault());
        }
    }

    public void initialize(){
        state = new State();
        cache = new StateCache();
    }

    public void load(){
        load(false);
    }

    public void load(boolean overwrite){
        Log.d(tag,"load(overwrite = "+overwrite+")");
        if(state!=null && !overwrite){
            return;
        }
        state = PreferencesManager.getInstance().getObject(TAG_STUDY_STATE, State.class);
        cache = CacheManager.getInstance().getObject(TAG_STUDY_STATE_CACHE, StateCache.class);

        if(cache==null) {
            cache = new StateCache();
        }
        if(cache.segments==null) {
            cache.segments = new ArrayList<>();
        }
        if(cache.data==null) {
            cache.data = new ArrayList<>();
        }

    }

    public void save(){
        save(false);
    }

    public void save(boolean saveCache){
        Log.d(tag,"save(saveCache = "+saveCache+")");
        PreferencesManager.getInstance().putObject(TAG_STUDY_STATE, state);
        CacheManager.getInstance().putObject(TAG_STUDY_STATE_CACHE,cache);
        if(saveCache){
            CacheManager.getInstance().save(TAG_STUDY_STATE_CACHE);
        }
    }

    public void decidePath(){

    }

    public void abandonTest(){
        Participant participant = Study.getParticipant();

        Log.i(tag, "loading in the middle of an indexed test, marking it abandoned");
        participant.getCurrentTestSession().markAbandoned();

        Log.i(tag, "collecting data from each existing segment");
        for(PathSegment segment : cache.segments){
            BaseData object = segment.collectData();
            if(object!=null){
                cache.data.add(object);
            }
        }

        loadTestDataFromCache();
        cache.segments.clear();
        cache.data.clear();

        submitTest(participant.getCurrentTestSession());
        participant.moveOnToNextTestSession(true);
        save();
    }

    public void submitTest(TestSession testSession) {
        throw new IllegalStateException("To be implemented by sub-class");
    }

    public void submitSignature(Bitmap signature) {
        throw new IllegalStateException("To be implemented by sub-class");
    }

    protected void setupPath(){

    }

    // this is where we can use the cache of segments
    protected void endOfPath(){

    }

    public boolean skipToNextSegment(){

        if(cache.segments.size() > 0) {
            BaseData object = cache.segments.get(0).collectData();
            if (object != null) {
                cache.data.add(object);
            }
            cache.segments.remove(0);

            NavigationManager.getInstance().clearBackStack();
            Study.getInstance().getParticipant().save();
            save();
        }

        if(cache.segments.size()>0){
            return openNext();
        } else {
            endOfPath();
            cache.data.clear();
            decidePath();
            setupPath();
            return openNext();
        }
    }

    public boolean openNext() {
        return openNext(0);
    }

    public boolean openNext(int skips){
        save();
        if(cache.segments.size()>0){
            if(cache.segments.get(0).openNext(skips)) {
                return true;
            } else {
                return endOfSegment();
            }
        } else {
            return moveOn();
        }
    }

    protected boolean endOfSegment(){
        // else at the end of segment
        BaseData object = cache.segments.get(0).collectData();
        if(object!=null){
            cache.data.add(object);
        }
        cache.segments.remove(0);

        NavigationManager.getInstance().clearBackStack();
        NavigationManager.getInstance().removeController();
        Study.getInstance().getParticipant().save();
        save();

        if(cache.segments.size()>0){
            return openNext();
        } else {
            endOfPath();
            return moveOn();
        }
    }

    protected boolean moveOn(){
        cache.data.clear();
        decidePath();
        setupPath();
        Study.getInstance().getParticipant().save();
        save();
        return openNext();
    }

    public boolean openPrevious() {
        return openPrevious(0);
    }

    public boolean openPrevious(int skips){
        if(cache.segments.size()>0){
            return cache.segments.get(0).openPrevious(skips);
        }
        return false;
    }

    // ------------------------------------------


    protected void setTestCompleteFlag(boolean complete){
        Log.i(tag, "setTestCompleteFlag("+complete+")");
        PreferencesManager.getInstance().putBoolean(TAG_TEST_COMPLETE,complete);
    }

    protected boolean isTestCompleteFlagSet(){
        return PreferencesManager.getInstance().getBoolean(TAG_TEST_COMPLETE,false);
    }

    public boolean isCurrentlyInTestPath(){
        return currentlyInTestPath;
    }

    public boolean hasValidFragments() {
        if(cache.segments.size() == 0) {
            return false;
        }

        for(int i = 0; i < cache.segments.size(); i++) {
            if(cache.segments.get(i).fragments.size() == 0) {
                return false;
            }
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    public void addChronotypeSurvey(){
        List<ArcBaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.chronotype_header),
                res.getString(R.string.chronotype_subheader),
                res.getString(R.string.chronotype_body1),
                res.getString(R.string.button_beginsurvey)));

        fragments.add(new QuestionPolar(true, res.getString(R.string.chronotype_q1),""));

        List<String> workingDayCountOptions = new ArrayList<>();
        workingDayCountOptions.add("0");
        workingDayCountOptions.add("1");
        workingDayCountOptions.add("2");
        workingDayCountOptions.add("3");
        workingDayCountOptions.add("4");
        workingDayCountOptions.add("5");
        workingDayCountOptions.add("6");
        workingDayCountOptions.add("7");

        fragments.add(new QuestionRadioButtons(true,false, res.getString(R.string.chronotype_q2), res.getString(R.string.list_selectone ),workingDayCountOptions));

        fragments.add(new StateInfoTemplate(
                false,
                "",
                "",
                res.getString(R.string.chronotype_body2),
                res.getString(R.string.button_next)));


        CircadianClock clock = Study.getParticipant().getCircadianClock();
        CircadianRhythm rhythm;
        String weekday;


        weekday = TimeUtil.getWeekday();
        if(!clock.hasWakeRhythmChanged(weekday)){
            weekday = TimeUtil.getWeekday(DateTime.now().minusDays(1));
        }
        rhythm = clock.getRhythm(weekday);
        LocalTime wakeTime = rhythm.getWakeTime();


        weekday = TimeUtil.getWeekday();
        if(!clock.hasBedRhythmChanged(weekday)){
            weekday = TimeUtil.getWeekday(DateTime.now().minusDays(1));
        }
        rhythm = clock.getRhythm(weekday);
        LocalTime bedTime = rhythm.getBedTime();

        String buttonText = ViewUtil.getString(R.string.button_choosetime);
        fragments.add(new QuestionTime(true, res.getString(R.string.chronotype_workdays_sleep), res.getString(R.string.chronotype_disclaim1), bedTime,buttonText));
        fragments.add(new QuestionTime(true, res.getString(R.string.chronotype_workdays_wake), res.getString(R.string.chronotype_disclaim2), wakeTime,buttonText));
        fragments.add(new QuestionTime(true, res.getString(R.string.chronotype_workfree_sleep), res.getString(R.string.chronotype_disclaim1), bedTime,buttonText));
        fragments.add(new QuestionTime(true, res.getString(R.string.chronotype_workfree_wake), res.getString(R.string.chronotype_disclaim2), wakeTime,buttonText));

        PathSegment segment = new PathSegment(fragments,ChronotypePathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addWakeSurvey(){
        List<ArcBaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.wakesurvey_header),
                res.getString(R.string.wakesurvey_subheader),
                res.getString(R.string.wakesurvey_body),
                res.getString(R.string.button_beginsurvey)));


        CircadianClock clock = Study.getParticipant().getCircadianClock();
        CircadianRhythm rhythm;
        String weekday;


        weekday = TimeUtil.getWeekday();
        if(!clock.hasWakeRhythmChanged(weekday)){
            weekday = TimeUtil.getWeekday(DateTime.now().minusDays(1));
        }
        rhythm = clock.getRhythm(weekday);
        LocalTime wakeTime = rhythm.getWakeTime();


        weekday = TimeUtil.getWeekday();
        if(!clock.hasBedRhythmChanged(weekday)){
            weekday = TimeUtil.getWeekday(DateTime.now().minusDays(1));
        }
        rhythm = clock.getRhythm(weekday);
        LocalTime bedTime = rhythm.getWakeTime();


        fragments.add(new QuestionTime(true, res.getString(R.string.wake_q1),"",bedTime));
        fragments.add(new QuestionDuration(true, res.getString(R.string.wake_q2)," "));
        fragments.add(new QuestionInteger(true, res.getString(R.string.wake_q3a), res.getString(R.string.wake_q3b),2));
        fragments.add(new QuestionTime(true, res.getString(R.string.wake_q4)," ",wakeTime));
        fragments.add(new QuestionTime(true, res.getString(R.string.wake_q5)," ",wakeTime));
        fragments.add(new QuestionRating(true, res.getString(R.string.wake_q6), "", res.getString(R.string.wake_poor), res.getString(R.string.wake_excellent)));

        PathSegment segment = new PathSegment(fragments,WakePathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addContextSurvey(){
        List<ArcBaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        fragments.add(new StateInfoTemplate(
                false,
                res.getString(R.string.context_header),
                res.getString(R.string.context_subheader),
                res.getString(R.string.context_body),
                res.getString(R.string.button_beginsurvey)));

        List<String> who = new ArrayList<>();
        who.add(res.getString(R.string.context_q1_a1));
        who.add(res.getString(R.string.context_q1_a2));
        who.add(res.getString(R.string.context_q1_a3));
        who.add(res.getString(R.string.context_q1_a4));
        who.add(res.getString(R.string.context_q1_a5));
        who.add(res.getString(R.string.context_q1_a6));
        who.add(res.getString(R.string.context_q1_a7));
        fragments.add(new QuestionCheckBoxes(true, res.getString(R.string.context_q1), res.getString(R.string.list_selectall), who, res.getString(R.string.context_q1_a1)));

        List<String> where = new ArrayList<>();
        where.add(res.getString(R.string.context_q2_a1));
        where.add(res.getString(R.string.context_q2_a2));
        where.add(res.getString(R.string.context_q2_a3));
        where.add(res.getString(R.string.context_q2_a4));
        where.add(res.getString(R.string.context_q2_a5));
        where.add(res.getString(R.string.context_q2_a6));
        where.add(res.getString(R.string.context_q2_a7));
        fragments.add(new QuestionRadioButtons(true, false, res.getString(R.string.context_q2), res.getString(R.string.list_selectone), where));

        fragments.add(new QuestionRating(true, res.getString(R.string.context_q3), "", res.getString(R.string.context_bad), res.getString(R.string.context_good)));
        fragments.add(new QuestionRating(true, res.getString(R.string.context_q4), "", res.getString(R.string.context_tired), res.getString(R.string.context_active)));

        List<String> what = new ArrayList<>();
        what.add(res.getString(R.string.context_q5_a1));
        what.add(res.getString(R.string.context_q5_a2));
        what.add(res.getString(R.string.context_q5_a3));
        what.add(res.getString(R.string.context_q5_a4));
        what.add(res.getString(R.string.context_q5_a5));
        what.add(res.getString(R.string.context_q5_a6));
        what.add(res.getString(R.string.context_q5_a7));
        what.add(res.getString(R.string.context_q5_a8));
        what.add(res.getString(R.string.context_q5_a9));
        what.add(res.getString(R.string.context_q5_a10));
        fragments.add(new QuestionRadioButtons(true, false, res.getString(R.string.context_q5), "", what));

        PathSegment segment = new PathSegment(fragments,ContextPathData.class);
        enableTransition(segment,true);
        cache.segments.add(segment);
    }

    public void addTests(){

        List<ArcBaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        TestIntro info = new TestIntro();
//        StateInfoTemplate info = new StateInfoTemplate(
//                false,
//                res.getString(R.string.testing_intro_header),
//                res.getString(R.string.testing_intro_subhead),
//                res.getString(R.string.testing_intro_body),
//                res.getString(R.string.button_next));
        //info.setEnterTransitions(R.anim.slide_in_right,R.anim.slide_in_left);
        fragments.add(info);
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);

        Integer[] orderArray = new Integer[]{1,2,3};
        List<Integer> order = Arrays.asList(orderArray);
        if(AUTOMATED_TESTS_RANDOM_SEED == -1){
            Collections.shuffle(order);
        }else{
            Collections.shuffle(order, new Random(AUTOMATED_TESTS_RANDOM_SEED));
        }

        for(int i =0;i<3;i++){
            switch(order.get(i)){
                case 1:
                    addSymbolsTest(i);
                    break;
                case 2:
                    addPricesTest(i);
                    break;
                case 3:
                    addGridTest(i);
                    break;
            }
        }
    }

    public void addPricesTest(int index){
        List<ArcBaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        String testNumber = getTestNumberString(index);

        TestInfoTemplate info = new TestInfoTemplate(
                testNumber,
                ViewUtil.getHtmlString(R.string.prices_header),
                ViewUtil.getHtmlString(R.string.prices_body),
                "prices",
                ViewUtil.getHtmlString(R.string.button_begintest));
        fragments.add(info);

        fragments.add(new TestBegin());

        int size = PriceManager.getInstance().getPriceSet().size();
        for(int i=0;i<size;i++){
            fragments.add(new PriceTestCompareFragment(i));
        }

        fragments.add(new SimplePopupScreen(
                ViewUtil.getHtmlString(R.string.prices_overlay),
                ViewUtil.getHtmlString(R.string.button_begin),
                3000,
                15000,
                true));

        fragments.add(new PriceTestMatchFragment());
        fragments.add(new TestProgress(ViewUtil.getString(R.string.prices_complete), index));
        PathSegment segment = new PathSegment(fragments,PriceTestPathData.class);
        cache.segments.add(segment);
    }

    public void addSymbolsTest(int index){
        List<ArcBaseFragment> fragments = new ArrayList<>();

        Resources res = Application.getInstance().getResources();

        String testNumber = getTestNumberString(index);

        TestInfoTemplate info = new TestInfoTemplate(
                testNumber,
                ViewUtil.getHtmlString(R.string.symbols_header),
                ViewUtil.getHtmlString(R.string.symbols_body),
                "symbols",
                ViewUtil.getHtmlString(R.string.button_begintest));
        fragments.add(info);

        fragments.add(new TestBegin());

        fragments.add(new SymbolTest());
        fragments.add(new TestProgress(ViewUtil.getString(R.string.symbols_complete), index));
        PathSegment segment = new PathSegment(fragments,SymbolsTestPathData.class);
        cache.segments.add(segment);
    }

    public void addGridTest(int index){
        switch (Config.TEST_VARIANT_GRID) {
            case V1:
                addGrid1Test(index);
                break;
            case V2:
                addGrid2Test(index);
                break;
        }
    }

    public void addGrid1Test(int index){
        List<ArcBaseFragment> fragments = new ArrayList<>();

        String testNumber = getTestNumberString(index);

        TestInfoTemplate info0 = new TestInfoTemplate(
                testNumber,
                ViewUtil.getHtmlString(R.string.grids_header),
                ViewUtil.getHtmlString(R.string.grids_body),
                "grids",
                ViewUtil.getHtmlString(R.string.button_begintest));
        fragments.add(info0);

        fragments.add(new TestBegin());
        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        fragments.add(new GridTest());
        fragments.add(new GridStudy());
        fragments.add(new GridLetters());
        GridTest gridTestFragment = new GridTest();
        gridTestFragment.second = true;
        fragments.add(gridTestFragment);
        fragments.add(new TestProgress(ViewUtil.getString(R.string.grids_complete), index));
        PathSegment segment = new PathSegment(fragments,GridTestPathData.class);
        enableTransitionGrids(segment,true);
        cache.segments.add(segment);
    }

    public void addGrid2Test(int index){
        List<ArcBaseFragment> fragments = new ArrayList<>();

        String testNumber = getTestNumberString(index);

        TestInfoTemplate info0 = new TestInfoTemplate(
                testNumber,
                ViewUtil.getHtmlString(R.string.grids_header),
                ViewUtil.getHtmlString(R.string.grids_vb_body),
                "grids",
                ViewUtil.getHtmlString(R.string.button_begintest));
        fragments.add(info0);

        fragments.add(new TestBegin());
        fragments.add(new Grid2Study());
        fragments.add(new Grid2Letters());
        fragments.add(new Grid2Test());
        fragments.add(new Grid2Study());
        fragments.add(new Grid2Letters());
        fragments.add(new Grid2Test());

        fragments.add(new TestProgress(ViewUtil.getString(R.string.grids_complete), index));

        PathSegment segment = new PathSegment(fragments, Grid2TestPathData.class);
        enableTransitionGrids(segment,true);
        cache.segments.add(segment);
    }

    private String getTestNumberString(final int index) {
        String testNumberString = "invalid test index";
        switch(index) {
            case 0:
                testNumberString = ViewUtil.getHtmlString(R.string.testing_header_1);
                break;
            case 1:
                testNumberString = ViewUtil.getHtmlString(R.string.testing_header_2);
                break;
            case 2:
                testNumberString = ViewUtil.getHtmlString(R.string.testing_header_3);
                break;
            default:
                break;
        }

        return testNumberString;
    }

    public void addInterruptedPage(){

        Resources res = Application.getInstance().getResources();

        List<ArcBaseFragment> fragments = new ArrayList<>();
        fragments.add(new QuestionInterrupted(false, ViewUtil.getHtmlString(R.string.testing_interrupted_body),""));
        PathSegment segment = new PathSegment(fragments);
        cache.segments.add(segment);
    }

    // -----------------------

    public String getLifecycleName(int lifecycle){
        return "";
    }

    public String getPathName(int path){
        return "";
    }

    public State getState(){
        return state;
    }

    public StateCache getCache(){
        return cache;
    }

    // loadTestDataFromCache() is called from abandonTest().
    // Override this method to handle loading test data from cache.
    public void loadTestDataFromCache() {

    }

    public void loadCognitiveTestFromCache(){
        Log.i("StateMachine", "loadCognitiveTestFromCache");
        CognitiveTest cognitiveTest = new CognitiveTest();
        cognitiveTest.load(cache.data);
        Study.getInstance().getCurrentTestSession().addTestData(cognitiveTest);
    }

    /**
     * @return true if we want phone number and email to show up on the contact screen
     *         false for DIAN-150 - to hide the rest of the phone and emails
     */
    public boolean shouldShowDetailedContactScreen() {
        return true;
    }

    public ArcBaseFragment createHelpScreen() {
        throw new IllegalStateException("Must be implemented by sub-class");
    }

    public ArcBaseFragment createContactScreen() {
        throw new IllegalStateException("Must be implemented by sub-class");
    }
}
