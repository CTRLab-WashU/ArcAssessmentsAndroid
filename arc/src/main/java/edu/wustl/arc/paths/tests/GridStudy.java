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

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.core.TimedDialog;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.GridTestPathData;
import edu.wustl.arc.study.StateMachine;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridStudy extends ArcBaseFragment {

    boolean paused;

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;

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

    public GridStudy() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_study, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);

        dialog = new TimedDialog(ViewUtil.getHtmlString(R.string.grids_overlay1),2000);
        dialog.setOnDialogDismissListener(new TimedDialog.OnDialogDismiss() {
            @Override
            public void dismiss() {
                setupTest();
                handler = new Handler();
                handler.postDelayed(runnable,3000);
            }
        });
        dialog.show(getFragmentManager(), TimedDialog.class.getSimpleName());

        gridTest = (GridTestPathData) Study.getCurrentSegmentData();


        return view;
    }

    private ImageView getImageView(int row, int col){
        return (ImageView)gridLayout.getChildAt((5*row)+col);
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

    private void setupTest(){
        List<GridTestPathData.Image> images = new ArrayList<>();
        Random random;
        if(StateMachine.AUTOMATED_TESTS_RANDOM_SEED == -1){
             random = new Random(SystemClock.currentThreadTimeMillis());
        }
        else{
            random = new Random(StateMachine.AUTOMATED_TESTS_RANDOM_SEED);
        }

       
        int row1 = random.nextInt(5);
        int row2 = random.nextInt(5);
        int row3 = random.nextInt(5);
        while(row1 == row2){
            row2 = random.nextInt(5);
        }
        while(row3 == row1 || row3 == row2){
            row3 = random.nextInt(5);
        }

        int col1 = random.nextInt(5);
        int col2 = random.nextInt(5);
        int col3 = random.nextInt(5);
        while(col1 == col2){
            col2 = random.nextInt(5);
        }
        while(col3 == col1 || col3 == col2){
            col3 = random.nextInt(5);
        }

        getImageView(row1,col1).setImageResource(R.drawable.phone);
        getImageView(row2,col2).setImageResource(R.drawable.pen);
        getImageView(row3,col3).setImageResource(R.drawable.key);

        section.markSymbolsDisplayed();

        images.add(new GridTestPathData.Image(row1,col1,"phone"));
        images.add(new GridTestPathData.Image(row2,col2,"pen"));
        images.add(new GridTestPathData.Image(row3,col3,"key"));
        section.setImages(images);
        gridTest.updateCurrentSection(section);
        Study.setCurrentSegmentData(gridTest);
    }

}
