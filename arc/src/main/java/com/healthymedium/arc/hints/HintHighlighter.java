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
package com.healthymedium.arc.hints;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.healthymedium.arc.library.R;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class HintHighlighter extends FrameLayout {

    private static final int backgroundColor = ViewUtil.getColor(R.color.shadow);
    private static final int transparentColor = ViewUtil.getColor(R.color.transparent);

    private List<HintHighlightTarget> targets;
    private ViewGroup parent;
    private boolean dismissing = false;

    public HintHighlighter(Activity activity) {
        super(activity);
        super.setOnTouchListener(touchListener);
        super.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        parent = (ViewGroup) activity.getWindow().getDecorView();
        targets = new ArrayList<>();
        init();
    }

    public HintHighlighter(Activity activity, View view) {
        super(activity);
        super.setOnTouchListener(touchListener);

        parent = (ViewGroup) activity.getWindow().getDecorView();
        targets = new ArrayList<>();
        targets.add(new HintHighlightTarget(getContext(),view,targetListener));
        view.addOnAttachStateChangeListener(attachStateChangeListener);
        init();
    }

    public HintHighlighter(Activity activity, List<View> views) {
        super(activity);
        super.setOnTouchListener(touchListener);

        parent = (ViewGroup) activity.getWindow().getDecorView();
        for(View view : views){
            view.addOnAttachStateChangeListener(attachStateChangeListener);
            targets.add(new HintHighlightTarget(getContext(),view,targetListener));
        }
        init();
    }

    private void init(){
        setBackgroundColor(backgroundColor);
    }

    public HintHighlightTarget getTarget(View view) {
        for(HintHighlightTarget target : targets){
            if(target.getView().equals(view)){
                return target;
            }
        }
        return null;
    }

    public void setShadowEnabled(boolean enabled, boolean animate) {
        if(!animate) {
            setBackgroundColor(enabled ? backgroundColor:transparentColor);
            return;
        }

        int colorFrom = (enabled ? transparentColor:backgroundColor);
        int colorTo = (enabled ? backgroundColor:transparentColor);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    public void addPulsingTarget(View view) {
        view.addOnAttachStateChangeListener(attachStateChangeListener);
        HintHighlightTarget target = new HintHighlightTarget(getContext(),view,targetListener);
        target.setPulsing(getContext(),0);
        targets.add(target);
    }

    public void addPulsingTarget(View view, int dpRaduis) {
        view.addOnAttachStateChangeListener(attachStateChangeListener);
        HintHighlightTarget target = new HintHighlightTarget(getContext(),view,targetListener);
        target.setRadius(dpRaduis);
        target.setPulsing(getContext(),0);
        targets.add(target);
    }

    public void addTarget(View view) {
        view.addOnAttachStateChangeListener(attachStateChangeListener);
        targets.add(new HintHighlightTarget(getContext(),view,targetListener));
    }

    public void addTarget(View view, int dpRaduis) {
        view.addOnAttachStateChangeListener(attachStateChangeListener);
        HintHighlightTarget target = new HintHighlightTarget(getContext(),view,targetListener);
        target.setRadius(dpRaduis);
        targets.add(target);
    }

    public void addTarget(View view, int dpRaduis, int dpPadding) {
        view.addOnAttachStateChangeListener(attachStateChangeListener);
        HintHighlightTarget target = new HintHighlightTarget(getContext(),view, targetListener);
        target.setRadius(dpRaduis);
        target.setPadding(dpPadding);
        targets.add(target);
    }

    public void clearTargets() {
        for(HintHighlightTarget target : targets) {
            if(target!=null){
                target.removeOnAttachStateChangeListener(attachStateChangeListener);
            }
        }
        targets.clear();
    }

    public void show() {
        setAlpha(0.0f);

        parent.addView(HintHighlighter.this);
        for(HintHighlightTarget target : targets) {
            if(target.getPulse()!=null){
                addView(target.getPulse());
            }
            addView(target);
        }

        HintHighlighter.this.animate()
                .alpha(1.0f)
                .setDuration(400);
    }

    public void dismiss() {
        if(dismissing){
            return;
        }
        dismissing = true;

        for(HintHighlightTarget target : targets) {
            if(target!=null){
                target.removeOnAttachStateChangeListener(attachStateChangeListener);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                parent.removeView(HintHighlighter.this);
                for(HintHighlightTarget target : targets) {
                    target.cleanup();
                }
                dismissing = false;
            }
        },500);

        HintHighlighter.this.animate()
                .alpha(0.0f)
                .setDuration(400);
    }

    OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            for(HintHighlightTarget target : targets){
                if(target.wasTouched(event)){
                    return false;
                }
            }
            return true;
        }
    };

    HintHighlightTarget.Listener targetListener = new HintHighlightTarget.Listener() {
        @Override
        public void onLayout(HintHighlightTarget target) {
            invalidate();
        }
    };

    OnAttachStateChangeListener attachStateChangeListener = new OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {

        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if(!dismissing) {
                dismiss();
            }
        }
    };

}
