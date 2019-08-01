package com.healthymedium.arc.paths.informative;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

public class EarningsScreen extends BaseFragment {

    TextView earningsBody1;
    TextView bonusBody;
    TextView fourOfFourBody;
    TextView twoADayBody;
    TextView twentyOneTestsBody;

    public EarningsScreen() {
        allowBackPress(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);

        earningsBody1 = view.findViewById(R.id.earningsBody1);
        earningsBody1.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_body1)));

        bonusBody = view.findViewById(R.id.bonusBody);
        bonusBody.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_bonus_body)));

        fourOfFourBody = view.findViewById(R.id.fourOfFourBody);
        fourOfFourBody.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_4of4_body)));

        twoADayBody = view.findViewById(R.id.twoADayBody);
        twoADayBody.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_2aday_body)));

        twentyOneTestsBody = view.findViewById(R.id.twentyOneTestsBody);
        twentyOneTestsBody.setText(Html.fromHtml(ViewUtil.getString(R.string.earnings_21tests_body)));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
    }
}
