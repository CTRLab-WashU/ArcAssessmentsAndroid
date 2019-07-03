package com.healthymedium.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.custom.Button;
import com.healthymedium.arc.custom.DialogButtonTutorial;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.NavigationManager;

public class GridTutorial extends BaseFragment {

    int selectedCount;

    GridLayout gridLayout;
    GridLayout gridLayoutLetters;

    DialogButtonTutorial centerPopup;

    FrameLayout fullScreenGray;
    FrameLayout progressBarGradient;

    ImageView closeButton;
    ImageView checkmark;

    TextView textViewComplete;

    Button endButton;

    private int shortAnimationDuration;

    Handler handler;
    Runnable runnableProceedToPartTwo = new Runnable() {
        @Override
        public void run() {
            fadeInView(centerPopup, 1f);
            fadeInView(fullScreenGray, 0.9f);

            centerPopup.header.setText("Great!");
            centerPopup.body.setText("Let's proceed to part two.");
            centerPopup.button.setText("Next");

            centerPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fadeOutView(centerPopup);
                    fadeOutView(fullScreenGray);
                    setInitialLetterLayout();
                }
            });
        }
    };

    Runnable runnableTapTheFs = new Runnable() {
        @Override
        public void run() {
            fadeInView(centerPopup, 1f);
            fadeInView(fullScreenGray, 0.9f);

            centerPopup.header.setText("Nice work!");
            centerPopup.body.setText("Don't worry if you didn't find them all.");
            centerPopup.button.setText("Next");

            // TODO
            // This will need to be split up into a couple functions once the rest of the tutorial's functionality is implemented
            centerPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fadeOutView(centerPopup);
                    fadeOutView(fullScreenGray);
                    gridLayout.setVisibility(View.VISIBLE);
                    gridLayoutLetters.setVisibility(View.GONE);

                    getImageView(3,0).setImageResource(0);
                    getImageView(2,2).setImageResource(0);
                    getImageView(1,3).setImageResource(0);

                    selectedCount = 0;

                    View.OnTouchListener listener = new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {
                            int action = event.getAction();
                            switch (action){
                                case MotionEvent.ACTION_DOWN:
                                    view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridSelected));
                                    selectedCount += 1;
                                    break;
                                case MotionEvent.ACTION_UP:
                                    break;
                            }

                            if (selectedCount == 3) {
                                showComplete();
                            }

                            return false;
                        }
                    };

                    gridLayout.getChildAt(8).setTag(R.id.tag_color,R.color.gridNormal);
                    gridLayout.getChildAt(8).setOnTouchListener(listener);

                    gridLayout.getChildAt(12).setTag(R.id.tag_color,R.color.gridNormal);
                    gridLayout.getChildAt(12).setOnTouchListener(listener);

                    gridLayout.getChildAt(15).setTag(R.id.tag_color,R.color.gridNormal);
                    gridLayout.getChildAt(15).setOnTouchListener(listener);
                }
            });
        }
    };

    public GridTutorial() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_tutorial, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);
        gridLayoutLetters = view.findViewById(R.id.gridLettersLayout);

        centerPopup = view.findViewById(R.id.centerPopup);

        fullScreenGray = view.findViewById(R.id.fullScreenGray);
        progressBarGradient = view.findViewById(R.id.progressBarGradient);

        closeButton = view.findViewById(R.id.closeButton);
        checkmark = view.findViewById(R.id.checkmark);

        textViewComplete = view.findViewById(R.id.textViewComplete);

        endButton = view.findViewById(R.id.endButton);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });

        setInitialGridLayout();

        return view;
    }

    private void setInitialGridLayout() {
        fadeInView(centerPopup, 1f);
        fadeInView(fullScreenGray, 0.9f);

        centerPopup.header.setText("The items will be placed in a grid of boxes. Remember which box each item is in. You will have 3 seconds.");
        centerPopup.body.setText("");
        centerPopup.button.setText("I'm Ready");

        getImageView(3,0).setImageResource(R.drawable.phone);
        getImageView(2,2).setImageResource(R.drawable.pen);
        getImageView(1,3).setImageResource(R.drawable.key);

        centerPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeOutView(centerPopup);
                fadeOutView(fullScreenGray);

                handler = new Handler();
                handler.postDelayed(runnableProceedToPartTwo,3000);
            }
        });
    }

    private void setInitialLetterLayout() {
        gridLayout.setVisibility(View.GONE);
        gridLayoutLetters.setVisibility(View.VISIBLE);

        Typeface font = Fonts.georgia;
        int size = gridLayoutLetters.getChildCount();
        for(int i=0;i<size;i++){
            ((TextView)gridLayoutLetters.getChildAt(i)).setTypeface(font);
        }

        fadeInView(centerPopup, 1f);
        fadeInView(fullScreenGray, 0.9f);

        centerPopup.header.setText("Perfect!");
        centerPopup.body.setText("Now: Tap all the F's you see as quickly as you can. You will have 3 seconds.");
        centerPopup.button.setText("I'm Ready");

        centerPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeOutView(centerPopup);
                fadeOutView(fullScreenGray);
                tapLetters();
            }
        });
    }

    private void tapLetters() {
        handler = new Handler();
        handler.postDelayed(runnableTapTheFs,3000);

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        if(view.getTag() == null){
                            view.setTag(true);
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                            return false;
                        }
                        if(view.getTag().equals(false)) {
                            view.setTag(true);
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                        } else {
                            view.setTag(false);
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        };

        int size = gridLayoutLetters.getChildCount();
        for(int i=0;i<size;i++){
            gridLayoutLetters.getChildAt(i).setOnTouchListener(listener);
        }
    }

    private ImageView getImageView(int row, int col){
        return (ImageView)gridLayout.getChildAt((5*row)+col);
    }

    private void fadeInView(View view, Float opacity) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(opacity)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }

    private void fadeOutView(final View view) {
        view.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    private void showComplete() {
        fadeInView(checkmark, 1f);
        fadeInView(textViewComplete, 1f);
        fadeInView(endButton, 1f);

        fadeOutView(gridLayout);

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });
    }
}