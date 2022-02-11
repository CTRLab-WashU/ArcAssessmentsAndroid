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
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import edu.wustl.arc.hints.Hints;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestCycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import edu.wustl.arc.font.FontFactory;
import edu.wustl.arc.font.Fonts;

import edu.wustl.arc.assessments.R;

@SuppressLint("ValidFragment")
public class SplashScreen extends BaseFragment {

    boolean paused = false;
    boolean ready = false;
    boolean skipSegment = false;
    protected boolean visible = true;

    public SplashScreen() {
    }

    public SplashScreen(boolean visible) {
        this.visible = visible;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(viewLayout(), container, false);
        if(!visible) {
            view.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    public @LayoutRes int viewLayout() {
        return R.layout.core_fragment_splash;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        initializeApp();
    }

    private void initializeApp() {
        Context context = getContext();
        getMainActivity().setupKeyboardWatcher();

        Application.getInstance().updateLocale(getContext());

        if(FontFactory.getInstance()==null) {
            FontFactory.initialize(context);
        }

        if(!Fonts.areLoaded()){
            Fonts.load();
            FontFactory.getInstance().setDefaultFont(Fonts.roboto);
            FontFactory.getInstance().setDefaultBoldFont(Fonts.robotoBold);
        }

        Hints.load();

        // We need to check to see if we're currently in the middle of a test session.
        // If we are, and if the state machine has valid fragments, we should let it continue
        // displaying those.
        // Otherwise, just run the Study instance, and let it figure out where it needs to be.

        if(Study.getParticipant().isCurrentlyInTestSession()
                && Study.getParticipant().checkForTestAbandonment() == false
                && Study.getStateMachine().hasValidFragments()
        ) {
            skipSegment = true;
        } else {
            skipSegment = false;
            Study.getInstance().run();
        }

        ready = true;

        getMainActivity().getWindow().setBackgroundDrawableResource(R.drawable.core_background);

        if(!paused){
            exit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(paused && ready) {
            if(Study.isValid()){
                exit();
            }
        }
        paused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    public void exit(){
        if(getFragmentManager() != null) {
            getFragmentManager().popBackStack();
            if(skipSegment) {
                Study.getInstance().skipToNextSegment();
            } else {
                Study.getInstance().openNextFragment();
            }
        }
    }
}
