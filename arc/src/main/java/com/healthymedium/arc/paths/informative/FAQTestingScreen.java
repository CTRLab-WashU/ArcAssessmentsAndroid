package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class FAQTestingScreen extends BaseFragment {

    TextView textViewBack;

    RelativeLayout test_q1;
    RelativeLayout test_q2;
    RelativeLayout test_q3;
    RelativeLayout test_q4;
    RelativeLayout test_q5;
    RelativeLayout test_q6;
    RelativeLayout test_q7;
    RelativeLayout test_q8;
    RelativeLayout test_q9;
    RelativeLayout test_q10;

    public FAQTestingScreen() {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq_testing, container, false);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });
        textViewBack.setVisibility(View.VISIBLE);

        test_q1 = view.findViewById(R.id.test_q1);
        test_q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q1), ViewUtil.getString(R.string.faq_testing_a1));
                NavigationManager.getInstance().open(fragment);
            }
        });

        test_q2 = view.findViewById(R.id.test_q2);
        test_q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q2), ViewUtil.getString(R.string.faq_testing_a2));
                NavigationManager.getInstance().open(fragment);
            }
        });

        test_q3 = view.findViewById(R.id.test_q3);
        test_q3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q3), ViewUtil.getString(R.string.faq_testing_a3));
                NavigationManager.getInstance().open(fragment);
            }
        });

        test_q4 = view.findViewById(R.id.test_q4);
        test_q4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q4), ViewUtil.getString(R.string.faq_testing_a4));
                NavigationManager.getInstance().open(fragment);
            }
        });

        test_q5 = view.findViewById(R.id.test_q5);
        test_q5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q5), ViewUtil.getString(R.string.faq_testing_a5));
                NavigationManager.getInstance().open(fragment);
            }
        });

        test_q6 = view.findViewById(R.id.test_q6);
        test_q6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q6), ViewUtil.getString(R.string.faq_testing_a6));
                NavigationManager.getInstance().open(fragment);
            }
        });

        test_q7 = view.findViewById(R.id.test_q7);
        test_q7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q7), ViewUtil.getString(R.string.faq_testing_a7));
                NavigationManager.getInstance().open(fragment);
            }
        });

        test_q8 = view.findViewById(R.id.test_q8);
        test_q8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q8), ViewUtil.getString(R.string.faq_testing_a8));
                NavigationManager.getInstance().open(fragment);
            }
        });

        test_q9 = view.findViewById(R.id.test_q9);
        test_q9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q9), ViewUtil.getString(R.string.faq_testing_a9));
                NavigationManager.getInstance().open(fragment);
            }
        });

        test_q10 = view.findViewById(R.id.test_q10);
        test_q10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(ViewUtil.getString(R.string.faq_testing_q10), ViewUtil.getString(R.string.faq_testing_a10));
                NavigationManager.getInstance().open(fragment);
            }
        });

        return view;
    }
}