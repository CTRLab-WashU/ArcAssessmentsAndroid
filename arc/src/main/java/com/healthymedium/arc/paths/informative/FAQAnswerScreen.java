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
package com.healthymedium.arc.paths.informative;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class FAQAnswerScreen extends BaseFragment {

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewSubHeader;

    String headerText;
    String bodyText;

    public FAQAnswerScreen(String header, String body) {
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());

        headerText = header;
        bodyText = body;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq_answer, container, false);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setText(ViewUtil.getString(R.string.button_back));
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });
        textViewBack.setVisibility(View.VISIBLE);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.robotoMedium);
        textViewHeader.setText(headerText);

        textViewSubHeader = view.findViewById(R.id.textViewSubHeader);
        textViewSubHeader.setText(Html.fromHtml(bodyText));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setPadding(0, ViewUtil.getStatusBarHeight(),0,0);
    }

}
