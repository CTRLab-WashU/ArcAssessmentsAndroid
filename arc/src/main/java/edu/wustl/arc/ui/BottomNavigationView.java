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
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.annotation.DrawableRes;
import edu.wustl.arc.hints.HintHighlighter;
import edu.wustl.arc.hints.HintPointer;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.wustl.arc.assessments.R;
import edu.wustl.arc.ui.base.RoundedLinearLayout;
import edu.wustl.arc.utilities.ViewUtil;

public class BottomNavigationView extends RoundedLinearLayout {

    public static boolean shouldShowEarnings = false;

    public static final int TAG_HOME = 0;
    public static final int TAG_PROGRESS = 1;
    public static final int TAG_EARNINGS = 2;
    public static final int TAG_RESOURCES = 3;

    private MenuItem home;
    private MenuItem progress;
    private MenuItem earnings;
    private MenuItem resources;

    // ---------------------------------------------------------------------------------------------

    private MenuItem lastSelected;
    private int normalColor;
    private int selectedColor;
    private Listener listener;

    boolean enabled = true;

    public BottomNavigationView(Context context) {
        super(context);
        init(null,0);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        Context context = getContext();

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setStrokeWidth(1);
        setStrokeColor(R.color.text);
        setFillColor(R.color.white);

        normalColor = ViewUtil.getColor(getContext(),R.color.text);
        selectedColor = ViewUtil.getColor(getContext(),R.color.primary);

        home = new MenuItem(context,
                ViewUtil.getString(R.string.resources_nav_home),
                R.drawable.ic_home_inactive,
                R.drawable.ic_home_active,
                TAG_HOME);

        progress = new MenuItem(context,
                ViewUtil.getString(R.string.resources_nav_progress),
                R.drawable.ic_progress_inactive,
                R.drawable.ic_progress_active,
                TAG_PROGRESS);

        earnings = new MenuItem(context,
                ViewUtil.getString(R.string.resources_nav_earnings),
                R.drawable.ic_earnings_inactive,
                R.drawable.ic_earnings_active,
                TAG_EARNINGS);

        resources = new MenuItem(context,
                ViewUtil.getString(R.string.resources_nav_resources),
                R.drawable.ic_resources_inactive,
                R.drawable.ic_resources_active,
                TAG_RESOURCES);

        addView(home);
        addView(progress);
        if (shouldShowEarnings) {
            addView(earnings);
        }
        addView(resources);

        setPadding(0,0,0, ViewUtil.getNavBarHeight());

        // set home as default
        home.setSelected(true);
        lastSelected = home;
    }

    public void setHomeSelected() {
        home.setSelected(true);
        if (lastSelected != home) {
            lastSelected.setSelected(false);
        }
        lastSelected = home;
    }

    public class MenuItem extends LinearLayout {

        private Drawable drawableNormal;
        private Drawable drawableSelected;

        private ImageView imageView;
        private TextView textView;

        public MenuItem(Context context, String name, @DrawableRes int resNormal, @DrawableRes int resSelected, final int tag){
            super(context);

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lastSelected!=null && lastSelected!=MenuItem.this){
                        lastSelected.setSelected(false);
                    }
                    setSelected(true);
                    lastSelected = MenuItem.this;
                    if(listener!=null){
                        listener.onSelected(tag);
                    }
                }
            });

            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setPadding(0,ViewUtil.dpToPx(8),0,ViewUtil.dpToPx(4));
            setLayoutParams(new LayoutParams(ViewUtil.dpToPx(80), ViewGroup.LayoutParams.MATCH_PARENT));

            imageView = new ImageView(context);
            addView(imageView);

            textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
            textView.setLineSpacing(ViewUtil.dpToPx(2),0);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            addView(textView);

            drawableNormal = ViewUtil.getDrawable(context,resNormal);
            drawableSelected = ViewUtil.getDrawable(context,resSelected);
            textView.setText(name);

            setSelected(false);
        }

        public void setSelected(boolean selected){
            imageView.setImageDrawable(selected ? drawableSelected : drawableNormal);
            textView.setTextColor(selected ? selectedColor : normalColor);
        }

    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(!enabled){
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void openHome() {
        home.callOnClick();
    }

    public void openProgress() {
        progress.callOnClick();
    }

    public void openEarnings() {
        if (!shouldShowEarnings) {
            return;
        }
        earnings.callOnClick();
    }

    public void openResources() {
        resources.callOnClick();
    }

    public void showHomeHint(final Activity activity) {

        enabled = false;

        final HintPointer homeHint = new HintPointer(activity, home, true, true);
        homeHint.setText(ViewUtil.getString(R.string.popup_tab_home));

        final HintHighlighter homeHighlight = new HintHighlighter(activity);
        homeHighlight.addTarget(home, 40, 0);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeHint.dismiss();
                homeHighlight.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        showProgressHint(activity);
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        homeHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        homeHint.show();
        homeHighlight.show();
    }

    public void showProgressHint(final Activity activity) {
        final HintPointer progressHint = new HintPointer(activity, progress, true, true);
        progressHint.setText(ViewUtil.getString(R.string.popup_tab_progress));

        final HintHighlighter progressHighlight = new HintHighlighter(activity);
        progressHighlight.addTarget(progress, 40, 0);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressHint.dismiss();
                progressHighlight.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        showEarningsHint(activity);
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        progressHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        progressHint.show();
        progressHighlight.show();
    }

    public void showEarningsHint(final Activity activity) {
        if (!shouldShowEarnings) {
            showResourcesHint(activity);
            return;
        }

        final HintPointer earningsHint = new HintPointer(activity, earnings, true, true);
        earningsHint.setText(ViewUtil.getString(R.string.popup_tab_earnings));

        final HintHighlighter earningsHighlight = new HintHighlighter(activity);
        earningsHighlight.addTarget(earnings, 40, 0);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                earningsHint.dismiss();
                earningsHighlight.dismiss();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        showResourcesHint(activity);
                    }
                };
                handler.postDelayed(runnable,600);
            }
        };

        earningsHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        earningsHint.show();
        earningsHighlight.show();
    }

    public void showResourcesHint(Activity activity) {
        final HintPointer resourcesHint = new HintPointer(activity, resources, true, true);
        resourcesHint.setText(ViewUtil.getString(R.string.popup_tab_resources));

        final HintHighlighter resourcesHighlight = new HintHighlighter(activity);
        resourcesHighlight.addTarget(resources, 40, 0);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resourcesHint.dismiss();
                resourcesHighlight.dismiss();

                enabled = true;
            }
        };

        resourcesHint.addButton(ViewUtil.getString(R.string.button_next), listener);

        resourcesHint.show();
        resourcesHighlight.show();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onSelected(int tag);
    }

}
