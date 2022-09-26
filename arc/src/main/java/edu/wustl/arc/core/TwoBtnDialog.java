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
package edu.wustl.arc.core;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import edu.wustl.arc.ui.ArcButton;

import edu.wustl.arc.assessments.R;

@SuppressLint("ValidFragment")
public class TwoBtnDialog extends DialogFragment {
    public String headerText;
    public String bodyText;
    public String buttonText;
    public String buttonTextBottom;

    Handler handlerTimeout;
    Runnable runnableTimeout;

    Handler handlerEnable;
    Runnable runnableEnable;

    TextView textViewHeader;
    TextView textViewBody;
    ArcButton button;
    ArcButton buttonBottom;
    SimpleDialog.OnDialogDismiss listener;

    public TwoBtnDialog(String headerText, String bodyText, String buttonText, String buttonTextBottom){
        this.headerText = headerText;
        this.bodyText = bodyText;
        this.buttonText = buttonText;
        this.buttonTextBottom = buttonTextBottom;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        //setStyle(STYLE_NO_TITLE,R.style.AppTheme);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_two_btn, container, false);
        button = view.findViewById(R.id.buttonTop);
        buttonBottom = view.findViewById(R.id.buttonBottom);

        if(buttonText.isEmpty()){
            button.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.dismiss();
                }
            });
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(handlerTimeout != null){
                        handlerTimeout.removeCallbacks(runnableTimeout);
                    }
                    if(listener != null) {
                        listener.dismiss();
                    }
                    dismiss();
                }
            });
            button.setText(buttonText);
        }

        // TODO
        // Generalize the onClickListener
        // Hardcoded in the interest of time
        if(buttonTextBottom.isEmpty()){
            buttonBottom.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.dismiss();
                }
            });
        } else {
            buttonBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(handlerTimeout != null){
                        handlerTimeout.removeCallbacks(runnableTimeout);
                    }
                    if(listener != null) {
                        listener.dismiss();
                    }
                    dismiss();
                }
            });
            buttonBottom.setText(buttonTextBottom);
        }

        textViewHeader = view.findViewById(R.id.textviewHeaderText);
        textViewHeader.setText(Html.fromHtml(headerText));

        textViewBody = view.findViewById(R.id.textviewBodyText);
        textViewBody.setText(Html.fromHtml(bodyText));

        return view;
    }

    public void setOnDialogDismissListener(SimpleDialog.OnDialogDismiss listener){
        this.listener = listener;
    }

    public interface OnDialogDismiss{
        void dismiss();
    }
}
