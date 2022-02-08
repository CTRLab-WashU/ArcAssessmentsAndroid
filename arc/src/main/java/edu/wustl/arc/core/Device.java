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
import android.content.res.Resources;
import android.os.Build;
import androidx.annotation.Dimension;
import edu.wustl.arc.utilities.PreferencesManager;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.UUID;

public class Device {

    public static final String TAG_DEVICE_ID = "deviceId";

    private static String id;
    private static String info;
    private static String name;
    private static boolean initialized = false;

    private Device(){

    }

    public static void initialize(Context context) {

        if(PreferencesManager.getInstance().contains(TAG_DEVICE_ID)){
            id = PreferencesManager.getInstance().getString(TAG_DEVICE_ID,"");
        } else {
            id = UUID.randomUUID().toString();
            PreferencesManager.getInstance().putString(TAG_DEVICE_ID,id);
        }

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            name = capitalize(model);
        } else {
            name = capitalize(manufacturer) + " " + model;
        }
        info = "Android|"+name+"|"+ Build.VERSION.RELEASE+"|"+Build.VERSION.SDK_INT+"|"+Build.FINGERPRINT;
        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }


    public static String getId(){
        return id;
    }

    public static String getInfo(){
        return info;
    }

    public static String getName(){
        return name;
    }

    public static boolean screenIsBiggerThan(@Dimension int inchesX, @Dimension double inchesY){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float inY = metrics.heightPixels/metrics.ydpi;
        float inX = metrics.widthPixels/metrics.xdpi;
        Log.i("Device","width = "+inX+" inches, height = "+inY+" inches");
        return (inchesX>inX && inchesY>inY);

    }

}
