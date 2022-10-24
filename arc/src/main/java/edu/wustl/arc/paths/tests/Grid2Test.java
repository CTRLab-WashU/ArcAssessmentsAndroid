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
package edu.wustl.arc.paths.tests;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.Grid2TestPathData;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.ui.ArcButton;
import edu.wustl.arc.ui.Grid2BoxView;
import edu.wustl.arc.ui.Grid2ChoiceDialog;
import edu.wustl.arc.ui.base.PointerDrawable;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class Grid2Test extends ArcBaseFragment {

    boolean paused;
    long pausedTime;

    boolean phoneSelected = false;
    boolean keySelected = false;
    boolean penSelected = false;

    GridLayout gridLayout;
    Grid2TestPathData gridTest;
    Grid2TestPathData.Section section;

    ArcButton button;
    Grid2ChoiceDialog dialog;

    Handler handler;
    Handler handlerInteraction;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            openNextFragment();
        }
    };

    public Grid2Test() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid2_test, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);

        Grid2BoxView.Listener listener = new Grid2BoxView.Listener() {
            @Override
            public void onSelected(final Grid2BoxView view) {
                handler.removeCallbacks(runnable);

                if(dialog!=null) {
                    if (dialog.isAttachedToWindow()) {
                        dialog.dismiss();
                        if(view.getImage()==0){
                            view.setSelected(false);
                        }
                        enableGrids();
                        return;
                    }
                }

                disableGrids(view);

                int pointerPosition = determinePointerPosition(view);
                view.setSelected(true);

                dialog = new Grid2ChoiceDialog(
                        getMainActivity(),
                        view,
                        pointerPosition);

                dialog.setAnimationDuration(50);

                if(view.getImage()!=0) {
                    dialog.disableChoice(view.getImage());
                }

                dialog.setListener(new Grid2ChoiceDialog.Listener() {
                    @Override
                    public void onSelected(int image) {
                        removeSelection(image);
                        view.setImage(image);
                        updateSelections();
                        enableGrids();
                    }

                    @Override
                    public void onRemove() {
                        view.removeImage();
                        view.setSelected(false);
                        updateSelections();
                        enableGrids();
                    }
                });

                dialog.show();
            }
        };

        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView grid2BoxView = (Grid2BoxView) gridLayout.getChildAt(i);
            grid2BoxView.setListener(listener);
        }

        button = view.findViewById(R.id.buttonContinue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNextFragment();
            }
        });

        gridTest = (Grid2TestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();

        handler = new Handler();
        handlerInteraction = new Handler();
        handlerInteraction.postDelayed(runnable,40000);

        adjustGridLayout();

        return view;
    }

    private void openNextFragment() {
        handler.removeCallbacks(runnable);
        handlerInteraction.removeCallbacks(runnable);
        if (isVisible()) {
            updateSection();
            Study.openNextFragment();
        }
    }

    private int determinePointerPosition(Grid2BoxView view) {
        int gridBoxHeight = view.getHeight();
        int[] gridBoxLocation = new int[2];
        view.getLocationOnScreen(gridBoxLocation);

        if(gridBoxLocation[1] < (2*gridBoxHeight)) {
            // if grid box is in the first two rows of the grid, dialog appears below grid box
            return PointerDrawable.POINTER_BELOW;
        } else {
            return PointerDrawable.POINTER_ABOVE;
        }
    }

    private void updateSection(){
        List<Grid2TestPathData.Tap> choices = new ArrayList<>();

        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(view.isSelected()) {
                int x = i / 5;
                int y = i % 5;
                int image = view.getImage();
                long timestampSelect = view.getTimestampSelect();
                long timestampImage = view.getTimestampImage();
                choices.add(new Grid2TestPathData.Tap(x,y,image,timestampSelect,timestampImage));
            }
        }

        section.setChoices(choices);
        gridTest.updateCurrentSection(section);
        Study.setCurrentSegmentData(gridTest);
    }

    @Override
    public void onStart() {
        super.onStart();
        section.markTestGridDisplayed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused && SymbolTest.isPastAllowedPauseTime(pausedTime)) {
            updateSection();
            Study.skipToNextSegment();
        }
        paused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerInteraction.removeCallbacks(runnable);
        handler.removeCallbacks(runnable);
        paused = true;
        pausedTime = System.currentTimeMillis();
    }


    private void disableGrids(Grid2BoxView exemption){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(false);
        }
        if(exemption!=null) {
            exemption.setSelectable(true);
        }
    }

    private void enableGridsFinal(){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(view.isSelected()) {
                view.setSelectable(true);
            } else {
                view.setSelectable(false);
            }
        }
    }

    private void enableGrids(){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            view.setSelectable(true);
        }
    }



    private void adjustGridLayout(){
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        int auxHeight = ViewUtil.dpToPx(124);
        int viewHeight = displayHeight-ViewUtil.getStatusBarHeight()-ViewUtil.getNavBarHeight()-auxHeight;

        float aspectRatio = ((float)displayWidth)/((float)viewHeight);
        if(aspectRatio < 0.75f) {
            return;
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) (0.75f * viewHeight),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ViewUtil.dpToPx(26);
        layoutParams.bottomMargin = ViewUtil.dpToPx(2);
        layoutParams.gravity = Gravity.CENTER;
        gridLayout.setLayoutParams(layoutParams);
    }

    private void removeSelection(@DrawableRes int id){
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(!view.isSelected()){
                continue;
            }
            int image = view.getImage();
            if(image == id) {
                view.removeImage();
                view.setSelected(false);
                return;
            }
        }
    }

    private void updateSelections(){
        phoneSelected = false;
        keySelected = false;
        penSelected = false;

        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            Grid2BoxView view = (Grid2BoxView) gridLayout.getChildAt(i);
            if(!view.isSelected()){
                continue;
            }
            int id = view.getImage();
            if(id == R.drawable.phone) {
                phoneSelected = true;
            }
            if(id == R.drawable.key) {
                keySelected = true;
            }
            if(id == R.drawable.pen) {
                penSelected = true;
            }
        }
        updateButtonVisibility();
    }

    private void updateButtonVisibility(){
        if(phoneSelected && keySelected && penSelected) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

}
