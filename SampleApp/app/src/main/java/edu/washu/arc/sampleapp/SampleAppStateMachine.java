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
package edu.washu.arc.sampleapp;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.arc.core.LoadingDialog;
import edu.wustl.arc.core.SplashScreen;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.study.StateMachineAlpha;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestSession;

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
