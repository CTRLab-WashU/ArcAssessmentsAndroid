package edu.washu.arc.sampleapp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.arc.core.Application;
import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.core.LoadingDialog;
import edu.wustl.arc.core.SplashScreen;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.path_data.ContextPathData;
import edu.wustl.arc.paths.templates.TestInfoTemplate;
import edu.wustl.arc.paths.tests.SymbolTest;
import edu.wustl.arc.paths.tests.TestBegin;
import edu.wustl.arc.study.PathSegment;
import edu.wustl.arc.study.StateMachineAlpha;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestSession;
import edu.wustl.arc.ui.Signature;
import edu.wustl.arc.utilities.ViewUtil;

public class SampleAppStateMachine extends StateMachineAlpha {

    public interface AssessmentCompleteListener {
        void onAssessmentComplete(List<Bitmap> signatureList, TestSession session);
    }
    public AssessmentCompleteListener listener;
    private List<Bitmap> signatureList = new ArrayList<>();

    @Override
    public void initialize() {
        super.initialize();
        state.lifecycle = LIFECYCLE_ARC;
        state.currentPath = PATH_TEST_NONE;
    }

    @Override
    public void showSplashScreen() {
        NavigationManager.getInstance().open(new SplashScreen());
    }

    @Override
    protected void setupPath(){
        Log.i(tag, "setupPath");
        Log.i(tag, "path = "+getPathName(state.currentPath));

        state.currentPath = PATH_TEST_FIRST_OF_BASELINE;

        switch (state.currentPath){
            case PATH_TEST_FIRST_OF_BASELINE:
                setPathFirstOfBaseline();
                break;
            case PATH_TEST_BASELINE:
                setPathBaselineTest();
                break;
            case PATH_TEST_FIRST_OF_DAY:
                setPathTestFirstOfDay();
                break;
            case PATH_TEST_FIRST_OF_VISIT:
                setPathTestFirstOfVisit();
                break;
            case PATH_TEST_OTHER:
                setPathTestOther();
                break;
            case PATH_TEST_NONE:
                setPathNoTests();
                break;
            case PATH_STUDY_OVER:
                setPathOver();
                break;
        }
    }

    // DIAN-149 - hide the rest of the phone and emails
    @Override
    public boolean shouldShowDetailedContactScreen() {
        return false;
    }

    @Override
    public void submitTest(TestSession testSession) {
        if (listener != null) {
            listener.onAssessmentComplete(signatureList, testSession);
        }
    }

    @Override
    public void submitSignature(Bitmap signature) {
        signatureList.add(signature);
    }

    protected void endOfPath(){
        Log.i(tag, "gather data from test");
        // set up a loading dialog in case this takes a bit
        LoadingDialog dialog = new LoadingDialog();
        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");

        TestSession currentTest = Study.getCurrentTestSession();
        currentTest.markCompleted();
        setTestCompleteFlag(true);
        loadTestDataFromCache();

        save();
        submitTest(Study.getCurrentTestSession());

        dialog.dismiss();
    }

    public void setPathForCellRow(MainActivity.CellRows cellRow) {
        signatureList.clear();

        checkForSignaturePage(true);

        switch (cellRow) {
            case CONTEXT_SURVEY:
                addContextSurvey();
                break;
            case CHRONOTYPE_SURVEY:
                addChronotypeSurvey();
                break;
            case WAKE_SURVEY:
                addWakeSurvey();
                break;
            case SYMBOLS_TEST:
                addSymbolsTest(2);
                break;
            case PRICING_TEST:
                addPricesTest(2);
                break;
            case GRIDS_TEST:
                addGridTest(2);
                break;
            case ALL_TESTS:
                addChronotypeSurvey();
                addWakeSurvey();
                addContextSurvey();
                addTests();
                addInterruptedPage();
                break;
        }
        checkForSignaturePage(false);
    }
}
