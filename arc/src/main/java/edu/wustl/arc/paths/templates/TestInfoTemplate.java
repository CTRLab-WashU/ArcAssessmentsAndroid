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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import edu.wustl.arc.hints.HintHighlighter;
import edu.wustl.arc.hints.HintPointer;
import edu.wustl.arc.hints.Hints;

import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.wustl.arc.core.Application;
import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.paths.tutorials.Grid2Tutorial;
import edu.wustl.arc.study.TestVariant;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.paths.tutorials.PricesTutorialRevised;
import edu.wustl.arc.ui.Button;

import edu.wustl.arc.assessments.R;
import edu.wustl.arc.paths.tutorials.GridTutorial;
import edu.wustl.arc.paths.tutorials.PricesTutorial;
import edu.wustl.arc.paths.tutorials.SymbolTutorial;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class TestInfoTemplate extends ArcBaseFragment {

    public static final String HINT_GRID_TUTORIAL = "HINT_GRID_TUTORIAL";
    public static final String HINT_PRICES_TUTORIAL = "HINT_PRICES_TUTORIAL";
    public static final String HINT_SYMBOL_TUTORIAL = "HINT_SYMBOL_TUTORIAL";
    public static final String HINT_REPEAT_TUTORIAL = "HINT_REPEAT_TUTORIAL";

    ImageView backgroundImageView;

    String stringTestNumber;
    String stringHeader;
    String stringBody;
    String stringButton;
    String stringType;

    Drawable buttonImage;

    TextView textViewTestNumber;
    TextView textViewHeader;
    TextView textViewBody;
    TextView textViewTutorial;

    LinearLayout content;

    Button button;

    HintPointer tutorialHint;
    HintHighlighter tutorialHintHighlighter;

    public TestInfoTemplate(String testNumber, String header, String body, String type, @Nullable String buttonText) {
        stringTestNumber = testNumber;
        stringHeader = header;
        stringBody = body;
        stringButton = buttonText;
        stringType = type;
    }

    public TestInfoTemplate(String testNumber, String header, String body, String type, @Nullable Drawable buttonImage) {
        stringTestNumber = testNumber;
        stringHeader = header;
        stringBody = body;
        this.buttonImage = buttonImage;
        stringType = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_test_info, container, false);
        content = view.findViewById(R.id.linearLayoutContent);

        backgroundImageView = view.findViewById(R.id.backgroundImageView);

        if (stringType.equals("grids")) {
            backgroundImageView.setImageDrawable(ViewUtil.getDrawable(R.drawable.ic_grids_bg));
        }
        else if (stringType.equals("symbols")) {
            backgroundImageView.setImageDrawable(ViewUtil.getDrawable(R.drawable.ic_symbols_bg));
        }
        else if (stringType.equals("prices")) {
            backgroundImageView.setImageDrawable(ViewUtil.getDrawable(R.drawable.ic_prices_bg));
        }

        textViewTestNumber = view.findViewById(R.id.textViewTestNumber);
        textViewTestNumber.setText(stringTestNumber);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setTypeface(Fonts.robotoMedium);
        textViewHeader.setText(stringHeader);

        textViewBody = view.findViewById(R.id.textViewBody);
        textViewBody.setText(Html.fromHtml(stringBody));

        SpannableString styledViewTutorialString =
                new SpannableString(Html.fromHtml(Application.getInstance().getResources().getString(R.string.testing_tutorial_link)));
        styledViewTutorialString.setSpan(new UnderlineSpan(), 0, styledViewTutorialString.length(), 0);
        styledViewTutorialString.setSpan(new StyleSpan(Typeface.BOLD), 0, styledViewTutorialString.length(), 0);

        textViewTutorial = view.findViewById(R.id.textViewTutorial);
        textViewTutorial.setText(styledViewTutorialString);
        textViewTutorial.setVisibility(View.VISIBLE);

        button = view.findViewById(R.id.button);
        if(stringButton!=null){
            button.setText(stringButton);
        } else if (buttonImage!=null) {
            button.setIcon(buttonImage);
        }

        button.setEnabled(false);

        // Show a hint if this test type's tutorial has not yet been completed
        if ((stringType.equals("grids") && !Hints.hasBeenShown(HINT_GRID_TUTORIAL))
                || (stringType.equals("prices") && !Hints.hasBeenShown(HINT_PRICES_TUTORIAL))
                || (stringType.equals("symbols") && !Hints.hasBeenShown(HINT_SYMBOL_TUTORIAL))) {
            tutorialHint = new HintPointer(getActivity(), textViewTutorial, true, true);
            tutorialHint.setText(ViewUtil.getString(R.string.popup_tutorial_view));
            tutorialHint.show();
        }
        else if (!Hints.hasBeenShown(HINT_REPEAT_TUTORIAL)) {
            Hints.markShown(HINT_REPEAT_TUTORIAL);

            tutorialHintHighlighter = new HintHighlighter(getActivity());
            tutorialHintHighlighter.addTarget(textViewTutorial, 5, 0);
            tutorialHintHighlighter.show();

            tutorialHint = new HintPointer(getActivity(), textViewTutorial, true, true);
            tutorialHint.setText(ViewUtil.getString(R.string.popup_tutorial_complete));

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tutorialHint.dismiss();
                    tutorialHintHighlighter.dismiss();
                    enableButton();
                }
            };

            tutorialHint.addButton(ViewUtil.getString(R.string.popup_gotit), listener);
            tutorialHint.show();
        }
        // If the tutorial has been completed, enable the test button
        else {
            enableButton();
        }

        textViewTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMainActivity().getWindow().setBackgroundDrawableResource(R.color.secondary);
                if(tutorialHint!=null) {
                    tutorialHint.dismiss();
                }

                if (tutorialHintHighlighter!=null) {
                    tutorialHintHighlighter.dismiss();
                }

                if (stringType.equals("grids")) {
                    Hints.markShown(HINT_GRID_TUTORIAL);

                    ArcBaseFragment gridTutorial = null;
                    switch (Config.TEST_VARIANT_GRID) {
                        case V1:
                            gridTutorial = new GridTutorial();
                            break;
                        case V2:
                            gridTutorial = new Grid2Tutorial();
                            break;
                    }
                    NavigationManager.getInstance().open(gridTutorial);
                }
                else if (stringType.equals("symbols")) {
                    Hints.markShown(HINT_SYMBOL_TUTORIAL);
                    SymbolTutorial symbolTutorial = new SymbolTutorial();
                    NavigationManager.getInstance().open(symbolTutorial);
                }
                else if (stringType.equals("prices")) {
                    Hints.markShown(HINT_PRICES_TUTORIAL);
                    if (Config.TEST_VARIANT_PRICE == TestVariant.Price.Revised) {
                        PricesTutorialRevised pricesTutorialRevised = new PricesTutorialRevised();
                        NavigationManager.getInstance().open(pricesTutorialRevised);
                    } else {
                        PricesTutorial pricesTutorial = new PricesTutorial();
                        NavigationManager.getInstance().open(pricesTutorial);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getMainActivity()==null){
                    return;
                }
                getMainActivity().getWindow().setBackgroundDrawableResource(R.drawable.core_background);
            }
            }, 1000);
    }

    private void enableButton() {
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tutorialHint!=null) {
                    tutorialHint.dismiss();
                }

                if (tutorialHintHighlighter!=null) {
                    tutorialHintHighlighter.dismiss();
                }

                Study.getInstance().openNextFragment();
            }
        });
    }

}
