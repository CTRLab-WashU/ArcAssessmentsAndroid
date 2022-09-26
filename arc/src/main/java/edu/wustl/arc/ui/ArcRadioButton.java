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
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;

public class ArcRadioButton extends FrameLayout {

    String text;
    AppCompatRadioButton radioButton;
    FrameLayout frameLayoutRadioButton;
    CompoundButton.OnCheckedChangeListener listener;

    int paddingLeft;
    int paddingTop;
    int paddingRight;
    int paddingBottom;

    public ArcRadioButton(Context context) {
        super(context);
        init(context, null);
    }

    public ArcRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ArcRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        View view = inflate(context,R.layout.custom_radio_button,this);
        radioButton = view.findViewById(R.id.radioButton);
        radioButton.setText(text);
        radioButton.setTypeface(Fonts.robotoMedium);

        frameLayoutRadioButton = view.findViewById(R.id.frameLayoutRadioButton);
        paddingLeft = frameLayoutRadioButton.getPaddingLeft();
        paddingTop = frameLayoutRadioButton.getPaddingTop();
        paddingRight = frameLayoutRadioButton.getPaddingRight();
        paddingBottom = frameLayoutRadioButton.getPaddingBottom();

        // attributes
        TypedArray options = context.obtainStyledAttributes(attrs, R.styleable.RadioButton, 0, 0);
        boolean showButton = options.getBoolean(R.styleable.RadioButton_showButton, true);
        if(!showButton) {
            radioButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }
        int labelPosition = options.getInteger(R.styleable.RadioButton_labelPosition, 0);
        if(labelPosition == 2) {
            radioButton.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    frameLayoutRadioButton.setBackgroundResource(R.drawable.background_accent_rounded);
                    radioButton.setTypeface(Fonts.robotoBlack);
                } else {
                    frameLayoutRadioButton.setBackgroundResource(R.drawable.btn_border_unselected);
                    radioButton.setTypeface(Fonts.robotoMedium);
                }
                frameLayoutRadioButton.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
                compoundButton.setChecked(b);

                if(listener!=null) {
                    listener.onCheckedChanged(compoundButton, b);
                }
            }
        });
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener){
        this.listener = listener;
    }

    public void setText(String text){
        this.text = text;
        if(radioButton!=null){
            radioButton.setText(text);
        }
    }
    public String getText() {
        if(this.text != null){
            return this.text;
        }
        return null;
    }

    public void setChecked(final boolean checked){
        radioButton.post(new Runnable() {
            @Override
            public void run() {
                radioButton.setChecked(checked);
            }
        });

    }

    public void setCheckable(boolean checkable){
        radioButton.setClickable(checkable);
    }

    public boolean isChecked(){
        return radioButton.isChecked();
    }

    public void showButton(boolean showButton) {
        radioButton.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.radiobutton),null,null,null);
        if(!showButton) {
            radioButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        }
    }

    public void setLabelPosition(int position) {
        radioButton.setTextAlignment(position);
    }


}
