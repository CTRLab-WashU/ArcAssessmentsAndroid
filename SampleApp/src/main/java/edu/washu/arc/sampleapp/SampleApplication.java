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
package edu.washu.arc.sampleapp;

import android.app.Application;

import edu.wustl.arc.core.ArcApplication;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestVariant;

public class SampleApplication extends Application {

    public void onCreate() {
        super.onCreate();

        ArcAssessmentsConfig.setupConfig();
        ArcApplication.initialize(this, () -> {
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
