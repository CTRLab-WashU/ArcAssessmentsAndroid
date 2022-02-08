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
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.wustl.arc.assessments.R;
import edu.wustl.arc.utilities.ViewUtil;

public class Rating extends LinearLayoutCompat{

    TextView textLow;
    TextView textHigh;

    SeekBar seekBar;
    SeekBar.OnSeekBarChangeListener listener;

    Drawable thumbNormal;
    Drawable thumbPressed;

    int padding;

    public Rating(Context context) {
        super(context);
        init(context);
    }

    public Rating(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setText(context,attrs);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.custom_rating,this);
        textLow = view.findViewById(R.id.textviewRatingLow);
        textHigh = view.findViewById(R.id.textviewRatingHigh);
        seekBar = view.findViewById(R.id.seekbarRating);
        seekBar.setThumbOffset(0);

        padding = ViewUtil.dpToPx(49);

        thumbPressed =  ViewUtil.getDrawable(R.drawable.rating_thumb_pressed);
        thumbNormal =  ViewUtil.getDrawable(R.drawable.rating_thumb_normal);


        seekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        seekBar.setThumb(thumbPressed);
                        seekBar.setPadding(padding,0,padding,0);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        seekBar.setThumb(thumbNormal);
                        break;
                }
                return false;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(listener!=null){
                    listener.onProgressChanged(seekBar,i,b);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(listener!=null){
                    listener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setThumb(thumbNormal);
                if(listener!=null){
                    listener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    private void setText(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Rating);
        try {
            textLow.setText(a.getString(R.styleable.Rating_text_low));
            textHigh.setText(a.getString(R.styleable.Rating_text_high));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    public void setLowText(String low){
        textLow.setText(low);
    }


    public void setHighText(String high){
        textHigh.setText(high);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener){
        this.listener = listener;
    }

    public float getValue(){
        return (float) seekBar.getProgress()/100;
    }

    public void setValue(float value){
        if(seekBar!=null){
            seekBar.setProgress((int) (value*100));
        }
    }

    public SeekBar getSeekBar(){
        return seekBar;
    }



}
