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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.ui.CircleProgressView;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class TestProgress extends ArcBaseFragment {

    CircleProgressView circleProgressView;

    TextView textViewHeader;
    String headerText;

    TextView textViewSubHeader;
    String subheaderText;

    TextView textViewThree;
    TextView textViewTextNumber;
    String testNumber;

    int percentageFrom;
    int percentageTo;

    boolean wasPaused = false;
    boolean ready = false;

    // Constructor for single test display
    public TestProgress(String header) {
        this.headerText = header;
        this.testNumber = String.valueOf(0);
        percentageFrom = 0;
        percentageTo = 100;
        subheaderText = ViewUtil.getString(R.string.testing_done);
    }

    public TestProgress(String header, int index) {
        this.headerText = header;
        this.testNumber = String.valueOf(index+1);
        switch (index){
            case 0:
                percentageFrom = 0;
                percentageTo = 33;
                subheaderText = ViewUtil.getString(R.string.testing_loading);
                break;
            case 1:
                percentageFrom = 33;
                percentageTo = 66;
                subheaderText = ViewUtil.getString(R.string.testing_loading);
                break;
            case 2:
                percentageFrom = 66;
                percentageTo = 100;
                subheaderText = ViewUtil.getString(R.string.testing_done);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_progress, container, false);

        textViewTextNumber = view.findViewById(R.id.textViewTextNumber);
        textViewTextNumber.setTypeface(Fonts.georgia);
        textViewTextNumber.setText(testNumber);

        textViewThree = view.findViewById(R.id.textViewThree);
        textViewThree.setTypeface(Fonts.georgia);
        // Check for single test language
        if (percentageFrom == 0 && percentageTo == 100) {
            textViewThree.setText("1");
        }

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.robotoBold);
        textViewHeader.setText(Html.fromHtml(headerText));

        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        textViewSubHeader.setTypeface(Fonts.georgiaItalic);
        textViewSubHeader.setText(Html.fromHtml(subheaderText));

        circleProgressView = view.findViewById(R.id.circleProgressView);
        circleProgressView.setProgress(percentageFrom,false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(wasPaused) {
            checkExit();
            return;
        }

        circleProgressView.setProgress(percentageTo,true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ready = true;
                checkExit();
            }
        },5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        wasPaused = true;
    }

    private void checkExit() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ready && isVisible()) {
                    Study.openNextFragment();
                }
            }
        },100);
    }


}
