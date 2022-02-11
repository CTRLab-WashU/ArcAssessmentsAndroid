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
package edu.wustl.arc.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import edu.wustl.arc.assessments.R;

import java.util.ArrayList;
import java.util.List;

public class DurationInput extends FrameLayout {

    NumberPicker minutePicker;
    NumberPicker hourPicker;
    TextView textViewMinute;
    TextView textViewHour;

    Listener listener;
    int minutes = 5;

    public DurationInput(Context context) {
        super(context);
        init(context);
    }

    public DurationInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DurationInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_duration_input,this);

        textViewHour = view.findViewById(R.id.textViewHour);
        hourPicker = view.findViewById(R.id.hourPicker);
        hourPicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        hourPicker.setWrapSelectorWheel(false);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(0);
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minutes = (minutePicker.getValue()*5);
                minutes += newVal*60;

                if(listener!=null){
                    listener.onDurationChanged(minutes);
                }

            }
        });

        textViewMinute = view.findViewById(R.id.textViewMinute);
        minutePicker = view.findViewById(R.id.minutePicker);
        minutePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        minutePicker.setWrapSelectorWheel(false);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue((60 / 5) - 1);
        List<String> displayedValues = new ArrayList<>();
        for (int i = 0; i < 60; i += 5) {
            displayedValues.add(String.format("%01d", i));
        }
        minutePicker.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minutes = (newVal*5);
                minutes += hourPicker.getValue()*60;

                if(listener!=null){
                    listener.onDurationChanged(minutes);
                }
            }
        });
    }

    public void setDuration(int minutes){
        this.minutes = minutes;
        if(minutePicker!=null && hourPicker!=null){
            minutePicker.setValue((((minutes)%60)/5));
            hourPicker.setValue(minutes/60);
        }
    }

    public int getDuration(){
        return minutes;
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public interface Listener{
        void onDurationChanged(int minutes);
    }

}
