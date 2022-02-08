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
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.study.StateMachine;
import edu.wustl.arc.study.TestVariant;
import edu.wustl.arc.ui.RadioButton;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.PriceTestPathData;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.PriceManager;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.Random;

@SuppressLint("ValidFragment")
public class PriceTestCompareFragment extends BaseFragment {

    int index;

    PriceManager.Item item;

    PriceTestPathData priceTest;
    PriceTestPathData.Section section;
    String prefix;
    String suffix;

    RadioButton buttonYes;
    RadioButton buttonNo;
    TextView textviewFood;
    TextView textviewPrice;
    TextView textviewGoodPrice;

    private boolean revised = false;
    boolean paused;
    Random random;
    Handler handler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            saveIteration();
            if(!paused) {
                Study.setCurrentSegmentData(priceTest);
                Study.openNextFragment();
            }
        }
    };

    public PriceTestCompareFragment(int index) {
        this.index = index;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        revised = (Config.TEST_VARIANT_PRICE == TestVariant.Price.Revised);
        if (revised) {
            return createRevisedFragment(inflater, container, savedInstanceState);
        } else {
            return createFullFragment(inflater, container, savedInstanceState);
        }
    }

    private View createRevisedFragment(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_revised_price_test_compare, container, false);
        if(StateMachine.AUTOMATED_TESTS_RANDOM_SEED == -1){
            random = new Random(System.currentTimeMillis());
        }else{
            random = new Random(StateMachine.AUTOMATED_TESTS_RANDOM_SEED);
        }

        prefix = ViewUtil.getString(R.string.money_prefix);
        suffix = getString(R.string.money_suffix);

        priceTest = (PriceTestPathData) Study.getCurrentSegmentData();
        if(!priceTest.hasStarted()){
            priceTest.markStarted();
        }
        section = priceTest.getSections().get(index);
        item = priceTest.getPriceSet().get(index);

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        textviewPrice = view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(Fonts.georgiaItalic);

        handler = new Handler();
        setupIteration();

        return view;
    }

    private View createFullFragment(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_test_compare, container, false);
        if(StateMachine.AUTOMATED_TESTS_RANDOM_SEED == -1){
            random = new Random(System.currentTimeMillis());
        }else{
            random = new Random(StateMachine.AUTOMATED_TESTS_RANDOM_SEED);
        }
        prefix = ViewUtil.getString(R.string.money_prefix);
        suffix = getString(R.string.money_suffix);

        priceTest = (PriceTestPathData) Study.getCurrentSegmentData();
        if(!priceTest.hasStarted()){
            priceTest.markStarted();
        }
        section = priceTest.getSections().get(index);
        item = priceTest.getPriceSet().get(index);

        buttonYes = view.findViewById(R.id.radioButtonYes);
        buttonYes.setText(ViewUtil.getString(R.string.radio_yes));
        buttonYes.setCheckable(false);
        buttonYes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonNo.setChecked(false);
                        buttonYes.setChecked(true);
                        break;
                }
                return true;
            }
        });

        buttonNo = view.findViewById(R.id.radioButtonNo);
        buttonNo.setText(ViewUtil.getString(R.string.radio_no));
        buttonNo.setCheckable(false);
        buttonNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        buttonYes.setChecked(false);
                        buttonNo.setChecked(true);
                        break;
                }
                return true;
            }
        });

        textviewGoodPrice = view.findViewById(R.id.textView12);
        textviewGoodPrice.setText(ViewUtil.getString(R.string.prices_isthisgood));

        textviewFood = view.findViewById(R.id.textviewFood);
        textviewFood.setTypeface(Fonts.georgiaItalic);

        textviewPrice = view.findViewById(R.id.textviewPrice);
        textviewPrice.setTypeface(Fonts.georgiaItalic);

        handler = new Handler();
        setupIteration();

        return view;
    }

    private void setupIteration(){
        String price = prefix+item.price+suffix;
        section.setItem(item.item);
        section.setPrice(price);
        section.setAltPrice( prefix+item.alt+suffix);

        if(random.nextBoolean()){
            textviewFood.setText(item.item);
            textviewPrice.setText(price);
            setFonts(false);
        } else {
            textviewFood.setText(price);
            textviewPrice.setText(item.item);
            setFonts(true);
        }

        section.markStimulusDisplayed();
        handler.postDelayed(runnable,3000);
    }

    private void setFonts(Boolean flipped) {
        if (revised) { //only for the updated version of prices test
            if (flipped) {
                textviewFood.setTypeface(Fonts.georgia);
                textviewPrice.setTypeface(Fonts.georgiaItalic);
            } else {
                textviewFood.setTypeface(Fonts.georgiaItalic);
                textviewPrice.setTypeface(Fonts.georgia);
            }
        }
    }

    private void saveIteration(){
        if (buttonYes == null || buttonNo == null) {
            section.selectGoodPrice(99);
        } else {
            if(buttonYes.isChecked()){
                section.selectGoodPrice(1);
            } else if (buttonNo.isChecked()){
                section.selectGoodPrice(0);
            } else {
                section.selectGoodPrice(99);
            }
        }
        priceTest.getSections().set(index,section);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused) {
            Study.setCurrentSegmentData(priceTest);
            Study.skipToNextSegment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        paused = true;
    }



}
