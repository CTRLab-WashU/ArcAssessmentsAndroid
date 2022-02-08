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

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

// This is only used for notifications that are not proctored.

public class NotificationNotifyJob extends JobService {

    static private final String tag = "NotificationNotifyJob";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(tag,"onStartJob");

        // extract parameters
        int id = params.getExtras().getInt(NotificationManager.NOTIFICATION_ID,0);
        int typeId = params.getExtras().getInt(NotificationManager.NOTIFICATION_TYPE,1);

        NotificationManager manager = NotificationManager.getInstance();

        // grab the node
        NotificationNode node = manager.getNode(typeId, id);
        if(node==null){
            Log.w(tag,"Unable to find node, aborting");
            return false;
        }

        manager.notifyUser(node);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(tag,"onStopJob");
        return false;
    }

    public static JobInfo build(Context context, int id, int type){

        PersistableBundle extras = new PersistableBundle();
        extras.putInt(NotificationManager.NOTIFICATION_ID, id);
        extras.putInt(NotificationManager.NOTIFICATION_TYPE, type);

        NotificationNode node = NotificationManager.getInstance().getNode(type, id);

        ComponentName serviceComponent = new ComponentName(context, NotificationNotifyJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(node.getNotifyId(), serviceComponent);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setPersisted(false);
        builder.setExtras(extras);

        builder.setMinimumLatency(1);
        builder.setOverrideDeadline(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }
        JobInfo info = builder.build();
        return info;
    }

}