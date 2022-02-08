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
package com.healthymedium.arc.paths.availability;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.path_data.AvailabilityPathData;
import com.healthymedium.arc.paths.questions.QuestionPolar;
import com.healthymedium.arc.study.CircadianClock;
import com.healthymedium.arc.study.CircadianRhythm;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;

public class AvailabilityWeekdayConfirm extends QuestionPolar {

    CircadianClock clock;
    AvailabilityPathData data;

    public AvailabilityWeekdayConfirm() {
        super(true,"","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        data = (AvailabilityPathData) Study.getInstance().getCurrentSegmentData();

        CircadianRhythm monday = clock.getRhythm("Monday");
        String wakeTime = monday.getWakeTime().toString("h:mm a");
        String bedTime = monday.getBedTime().toString("h:mm a");

        String header = "";
//        String header = ViewUtil.getString(R.string.availability_same_tuesday_friday).replace("{TIME1}", wakeTime);
//        header = header.replace("{TIME2}", bedTime);



        textViewHeader.setText(Html.fromHtml(header));

        buttonNext.setText(ViewUtil.getString(R.string.button_submittime));
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.weekdaySame = answerIsYes;
                if(answerIsYes){
                    LocalTime bedTime = clock.getRhythm("Monday").getBedTime();
                    clock.getRhythm("Tuesday").setBedTime(bedTime);
                    clock.getRhythm("Wednesday").setBedTime(bedTime);
                    clock.getRhythm("Thursday").setBedTime(bedTime);
                    clock.getRhythm("Friday").setBedTime(bedTime);

                    LocalTime wakeTime = clock.getRhythm("Monday").getWakeTime();
                    clock.getRhythm("Tuesday").setWakeTime(wakeTime);
                    clock.getRhythm("Wednesday").setWakeTime(wakeTime);
                    clock.getRhythm("Thursday").setWakeTime(wakeTime);
                    clock.getRhythm("Friday").setWakeTime(wakeTime);
                }
                Study.setCurrentSegmentData(data);

                if(data.weekdaySame){
                    Study.openNextFragment(8);
                } else {
                    Study.openNextFragment();
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        Study.getParticipant().save();
        super.onPause();
    }

    @Override
    protected void onNextButtonEnabled(boolean enabled){
        buttonNext.setText(ViewUtil.getString(R.string.button_next));
    }

    @Override
    public Object onDataCollection(){
        return null;
    }

}
