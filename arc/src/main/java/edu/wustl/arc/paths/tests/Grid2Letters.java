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
import android.os.SystemClock;
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

import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.core.TimedDialog;
import edu.wustl.arc.core.TimedDialogMultipart;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.Grid2TestPathData;
import edu.wustl.arc.study.StateMachine;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.ui.Grid2LetterView;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Grid2Letters extends BaseFragment {

    Grid2TestPathData gridTest;
    Grid2TestPathData.Section section;

    GridLayout gridLayout;
    int columnCount;
    int rowCount;

    TimedDialogMultipart dialog;
    Handler handler;
    boolean paused;

    int eCount = 0;
    int fCount = 0;

    public Grid2Letters() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid2_letters, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);
        columnCount = gridLayout.getColumnCount();
        rowCount = gridLayout.getRowCount();

        Random random;
        if(StateMachine.AUTOMATED_TESTS_RANDOM_SEED == -1) {
            random = new Random(SystemClock.elapsedRealtime());
        }
        else{
            random = new Random(StateMachine.AUTOMATED_TESTS_RANDOM_SEED);
        }

        List<Integer> indices = setupTest(
                random,
                rowCount,
                columnCount);

        for(Integer index: indices){
            getView(index / rowCount,index % rowCount).setF();
        }

        gridTest = (Grid2TestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();

        handler = new Handler();
        handler.postDelayed(runnable,8000);

        adjustGridLayout();

        return view;
    }

    private Grid2LetterView getView(int row, int col) {
        return (Grid2LetterView)gridLayout.getChildAt((columnCount*row)+col);
    }

    public static List<Integer> setupTest(Random random, int rowCount, int columnCount) {

        // init variables
        List<Integer> items = new ArrayList<>();
        int gap = rowCount*columnCount;
        int lower = 0;
        int picked = 0;

        // add bounds
        items.add(-1);
        items.add(gap);

        // loop
        while(picked < 8){
            int var = random.nextInt(gap-1)+1;
            items.add(var+lower);
            Collections.sort(items);
            gap = 0;
            lower = 0;
            for(int i=0;i<items.size()-1;i++){
                int tempGap = items.get(i+1)-items.get(i);
                if(tempGap > gap){
                    gap = tempGap;
                    lower = items.get(i);
                }
            }
            picked++;
        }
        Collections.sort(items);

        // remove bounds
        items.remove(0);
        items.remove(items.size()-1);

        // exit, stage left
        return items;
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
            viewHeight = (int) (displayWidth / 0.75f);
        }

        viewHeight -= ViewUtil.dpToPx(16);

        float letterRatio = ((float)columnCount)/((float)rowCount);
        int width = (int) (letterRatio*(viewHeight));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ViewUtil.dpToPx(28);
        layoutParams.bottomMargin = ViewUtil.dpToPx(4);
        layoutParams.gravity = Gravity.CENTER;
        gridLayout.setLayoutParams(layoutParams);
    }

    private void calculateCounts() {
        if(gridLayout==null){
            return;
        }

        for(int i=0; i<rowCount; i++) {
            for(int j=0; j<columnCount; j++) {
                Grid2LetterView view = getView(i,j);
                if(!view.isSelected()){
                    continue;
                }
                if(view.isF()){
                    fCount++;
                } else {
                    eCount++;
                }

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        section.markDistractionDisplayed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        if(dialog!=null){
            if(dialog.isVisible()){
                dialog.setOnDialogDismissListener(null);
                dialog.dismiss();
            }
        }
        paused = true;
    }

    protected Runnable runnable = new Runnable() {
        @Override
        public void run() {

            calculateCounts();

            if(isVisible()){
                dialog = new TimedDialogMultipart(
                        ViewUtil.getHtmlString(R.string.grids_overlay3),
                        ViewUtil.getHtmlString(R.string.grids_overlay3_pt2),
                        3000,
                        6000
                );
                dialog.setOnDialogDismissListener(new TimedDialogMultipart.OnDialogDismiss() {
                    @Override
                    public void dismiss() {
                        section.setECount(eCount);
                        section.setFCount(fCount);
                        gridTest.updateCurrentSection(section);
                        Study.setCurrentSegmentData(gridTest);
                        Study.openNextFragment();
                    }
                });
                dialog.show(getFragmentManager(), TimedDialog.class.getSimpleName());
            }
        }
    };

}
