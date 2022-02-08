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
package edu.wustl.arc.paths.availability;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import edu.wustl.arc.paths.questions.QuestionTime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.wustl.arc.assessments.R;

import edu.wustl.arc.study.CircadianClock;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;

@SuppressLint("ValidFragment")
public class AvailabilityBed extends QuestionTime {

    CircadianClock clock;
    int minWakeTime = 4;
    int maxWakeTime = 24;

    public AvailabilityBed() {
        super(true, ViewUtil.getString(R.string.availability_stop),"",null);
    }

    public AvailabilityBed(int minWakeTime, int maxWakeTime) {
        super(true, ViewUtil.getString(R.string.availability_stop),"",null);
        this.minWakeTime = minWakeTime;
        this.maxWakeTime = maxWakeTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(true);

        clock = Study.getParticipant().getCircadianClock();
        if(time==null && clock.hasBedRhythmChanged("Monday")) {
            time = clock.getRhythm("Monday").getBedTime();
        }

        LocalTime wakeTime = clock.getRhythm("Monday").getWakeTime();
        timeInput.placeRestrictions(wakeTime, minWakeTime, maxWakeTime);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock.getRhythm("Monday").setBedTime(timeInput.getTime());

                // Set all of the remaining days to the same wake and sleep times
                LocalTime bedTime = clock.getRhythm("Monday").getBedTime();
                clock.getRhythm("Tuesday").setBedTime(bedTime);
                clock.getRhythm("Wednesday").setBedTime(bedTime);
                clock.getRhythm("Thursday").setBedTime(bedTime);
                clock.getRhythm("Friday").setBedTime(bedTime);
                clock.getRhythm("Saturday").setBedTime(bedTime);
                clock.getRhythm("Sunday").setBedTime(bedTime);

                LocalTime wakeTime = clock.getRhythm("Monday").getWakeTime();
                clock.getRhythm("Tuesday").setWakeTime(wakeTime);
                clock.getRhythm("Wednesday").setWakeTime(wakeTime);
                clock.getRhythm("Thursday").setWakeTime(wakeTime);
                clock.getRhythm("Friday").setWakeTime(wakeTime);
                clock.getRhythm("Saturday").setWakeTime(wakeTime);
                clock.getRhythm("Sunday").setWakeTime(wakeTime);

                Study.openNextFragment();
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
    public Object onDataCollection(){
        return null;
    }

}
