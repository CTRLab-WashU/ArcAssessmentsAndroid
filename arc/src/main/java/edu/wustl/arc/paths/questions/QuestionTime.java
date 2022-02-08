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
package edu.wustl.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import edu.wustl.arc.hints.HintPointer;
import edu.wustl.arc.hints.Hints;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.paths.informative.ContactScreen;
import edu.wustl.arc.paths.informative.HelpScreen;
import edu.wustl.arc.ui.TimeInput;

import edu.wustl.arc.assessments.R;
import edu.wustl.arc.paths.templates.QuestionTemplate;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;

import static edu.wustl.arc.core.Config.USE_HELP_SCREEN;

@SuppressLint("ValidFragment")
public class QuestionTime extends QuestionTemplate {

    private static final String HINT_QUESTION_TIME = "HINT_QUESTION_TIME";

    protected HintPointer pointer;
    protected TimeInput timeInput;
    protected LocalTime time;
    boolean enabled = false;
    boolean showHint;
    boolean didDismissPointer = false;
    boolean didPause = false;
    String disabledTxt;

    public QuestionTime(boolean allowBack, String header, String subheader,@Nullable LocalTime defaultTime, String buttonText) {
        super(allowBack,header,subheader, buttonText);
        disabledTxt = buttonText;
        time = defaultTime;
        type = "time";
    }

    public QuestionTime(boolean allowBack, String header, String subheader,@Nullable LocalTime defaultTime) {
        super(allowBack,header,subheader, ViewUtil.getString(R.string.button_submittime));
        disabledTxt = ViewUtil.getString(R.string.button_submittime);
        time = defaultTime;
        type = "time";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);


        timeInput = new TimeInput(getContext());
        timeInput.setValidity(false);
        timeInput.setListener(new TimeInput.Listener() {
            public void onValidityChanged(boolean valid) {
                dismissPointer();
                if(buttonNext.isEnabled() != valid){
                    enabled = valid;
                    String string = enabled?ViewUtil.getString(R.string.button_next):disabledTxt;
                    buttonNext.setText(string);
                    buttonNext.setEnabled(enabled);
                }
            }

            @Override
            public void onTimeChanged() {
                response_time = System.currentTimeMillis();
                dismissPointer();
            }
        });


        buttonNext.setEnabled(false);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(response_time==0.0){
                    response_time = System.currentTimeMillis();
                }
                dismissPointer();
                Study.getInstance().openNextFragment();

            }
        });

        textViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPointer();

                BaseFragment helpScreen;
                if (USE_HELP_SCREEN) {
                    helpScreen = new HelpScreen();
                } else {
                    helpScreen = new ContactScreen();
                }
                NavigationManager.getInstance().open(helpScreen);
            }
        });

        content.setGravity(Gravity.CENTER);
        content.addView(timeInput);



        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        if (didDismissPointer || didPause) {
            return;
        }

        if(!Hints.hasBeenShown(HINT_QUESTION_TIME)){
            pointer = new HintPointer(getMainActivity(),timeInput.getTimePicker(),true,false);
            pointer.setText(ViewUtil.getString(R.string.popup_scroll));
            pointer.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        didPause = true;
        time = timeInput.getTime();
        dismissPointer();
        didDismissPointer = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        didPause = false;
        if(time !=null) {
            timeInput.setTime(time,true);
        }
        String string = enabled?ViewUtil.getString(R.string.button_next):disabledTxt;
        buttonNext.setText(string);
        buttonNext.setEnabled(enabled);
    }

    @Override
    public Object onValueCollection(){
        if(timeInput!=null){
            time = timeInput.getTime();
        }
        if(time!=null){
            return time.toString("h:mm a");
        }
        return null;
    }

    protected void dismissPointer() {
        didDismissPointer = true;
        if(pointer!=null){
            pointer.dismiss();
            pointer = null;
        }
        if(!Hints.hasBeenShown(HINT_QUESTION_TIME)){
            Hints.markShown(HINT_QUESTION_TIME);
        }
    }
}
