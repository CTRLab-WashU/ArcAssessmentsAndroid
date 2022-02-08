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
package edu.wustl.arc.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.utilities.ViewUtil;

/*
    Displays an initial value of text on screen, then text slides up to reveal new text.

    Usage:
        Define in XML:
            <com.healthymedium.arc.ui.TotalEarningsView
                android:id="@+id/totalEarningsView"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"/>

        Set first and second values in Java:
            TotalEarningsView totalEarningsView = view.findViewById(R.id.totalEarningsView);
            totalEarningsView.setText($0.00);
            totalEarningsView.setText($7.50,true);
*/

public class TotalEarningsView extends LinearLayout {

    private int white;
    private Handler handler;
    TextSwitcher textSwitcher;

    public TotalEarningsView(Context context) {
        super(context);
        init();
    }

    public TotalEarningsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TotalEarningsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        white = ViewUtil.getColor(getContext(), R.color.white);
        handler = new Handler();

        textSwitcher = new TextSwitcher(getContext());
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getContext());
                textView.setTypeface(Fonts.robotoMedium);
                textView.setTextSize(32);
                textView.setTextColor(white);
                return textView;
            }
        });
        textSwitcher.setInAnimation(getContext(), R.anim.slide_in_up);
        textSwitcher.setOutAnimation(getContext(), R.anim.slide_out_up);
        addView(textSwitcher);
    }

    public void setText(String value) {
        setText(value,false,null);
    }

    public void setText(String value, boolean animate) {
        setText(value,animate,null);
    }

    public void setText(String value, boolean animate, final Listener listener) {
        if(animate){
            textSwitcher.setText(value);
            if(listener!=null){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFinished();
                    }
                },500);
            }
        } else {
            textSwitcher.setCurrentText(value);
        }
    }

    public interface Listener {
        void onFinished();
    }

}
