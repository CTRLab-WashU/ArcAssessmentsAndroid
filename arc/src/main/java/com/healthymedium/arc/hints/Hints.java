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
package com.healthymedium.arc.hints;

import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.VersionUtil;

import org.joda.time.DateTime;

import java.util.HashMap;

public class Hints {

    private static final String tag = "Hints";
    static HashMap<String, HintDetails> map;

    private Hints(){

    }

    public static void save(){
        PreferencesManager.getInstance().putObject(tag, map.values().toArray());
    }

    public static void load(){
        map = new HashMap<>();

        if(!PreferencesManager.getInstance().contains(tag)){
            return;
        }

        HintDetails[] details = PreferencesManager.getInstance().getObject(tag, HintDetails[].class);
        map = new HashMap<>();
        for (HintDetails detail : details) {
            map.put(detail.name, detail);
        }
    }

    public static boolean hasBeenShown(String key){
        return map.containsKey(key);
    }

    public static boolean hasBeenShownSinceVersion(String key, long versionCode){
        if(!map.containsKey(key)){
            return false;
        }
        HintDetails details = map.get(key);
        return details.versionCode > versionCode;
    }

    public static boolean hasBeenShownSinceDate(String key, DateTime dateTime){
        if(!map.containsKey(key)){
            return false;
        }
        HintDetails details = map.get(key);
        return details.timestamp > (dateTime.getMillis()/1000);
    }

    public static void markShown(String key){
        HintDetails details = new HintDetails();
        details.name = key;
        details.timestamp = DateTime.now().getMillis()/1000;
        details.versionCode = VersionUtil.getAppVersionCode();
        map.put(key,details);
        save();
    }


    public static class HintDetails {
        long versionCode;
        long timestamp;
        String name;
    }

}
