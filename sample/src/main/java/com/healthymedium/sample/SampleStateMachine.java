package com.wustl.sample;

import com.wustl.arc.study.StateMachine;

public class SampleStateMachine extends StateMachine {


    public SampleStateMachine() {
        super();
    }


    @Override
    public void decidePath(){

    }

    @Override
    protected void setupPath(){
        addContextSurvey();
    }

}
