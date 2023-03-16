package edu.wustl.arc.sageassessments

import android.app.Application
import edu.wustl.arc.core.ArcApplication
import edu.wustl.arc.core.Config
import edu.wustl.arc.study.Study
import edu.wustl.arc.study.TestVariant
import edu.wustl.arc.ui.BottomNavigationView
import java.util.*

class SageArcApplication {
    companion object {
        fun setupDefaultConfig(app: Application) {
            BottomNavigationView.shouldShowEarnings = false
            Config.CHOOSE_LOCALE = false
            Config.CHECK_CONTACT_INFO = true
            Config.CHECK_SESSION_INFO = true
            Config.CHECK_PROGRESS_INFO = true
            Config.ENABLE_VIGNETTES = true
            Config.IS_REMOTE = true
            Config.ENABLE_SIGNATURES = true
            Config.USE_HELP_SCREEN = false
            Config.TEST_VARIANT_GRID = TestVariant.Grid.V2
            Config.TEST_VARIANT_PRICE = TestVariant.Price.Original

            // Initialize library
            ArcApplication.initialize(app) {
                Study.getInstance().registerStateMachine(
                    ArcStateMachine::class.java
                )
            }
            Study.getStateMachine().initialize()
            ArcApplication.getInstance().localeOptions = ArrayList()
        }
    }
}