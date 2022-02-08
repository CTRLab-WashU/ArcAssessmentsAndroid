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
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.library.R;

public class DialogButtonTutorial extends LinearLayout {

    public TextView header;
    public TextView body;
    public TextView button;

    private FrameLayout lineFrameLayout;

    public DialogButtonTutorial(Context context) {
        super(context);
        init(context);
    }

    public DialogButtonTutorial(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DialogButtonTutorial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context, R.layout.dialog_btn_tutorial,this);
        header = view.findViewById(R.id.dialogBtnTutorialHeader);
        body = view.findViewById(R.id.dialogBtnTutorialBody);
        button = view.findViewById(R.id.button);

        lineFrameLayout = view.findViewById(R.id.lineFrameLayout);

        SpannableString content = new SpannableString("View a Tutorial");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        button.setText(content);
    }

    public void enableButton() {
        lineFrameLayout.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
    }

    public void disableButton() {
        lineFrameLayout.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
    }

}
