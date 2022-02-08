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
package edu.wustl.arc.paths.templates;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.util.Log;
import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.paths.informative.ContactScreen;
import edu.wustl.arc.ui.Button;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.paths.informative.HelpScreen;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.utilities.ViewUtil;

import static edu.wustl.arc.core.Config.USE_HELP_SCREEN;

@SuppressLint("ValidFragment")
public class AltStandardTemplate extends BaseFragment {

    TranslateAnimation showAnimation;
    TranslateAnimation hideAnimation;
    boolean buttonShowing;
    boolean autoscroll = false;

    String stringButton;
    String stringHeader;
    String stringSubHeader;

    protected TextView textViewHeader;
    TextView textViewSubheader;

    protected LinearLayout content;

    protected TextView textViewBack;

    protected TextView textViewHelp;

    ScrollView scrollView;

    protected Button buttonNext;
    Button textViewScroll;
    Button textViewScrollTop;

    boolean allowBack;
    boolean disableScrollBehavior;
    boolean showNextButton = true;

    public AltStandardTemplate(boolean allowBack, String header, String subheader) {
        this.allowBack = allowBack;
        stringButton = ViewUtil.getString(R.string.button_next);
        stringHeader = header;
        stringSubHeader = subheader;

        if(allowBack){
            allowBackPress(true);
        }
    }

    public AltStandardTemplate(boolean allowBack, String header, String subheader, Boolean showButton) {
        this.allowBack = allowBack;
        stringHeader = header;
        stringSubHeader = subheader;
        showNextButton = showButton;

        if(allowBack){
            allowBackPress(true);
        }
    }

    public AltStandardTemplate(boolean allowBack, String header, String subheader, String button) {
        this.allowBack = allowBack;
        stringButton = button;
        stringHeader = header;
        stringSubHeader = subheader;

        if(allowBack){
            allowBackPress(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_standard_alt, container, false);
        content = view.findViewById(R.id.linearLayoutContent);
        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));
        textViewHeader.setTypeface(Fonts.robotoMedium);

        if(stringSubHeader!=null){
            textViewSubheader = view.findViewById(R.id.textViewSubHeader);
            textViewSubheader.setText(Html.fromHtml(stringSubHeader));
            textViewSubheader.setVisibility(View.VISIBLE);
        }

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackRequested();
            }
        });

        textViewHelp = view.findViewById(R.id.textViewHelp);
        textViewHelp.setTypeface(Fonts.robotoMedium);
        textViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment helpScreen;
                if (USE_HELP_SCREEN) {
                    helpScreen = new HelpScreen();
                } else {
                    helpScreen = new ContactScreen();
                }
                NavigationManager.getInstance().open(helpScreen);
            }
        });

        buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextRequested();
            }
        });
        if(stringButton!=null){
            buttonNext.setText(stringButton);
        }

        textViewScroll = view.findViewById(R.id.textViewScroll);
        textViewScrollTop = view.findViewById(R.id.textViewScrollTop);

        scrollView = view.findViewById(R.id.scrollView);

        textViewScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.smoothScrollTo(0, scrollView.getHeight());
                autoscroll = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoscroll = false;
                    }
                }, 300);
            }
        });

        textViewScrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.smoothScrollTo(0, 0);
                autoscroll = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoscroll = false;
                    }
                }, 300);
            }
        });

        showAnimation = new TranslateAnimation(0,0,ViewUtil.dpToPx(100),0);
        showAnimation.setDuration(250);
        showAnimation.setAnimationListener(showAnimationListener);

        hideAnimation = new TranslateAnimation(0,0,0,ViewUtil.dpToPx(100));
        hideAnimation.setDuration(500);
        hideAnimation.setAnimationListener(hideAnimationListener);

        if(allowBack){
            textViewBack.setVisibility(View.VISIBLE);
        }

        setupDebug(view,R.id.textViewHeader);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(disableScrollBehavior) {
            if (!showNextButton) {
                hideNextButton();
                buttonShowing = false;
            }  else {
                buttonNext.setVisibility(View.VISIBLE);
                textViewScrollTop.setAlpha(0);
                textViewScrollTop.setVisibility(View.VISIBLE);
                buttonShowing = true;
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (scrollViewIsAtBottom()) {
                        if (!showNextButton) {
                            hideNextButton();
                            buttonShowing = false;
                        } else {
                            buttonNext.setVisibility(View.VISIBLE);
                            buttonShowing = true;
                        }
                    } else {
                        textViewScroll.setVisibility(View.VISIBLE);
                        buttonNext.startAnimation(hideAnimation);
                    }
                }
            }, 50);
        }
    }

    public void setSubHeaderTextSize(float size) {
        textViewSubheader.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setSubHeaderLineSpacing(float add, float multi) {
        textViewSubheader.setLineSpacing(add, multi);
    }

    protected void setHelpVisible(boolean visible){
        textViewHelp.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    boolean scrollViewIsAtBottom(){
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        Log.d(getSimpleTag(),"scroll - view.getBottom()="+view.getBottom()+" scrollView.getHeight()="+scrollView.getHeight()+" scrollView.getScrollY()=" + scrollView.getScrollY());
        return (diff <= 0);
    }

    protected void disableScrollBehavior(){
        disableScrollBehavior = true;
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    protected void onEnterTransitionEnd(boolean popped) {
        super.onEnterTransitionEnd(popped);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if(disableScrollBehavior){
                    return;
                }
                boolean needsToShow = scrollViewIsAtBottom();
                if(needsToShow==buttonShowing){
                    return;
                } else if(needsToShow){
                    buttonShowing = true;
                    buttonNext.startAnimation(showAnimation);
                } else {
                    buttonShowing = false;
                    buttonNext.startAnimation(hideAnimation);
                }
                if (!autoscroll) {
                    textViewScroll.animate().alpha(0.0f).setDuration(300);
                    textViewScrollTop.animate().alpha(0.0f).setDuration(300);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textViewScroll.setVisibility(View.GONE);
                            textViewScrollTop.setVisibility(View.GONE);
                        }
                    }, 300);
                }
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return disableScrollBehavior;
            }
        });
    }

    protected void onNextButtonEnabled(boolean enabled){

    }

    protected void onNextRequested() {
        Study.getInstance().openNextFragment();
    }

    protected void onBackRequested() {
        Log.i("StandardTemplate","onBackRequested");
        Study.openPreviousFragment();
    }


    private Animation.AnimationListener showAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            buttonNext.setVisibility(View.VISIBLE);
            textViewScroll.animate().alpha(0.0f).setDuration(300);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textViewScrollTop.animate().alpha(1.0f).setDuration(300);
                }
            }, 300);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //textViewScroll.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private Animation.AnimationListener hideAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            textViewScrollTop.animate().alpha(0.0f).setDuration(300);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textViewScroll.animate().alpha(1.0f).setDuration(300);
                }
            }, 300);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            buttonNext.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    protected void hideNextButton() {
        buttonNext.setVisibility(View.GONE);
    }
}
