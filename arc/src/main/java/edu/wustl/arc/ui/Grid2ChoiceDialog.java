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

import android.app.Activity;

import androidx.annotation.DrawableRes;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Log;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.ui.base.PointerDialog;
import edu.wustl.arc.utilities.ViewUtil;

public class Grid2ChoiceDialog extends PointerDialog {

    boolean selectable = true;

    Grid2ChoiceView phone;
    Grid2ChoiceView key;
    Grid2ChoiceView pen;

    TextView textViewGridDialog;
    Listener listener;

    TextView textViewRemoveItem;
    View divider;

    public Grid2ChoiceDialog(Activity activity, View target, int pointerConfig) {
        super(activity, target, null, pointerConfig);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_grid2_choice,null);

        OnTouchListener touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false;
                }
                if(listener==null){
                    return false;
                }
                if(selectable) {
                    int id = ((Grid2ChoiceView)v).getDrawableImageId();
                    if(id==R.drawable.phone) {
                        Log.d(getTag(),"onSelected(phone)");
                    }
                    if(id==R.drawable.key) {
                        Log.d(getTag(),"onSelected(key)");
                    }
                    if(id==R.drawable.pen) {
                        Log.d(getTag(),"onSelected(pen)");
                    }
                    listener.onSelected(id);
                }
                selectable = false;
                listener = null;
                dismiss();
                return false;
            }
        };

        phone = view.findViewById(R.id.phone);
        phone.setImage(R.drawable.phone);
        phone.setOnTouchListener(touchListener);

        key = view.findViewById(R.id.key);
        key.setImage(R.drawable.key);
        key.setOnTouchListener(touchListener);

        pen = view.findViewById(R.id.pen);
        pen.setImage(R.drawable.pen);
        pen.setOnTouchListener(touchListener);

        textViewGridDialog = view.findViewById(R.id.textViewGridDialog);
        textViewGridDialog.setText(ViewUtil.getString(R.string.grids_tutorial_vb_select));

        divider = view.findViewById(R.id.divider);
        divider.setVisibility(View.GONE);
        textViewRemoveItem = view.findViewById(R.id.textViewRemoveItem);
        textViewRemoveItem.setVisibility(View.GONE);

        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setRadius(16);
        setView(view);
    }

    public void disableChoice(@DrawableRes int image) {
        Grid2ChoiceView choice = null;

        if(image==R.drawable.phone) {
            choice = phone;
            Log.d(getTag(),"disableChoice(phone)");
        }
        if(image==R.drawable.key) {
            choice = key;
            Log.d(getTag(),"disableChoice(key)");
        }
        if(image==R.drawable.pen) {
            choice = pen;
            Log.d(getTag(),"disableChoice(pen)");
        }

        if(choice==null){
            return;
        }

        choice.setOnTouchListener(null);
        choice.setAlpha(0.4f);

        divider.setVisibility(View.VISIBLE);
        ViewUtil.underlineTextView(textViewRemoveItem);
        textViewRemoveItem.setVisibility(View.VISIBLE);

        OnTouchListener removeImageTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(getTag(),"removeItem");

                if(event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false;
                }
                if(listener==null){
                    return false;
                }
                listener.onRemove();
                listener = null;
                dismiss();
                return false;
            }
        };
        textViewRemoveItem.setOnTouchListener(removeImageTouchListener);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onSelected(@DrawableRes int image);
        void onRemove();
    }

    public Grid2ChoiceView getPhoneView() {
        return phone;
    }

    public Grid2ChoiceView getPenView() {
        return pen;
    }

    public Grid2ChoiceView getKeyView() {
        return key;
    }

    public TextView getRemoveItemView() {
        return textViewRemoveItem;
    }

}
