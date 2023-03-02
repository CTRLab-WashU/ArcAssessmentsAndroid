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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import edu.wustl.arc.assessments.R;
import edu.wustl.arc.ui.Rating;
import edu.wustl.arc.paths.templates.QuestionTemplate;
import edu.wustl.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class QuestionRating extends QuestionTemplate {

    private static final String HINT_QUESTION_RATING = "HINT_QUESTION_RATING";

    HintPointer pointer;
    float value = 0.5f;
    Rating rating;
    String high;
    String low;

    public QuestionRating(boolean allowBack, String header, String subheader, String low, String high) {
        super(allowBack,header,subheader);
        this.high = high;
        this.low = low;
        type = "slider";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        rating = new Rating(getContext());
        rating.setLowText(low);
        rating.setHighText(high);
        rating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!buttonNext.isEnabled()){
                    buttonNext.setEnabled(true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                response_time = System.currentTimeMillis();
                if(pointer!=null){
                    pointer.dismiss();
                    pointer = null;
                }
            }
        });

        content.addView(rating);

        return view;
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        if(!Hints.hasBeenShown(HINT_QUESTION_RATING)){
            pointer = new HintPointer(getActivity(),rating.getSeekBar(),true,true);
            pointer.setText(ViewUtil.getString(R.string.popup_drag));
            pointer.show();
            Hints.markShown(HINT_QUESTION_RATING);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        value = rating.getValue();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(rating !=null) {
            rating.setValue(value);
        }
    }

    @Override
    public Object onValueCollection(){
        if(rating!=null){
            return rating.getValue();
        }
        return null;
    }

}
