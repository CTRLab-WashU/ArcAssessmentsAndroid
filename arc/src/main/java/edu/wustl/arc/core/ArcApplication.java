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

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.annotation.Nullable;
import edu.wustl.arc.font.FontFactory;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.hints.Hints;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.CacheManager;
import edu.wustl.arc.utilities.PreferencesManager;
import edu.wustl.arc.utilities.VersionUtil;

import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;
import java.util.ArrayList;
import java.util.List;

/**
 * This class was originally designed to be a sub-class of Android Application class
 * However, due to the global nature of it's use through the library,
 * It is better to push it to it's own class that is owned by the actual Android Application class
 */
public class ArcApplication implements LifecycleObserver {

    public interface StudyComponentProvider {
        public void registerStudyComponents();
    }

    private static final String tag = "Application";
    public static final String TAG_RESTART = "TAG_APPLICATION_RESTARTING";

    public static ArcApplication getInstance() {
        return instance;
    }

    public Context getAppContext() {
        return context;
    }

    public Resources getResources() {
        return context.getResources();
    }

    public ContentResolver getContentResolver() {
        return context.getContentResolver();
    }

    static ArcApplication instance;

    private Context context;  // is the app context\

    private List<Locale> localeOptions;

    private boolean checkContext() {
        if (context == null) {
            Log.e(tag, "Attempting to access context before it has been set");
            return false;
        }
        return true;
    }

    java.util.Locale locale;
    boolean visible = false;

    /**
     * @param appContext MUST BE APPLICATION CONTEXT
     * @param provider provider of study components
     */
    public static void initialize(Context appContext, StudyComponentProvider provider) {
        if (instance != null) {
            Log.e(tag, "Cannot initialize app twice");
            return;
        }
        instance = new ArcApplication(appContext, provider);

        instance.setLocaleOptions(createDefaultLocaleOptions());

        instance.initializeStudy(appContext, provider);
        Study.getInstance().load();

        ArcApplication.getInstance().updateLocale(appContext);

        if(FontFactory.getInstance()==null) {
            FontFactory.initialize(appContext);
        }

        if(!Fonts.areLoaded()) {
            Fonts.load();
            FontFactory.getInstance().setDefaultFont(Fonts.roboto);
            FontFactory.getInstance().setDefaultBoldFont(Fonts.robotoBold);
        }

        Hints.load();
    }

    /**
     * Only to be used with the validation app, always overwrites the study
     * @param appContext MUST BE APPLICATION CONTEXT
     */
    @VisibleForTesting
    public static synchronized void initializeValidationAppOnly(
            Context appContext, StudyComponentProvider provider) {

        instance = new ArcApplication(appContext, provider);

        instance.setLocaleOptions(createDefaultLocaleOptions());

        instance.initializeStudyValidationAppOnly(appContext, provider);

        Study.getInstance().load();
    }

    /**
     * Only to be used with the validation app, always overwrites the study
     * @param appContext MUST BE APPLICATION CONTEXT
     */
    @VisibleForTesting
    private void initializeStudyValidationAppOnly(
            @NonNull Context appContext, StudyComponentProvider provider) {

        // Initialize study and assign study-specific components
        Study.initializeValidationAppOnly(context);
        provider.registerStudyComponents();
    }

    protected ArcApplication(Context appContext, StudyComponentProvider provider) {
        context = appContext;
        if (!checkContext()) {
            return;
        }

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        JodaTimeAndroid.init(appContext);
        VersionUtil.initialize(appContext);
        PreferencesManager.initialize(appContext);
        CacheManager.initialize(appContext);
        Device.initialize(appContext);
        updateLocale(appContext);
    }

    public void initializeStudy(@NonNull Context appContext, StudyComponentProvider provider) {
        // Initialize study and assign study-specific components
        Study.initialize(context);
        provider.registerStudyComponents();
    }

    public void onConfigurationChanged(Configuration config) {
        Log.i("Application","onConfigurationChanged");
        if (!checkContext()) {
            return;
        }
        updateLocale(context);
    }

    public void attachBaseContext(Context context) {
        if (!checkContext()) {
            return;
        }
        updateLocale(context);
    }

    public void updateLocale(@Nullable Context context) {
        PreferencesManager preferences = PreferencesManager.getInstance();
        if (preferences == null) {
            return;
        }
        if (preferences.contains(Locale.TAG_LANGUAGE)) {
            String language = preferences.getString(Locale.TAG_LANGUAGE, Locale.LANGUAGE_ENGLISH);
            String country = preferences.getString(Locale.TAG_COUNTRY, Locale.COUNTRY_UNITED_STATES);
            locale = new java.util.Locale(language, country);

            if (!checkContext()) {
                return;
            }

            // update application
            Resources appResources = context.getResources();
            Configuration config = appResources.getConfiguration();
            config.setLocale(locale);
            appResources.updateConfiguration(config, appResources.getDisplayMetrics());

            if (context != null) {
                Resources activityResources = context.getResources();
                activityResources.updateConfiguration(config, activityResources.getDisplayMetrics());
            }
        }
    }

    // list all locale options offered by the app
    public List<Locale> getLocaleOptions() {
        return localeOptions;
    }

    public void setLocaleOptions(List<Locale> locales) {
        localeOptions = locales;
    }

    public static List<Locale> createDefaultLocaleOptions() {
        List<Locale> locales = new ArrayList<>();
        locales.add(new Locale(true, Locale.LANGUAGE_ENGLISH,Locale.COUNTRY_UNITED_STATES));
        return locales;
    }

    public java.util.Locale getLocale() {
        if(locale==null){
            PreferencesManager preferences = PreferencesManager.getInstance();
            String language = preferences.getString(Locale.TAG_LANGUAGE,Locale.LANGUAGE_ENGLISH);
            String country = preferences.getString(Locale.TAG_COUNTRY,Locale.COUNTRY_UNITED_STATES);
            locale = new java.util.Locale(language,country);
        }
        return locale;
    }

    public void restart() {
        if (!checkContext()) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);

        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TAG_RESTART, true);
        context.startActivity(intent);
        Runtime.getRuntime().exit(0);
    }

    public boolean isVisible(){
        return visible;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStartForeground() {
        visible = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStopForeground() {
        visible = false;
    }
}
