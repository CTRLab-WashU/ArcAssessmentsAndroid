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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import edu.wustl.arc.ui.RadioButton;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.paths.templates.AltQuestionTemplate;
import edu.wustl.arc.utilities.ViewUtil;

// a yes or no question
@SuppressLint("ValidFragment")
public class QuestionPolarAlt extends AltQuestionTemplate {

    RadioButton yesButton;
    RadioButton noButton;
    protected boolean answerIsYes;
    protected boolean answered;

    String yesText;
    String noText;

    public QuestionPolarAlt(boolean allowBack, String header, String subheader) {
        super(allowBack,header,subheader);
        type = "choice";
    }

    public QuestionPolarAlt(boolean allowBack, String header, String subheader, String yesAnswer, String noAnswer) {
        super(allowBack,header,subheader);
        type = "choice";
        yesText = yesAnswer;
        noText = noAnswer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        yesButton = new RadioButton(getContext());

        if (yesText == null) {
            yesButton.setText(ViewUtil.getString(R.string.radio_yes));
        } else {
            yesButton.setText(yesText);
        }

        yesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                answered = true;
                if(b){
                    response_time = System.currentTimeMillis();
                    answerIsYes = true;
                    noButton.setChecked(false);
                }

                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    onNextButtonEnabled(true);
                    buttonNext.setText(ViewUtil.getString(R.string.button_next));
                }
            }
        });

        noButton = new RadioButton(getContext());

        if (noText == null) {
            noButton.setText(ViewUtil.getString(R.string.radio_no));
        } else {
            noButton.setText(noText);
        }

        noButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                answered = true;
                if(b){
                    response_time = System.currentTimeMillis();
                    answerIsYes = false;
                    yesButton.setChecked(false);
                }

                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    onNextButtonEnabled(true);
                    buttonNext.setText(ViewUtil.getString(R.string.button_next));
                }
            }
        });

        content.addView(yesButton);
        content.addView(noButton);
        buttonNext.setText(ViewUtil.getString(R.string.button_confirm));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(answered){
            if(answerIsYes){
                yesButton.setChecked(true);
            } else {
                noButton.setChecked(true);
            }
        }
    }

    @Override
    public Object onValueCollection(){
        if(!answered){
            return -1;
        }
        return answerIsYes ? 1:0;
    }

    @Override
    public String onTextValueCollection(){
        if(answered){
            return answerIsYes ? "Yes":"No";
        }
        return null;
    }
}
