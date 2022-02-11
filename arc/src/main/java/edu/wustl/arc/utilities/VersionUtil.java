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
package edu.wustl.arc.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class VersionUtil {

    private static final String tag = "VersionUtil";

    private static long app_code;
    private static String app_name;

    private VersionUtil(){
        app_name = new String();
    }

    public static void initialize(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            app_name = packageInfo.versionName;
            app_code = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.i(tag,"app       | code="+app_code+", name="+app_name);
        Log.i(tag,"core      | code="+getCoreVersionCode()+", name="+getCoreVersionName());
    }

    public static long getAppVersionCode(){
        return app_code;
    }

    public static String getAppVersionName(){
        return app_name;
    }

    public static long getCoreVersionCode(){
        return edu.wustl.arc.assessments.BuildConfig.ARC_VERSION_CODE;
    }

    public static String getCoreVersionName(){
        return edu.wustl.arc.assessments.BuildConfig.ARC_VERSION_NAME;
    }

    public static long getVersionCode(int major,int minor, int patch, int build){
        return major * 1000000 + minor * 10000 + patch * 100 + build;
    }

    public static long getVersionCode(int major,int minor, int patch){
        return getVersionCode(major,minor,patch,0);
    }

    public static long getVersionCode(int major,int minor){
        return getVersionCode(major,minor,0,0);
    }

    public static long getVersionCode(int major){
        return getVersionCode(major,0,0,0);
    }

}
