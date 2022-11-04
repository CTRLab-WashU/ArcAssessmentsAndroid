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

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.study.StateMachine;
import edu.wustl.arc.study.TestVariant;
import edu.wustl.arc.ui.ArcRadioButton;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.PriceTestPathData;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.PriceManager;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressLint("ValidFragment")
public class PriceTestMatchFragment extends ArcBaseFragment {

    List<PriceManager.Item> priceSet;
    PriceTestPathData priceTest;
    PriceTestPathData.Section pricesTestSection;
    int compareIndex = 0;
    String prefix;
    String suffix;
    Random random;
    ArcRadioButton radioButtonTop;
    ArcRadioButton radioButtonBottom;
    TextView textviewFood;
    TextView textViewIsIt;
    int iteration = 0;
    boolean paused;

    public PriceTestMatchFragment() {
        if(StateMachine.AUTOMATED_TESTS_RANDOM_SEED == -1){
            random = new Random(System.currentTimeMillis());
        }
        else{
            random = new Random(StateMachine.AUTOMATED_TESTS_RANDOM_SEED);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout;
        if (Config.TEST_VARIANT_PRICE == TestVariant.Price.Revised) {
            layout = R.layout.fragment_revised_price_test_match;
        } else {
            layout = R.layout.fragment_price_test_match;
        }
        View view = inflater.inflate(layout, container, false);

        priceTest = (PriceTestPathData) Study.getCurrentSegmentData();
        priceSet = priceTest.getPriceSet();
        Collections.shuffle(priceSet, random);

        prefix = ViewUtil.getString(R.string.money_prefix);
        suffix = getString(R.string.money_suffix);

        radioButtonTop = view.findViewById(R.id.buttonTop);
        radioButtonTop.setCheckable(false);
        radioButtonTop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        pricesTestSection.markSelection(0, System.currentTimeMillis());
                        radioButtonTop.setChecked(true);
                        saveIteration();
                        if(iteration==priceSet.size()) {
                            Study.setCurrentSegmentData(priceTest);
                            Study.openNextFragment();
                        } else {
                            setupNextIteration();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        radioButtonTop.setChecked(false);
                        break;
                }
                return true;
            }
        });

        radioButtonBottom = view.findViewById(R.id.buttonBottom);
        radioButtonBottom.setCheckable(false);
        radioButtonBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        pricesTestSection.markSelection(1, System.currentTimeMillis());
                        radioButtonBottom.setChecked(true);
                        saveIteration();
                        if(iteration==priceSet.size()) {
                            Study.setCurrentSegmentData(priceTest);
                            Study.openNextFragment();
                        } else {
                            setupNextIteration();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        radioButtonBottom.setChecked(false);
                        break;
                }
                return true;
            }
        });

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        textViewIsIt = view.findViewById(R.id.textView12);
        textViewIsIt.setText(ViewUtil.getHtmlString(R.string.prices_whatwasprice));

        setupNextIteration();

        return view;
    }

    private void setupNextIteration(){
        PriceManager.Item item = priceSet.get(iteration);
        textviewFood.setText(item.item);
        compareIndex = getPriceItemIndex(priceTest,item.item);
        pricesTestSection = priceTest.getSections().get(compareIndex);

        int rand = random.nextInt(2);
        if(rand==0){
            radioButtonTop.setText(prefix+item.price+suffix);
            radioButtonBottom.setText(prefix+item.alt+suffix);
        } else {
            radioButtonTop.setText(prefix+item.alt+suffix);
            radioButtonBottom.setText(prefix+item.price+suffix);
        }
        pricesTestSection.markQuestionDisplayed();
        pricesTestSection.setCorrectIndex(rand);
        iteration++;
    }

    private void saveIteration(){
         priceTest.getSections().set(compareIndex,pricesTestSection);
    }

    public int getPriceItemIndex(PriceTestPathData set, String name){

        List<PriceTestPathData.Section> sections = set.getSections();
        int size = sections.size();
        for(int i=0;i<size;i++){
            if(sections.get(i)==null){
                sections.add(new PriceTestPathData.Section());
            } else if(sections.get(i).getItem().equals(name)){
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

}
