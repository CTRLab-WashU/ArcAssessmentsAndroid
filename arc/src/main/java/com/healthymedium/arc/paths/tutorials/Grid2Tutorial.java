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
package com.healthymedium.arc.paths.tutorials;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.util.Log;
import com.healthymedium.arc.hints.HintHighlighter;
import com.healthymedium.arc.hints.HintPointer;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.TutorialTemplate;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.ui.Grid2BoxView;
import com.healthymedium.arc.ui.Grid2ChoiceDialog;
import com.healthymedium.arc.ui.Grid2LetterView;
import com.healthymedium.arc.ui.base.PointerDrawable;
import com.healthymedium.arc.utilities.ViewUtil;

public class Grid2Tutorial extends TutorialTemplate {

    public static final String HINT_PREVENT_TUTORIAL_CLOSE_GRIDS = "HINT_PREVENT_TUTORIAL_CLOSE_GRIDS";
    private final int REMIND_ME_HINT_DELAY = 5000;

    int selectedCount = 0;
    boolean phoneSelected = false;
    boolean keySelected = false;
    boolean penSelected = false;
    boolean othersReady = false;

    boolean firstGridSelected = false;

    boolean userMovedOrRemoved = false;
    boolean mechanicsHintShown = false;
    HintPointer mechanicsHint;

    View items;
    RelativeLayout itemsLayout;

    View letters;
    GridLayout letterLayout;

    View grids;
    GridLayout gridLayout;

    Button continueButton;
    TextView gridTextView;
    TextView gridHintTextView;
    View bottomAnchor;

    HintPointer remindMeHint;
    HintPointer otherItemsHint;

    Grid2ChoiceDialog dialog;
    Grid2BoxView.Listener boxViewListener;
    Grid2ChoiceDialog.Listener dialogListener;

    public Grid2Tutorial() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        bottomAnchor = view.findViewById(R.id.bottomAnchor);

        items = inflater.inflate(R.layout.fragment_grid2_tutorial_items, container, false);
        itemsLayout = items.findViewById(R.id.itemsLayout);

        grids = inflater.inflate(R.layout.fragment_grid2_test, container, false);
        gridLayout = grids.findViewById(R.id.gridLayout);
        continueButton = grids.findViewById(R.id.buttonContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userMovedOrRemoved) {
                    launchMechanicsTutorial();
                    return;
                }

