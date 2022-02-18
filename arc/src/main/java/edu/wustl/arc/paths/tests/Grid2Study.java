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

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.core.TimedDialog;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.Grid2TestPathData;
import edu.wustl.arc.path_data.GridTestPathData;
import edu.wustl.arc.study.StateMachine;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.ui.Grid2BoxView;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid2Study extends ArcBaseFragment {

    boolean paused;

    GridLayout gridLayout;
    Grid2TestPathData gridTest;
    Grid2TestPathData.Section section;

    int rowCount;
    int columnCount;

    TimedDialog dialog;
    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isVisible()){
                Study.openNextFragment();
            }
        }
    };

    public Grid2Study() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid2_study, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);
        columnCount = gridLayout.getColumnCount();
        rowCount = gridLayout.getRowCount();

        for(int i=0; i<rowCount; i++) {
            for(int j=0; j<columnCount; j++) {
                getView(i,j).setSelectable(false);
            }
        }

        dialog = new TimedDialog(ViewUtil.getHtmlString(R.string.grids_overlay1),2000);
        dialog.setOnDialogDismissListener(new TimedDialog.OnDialogDismiss() {
            @Override
            public void dismiss() {
                Random random;
                if(StateMachine.AUTOMATED_TESTS_RANDOM_SEED == -1) {
                    random = new Random(SystemClock.currentThreadTimeMillis());
                }
                else{
                    random = new Random(StateMachine.AUTOMATED_TESTS_RANDOM_SEED);
                }
                List<Grid2TestPathData.Image> images = setupTest(random,rowCount,columnCount);
                displayImages(images);

                handler = new Handler();
                handler.postDelayed(runnable,3000);
            }
        });
        dialog.show(getFragmentManager(), TimedDialog.class.getSimpleName());

        gridTest = (Grid2TestPathData) Study.getCurrentSegmentData();

        adjustGridLayout();

        return view;
    }

    private Grid2BoxView getView(int row, int col) {
        return (Grid2BoxView)gridLayout.getChildAt((columnCount*row)+col);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!gridTest.hasStarted()){
            gridTest.markStarted();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!paused) {
            gridTest.startNewSection();
            section = gridTest.getCurrentSection();
        } else {
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog.isVisible()){
            dialog.setOnDialogDismissListener(null);
            dialog.dismiss();
        }
        if(handler != null) {
            handler.removeCallbacks(runnable);
        }
        paused = true;
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

    public static List<Grid2TestPathData.Image> setupTest(Random random, int rowCount, int columnCount){
        List<Grid2TestPathData.Image> images = new ArrayList<>();

        int row1 = random.nextInt(rowCount);
        int row2 = random.nextInt(rowCount);
        int row3 = random.nextInt(rowCount);
        while(row1 == row2){
            row2 = random.nextInt(rowCount);
        }
        while(row3 == row1 || row3 == row2){
            row3 = random.nextInt(rowCount);
        }

        int col1 = random.nextInt(columnCount);
        int col2 = random.nextInt(columnCount);
        int col3 = random.nextInt(columnCount);
        while(col1 == col2){
            col2 = random.nextInt(columnCount);
        }
        while(col3 == col1 || col3 == col2){
            col3 = random.nextInt(columnCount);
        }

        images.add(new Grid2TestPathData.Image(row1,col1,GridTestPathData.Image.PHONE));
        images.add(new Grid2TestPathData.Image(row2,col2,GridTestPathData.Image.PEN));
        images.add(new Grid2TestPathData.Image(row3,col3,GridTestPathData.Image.KEY));

        return images;
    }

    private void displayImages(List<Grid2TestPathData.Image> images) {

        for(Grid2TestPathData.Image image : images) {
            getView(image.row(),image.column()).setImage(image.id());
        }

        section.markSymbolsDisplayed();
        section.setImages(images);
        gridTest.updateCurrentSection(section);

        Study.setCurrentSegmentData(gridTest);
    }
}
