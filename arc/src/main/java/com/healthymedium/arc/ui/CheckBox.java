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
import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;

public class CheckBox extends FrameLayout {

    String text;
    AppCompatCheckBox checkBox;
    FrameLayout frameLayoutCheckBox;
    CompoundButton.OnCheckedChangeListener listener;

    int paddingLeft;
    int paddingTop;
    int paddingRight;
    int paddingBottom;

    public CheckBox(Context context) {
        super(context);
        init(context);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_checkbox,this);
        checkBox = view.findViewById(R.id.checkBox);
        checkBox.setText(text);
        checkBox.setTypeface(Fonts.robotoMedium);

        frameLayoutCheckBox = view.findViewById(R.id.frameLayoutCheckBox);
        paddingLeft = frameLayoutCheckBox.getPaddingLeft();
        paddingTop = frameLayoutCheckBox.getPaddingTop();
        paddingRight = frameLayoutCheckBox.getPaddingRight();
        paddingBottom = frameLayoutCheckBox.getPaddingBottom();

        frameLayoutCheckBox.setBackgroundResource(R.drawable.background_checkbox_rounded_unselected);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    frameLayoutCheckBox.setBackgroundResource(R.drawable.background_checkbox_rounded);
                    checkBox.setTypeface(Fonts.robotoBlack);
                } else {
                    frameLayoutCheckBox.setBackgroundResource(R.drawable.background_checkbox_rounded_unselected);
                    checkBox.setTypeface(Fonts.robotoMedium);
                }
                frameLayoutCheckBox.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
                listener.onCheckedChanged(compoundButton, b);
                compoundButton.setChecked(b);
            }
        });
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener){
        this.listener = listener;
    }


    public void setText(String text){
        this.text = text;
        if(checkBox!=null){
            checkBox.setText(text);
        }
    }


    public void setChecked(boolean checked){
        if(checkBox!=null){
            checkBox.setChecked(checked);
        }
    }

    public boolean isChecked(){
        if(checkBox!=null){
            return checkBox.isChecked();
        }
        return false;
    }

}
