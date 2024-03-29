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
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.GridTestPathData;
import edu.wustl.arc.study.Study;

import java.util.ArrayList;
import java.util.List;

public class GridTest extends ArcBaseFragment {

    boolean paused;
    int selectedCount = 0;
    public boolean second = false;

    GridLayout gridLayout;
    GridTestPathData gridTest;
    GridTestPathData.Section section;
    List<View> selections;

    Handler handler;
    Handler handlerInteraction;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnable);
            handlerInteraction.removeCallbacks(runnable);
            if(isVisible()){
                updateSection();
                Study.openNextFragment();
            }
        }
    };

    public GridTest() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_test, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                boolean preventTouch = false;
                switch(action) {
                    case MotionEvent.ACTION_DOWN:
                        if (view.getTag(R.id.tag_color).equals(R.color.gridSelected)) {
                            view.setTag(R.id.tag_color,R.color.gridNormal);
                            if(selections.contains(view)){
                                view.setTag(R.id.tag_time,0);
                                selections.remove(view);
                            }
                            selectedCount--;
                        } else if (selectedCount < 3) {
                            selectedCount++;
                            view.setTag(R.id.tag_time, System.currentTimeMillis());
                            view.setTag(R.id.tag_color,R.color.gridSelected);
                            selections.add(view);
                        } else {
                            preventTouch = true;
                        }

                        handler.removeCallbacks(runnable);
                        if(selectedCount >= 3){
                            handler.postDelayed(runnable,2000);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return preventTouch;
            }
        };

        // Init each grid-cell as unselected + add onTouchlistener
        int size = gridLayout.getChildCount();
        for(int i=0;i<size;i++){
            gridLayout.getChildAt(i).setTag(R.id.tag_color,R.color.gridNormal);
            gridLayout.getChildAt(i).setOnTouchListener(listener);
        }

        selections = new ArrayList<>();
        gridTest = (GridTestPathData) Study.getCurrentSegmentData();
        section = gridTest.getCurrentSection();


        handler = new Handler();
        handlerInteraction = new Handler();
        handlerInteraction.postDelayed(runnable,20000);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        section.markTestGridDisplayed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            updateSection();
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerInteraction.removeCallbacks(runnable);
        handler.removeCallbacks(runnable);
        paused = true;
    }

    private void updateSection(){
        int size = gridLayout.getChildCount();
        List<GridTestPathData.Tap> choices = new ArrayList<>();
        for(int i=0;i<size;i++){
            if(selections.contains(gridLayout.getChildAt(i))){
                View view = gridLayout.getChildAt(i);
                choices.add(new GridTestPathData.Tap(i / 5,i % 5,(long)view.getTag(R.id.tag_time)));
            }
        }
        section.setChoices(choices);
        gridTest.updateCurrentSection(section);
        Study.setCurrentSegmentData(gridTest);
    }

}
