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
package edu.wustl.arc.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.wustl.arc.core.Application;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestCycle;

public class NotificationResetReceiver extends BroadcastReceiver {

    static private final String tag = "NotificationResetReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(tag,"onReceive");

        String action = intent.getAction();
        boolean isUpdate = action.equals(Intent.ACTION_MY_PACKAGE_REPLACED);
        boolean isReboot = action.equals(Intent.ACTION_BOOT_COMPLETED);

        // if intent is not from either a reboot or an update, nope out
        if (!(isReboot||isUpdate)){
            return;
        }

        if (Application.getInstance() == null ||
            Application.getInstance().getAppContext() == null) {
            Log.e(tag, "Trying to re-schedule notifications without app context");
            return;
        }

        NotificationManager.getInstance().scheduleAllNotifications();
        TestCycle cycle = Study.getCurrentTestCycle();
        if(cycle ==null){
            return;
        }
        if(cycle.getActualStartDate().isBeforeNow() && cycle.getActualEndDate().isAfterNow()){
            Log.i(tag,"starting proctor service");
            Proctor.startService(Application.getInstance().getAppContext());
        }
    }
}