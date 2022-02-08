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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.wustl.arc.api.RestClient;
import edu.wustl.arc.api.RestResponse;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.core.LoadingDialog;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.SetupPathData;
import edu.wustl.arc.paths.templates.QuestionTemplate;
import edu.wustl.arc.study.PathSegment;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.ui.PhoneInput;
import edu.wustl.arc.utilities.KeyboardWatcher;
import edu.wustl.arc.utilities.ViewUtil;

import androidx.annotation.Nullable;

@SuppressLint("ValidFragment")
public class Setup2Phone extends QuestionTemplate {

    String value;
    PhoneInput input;
    int maxLength;
    protected LoadingDialog loadingDialog;
    protected TextView textViewError;


    public Setup2Phone(boolean allowBack, String header, String subheader,
                       int maxLength, String initialValue) {
        super(allowBack,header,subheader);
        this.maxLength = maxLength;
        type = "multilineText";
        this.value = initialValue;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        input = new PhoneInput(getContext());
        input.setMaxLength(maxLength);
        input.setListener(new PhoneInput.Listener() {
            @Override
            public void onValueChanged() {
                if(isErrorShowing()){
                    hideError();
                }
                response_time = System.currentTimeMillis();
                if(input.getString().isEmpty()){
                    buttonNext.setEnabled(false);
                } else if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                }
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean done = false;
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    done = true;
                }

                if(keyEvent!=null){
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                        done = true;
                    }
                }

                if(done && input.length()>0){
//                    buttonNext.performClick();
                }
                return false;
            }
        });
        input.setSingleLine();
        content.addView(input);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = ViewUtil.dpToPx(32);
        params.topMargin = ViewUtil.dpToPx(19);
        content.setLayoutParams(params);

        textViewError = new TextView(getContext());
        textViewError.setTextSize(16);
        textViewError.setTextColor(ViewUtil.getColor(R.color.red));
        textViewError.setVisibility(View.INVISIBLE);
        content.addView(textViewError);

        if (value != null) {
            input.setText(value);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(input !=null) {
            value = input.getString();
        }
        hideKeyboard();
        getMainActivity().removeKeyboardListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMainActivity().setKeyboardListener(keyboardToggleListener);

    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        if(input != null) {
            input.setText(value);
            input.requestFocus();
            showKeyboard(input);
        }
    }

    @Override
    public Object onValueCollection(){
        if(input != null) {
            value = input.getString();
        }
        return value;
    }

    @Override
    protected void onNextRequested() {
        if(isErrorShowing()){
            hideError();
        }

        value = input.getString();

        if(Config.REST_BLACKHOLE){
            String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
            Study.getParticipant().getState().id = id;
            Study.getInstance().openNextFragment();
            return;
        }

        loadingDialog = new LoadingDialog();
        loadingDialog.show(getFragmentManager(),"LoadingDialog");
        SetupPathData pathData = (SetupPathData)Study.getCurrentSegmentData();
        Study.getRestClient().requestVerificationCode(value, requestVerificationListener);
    }

    RestClient.Listener requestVerificationListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {

            loadingDialog.dismiss();

            if(response.code >= 400 || !response.successful) {
                showError(getResources().getString(R.string.login_error1));
                loadingDialog.dismiss();
                return;
            }

            Study.openNextFragment();
        }

        @Override
        public void onFailure(RestResponse response) {
            showError(getResources().getString(R.string.login_error1));
            loadingDialog.dismiss();
        }
    };

    public void showError(String error) {
        textViewError.setVisibility(View.VISIBLE);
        textViewError.setText(error);
    }

    public void hideError(){
        textViewError.setVisibility(View.INVISIBLE);
        textViewError.setText("");
    }

    public boolean isErrorShowing(){
        return textViewError.getVisibility()==View.VISIBLE;
    }

    private boolean fragmentExists(PathSegment path, Class tClass) {
        int last = path.fragments.size()-1;
        String oldName = path.fragments.get(last).getSimpleTag();
        String newName = tClass.getSimpleName();
        return oldName.equals(newName);
    }

    KeyboardWatcher.OnKeyboardToggleListener keyboardToggleListener = new KeyboardWatcher.OnKeyboardToggleListener() {
        @Override
        public void onKeyboardShown(int keyboardSize) {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.GONE);
            }
        }

        @Override
        public void onKeyboardClosed() {
            if(buttonNext!=null){
                buttonNext.setVisibility(View.VISIBLE);
            }
        }
    };
}
