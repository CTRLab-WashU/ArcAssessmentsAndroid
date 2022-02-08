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
package edu.wustl.arc.paths.setup_v2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import edu.wustl.arc.core.Config;
import edu.wustl.arc.core.LoadingDialog;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.misc.TransitionSet;
import edu.wustl.arc.path_data.SetupPathData;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class Setup2AuthManual extends Setup2Template {


    public Setup2AuthManual(int digitCount) {
        super(digitCount,0, ViewUtil.getString(R.string.login_enter_2FA));
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected void onNextRequested() {
        super.onNextRequested();


        ((SetupPathData) Study.getCurrentSegmentData()).authCode = characterSequence.toString();


        if(Config.REST_BLACKHOLE){
            String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
            Study.getParticipant().getState().id = id;
            Study.getInstance().openNextFragment();
            return;
        }

        loadingDialog = new LoadingDialog();
        loadingDialog.show(getFragmentManager(),"LoadingDialog");
        SetupPathData pathData = (SetupPathData)Study.getCurrentSegmentData();
        Study.getRestClient().registerDevice(pathData.id, pathData.authCode, false, registrationListener);

    }

    @Override
    public void onErrorShown(){

    }

    public void hideError(){
        super.hideError();
    }

    @Override
    protected boolean shouldAutoProceed() {
        return false;
    }
}
