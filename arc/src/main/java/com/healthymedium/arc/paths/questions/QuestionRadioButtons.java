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
package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.healthymedium.arc.ui.RadioButton;
import com.healthymedium.arc.paths.templates.QuestionTemplate;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class QuestionRadioButtons extends QuestionTemplate {

    List<RadioButton> buttons;
    List<String> options;
    String selection;
    int lastIndex = -1;
    boolean allowHelp;

    public QuestionRadioButtons(boolean allowBack, boolean allowHelp, String header, String subheader, List<String> options) {
        super(allowBack,header,subheader);
        this.options = options;
        this.allowHelp = allowHelp;
        type = "choice";
    }

    public QuestionRadioButtons(boolean allowBack, boolean allowHelp, String header, String subheader, List<String> options, String button) {
        super(allowBack,header,subheader, button);
        this.options = options;
        this.allowHelp = allowHelp;
        type = "choice";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(allowHelp);

        buttons = new ArrayList<>();
        int index=0;
        for(String option : options){
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(option);

            final int radioButtonIndex = index;
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    response_time = System.currentTimeMillis();
                    if(b) {
                        if (radioButtonIndex != lastIndex) {
                            if (lastIndex != -1) {
                                buttons.get(lastIndex).setChecked(false);
                            }
                            //buttons.get(radioButtonIndex).setChecked(true);
                            selection = options.get(radioButtonIndex);
                            lastIndex = radioButtonIndex;


                        }
                        if (!buttonNext.isEnabled()) {
                            buttonNext.setEnabled(true);
                            onNextButtonEnabled(true);
                        }
                    }
                }
            });
            buttons.add(radioButton);
            content.addView(radioButton);
            index++;
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(buttons.size()>0 && lastIndex>-1) {
            buttons.get(lastIndex).setChecked(true);
        }
    }

    @Override
    public Object onValueCollection(){
        return lastIndex;
    }

    @Override
    public String onTextValueCollection(){
        return selection;
    }

}
