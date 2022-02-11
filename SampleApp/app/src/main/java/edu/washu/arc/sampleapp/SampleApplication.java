package edu.washu.arc.sampleapp;

import android.app.Application;

import edu.wustl.arc.core.Config;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestVariant;

public class SampleApplication extends Application {

    public void onCreate() {
        super.onCreate();

        ArcAssessmentsConfig.setupConfig();
        edu.wustl.arc.core.Application.initialize(this, () -> {
            Study.getInstance().registerStateMachine(SampleAppStateMachine.class);
        });
    }

    public static class ArcAssessmentsConfig {
        public static void setupConfig() {
            Config.CHOOSE_LOCALE = true;
            Config.CHECK_CONTACT_INFO = true;
            Config.CHECK_SESSION_INFO = true;
            Config.CHECK_PROGRESS_INFO = true;
            Config.ENABLE_VIGNETTES = true;
            Config.IS_REMOTE = true;
            Config.ENABLE_SIGNATURES = true;
            Config.USE_HELP_SCREEN = false;
            Config.TEST_VARIANT_GRID = TestVariant.Grid.V2;
            Config.LOGIN_USE_AUTH_DETAILS = true;
        }
    }
}
