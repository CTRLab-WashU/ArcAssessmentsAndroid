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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.paths.questions.QuestionLanguagePreference;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.KeyboardWatcher;
import edu.wustl.arc.utilities.Phrase;
import edu.wustl.arc.utilities.PreferencesManager;

import android.util.Log;

import android.view.View;
import android.widget.FrameLayout;

import edu.wustl.arc.assessments.R;

import java.util.ArrayList;
import java.util.List;

public class ArcAssessmentActivity extends AppCompatActivity {

    boolean paused = false;
    boolean backAllowed = false;
    boolean backInStudy = false;
    boolean hasNewIntent = false;
    int backInStudySkips = 0;

    FrameLayout contentView;
    KeyboardWatcher keyboardWatcher;


    @Override
    protected void onStart() {
        super.onStart();
        if(hasNewIntent){
            hasNewIntent = false;
            if(!Study.getStateMachine().isCurrentlyInTestPath()){
                Study.openNextFragment();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(new Bundle());
        Log.i("MainActivity","onCreate");

        Intent intent = getIntent();
        parseIntent(intent);

        setContentView(getLayoutId());
        contentView = findViewById(R.id.content_frame);

        setup();
    }

    @LayoutRes
    public int getLayoutId() {
        return R.layout.core_activity_main;
    }

    public boolean showLocalQuestion() {
        return false;
    }

    public boolean showSplashScreen() {
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("MainActivity", "onNewIntent");
        parseIntent(intent);
        hasNewIntent = true;
    }

    private void parseIntent(Intent intent){
        Log.i("MainActivity","parseIntent");
        if(intent!=null) {
            Config.OPENED_FROM_NOTIFICATION = intent.getBooleanExtra(Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION,false);
            Config.OPENED_FROM_VISIT_NOTIFICATION = intent.getBooleanExtra(Config.INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION,false);
            boolean restart = intent.getBooleanExtra(ArcApplication.TAG_RESTART,false);
            if(restart){
                PreferencesManager.getInstance().putBoolean(ArcApplication.TAG_RESTART,true);
                Log.i("MainActivity","APPLICATION_RESTART = "+restart);
            }
        }
        Log.i("MainActivity","OPENED_FROM_NOTIFICATION = "+Config.OPENED_FROM_NOTIFICATION);
        Log.i("MainActivity","OPENED_FROM_VISIT_NOTIFICATION = "+Config.OPENED_FROM_VISIT_NOTIFICATION);
    }

    public void setup(){
        NavigationManager.initialize(getSupportFragmentManager());
        setupKeyboardWatcher();

        if(PreferencesManager.getInstance().getBoolean(ArcApplication.TAG_RESTART,false)){
            PreferencesManager.getInstance().putBoolean(ArcApplication.TAG_RESTART,false);

            Phrase phrase = new Phrase(R.string.low_memory_restart_dialogue);
            phrase.replace(R.string.token_app_name, R.string.app_name);

            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(getString(R.string.low_memory_restart_dialogue_header))
                    .setMessage(phrase.toString())
                    .setPositiveButton(getString(R.string.button_okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        if (!showSplashScreen()) {
            return;
        }

        if(PreferencesManager.getInstance().contains(Locale.TAG_LANGUAGE) || !Config.CHOOSE_LOCALE) {
            Study.getStateMachine().showSplashScreen();
            return;
        }

        if (!showLocalQuestion()) {
            return;
        }

        List<Locale> locales = ArcApplication.getInstance().getLocaleOptions();
        List<String> options = new ArrayList<>();
        for(Locale locale : locales) {
            options.add(locale.getLabel());
        }

        QuestionLanguagePreference fragment = new QuestionLanguagePreference();
        NavigationManager.getInstance().open(fragment);
    }

    public void setupKeyboardWatcher(){
        keyboardWatcher = new KeyboardWatcher(this);
        keyboardWatcher.startWatch();
    }

    public void setKeyboardListener(KeyboardWatcher.OnKeyboardToggleListener listener){
        keyboardWatcher.setListener(listener);
    }

    public void removeKeyboardListener(){
        keyboardWatcher.setListener(null);
    }


    public void enableBackPress(boolean enable, boolean inStudy, int skips){
        Log.i("MainActivity","enableBackPress(enable="+enable+", inStudy="+inStudy+")");
        backInStudySkips = skips;
        backAllowed = enable;
        backInStudy = inStudy;
    }

    public void enableBackPress(boolean enable, boolean inStudy) {
        Log.i("MainActivity","enableBackPress(enable="+enable+", inStudy="+inStudy+")");
        backInStudySkips = 0;
        backAllowed = enable;
        backInStudy = inStudy;
    }

    @Override
    public void onBackPressed() {
        if(backAllowed){
            if(Study.isValid() && backInStudy){
                Study.openPreviousFragment(backInStudySkips);
            } else {
                NavigationManager.getInstance().popBackStack();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("MainActivity","onDestroy");
        if(keyboardWatcher != null){
            keyboardWatcher.stopWatch();
            keyboardWatcher = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity","onResume");
        paused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity","onPause");
        paused = true;
        if(Study.isValid()){
            Study.getParticipant().markPaused();
            Study.getStateMachine().save(true);
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        Log.i("MainActivity","attachBaseContext");
        super.attachBaseContext(context);
    }

    public boolean isVisible() {
        return !paused;
    }

    public View getContentView(){
        return contentView;
    }
}
