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
package edu.wustl.arc.paths.questions;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.wustl.arc.core.ArcApplication;
import edu.wustl.arc.core.Locale;
import edu.wustl.arc.font.FontFactory;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.utilities.PreferencesManager;
import edu.wustl.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class QuestionLanguagePreference extends QuestionRadioButtons {

    static List<Locale> locales;

    // Switch to false during QA to see all locales
    static Boolean hideUnsupportedLocales = true;

    public QuestionLanguagePreference() {
        super(false, true, "Language:", "", initOptions(), "CONFIRM");

        if(FontFactory.getInstance()==null) {
            FontFactory.initialize(ArcApplication.getInstance().getAppContext());
        }

        if(!Fonts.areLoaded()){
            Fonts.load();
            FontFactory.getInstance().setDefaultFont(Fonts.roboto);
            FontFactory.getInstance().setDefaultBoldFont(Fonts.robotoBold);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        // assumes same size and order between locales and options
        int size = buttons.size();
        for(int i=0;i<size;i++) {
            if(!locales.get(i).IsfullySupported()) {
                buttons.get(i).setAlpha(0.4f);
            }
        }

        return view;
    }

    @Override
    public Object onDataCollection(){

        Map<String, Object> response = new HashMap<>();

        Object value = onValueCollection();
        if(value!=null){
            response.put("value", value);
        }

        String language = Locale.LANGUAGE_ENGLISH;
        String country = Locale.COUNTRY_UNITED_STATES;

        selection = options.get((int)value);

        for(Locale locale : locales){
            String languageTitle = locale.getLabel().replace("\n", "");
            if(languageTitle.equals(selection)){
                language = locale.getLanguage();
                country = locale.getCountry();
                break;
            }
        }

        PreferencesManager.getInstance().putStringImmediately(Locale.TAG_LANGUAGE, language);
        PreferencesManager.getInstance().putStringImmediately(Locale.TAG_COUNTRY, country);

        return response;
    }

    @Override
    protected void onNextButtonEnabled(boolean enabled) {
        if (enabled) {
            buttonNext.setText(ViewUtil.getString(R.string.button_next));
        }
    }

    @Override
    protected void onNextRequested() {
        onDataCollection();
        triggerAppRestart();
    }

    /**
     * An app restart is necessary for forced localization to take effect.
     * See this blog post:
     * https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
     */
    public void triggerAppRestart() {
        Context context = getActivity().getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    static List<String> initOptions() {

        List<Locale> AllLocales = ArcApplication.getInstance().getLocaleOptions();
        List<String> options = new ArrayList<>();
        locales = new ArrayList<>();

        for(Locale locale : AllLocales) {
            if(hideUnsupportedLocales) {
                if(locale.IsfullySupported()){
                    options.add(locale.getLabel().replace("\n", ""));
                    locales.add(locale);
                }
            } else {
                options.add(locale.getLabel().replace("\n", ""));
                locales.add(locale);
            }
        }

        return options;
    }

}
