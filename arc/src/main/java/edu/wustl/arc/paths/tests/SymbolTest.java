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
import android.widget.TextView;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.study.StateMachine;
import edu.wustl.arc.ui.SymbolButton;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.SymbolsTestPathData;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.ui.SymbolView;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SymbolTest extends ArcBaseFragment {

    public static List<Integer> symbolset = new ArrayList<>(Arrays.asList(
            R.drawable.symbol_0,
            R.drawable.symbol_1,
            R.drawable.symbol_2,
            R.drawable.symbol_3,
            R.drawable.symbol_4,
            R.drawable.symbol_5,
            R.drawable.symbol_6,
            R.drawable.symbol_7));

    SymbolView buttonTop1;
    SymbolView buttonTop2;
    SymbolView buttonTop3;
    SymbolButton buttonBottom1;
    SymbolButton buttonBottom2;
    Random random;
    List symbols = new ArrayList();
    int iteration = 0;

    boolean paused;

    Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnable);
            saveIteration();
            if(iteration==12) {
                Study.setCurrentSegmentData(symbolsTest);
                Study.openNextFragment();
            }else {
                setupNextIteration();
            }
        }
    };

    TextView textViewWhichMatches;
    TextView textViewOr;

    SymbolsTestPathData symbolsTest;
    SymbolsTestPathData.Section symbolsTestSection;

    public SymbolTest() {
        if(StateMachine.AUTOMATED_TESTS_RANDOM_SEED == -1){
            random= new Random(System.currentTimeMillis());
        }
        else {
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
        View view = inflater.inflate(R.layout.fragment_symbols_test, container, false);
        handler = new Handler();
        buttonTop1 = view.findViewById(R.id.symbolbutton_top1);
        buttonTop2 = view.findViewById(R.id.symbolbutton_top2);
        buttonTop3 = view.findViewById(R.id.symbolbutton_top3);

        textViewWhichMatches = view.findViewById(R.id.textViewWhichMatches);
        textViewWhichMatches.setText(ViewUtil.getHtmlString(R.string.symbols_match));

        textViewOr = view.findViewById(R.id.textView19);
        textViewOr.setText(ViewUtil.getHtmlString(R.string.symbols_or));

        buttonBottom1 = view.findViewById(R.id.symbolbutton_bottom1);
        buttonBottom1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        symbolsTestSection.setSelected(1, System.currentTimeMillis());
                        handler.post(runnable);
                        buttonBottom1.setSelected(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        buttonBottom1.setSelected(false);
                        break;
                }
                return true;
            }
        });

        buttonBottom2 = view.findViewById(R.id.symbolbutton_bottom2);
        buttonBottom2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        symbolsTestSection.setSelected(2, System.currentTimeMillis());
                        handler.post(runnable);
                        buttonBottom2.setSelected(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        buttonBottom2.setSelected(false);
                        break;
                }
                return true;
            }
        });

        symbolsTest = (SymbolsTestPathData) Study.getCurrentSegmentData();
        symbolsTest.markStarted();
        setupNextIteration();

        return view;
    }

    private void setupNextIteration(){
        symbolsTestSection = symbolsTest.getSections().get(iteration);
        iteration++;
        symbols.clear();
        symbols.addAll(symbolset);

        int[] set = generateNextRandomSet(random, symbols);

        List<List<String>> options = new ArrayList<>();
        List<String> option1 = new ArrayList<>();
        option1.add(getResources().getResourceEntryName(set[0]).replace("symbol_", ""));
        option1.add(getResources().getResourceEntryName(set[1]).replace("symbol_", ""));
        List<String> option2 = new ArrayList<>();
        option2.add(getResources().getResourceEntryName(set[2]).replace("symbol_", ""));
        option2.add(getResources().getResourceEntryName(set[3]).replace("symbol_", ""));
        List<String> option3 = new ArrayList<>();
        option3.add(getResources().getResourceEntryName(set[4]).replace("symbol_", ""));
        option3.add(getResources().getResourceEntryName(set[5]).replace("symbol_", ""));

        options.add(option1);
        options.add(option2);
        options.add(option3);
        symbolsTestSection.setOptions(options);

        buttonTop1.setImages(set[0],set[1]);
        buttonTop2.setImages(set[2],set[3]);
        buttonTop3.setImages(set[4],set[5]);
        int topIndex = random.nextInt(3)*2;
        int c1 = set[topIndex];
        int c2 = set[topIndex+1];
        /*
        if(random.nextInt(2)==0){
            int copy = c1;
            c1 = c2;
            c2 = copy;
        }*/
        List<List<String>> choices = new ArrayList<>();
        List<String> choice1 = new ArrayList<>();
        List<String> choice2 = new ArrayList<>();
        if(random.nextInt(2)==0){
            symbolsTestSection.setCorrect(2);
            choice1.add(getResources().getResourceEntryName(set[6]).replace("symbol_", ""));
            choice1.add(getResources().getResourceEntryName(set[7]).replace("symbol_", ""));
            buttonBottom1.setImages(set[6],set[7]);
            choice2.add(getResources().getResourceEntryName(c1).replace("symbol_", ""));
            choice2.add(getResources().getResourceEntryName(c2).replace("symbol_", ""));
            buttonBottom2.setImages(c1,c2);
        } else {
            symbolsTestSection.setCorrect(1);
            choice1.add(getResources().getResourceEntryName(c1).replace("symbol_", ""));
            choice1.add(getResources().getResourceEntryName(c2).replace("symbol_", ""));
            buttonBottom1.setImages(c1,c2);
            choice2.add(getResources().getResourceEntryName(set[6]).replace("symbol_", ""));
            choice2.add(getResources().getResourceEntryName(set[7]).replace("symbol_", ""));
            buttonBottom2.setImages(set[6],set[7]);
        }
        choices.add(choice1);
        choices.add(choice2);

        symbolsTestSection.setChoices(choices);
        symbolsTestSection.markAppearanceTime();
    }

    public static int[] generateNextRandomSet(Random random, List symbols){
        int[] set =  new int[8];
        Integer i1;
        Integer i2;
        for(int i=0;i<8;i++){
            i1 = (int)symbols.get(random.nextInt(8));
            i2 = (int)symbols.get(random.nextInt(8));
            boolean run = true;
            while(run){
                if(run) {
                    i2 = (int) symbols.get(random.nextInt(8));
                    if (i1 != i2 && !similar(i1, i2, set)) {
                        run = false;
                    }
                }
            }
            set[i] = i1;
            set[i+1] = i2;
            i++;
        }
        return set;

    }

    private static boolean similar(int a1,int a2, int[] set){
        if(a1==a2){
            return true;
        }
        for(int i=0;i<set.length;i++){
            if((a1==set[i] && a2 == set[i+1]) || (a1==set[i+1] && a2 == set[i])) {
                return true;
            }
            i++;
        }
        return false;
    }

    private void saveIteration(){
        symbolsTest.getSections().set(iteration-1,symbolsTestSection);
    }

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        paused = true;
    }
}
