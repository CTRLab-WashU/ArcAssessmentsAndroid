package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthymedium.arc.custom.TimeInput;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.LocalTime;

@SuppressLint("ValidFragment")
public class QuestionTime extends QuestionTemplate {

    protected TimeInput timeInput;
    protected LocalTime time;
    boolean enabled;

    public QuestionTime(boolean allowBack, String header, String subheader,@Nullable LocalTime defaultTime) {
        super(allowBack,header,subheader, ViewUtil.getString(R.string.button_submit));
        time = defaultTime;
        type = "time";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        setHelpVisible(false);

        timeInput = new TimeInput(getContext());
        timeInput.setListener(new TimeInput.Listener() {
            public void onValidityChanged(boolean valid) {
                if(buttonNext.isEnabled() != valid){
                    enabled = valid;
                    buttonNext.setEnabled(enabled);
                }
            }

            @Override
            public void onTimeChanged() {
                response_time = System.currentTimeMillis();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(response_time==0.0){
                    response_time = System.currentTimeMillis();
                }
                Study.getInstance().openNextFragment();

            }
        });

        content.setGravity(Gravity.CENTER);
        content.addView(timeInput);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        time = timeInput.getTime();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(time !=null) {
            timeInput.setTime(time);
        }
        enabled = timeInput.isTimeValid();
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

}
