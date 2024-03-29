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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.wustl.arc.ui.DurationInput;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.paths.templates.QuestionTemplate;
import edu.wustl.arc.utilities.ViewUtil;

import static java.lang.Math.floor;

@SuppressLint("ValidFragment")
public class QuestionDuration extends QuestionTemplate {

    DurationInput durationInput;
    int minutes = 5;
    boolean valid = true;

    public QuestionDuration(boolean allowBack, String header, String subheader) {
        super(allowBack,header,subheader, ViewUtil.getString(R.string.button_submittime));
        type = "duration";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        durationInput = new DurationInput(getContext());
        durationInput.setListener(new DurationInput.Listener() {
            @Override
            public void onDurationChanged(int minutes) {
                response_time = System.currentTimeMillis();
                if(minutes==0){
                    buttonNext.setEnabled(false);
                    valid = false;
                } else if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                    valid = true;
                }
            }
        });
        content.addView(durationInput);
        content.setGravity(Gravity.CENTER);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        minutes = durationInput.getDuration();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(durationInput!=null){
            durationInput.setDuration(minutes);
            buttonNext.setEnabled(valid);
        }
    }

    @Override
    public Object onValueCollection(){
        if(durationInput==null){
            return null;
        }
        int hours = (int)floor(minutes/60);
        String value = minutes%60+" min";
        if(hours>0){
            value = hours +" hr " + value;
        }
        return value;
    }

}
