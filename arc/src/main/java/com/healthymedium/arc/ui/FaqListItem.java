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
import android.content.res.TypedArray;
import androidx.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.paths.informative.FAQAnswerScreen;
import com.healthymedium.arc.utilities.ViewUtil;

public class FaqListItem extends LinearLayout {

    String question = "";
    String answer = "";

    View borderTop;
    View borderBottom;
    TextView textView;

    public FaqListItem(Context context, @StringRes int question, @StringRes int answer) {
        super(context);
        this.question = ViewUtil.getString(question);
        this.answer = ViewUtil.getString(answer);
        init(context, null);
    }

    public FaqListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FaqListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context,R.layout.custom_faq_listitem,this);

        textView = view.findViewById(R.id.textviewQuestion);
        ViewUtil.setLineHeight(textView,26);

        borderTop = view.findViewById(R.id.borderTop);
        borderBottom = view.findViewById(R.id.borderBottom);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FaqListItem);

        try {
            boolean borderTopEnabled = a.getBoolean(R.styleable.FaqListItem_enableBorderTop,false);
            boolean borderBottomEnabled = a.getBoolean(R.styleable.FaqListItem_enableBorderBottom,false);
            setBorderEnabled(borderTopEnabled,borderBottomEnabled);

            if(a.hasValue(R.styleable.FaqListItem_question)){
                question = a.getString(R.styleable.FaqListItem_question);
            }
            if(a.hasValue(R.styleable.FaqListItem_answer)){
                answer = a.getString(R.styleable.FaqListItem_answer);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }

        if(question!=null){
            textView.setText(question);
        }

        // default behavior
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment fragment = new FAQAnswerScreen(question, answer);
                NavigationManager.getInstance().open(fragment);
            }
        });
    }

    void setBorderEnabled(boolean topEnabled, boolean bottomEnabled){
        borderTop.setVisibility(topEnabled ? VISIBLE:GONE);
        borderBottom.setVisibility(bottomEnabled ? VISIBLE:GONE);
    }

}
