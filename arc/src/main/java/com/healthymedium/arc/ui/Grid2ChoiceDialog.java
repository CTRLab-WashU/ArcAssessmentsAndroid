package com.healthymedium.arc.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.PointerDialog;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2ChoiceDialog extends PointerDialog {

    Grid2ChoiceView phone;
    Grid2ChoiceView key;
    Grid2ChoiceView pen;
    TextView textViewGridDialog;

    public Grid2ChoiceDialog(Activity activity, View target, int pointerConfig) {
        super(activity, target, null, pointerConfig);
        init();
    }

    public Grid2ChoiceDialog(Activity activity, View target) {
        super(activity, target, null);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_grid2_choice,null);

        phone = view.findViewById(R.id.phone);
        key = view.findViewById(R.id.key);
        pen = view.findViewById(R.id.pen);

        phone.setImage(R.drawable.phone);
        key.setImage(R.drawable.key);
        pen.setImage(R.drawable.pen);

        textViewGridDialog = view.findViewById(R.id.textViewGridDialog);
        textViewGridDialog.setText("Select the item that was here");

        setElevation(ViewUtil.dpToPx(4));
        setRadius(16);
        setView(view);

        OnTouchListener listener = new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN) {

                    Grid2ChoiceView image = (Grid2ChoiceView) view;
                    int img = image.getDrawableImageId();
                    //TODO: return clicked image to GridTest2, set on selected grid coordinate
                     Grid2ChoiceDialog.super.dismiss();
                }
                return view.performClick();
            }
        };

        phone.setOnTouchListener(listener);
        key.setOnTouchListener(listener);
        pen.setOnTouchListener(listener);
    }

}