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
package com.healthymedium.arc.ui;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.healthymedium.arc.library.R;

public class DigitView extends FrameLayout {

    TextView textViewDigit;
    TextView textViewUnselected;
    TextView textViewSelected;
    OnFocusChangeListener listener;

    public DigitView(Context context) {
        super(context);
        init(context);
    }

    public DigitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DigitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_input_digit,this);
        textViewDigit = view.findViewById(R.id.textViewDigit);
        textViewUnselected = view.findViewById(R.id.textViewUnselected);
        textViewSelected = view.findViewById(R.id.textViewSelected);
    }

    public void setFocused(boolean focused){
        if(focused){
            textViewUnselected.setVisibility(INVISIBLE);
            textViewSelected.setVisibility(VISIBLE);
        } else {
            textViewUnselected.setVisibility(VISIBLE);
            textViewSelected.setVisibility(INVISIBLE);
        }
        if(listener!=null){
            listener.onFocusChange(this,focused);
        }
    }

    public char getDigit(){
        return textViewDigit.getText().charAt(0);
    }

    public void setDigit(Character digit, boolean error){
        textViewDigit.setText(Character.toString(digit));
        int color = error ? R.color.red : R.color.primary;
        textViewUnselected.setBackgroundResource(color);
        textViewSelected.setBackgroundResource(color);
    }

    public void removeDigit(){
        textViewDigit.setText("");
        textViewUnselected.setBackgroundResource(R.color.grey);
        textViewSelected.setBackgroundResource(R.color.primary);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener listener){
        this.listener = listener;
    }

    @Override
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        super.setOnClickListener(l);
        textViewDigit.setOnClickListener(l);
        textViewSelected.setOnClickListener(l);
        textViewUnselected.setOnClickListener(l);
    }
}
