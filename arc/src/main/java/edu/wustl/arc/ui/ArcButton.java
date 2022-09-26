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
import androidx.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.wustl.arc.ui.base.ChipButton;
import edu.wustl.arc.ui.base.SimpleGradient;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.utilities.ViewUtil;

public class ArcButton extends ChipButton {

    public static final int THEME_PRIMARY = 0;
    public static final int THEME_WHITE = 1;
    public static final int THEME_BLACK = 2;

    TextView textView;
    ImageView imageView;

    public ArcButton(Context context) {
        super(context);
        init(context);
        applyAttributeSet(context,null);
    }

    public ArcButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        applyAttributeSet(context, attrs);
    }

    public ArcButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        applyAttributeSet(context, attrs);
    }

    private void init(Context context){
        setOrientation(HORIZONTAL);
        textView = new TextView(context);
        textView.setTypeface(Fonts.robotoBold);
        textView.setTextSize(18);
        textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        imageView = new ImageView(context);
        imageView.setVisibility(GONE);

        addView(imageView);
        addView(textView);

        setElevation(ViewUtil.dpToPx(2));
    }

    private void applyAttributeSet(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Button);
        try {
            int normalTop = R.color.primaryButtonLight;
            int normalBottom = R.color.primaryButtonDark;
            int selected = R.color.primaryButtonDark;
            int textColor = R.color.white;

            textView.setText(a.getString(R.styleable.Button_android_text));
            if(a.hasValue(R.styleable.Button_buttonTheme)){
                int styleEnum = a.getInt(R.styleable.Button_buttonTheme,0);
                switch (styleEnum){
                    case THEME_PRIMARY:
                        break; // already set
                    case THEME_WHITE:
                        normalTop = R.color.whiteButtonLight;
                        normalBottom = R.color.whiteButtonDark;
                        selected = R.color.whiteButtonSelected;
                        textColor = R.color.black;
                        break;
                    case THEME_BLACK:
                        normalTop = R.color.blackButtonLight;
                        normalBottom = R.color.blackButtonDark;
                        selected = R.color.blackButtonSelected;
                        break;
                }
            }

            topLayer.setStrokeGradient(SimpleGradient.LINEAR_VERTICAL, ViewUtil.getColor(context,normalTop), ViewUtil.getColor(context,normalBottom));
            topLayer.setFillGradient(SimpleGradient.LINEAR_VERTICAL, ViewUtil.getColor(context,normalTop), ViewUtil.getColor(context,normalBottom));
            bottomLayer.setFillColor(ViewUtil.getColor(context,selected));
            textView.setTextColor(ViewUtil.getColor(context,textColor));

            if(a.getDrawable(R.styleable.Button_iconRight) != null) {
                ImageView imageViewRight = new ImageView(context);
                imageViewRight.setBackground(a.getDrawable(R.styleable.Button_iconRight));
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL;
                params.leftMargin = ViewUtil.dpToPx(8);
                imageViewRight.setLayoutParams(params);
                addView(imageViewRight);
            }

            textView.setAllCaps(a.getBoolean(R.styleable.Button_allCaps, false));

            setIcon(a.getDrawable(R.styleable.Button_icon));
            boolean enabled = a.getBoolean(R.styleable.Button_android_enabled,true);
            setEnabled(enabled);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    public void clearText() {
        textView.setText("");
    }

    public void setText(String string){
        imageView.setVisibility(GONE);
        textView.setText(string);
    }

    public void setIcon(@DrawableRes int id) {
        setIcon(ViewUtil.getDrawable(id));
    }

    public void setIcon(Drawable drawable){
        if(drawable!=null) {
            clearText();
            imageView.setBackground(drawable);
            imageView.setVisibility(VISIBLE);
        }
    }
}
