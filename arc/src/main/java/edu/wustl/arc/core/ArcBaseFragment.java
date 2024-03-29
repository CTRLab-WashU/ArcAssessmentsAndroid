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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.ViewUtil;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import edu.wustl.arc.misc.TransitionSet;

public class ArcBaseFragment extends Fragment {

    TransitionSet transitions = new TransitionSet();
    String tag = getClass().getSimpleName();

    boolean backAllowed = false;
    boolean backInStudy = false;

    // methods related to enabling back press from a base fragment ---------------------------------

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setPadding(0, ViewUtil.getStatusBarHeight(),0,ViewUtil.getNavBarHeight());
    }
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ArcAssessmentActivity) {
            getMainActivity().enableBackPress(backAllowed, backInStudy);
        } else {
            requireActivity().getOnBackPressedDispatcher().addCallback(
                    new OnBackPressedCallback(!backAllowed) {
                @Override
                public void handleOnBackPressed() {
                    if(backAllowed){
                        if(Study.isValid() && backInStudy){
                            Study.openPreviousFragment();
                        } else {
                            NavigationManager.getInstance().popBackStack();
                        }
                    }
                }
            });
        }
    }

    public void allowBackPress(boolean inStudy){
        backAllowed = true;
        backInStudy = inStudy;
    }

    public boolean isBackAllowed(){
        return backAllowed;
    }

    // convenience getters -------------------------------------------------------------------------

    public String getSimpleTag(){
        return tag;
    }

    public ArcAssessmentActivity getMainActivity(){
        return (ArcAssessmentActivity)getActivity();
    }

    // convenience methods for manipulating the keyboard -------------------------------------------

    public void hideKeyboard() {
        if (getActivity() == null) {
            return;
        }
        Activity activity = getActivity();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(View view){
        if (getActivity() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    // part of the magic sewage system -------------------------------------------------------------

    public Object onDataCollection(){
        return null;
    }

    // methods relating to transitions -------------------------------------------------------------

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, final int nextAnim) {
        if(nextAnim==0){
            return null;
        }

        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(nextAnim==transitions.enter){
                    onEnterTransitionStart(false);
                } else if(nextAnim==transitions.exit){
                    onExitTransitionStart(false);
                } else if(nextAnim==transitions.popEnter){
                    onEnterTransitionStart(true);
                } else if(nextAnim==transitions.popExit){
                    onExitTransitionStart(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(nextAnim==transitions.enter){
                    onEnterTransitionEnd(false);
                } else if(nextAnim==transitions.exit){
                    onExitTransitionEnd(false);
                } else if(nextAnim==transitions.popEnter){
                    onEnterTransitionEnd(true);
                } else if(nextAnim==transitions.popExit){
                    onExitTransitionEnd(true);
                }
            }
        });

        return anim;
    }


    protected void onEnterTransitionStart(boolean popped) {
        Log.v(tag,"onEnterTransitionStart");
    }

    protected void onEnterTransitionEnd(boolean popped) {
        Log.v(tag,"onEnterTransitionEnd");
    }

    protected void onExitTransitionStart(boolean popped) {
        Log.v(tag,"onExitTransitionStart");
    }

    protected void onExitTransitionEnd(boolean popped) {
        Log.v(tag,"onExitTransitionEnd");
    }

    public TransitionSet getTransitionSet() {
        return transitions;
    }

    public void setTransitionSet(TransitionSet transitions) {
        if(transitions!=null){
            this.transitions = transitions;
        }
    }
}
