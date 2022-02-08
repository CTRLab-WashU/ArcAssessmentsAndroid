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
package edu.wustl.arc.paths.battery_optimization;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.Nullable;
import edu.wustl.arc.paths.templates.StateInfoTemplate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.wustl.arc.core.Application;
import edu.wustl.arc.font.FontFactory;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.notifications.ProctorDeviation;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.Phrase;
import edu.wustl.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class BatteryOptimizationReminder extends StateInfoTemplate {

    public static boolean DEBUG_SHOW_REQUEST = false;
    boolean requested = false;

    public BatteryOptimizationReminder() {
        super(false,
                ViewUtil.getString(R.string.battery_optimization_header),
                null,
                "",
                ViewUtil.getString(R.string.button_next)
        );

        if(FontFactory.getInstance()==null) {
            FontFactory.initialize(Application.getInstance().getAppContext());
        }

        if(!Fonts.areLoaded()){
            Fonts.load();
            FontFactory.getInstance().setDefaultFont(Fonts.roboto);
            FontFactory.getInstance().setDefaultBoldFont(Fonts.robotoBold);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);

        if(!DEBUG_SHOW_REQUEST) {
            // in case someone experiences a deviation and turns off optimizations before opening the app
            // however unlikely that is
            PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            if (powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName())) {

                ProctorDeviation deviation = ProctorDeviation.load();
                deviation.markRequest();
                deviation.save();

                NavigationManager.getInstance().popBackStack();
                Study.getStateMachine().showSplashScreen();
            }
        }

        TextView textViewBody = view.findViewById(R.id.textViewBody);

        Phrase phrase = new Phrase(R.string.battery_optimization_reminder);
        phrase.replace(R.string.token_app_name,R.string.app_name);
        textViewBody.setText(phrase.toHtmlString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                requested = true;

                ProctorDeviation deviation = ProctorDeviation.load();
                deviation.markRequest();
                deviation.save();

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(requested) {
            NavigationManager.getInstance().popBackStack();
            Study.getStateMachine().showSplashScreen();
        }
    }

}
