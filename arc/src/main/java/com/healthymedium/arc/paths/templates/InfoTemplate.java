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
package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.Study;

@SuppressLint("ValidFragment")
public class InfoTemplate extends BaseFragment {

    String stringHeader;
    String stringBody;
    String stringButton;

    Drawable buttonImage;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewBody;

    protected LinearLayout content;

    protected Button button;
    boolean allowBack;

    public InfoTemplate(boolean allowBack, String header, String body, @Nullable String buttonText) {
        this.allowBack = allowBack;
        stringHeader = header;
        stringBody = body;
        stringButton = buttonText;

        if(allowBack){
            allowBackPress(true);
        }
    }

    public InfoTemplate(boolean allowBack, String header, String subheader, String body, @Nullable Drawable buttonImage) {
        this.allowBack = allowBack;
        stringHeader = header;
        //stringSubHeader = subheader;
        stringBody = body;
        this.buttonImage = buttonImage;

        if(allowBack){
            allowBackPress(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_info, container, false);
        content = view.findViewById(R.id.linearLayoutContent);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.georgiaItalic);
        textViewHeader.setText(stringHeader);


        textViewBody = view.findViewById(R.id.textViewBody);
        textViewBody.setText(Html.fromHtml(stringBody));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        float dpRatio = getResources().getDisplayMetrics().density;
        int side = (int)(32 * dpRatio);
        int top = (int)(15 * dpRatio);

        params.setMargins(side,top,side,0);
        textViewBody.setLayoutParams(params);



        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getInstance().openPreviousFragment();
            }
        });


        button = view.findViewById(R.id.button);
        if(stringButton!=null){
            button.setText(stringButton);
        } else if (buttonImage!=null) {
            button.setIcon(buttonImage);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getInstance().openNextFragment();
            }
        });

        if(allowBack){
            textViewBack.setVisibility(View.VISIBLE);
        }

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

}
