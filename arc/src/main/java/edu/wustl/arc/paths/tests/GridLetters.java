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

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.core.TimedDialog;
import edu.wustl.arc.core.TimedDialogMultipart;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.GridTestPathData;
import edu.wustl.arc.study.StateMachine;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GridLetters extends ArcBaseFragment {

    boolean paused;
    GridLayout gridLayout;
    protected  GridTestPathData gridTest;
    protected GridTestPathData.Section section;
    protected int eCount = 0;
    protected int fCount = 0;

    private TextView textViewTapFsLabel;

    protected TimedDialogMultipart dialog;
    Handler handler;
    protected Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isVisible()){

                dialog = new TimedDialogMultipart (
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
                dialog.show(getFragmentManager(),TimedDialog.class.getSimpleName());
            }
        }
    };

    public GridLetters() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_letters, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);

        textViewTapFsLabel = view.findViewById(R.id.textView32);
        textViewTapFsLabel.setText(ViewUtil.getHtmlString(R.string.grids_subheader_fs));

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        boolean isE = ((TextView)view).getText().toString().equals("E");
                        if(view.getTag() == null){
                            view.setTag(true);
                            if(isE){
                                eCount++;
                            } else {
                                fCount++;
                            }
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                            return false;
                        }
                        if(view.getTag().equals(false)) {
                            view.setTag(true);
                            if(isE){
                                eCount++;
                            } else {
                                fCount++;
                            }
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gridNormal));
                        } else {
                            view.setTag(false);
                            if(isE){
                                eCount--;
                            } else {
                                fCount--;
                            }
                            view.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        };


        Typeface font = Fonts.georgia;
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            gridLayout.getChildAt(i).setOnTouchListener(listener);
            ((TextView)gridLayout.getChildAt(i)).setTypeface(font);
        }
        Random random;
        if(StateMachine.AUTOMATED_TESTS_RANDOM_SEED == -1) {
            random = new Random(SystemClock.elapsedRealtime());
        }
        else{
            random = new Random(StateMachine.AUTOMATED_TESTS_RANDOM_SEED);
        }
        List<Integer> items = new ArrayList<>();
        items.add(-1);
        items.add(60);
        int gap = 60;
        int lower = 0;
        int picked = 0;
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
        items.remove(0);
        items.remove(items.size()-1);

        for(Integer var: items){
            getTextView(var / 6,var % 6).setText("F");
        }

        handler = new Handler();
        handler.postDelayed(runnable,8000);
        gridTest = (GridTestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();
        return view;
    }

    private TextView getTextView(int row, int col){
        return (TextView)gridLayout.getChildAt((6*row)+col);
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
}
