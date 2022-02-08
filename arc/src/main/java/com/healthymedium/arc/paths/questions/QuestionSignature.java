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
package com.healthymedium.arc.paths.questions;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.healthymedium.arc.ui.Signature;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.paths.templates.QuestionTemplate;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class QuestionSignature extends QuestionTemplate {
    boolean allowHelp;
    Signature signature;

    public QuestionSignature(boolean allowBack, boolean allowHelp, String header, String subheader) {
        super(allowBack,header,subheader, ViewUtil.getString(R.string.button_next));
        this.allowHelp = allowHelp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        setHelpVisible(allowHelp);

        //Add spacer
        View spacer = new View(getContext());
        LinearLayout.LayoutParams spacerParams =
                new LinearLayout.LayoutParams (
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewUtil.dpToPx(100)
                );
        spacer.setLayoutParams(spacerParams);
        spacer.setBackgroundColor(Color.RED);
        spacer.setVisibility(View.INVISIBLE);
        content.addView(spacer);
        
        signature = new Signature(getContext());
        content.addView(signature);

        textViewSubheader.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17.0f);
        textViewSubheader.setLineSpacing(26, 1);

        signature.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signature.mSignaturePad.clear();
            }
        });

        signature.mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                buttonNext.setEnabled(true);
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                buttonNext.setEnabled(false);
            }
        });

        return view;
    }

    @Override
    protected void onNextRequested() {
        Bitmap bitmap = getSignature();
        Study.getRestClient().submitSignature(bitmap);
        if(!bitmap.isRecycled()){
            bitmap.recycle();
        }
        Study.getInstance().openNextFragment();
    }

    public Bitmap getSignature(){
        Bitmap bitmap = signature.mSignaturePad.getSignatureBitmap();
        return bitmap;
    }

    @Override
    public Object onValueCollection(){
        return null;
    }
}