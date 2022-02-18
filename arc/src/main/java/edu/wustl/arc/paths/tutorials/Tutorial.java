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
package edu.wustl.arc.paths.tutorials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.ui.Button;
import edu.wustl.arc.ui.TutorialProgressView;
import edu.wustl.arc.hints.HintHighlighter;
import edu.wustl.arc.hints.HintPointer;
import edu.wustl.arc.hints.Hints;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.utilities.ViewUtil;

public class Tutorial extends ArcBaseFragment {

    protected int shortAnimationDuration;

    protected HintHighlighter welcomeHighlight;
    protected HintPointer welcomeHint;
    protected HintHighlighter quitHighlight;
    protected HintPointer quitHint;

    protected TutorialProgressView progressView;
    protected ImageView closeButton;
    protected View loadingView;

    protected LinearLayout progressBar;
    protected int progressIncrement;
    protected int progress = 0;

    protected ImageView checkmark;
    protected TextView textViewComplete;
    protected Button endButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);

        loadingView.animate()
                .setStartDelay(400)
                .setDuration(400)
                .translationYBy(-loadingView.getHeight());
    }

    public Tutorial() {
    }

    // Display the hints for the progress bar and quit button
    protected void showProgressTutorial(final String tag, final Runnable nextSection) {
        welcomeHighlight.addTarget(progressView, 10, 2);
        welcomeHint.setText(ViewUtil.getString(R.string.popup_tutorial_welcome));

        View.OnClickListener welcomeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcomeHint.dismiss();
                welcomeHighlight.dismiss();
                Hints.markShown(tag);

                Handler handler = new Handler();
                handler.postDelayed(nextSection,600);
            }
        };

        welcomeHint.addButton(ViewUtil.getString(R.string.popup_gotit), welcomeListener);
        welcomeHighlight.show();
        welcomeHint.show();
    }

    protected void showCloseTutorial(final String tag, final Runnable nextSection) {
        quitHighlight.addTarget(closeButton, 50, 10);
        quitHint.setText(ViewUtil.getString(R.string.popup_tutorial_quit));

        View.OnClickListener quitListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitHint.dismiss();
                quitHighlight.dismiss();
                Hints.markShown(tag);

                Handler handler = new Handler();
                handler.postDelayed(nextSection,600);
            }
        };

        quitHint.addButton(ViewUtil.getString(R.string.popup_gotit), quitListener);
        quitHighlight.show();
        quitHint.show();
    }


    protected void fadeInView(View view, Float opacity) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(opacity)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }

    protected void fadeOutView(final View view) {
        view.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    // Displays the tutorial complete screen
    protected void showComplete() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeInView(checkmark, 1f);
                fadeInView(textViewComplete, 1f);
                fadeInView(endButton, 1f);

                endButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        endButton.setEnabled(false);
                        exit();
                    }
                });
            }
        }, 1000);
    }

    protected void exit(){
        loadingView.animate()
                .setDuration(400)
                .translationY(0);
        progressBar.animate()
                .setDuration(400)
                .alpha(0.0f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NavigationManager.getInstance().popBackStack();
            }
        },1200);
    }

    protected void incrementProgress(){
        progress += progressIncrement;
        progressView.setProgress(progress,true);
    }
}