                incrementProgress();
                fadeOutView(grids);
                showComplete();
            }
        });

        gridHintTextView = grids.findViewById(R.id.tapGridText);
        gridHintTextView.setVisibility(View.INVISIBLE);

        gridTextView = grids.findViewById(R.id.tapGridText);
        gridTextView.setText(Html.fromHtml(ViewUtil.getString(R.string.grids_tutorial_vb_place)));

        letters = inflater.inflate(R.layout.fragment_grid2_letters, container, false);
        letterLayout = letters.findViewById(R.id.gridLayout);

        adjustLayouts();

        remindMeHint = new HintPointer(getActivity(), bottomAnchor, false, true,false);
        register(remindMeHint);

    }

    @Override
    protected String getClosePreventionHintTag() {
        return HINT_PREVENT_TUTORIAL_CLOSE_GRIDS;
    }

    @Override
    protected int getProgressIncrement() {
        return 25;
    }

    private Grid2BoxView getGridView(int row, int col) {
        return (Grid2BoxView) gridLayout.getChildAt((gridLayout.getColumnCount() * row) + col);
    }

    private Grid2LetterView getLetterView(int row, int col) {
        return (Grid2LetterView) letterLayout.getChildAt((letterLayout.getColumnCount() * row) + col);
    }

    private void adjustLayouts() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        int auxHeight = ViewUtil.dpToPx(124);
        int viewHeight = displayHeight - ViewUtil.getStatusBarHeight() - ViewUtil.getNavBarHeight() - auxHeight;

        float aspectRatio = ((float) displayWidth) / ((float) viewHeight);

        int lettersHeight = viewHeight;
        int gridsHeight = viewHeight;

        if (aspectRatio < 0.75f) {
            lettersHeight = (int) (displayWidth / 0.75f);
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (0.75f * gridsHeight), LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = ViewUtil.dpToPx(26);
            layoutParams.bottomMargin = ViewUtil.dpToPx(2);
            layoutParams.gravity = Gravity.CENTER;
            gridLayout.setLayoutParams(layoutParams);
        }

        lettersHeight -= -ViewUtil.dpToPx(16);

        float letterRatio = ((float) letterLayout.getColumnCount()) / ((float) letterLayout.getRowCount());
        int lettersWidth = (int) (letterRatio * (lettersHeight));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(lettersWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ViewUtil.dpToPx(28);
        layoutParams.bottomMargin = ViewUtil.dpToPx(4);
        layoutParams.gravity = Gravity.CENTER;
        letterLayout.setLayoutParams(layoutParams);
    }


    @Override
    protected void setupInitialLayout() {
        setupStudySection();
    }

    private void setupStudySection() {
        Log.d(getTag(),"setupStudySection");
        getGridView(3, 0).setImage(R.drawable.pen);
        getGridView(1, 1).setImage(R.drawable.phone);
        getGridView(2, 3).setImage(R.drawable.key);
        fadeInView(grids);

        final HintPointer gridsHint = new HintPointer(getActivity(), gridLayout, true);
        register(gridsHint);

        gridsHint.setText(ViewUtil.getString(R.string.grids_tutorial_vb_step1));
        gridsHint.getShadow().addTarget(progressBar);
        gridsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridsHint.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Run after the user has studied the initial layout of the items in the grid
                        // Advances the user to setInitialLetterLayout(), the letter tapping test

                        incrementProgress();

                        final HintPointer partTwoHint = new HintPointer(getActivity(), gridLayout, true);
                        partTwoHint.getShadow().addTarget(progressBar);
                        register(partTwoHint);

                        partTwoHint.setText(ViewUtil.getString(R.string.popup_tutorial_part2));
                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                partTwoHint.dismiss();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setupLetterLayout();
                                    }
                                }, 600);
                            }
                        };
                        partTwoHint.addButton(ViewUtil.getString(R.string.button_next), listener);
                        partTwoHint.show();

                    }
                }, 3000);
            }
        });
        gridsHint.show();
    }


    // Displays the letters layout and prompts the user to tap a specific letter F
    private void setupLetterLayout() {
        Log.d(getTag(),"setupLetterLayout");
        getLetterView(0, 0).setF();
        getLetterView(0, 3).setF();
        getLetterView(1, 4).setF();
        getLetterView(3, 1).setF();
        getLetterView(3, 3).setF();
        getLetterView(3, 5).setF();
        getLetterView(5, 0).setF();
        getLetterView(5, 3).setF();
        getLetterView(6, 4).setF();
        getLetterView(8, 1).setF();
        getLetterView(8, 3).setF();
        getLetterView(8, 5).setF();

        fadeOutView(grids);
        fadeInView(letters);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final HintHighlighter pulsateF = new HintHighlighter(getActivity());
                register(pulsateF);

                pulsateF.addPulsingTarget(getLetterView(3, 1), getLetterView(3, 1).getWidth() / 2);
                pulsateF.addTarget(progressBar);
                pulsateF.show();

                final HintPointer tapThisFHint = new HintPointer(getActivity(), getLetterView(3, 1), true, false);
                register(tapThisFHint);

                tapThisFHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf1));
                tapThisFHint.show();

                View.OnTouchListener listener = new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        int action = event.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                Log.d(getTag(),"first f tapped");

                                getLetterView(3, 1).setOnTouchListener(null);
                                pulsateF.dismiss();
                                tapThisFHint.dismiss();
                                incrementProgress();

                                final HintPointer tapAllFsHint = new HintPointer(getActivity(), letterLayout, true);
                                register(tapAllFsHint);

                                tapAllFsHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf2));
                                tapAllFsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        tapAllFsHint.dismiss();
                                        setupLetterStop();
                                    }
                                });
                                tapAllFsHint.getShadow().addTarget(progressBar);
                                tapAllFsHint.show();
                        }
                        return false;
                    }
                };
                getLetterView(3, 1).setOnTouchListener(listener);

            }
        }, 500);
    }

    // Responds to letter that are tapped, changes their color
    private void setupLetterStop() {
        Log.d(getTag(),"setupLetterStop");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Run when the user has exceeded the given time to tap Fs
                // Displays a popup and advances to setSecondItemLayout()

                incrementProgress();
                disableLetters();

                final HintPointer niceWorkHint = new HintPointer(getActivity(), letterLayout, true);
                register(niceWorkHint);

                niceWorkHint.getShadow().addTarget(progressBar);
                niceWorkHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapf3));
                niceWorkHint.addButton(ViewUtil.getString(R.string.button_next), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        niceWorkHint.dismiss();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fadeOutView(letters);
                                setSecondGridLayout();
                            }
                        }, 600);
                    }
                });
                niceWorkHint.show();
            }
        }, 8000);
    }


    private void setSecondGridLayout() {
        Log.d(getTag(),"setSecondGridLayout");

        gridTextView.setVisibility(View.VISIBLE);
        getGridView(3, 0).removeImage();
        getGridView(1, 1).removeImage();
        getGridView(2, 3).removeImage();
        fadeInView(grids);

        final HintPointer secondItemsHint = new HintPointer(getActivity(), gridLayout, true);
        register(secondItemsHint);

        remindMeHint.setText(ViewUtil.getString(R.string.popup_tutorial_needhelp));
        remindMeHint.addButton(ViewUtil.getString(R.string.popup_tutorial_remindme), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindMeHint.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        remindMeHighlights();
                    }
                }, 600);
            }
        });

        otherItemsHint = new HintPointer(getActivity(), bottomAnchor,false,true);
        otherItemsHint.setText(ViewUtil.getString(R.string.grids_tutorial_vb_place_other));
        register(otherItemsHint);

        secondItemsHint.setText(ViewUtil.getString(R.string.popup_tutorial_selectboxV2));
        secondItemsHint.getShadow().addTarget(progressBar);
        secondItemsHint.addButton(ViewUtil.getString(R.string.popup_tutorial_ready), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secondItemsHint.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setGridRecall();
                    }
                }, 600);
                handler.postDelayed(firstRecallStepRunnable,5000);
            }
        });
        secondItemsHint.show();
    }

    // Displays the grid recall test and associated hints/prompts
    private void setGridRecall() {
        Log.d(getTag(),"setGridRecall");

        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(true);
            view.setListener(defaultListener);
        }
    }

    private void disableGrids(Grid2BoxView exemption) {
        Log.d(getTag(),"disableGrids");

        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(false);
        }
        if (exemption != null) {
            exemption.setSelectable(true);
        }
    }

    private void enableGrids() {
        Log.d(getTag(),"enableGrids");

        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(true);
        }
    }

    private void disableLetters() {
        Log.d(getTag(),"disableLetters");

        int size = letterLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2LetterView view = (Grid2LetterView) letterLayout.getChildAt(i);
            view.setEnabled(false);
        }
    }

    private int determinePointerPosition(Grid2BoxView view) {
        int gridBoxHeight = view.getHeight();
        int[] gridBoxLocation = new int[2];
        view.getLocationOnScreen(gridBoxLocation);

        if (gridBoxLocation[1] < ((2 * gridBoxHeight) + progressBar.getHeight())) {
            // if grid box is in the first two rows of the grid, dialog appears below grid box
            return PointerDrawable.POINTER_BELOW;
        } else {
            return PointerDrawable.POINTER_ABOVE;
        }
    }


    private boolean removeSelection(@DrawableRes int id) {
        Log.d(getTag(),"removeSelection");

        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if (!view.isSelected()) {
                continue;
            }
            int image = view.getImage();
            if (image == id) {
                view.removeImage();
                view.setSelected(false);
                return true;
            }
        }
        return false;
    }

    private void updateSelections() {
        phoneSelected = false;
        keySelected = false;
        penSelected = false;
        selectedCount = 0;

        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if (!view.isSelected()) {
                continue;
            }
            int id = view.getImage();
            if (id == R.drawable.phone) {
                phoneSelected = true;
                selectedCount++;
            }
            if (id == R.drawable.key) {
                keySelected = true;
                selectedCount++;
            }
            if (id == R.drawable.pen) {
                penSelected = true;
                selectedCount++;
            }
        }
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        if(remindMeHint != null){
            remindMeHint.dismiss();
        }
        if(selectedCount >= 2 && otherItemsHint != null){
            otherItemsHint.setVisibility(View.INVISIBLE);
            otherItemsHint.dismiss();
        }

        if (phoneSelected && keySelected && penSelected) {
            continueButton.setVisibility(View.VISIBLE);
            gridHintTextView.setVisibility(View.INVISIBLE);
        } else {
            gridHintTextView.setVisibility(View.VISIBLE);
            continueButton.setVisibility(View.GONE);
        }
    }

    // Determines which items to highlight for the remind me hints in the grid recall
    private void remindMeHighlights() {
        Log.d(getTag(),"remindMeHighlights");

        int targetCount = 0;
        final HintHighlighter remindMeTapHighlight = new HintHighlighter(getActivity());
        register(remindMeTapHighlight);

        remindMeTapHighlight.addTarget(progressBar);

        Grid2BoxView phone = getGridView(1, 1);
        boolean phoneTargeted = false;
        Grid2BoxView pen = getGridView(3, 0);
        boolean penTargeted = false;
        Grid2BoxView key = getGridView(2, 3);
        boolean keyTargeted = false;

        if (!phoneSelected) {
            if(phone.getImage()==0) {
                remindMeTapHighlight.addPulsingTarget(phone, 8);
                phoneTargeted = true;
            } else if(!penTargeted && pen.getImage()==0){
                remindMeTapHighlight.addPulsingTarget(pen, 8);
                penTargeted = true;
            } else if(!keyTargeted && key.getImage()==0) {
                remindMeTapHighlight.addPulsingTarget(key, 8);
                keyTargeted = true;
            }
        }

        if (!penSelected) {
            if(pen.getImage()==0) {
                remindMeTapHighlight.addPulsingTarget(pen, 8);
                penTargeted = true;
            } else if(!keyTargeted && key.getImage()==0){
                remindMeTapHighlight.addPulsingTarget(key, 8);
                keyTargeted = true;
            } else if(!phoneTargeted && phone.getImage()==0) {
                remindMeTapHighlight.addPulsingTarget(phone, 8);
                phoneTargeted = true;
            }
        }

        if (!keySelected) {
            if(key.getImage()==0) {
                remindMeTapHighlight.addPulsingTarget(key, 8);
                keyTargeted = true;
            } else if(!penTargeted && pen.getImage()==0){
                remindMeTapHighlight.addPulsingTarget(pen, 8);
                penTargeted = true;
            } else if(!phoneTargeted && phone.getImage()==0) {
                remindMeTapHighlight.addPulsingTarget(phone, 8);
                phoneTargeted = true;
            }

        }

        Log.d(getTag(),"phoneTargeted = "+phoneTargeted);
        Log.d(getTag(),"penTargeted = "+penTargeted);
        Log.d(getTag(),"keyTargeted = "+keyTargeted);

        if (phoneTargeted || penTargeted || keyTargeted) {
            boxViewListener = new Grid2BoxView.Listener() {
                @Override
                public void onSelected(Grid2BoxView view) {
                    remindMeTapHighlight.dismiss();
                }
            };
            remindMeTapHighlight.show();
        }
    }

    Grid2BoxView.Listener defaultListener = new Grid2BoxView.Listener() {
        @Override
        public void onSelected(final Grid2BoxView view) {
            handler.removeCallbacks(remindMeRunnable);
            handler.removeCallbacks(firstRecallStepRunnable);
            if (mechanicsHint != null) {
                mechanicsHint.dismiss();
            }

            final boolean deselecting =
                    (view.getImage() == R.drawable.phone && phoneSelected) ||
                            (view.getImage() == R.drawable.key && keySelected) ||
                            (view.getImage() == R.drawable.pen && penSelected);

            if (!deselecting && phoneSelected && keySelected && penSelected) {
                userMovedOrRemoved = true;
                Log.d(getTag(),"userMovedOrRemoved = true");
            }

            if (dialog != null) {
                if (dialog.isAttachedToWindow()) {
                    dialog.dismiss();
                    if (view.getImage() == 0) {
                        view.setSelected(false);
                    }
                    handler.postDelayed(remindMeRunnable, REMIND_ME_HINT_DELAY);
                    enableGrids();
                    return;
                }
            }

            disableGrids(view);

            int pointerPosition = determinePointerPosition(view);
            view.setSelected(true);

            dialog = new Grid2ChoiceDialog(
                    getActivity(),
                    view,
                    pointerPosition);

            dialog.setAnimationDuration(50);

            if (view.getImage() != 0) {
                dialog.disableChoice(view.getImage());
            }

            dialog.setListener(new Grid2ChoiceDialog.Listener() {
                @Override
                public void onSelected(int image) {
                    boolean removed = removeSelection(image);
                    if(deselecting || removed){
                        userMovedOrRemoved = true;
                        Log.d(getTag(),"userMovedOrRemoved = true");
                    }
                    view.setImage(image);
                    updateSelections();

                    if (dialogListener != null) {
                        dialogListener.onSelected(image);
                        dialogListener = null;
                    }

                    if (!firstGridSelected) {
                        Log.d(getTag(),"firstGridSelected = true");
                        firstGridSelected = true;
                        otherItemsHint.show();
                        othersReady = true;
                        Log.d(getTag(),"othersReady = true");

                        handler.postDelayed(remindMeRunnable, REMIND_ME_HINT_DELAY);
                    }

                    if (othersReady && !(phoneSelected && penSelected && keySelected)) {
                        handler.postDelayed(remindMeRunnable, REMIND_ME_HINT_DELAY);
                    }
                    enableGrids();

                }

                @Override
                public void onRemove() {
                    userMovedOrRemoved = true;
                    Log.d(getTag(),"userMovedOrRemoved = true");

                    view.removeImage();
                    view.setSelected(false);
                    updateSelections();
                    if (dialogListener != null) {
                        dialogListener.onRemove();
                        dialogListener = null;
                    }
                    if (othersReady && !(phoneSelected && penSelected && keySelected)) {
                        handler.postDelayed(remindMeRunnable, REMIND_ME_HINT_DELAY);
                    }
                    enableGrids();
                }
            });

            dialog.show();

            if (deselecting) {
                checkForMechanicsHint();
            }

            if (boxViewListener != null) {
                boxViewListener.onSelected(view);
                boxViewListener = null;
            }
        }
    };

    Runnable remindMeRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(getTag(),"remindMeRunnable");
            if (otherItemsHint != null) {
                otherItemsHint.dismiss();
            }
            remindMeHint.show();
        }
    };

    Runnable firstRecallStepRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(getTag(),"firstRecallStepRunnable");

            final Grid2BoxView boxView = getGridView(1, 1);

            final HintPointer recallHint = new HintPointer(getActivity(), getGridView(1, 1), true, false);
            register(recallHint);

            final HintPointer choiceSelectHint = new HintPointer(getActivity(), gridTextView);
            register(choiceSelectHint);

            final HintHighlighter pulsateGridItem = new HintHighlighter(getActivity());
            register(pulsateGridItem);

            final HintHighlighter pulsatePhone = new HintHighlighter(getActivity());
            register(pulsatePhone);

            boxViewListener = new Grid2BoxView.Listener() {
                @Override
                public void onSelected(Grid2BoxView view) {
                    handler.removeCallbacks(remindMeRunnable);

                    view.setSelectable(false);
                    if (pulsateGridItem != null) {
                        pulsateGridItem.dismiss();
                    }
                    if (recallHint != null) {
                        recallHint.dismiss();
                    }
                    dialogListener = new Grid2ChoiceDialog.Listener() {
                        @Override
                        public void onSelected(int image) {
                            choiceSelectHint.dismiss();
                            pulsatePhone.dismiss();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    enableGrids();

                                    firstGridSelected = true;
                                    Log.d(getTag(),"firstGridSelected = true");

                                    otherItemsHint.show();
                                    othersReady = true;
                                    Log.d(getTag(),"othersReady = true");
                                    handler.postDelayed(remindMeRunnable, REMIND_ME_HINT_DELAY);

                                }
                            }, 300);

                        }

                        @Override
                        public void onRemove() {
                            userMovedOrRemoved = true;
                        }
                    };


                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.getPenView().setOnTouchListener(null);
                            dialog.getKeyView().setOnTouchListener(null);
                            pulsatePhone.addTarget(progressBar);
                            pulsatePhone.addTarget(dialog, 8, 16);
                            pulsatePhone.addTarget(boxView, 8, 16);
                            pulsatePhone.addPulsingTarget(dialog.getPhoneView(), 8);
                            pulsatePhone.show();

                            choiceSelectHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapsymbol));
                            choiceSelectHint.show();

                        }
                    }, 500);

                }
            };
            recallHint.setText(ViewUtil.getString(R.string.popup_tutorial_cellbox));
            pulsateGridItem.addPulsingTarget(boxView, 8);
            pulsateGridItem.addTarget(progressBar);
            pulsateGridItem.show();
            recallHint.show();
        }
    };

    private void checkForMechanicsHint() {
        Log.d(getTag(),"checkForMechanicsHint");

        if (!mechanicsHintShown) {
            mechanicsHintShown = true;
            userMovedOrRemoved = true;

            if (remindMeHint != null) {
                remindMeHint.dismiss();
            }

            if (otherItemsHint != null) {
                otherItemsHint.dismiss();
            }

            mechanicsHint = new HintPointer(getActivity(), bottomAnchor,false,true);
            mechanicsHint.setText(ViewUtil.getString(R.string.grids_tutorial_vb_change_mind));
            mechanicsHint.addButton(ViewUtil.getString(R.string.popup_gotit), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mechanicsHint.dismiss();
                }
            });
            register(mechanicsHint);
            mechanicsHint.show();
        }
    }

    private void launchMechanicsTutorial() {
        Log.d(getTag(),"launchMechanicsTutorial");

        handler.removeCallbacks(remindMeRunnable);
        gridHintTextView.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.GONE);

        int size = gridLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.removeImage();
            view.setSelected(false);
        }

        getGridView(1, 1).setSelected(true);
        getGridView(1, 1).setImage(R.drawable.phone);

        final HintPointer hint = new HintPointer(getActivity(), gridLayout, true);
        hint.setText(ViewUtil.getString(R.string.grids_tutorial_vb_mech1));
        hint.addButton(ViewUtil.getString(R.string.button_showme), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint.dismiss();
                mechanicsTutorial1();
            }
        });
        register(hint);
        hint.show();

    }

    private void mechanicsTutorial1() {
        Log.d(getTag(),"mechanicsTutorial1");

        disableGrids(null);
        final HintPointer hint = new HintPointer(getActivity(), getGridView(4, 2), true);
        hint.setText(ViewUtil.getString(R.string.grids_tutorial_vb_mech2));
        hint.addButton(ViewUtil.getString(R.string.button_okay), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint.dismiss();
                mechanicsTutorial2();
            }
        });
        int padding = ViewUtil.pxToDp((int) (1.75 * getGridView(2, 2).getWidth()));
        hint.getShadow().addTarget(getGridView(2, 2), 8, padding);
        hint.getShadow().addTarget(progressBar);
        register(hint);
        hint.show();
    }

    private void mechanicsTutorial2() {
        Log.d(getTag(),"mechanicsTutorial2");

        final Grid2BoxView boxView = getGridView(1, 3);
        final HintPointer hint = new HintPointer(getActivity(), boxView, true, false, true);
        hint.setText(ViewUtil.getString(R.string.popup_tutorial_tapbox4));
        hint.getShadow().addPulsingTarget(boxView, 8);
        hint.getShadow().addTarget(progressBar);
        register(hint);

        boxView.setListener(new Grid2BoxView.Listener() {
            @Override
            public void onSelected(Grid2BoxView view) {
                hint.dismiss();
                view.setSelected(true);
                view.setSelectable(false);

                int pointerPosition = determinePointerPosition(view);

                dialog = new Grid2ChoiceDialog(
                        getActivity(),
                        view,
                        pointerPosition);
                dialog.setAnimationDuration(50);
                dialog.getPenView().setOnTouchListener(null);
                dialog.getKeyView().setOnTouchListener(null);

                dialog.setListener(new Grid2ChoiceDialog.Listener() {
                    @Override
                    public void onSelected(int image) {
                        if(removeSelection(image)){
                            userMovedOrRemoved = true;
                        }
                        boxView.setImage(image);
                        if(mechanicsHint!=null){
                            mechanicsHint.dismiss();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mechanicsTutorial3();
                            }
                        },300);
                    }

                    @Override
                    public void onRemove() {

                    }
                });
                dialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mechanicsHint = new HintPointer(getActivity(), bottomAnchor, false,true,true);
                        mechanicsHint.setText(ViewUtil.getString(R.string.popup_tutorial_tapsymbol2));
                        mechanicsHint.getShadow().addTarget(dialog, 8, 8);
                        mechanicsHint.getShadow().addTarget(progressBar);
                        mechanicsHint.getShadow().addTarget(getGridView(1, 3),8,8);
                        mechanicsHint.getShadow().addPulsingTarget(dialog.getPhoneView(), 8);

                        register(mechanicsHint);
                        mechanicsHint.show();
                    }
                }, 300);

            }
        });
        hint.show();

        boxView.setSelectable(true);
    }

    private void mechanicsTutorial3() {
        Log.d(getTag(),"mechanicsTutorial3");

        final Grid2BoxView boxView = getGridView(1, 3);
        boxView.setSelectable(true);

        final HintPointer hintGreat = new HintPointer(getActivity(), boxView, true, false, true);
        hintGreat.setText(ViewUtil.getString(R.string.grids_tutorial_vb_mech3));
        hintGreat.getShadow().addPulsingTarget(boxView, 8);
        hintGreat.getShadow().addTarget(progressBar);
        register(hintGreat);

        boxView.setListener(new Grid2BoxView.Listener() {
            @Override
            public void onSelected(Grid2BoxView view) {
                hintGreat.dismiss();
                view.setSelected(true);
                view.setSelectable(false);

                int pointerPosition = determinePointerPosition(view);

                dialog = new Grid2ChoiceDialog(
                        getActivity(),
                        view,
                        pointerPosition);
                dialog.setAnimationDuration(50);
                dialog.disableChoice(R.drawable.phone);
                dialog.getPenView().setOnTouchListener(null);
                dialog.getKeyView().setOnTouchListener(null);

                dialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        TextView textView = dialog.getRemoveItemView();

                        final HintPointer hint = new HintPointer(getActivity(), textView, true,false,true);
                        hint.setText(ViewUtil.getString(R.string.grids_tutorial_vb_mech4));

                        hint.getShadow().addTarget(textView, 8, 8);
                        hint.getShadow().addTarget(progressBar);

                        dialog.setListener(new Grid2ChoiceDialog.Listener() {
                            @Override
                            public void onSelected(int image) {

                            }

                            @Override
                            public void onRemove() {
                                hint.setVisibility(View.INVISIBLE);
                                hint.dismiss();
                                mechanicsTutorial4();
                            }
                        });

                        register(hint);
                        hint.show();
                    }
                }, 500);

            }
        });
        hintGreat.show();
    }

    private void mechanicsTutorial4() {
        Log.d(getTag(),"mechanicsTutorial4");

        final Grid2BoxView boxView = getGridView(1, 3);
        boxView.setSelected(false);
        boxView.removeImage();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final HintPointer hint = new HintPointer(getActivity(),bottomAnchor,false,true);
                hint.setText(ViewUtil.getString(R.string.popup_tutorial_perfect));
                hint.addButton(ViewUtil.getString(R.string.button_finishtutorial), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hint.setVisibility(View.INVISIBLE);
                        hint.dismiss();
                        incrementProgress();
                        fadeOutView(grids);
                        showComplete();
                    }
                });
                register(hint);
                hint.show();
            }
        },500);
    }

}