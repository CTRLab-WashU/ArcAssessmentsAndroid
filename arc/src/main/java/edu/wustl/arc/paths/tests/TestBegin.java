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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.ViewUtil;

public class TestBegin extends BaseFragment {

    TextView begin;
    TextView number;

    int count = 3;
    boolean paused = false;
    boolean done = false;

    Handler handler;
    Runnable runnableCountdown = new Runnable() {
        @Override
        public void run() {
            count--;
            if (count >= 1) {
                number.animate()
                        .alpha(0)
                        .setDuration(100)
                        .withEndAction(runnableDisappear);
            } else if(paused) {
                done = true;
            } else {
                Study.openNextFragment();
            }
        }
    };

    Runnable runnableDisappear = new Runnable() {
        @Override
        public void run() {
            number.setAlpha(0);
            number.setText(String.valueOf(count));
            number.animate()
                    .alpha(1)
                    .setDuration(100)
                    .withEndAction(runnableReappear);
        }
    };

    Runnable runnableReappear = new Runnable() {
        @Override
        public void run() {
            number.setAlpha(1);
            handler.postDelayed(runnableCountdown,800);
        }
    };

    public TestBegin() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_begin, container, false);

        begin = view.findViewById(R.id.header);
        begin.setText(Html.fromHtml(ViewUtil.getString(R.string.testing_begin)));

        number = view.findViewById(R.id.number);
        number.setTypeface(Fonts.georgiaItalic);

        handler = new Handler();
        handler.postDelayed(runnableCountdown,900);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused && done) {
            Study.openNextFragment();
        } else {
            paused = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

}
