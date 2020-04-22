package com.healthymedium.arc.paths.battery_optimization;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.StateInfoTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.Phrase;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class BatteryOptimizationOverview extends StateInfoTemplate {

    String body = "Your phone has a setting to help preserve your battery life, and it is called a “battery optimization” setting.<br><br>The {APP_NAME} app will have minimal impact on the battery life of your phone. It is recommended that you turn off the battery optimization setting for the {APP_NAME} app. Doing so will help make sure you receive test notifications on time.";

    public BatteryOptimizationOverview() {
        super(false,
                "Battery Optimization",
                null,
                "",
                ViewUtil.getString(R.string.button_next)
                );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        if(Study.getParticipant().hasBeenShownBatteryOptimizationOverview()){
            Study.openNextFragment();
            return view;
        }

        TextView textViewBody = view.findViewById(R.id.textViewBody);

        Phrase phrase = new Phrase(body);
        phrase.replace(R.string.token_app_name,R.string.app_name);
        textViewBody.setText(phrase.toHtmlString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                Study.openNextFragment();
            }
        });

        return view;
    }

}
