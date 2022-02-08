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
package com.healthymedium.arc.paths.setup_v2;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.api.models.AuthDetails;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.setup_v2.Setup2Phone;
import com.healthymedium.arc.paths.setup.SetupAuthCode;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class Setup2ParticipantConfirm extends Setup2Template {

    CharSequence previousCharacterSequence = "";

    public Setup2ParticipantConfirm(int firstDigitCount, int secondDigitCount) {
        super(firstDigitCount, secondDigitCount, ViewUtil.getString(R.string.login_confirm_ARCID));
    }

    @Override
    protected boolean isFormValid(CharSequence sequence) {
        if (sequence.toString().equals(previousCharacterSequence.toString())) {
            return true;
        } else {
            showError(Application.getInstance().getResources().getString(R.string.login_error1));
            return false;
        }
    }

    @Override
    protected boolean shouldAutoProceed() {
        return true;
    }

    @Override
    public void onResume() {
        String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
        previousCharacterSequence = id;
        super.onResume();
    }

    @Override
    protected void onNextRequested() {
        super.onNextRequested();

        if(Config.REST_BLACKHOLE) {
            Study.getInstance().openNextFragment();
            return;
        }

        SetupPathData setupPathData = ((SetupPathData)Study.getCurrentSegmentData());
        loadingDialog = new LoadingDialog();
        loadingDialog.show(getFragmentManager(),"LoadingDialog");
        Study.getRestClient().requestAuthDetails(setupPathData.id, authDetailsListener);
    }

    private boolean fragmentExists(PathSegment path, Class tClass) {
        int last = path.fragments.size()-1;
        String oldName = path.fragments.get(last).getSimpleTag();
        String newName = tClass.getSimpleName();
        return oldName.equals(newName);
    }

    RestClient.Listener authDetailsListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            loadingDialog.dismiss();

            SetupError error = parseForError(response,false);
            if(error.string!=null) {
                showError(error);
                return;
            }

            AuthDetails authDetails = response.getOptionalAs(AuthDetails.class);
            PathSegment path = Study.getCurrentSegment();

            String authType = authDetails.getType();
            int authLength = authDetails.getCodeLength();
            String regionCode = authDetails.getCountryCode();

            if(authType.equals(AuthDetails.TYPE_RATER)){
                if(!fragmentExists(path, Setup2AuthRater.class)){
                    path.fragments.add(new Setup2AuthRater(6));
                }
            } else if(authType.equals(AuthDetails.TYPE_MANUAL)) {
                if(!fragmentExists(path,Setup2AuthManual.class)){
                    path.fragments.add(new Setup2AuthManual(authLength));
                }
                loadingDialog.dismiss();
            }
            Study.openNextFragment();
        }

        @Override
        public void onFailure(RestResponse response) {
            SetupError error = parseForError(response,true);
            showError(error);
            loadingDialog.dismiss();
        }
    };

    RestClient.Listener verificationCodeListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            SetupError error = parseForError(response,false);
            loadingDialog.dismiss();
            if(error.string==null) {
                Study.openNextFragment();
            } else {
                showError(error);
            }
        }

        @Override
        public void onFailure(RestResponse response) {
            SetupError error = parseForError(response,true);
            showError(error);
            loadingDialog.dismiss();
        }
    };

}
