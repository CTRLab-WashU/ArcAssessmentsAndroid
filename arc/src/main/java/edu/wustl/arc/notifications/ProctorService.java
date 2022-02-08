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

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import edu.wustl.arc.utilities.ViewUtil;

import android.util.Log;

import edu.wustl.arc.assessments.R;

public class ProctorService extends Service {

    private static final String tag = "ProctorService";

    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    public static final String ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE";
    public static final String ACTION_RESUME_SERVICE = "ACTION_RESUME_SERVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_REFRESH_DATA = "ACTION_REFRESH_DATA";

    private ProctorServiceHandler serviceHandler;
    private Handler handler = new Handler();
    private boolean intentionalDestruction = false;

    public ProctorService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
    }

    @Override
    public void onDestroy() {
        if(!intentionalDestruction){
            Proctor.startService(this);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            stopForegroundService();
            return START_STICKY;
        }

        String action = intent.getAction();
        Log.d(tag, action);

        switch (action) {
            case ACTION_START_SERVICE:

                if(serviceHandler==null){
                    serviceHandler = new ProctorServiceHandler(listener);
                    serviceHandler.refreshData();
                }

                if(!serviceHandler.isRunning()){
                    serviceHandler.start();
                }

                // TODO: mdephillips 3/18/21 Job scheduler shouldn't be used and should be replaced
                // TODO with WorkManager which has better support for all Android OS'
                if(!ProctorWatchdogJob.isScheduled(this)) {
                    ProctorWatchdogJob.start(this);
                }
                break;

            case ACTION_PAUSE_SERVICE:
                if(serviceHandler==null) {
                    serviceHandler = new ProctorServiceHandler(listener);
                }
                serviceHandler.stop();
                break;

            case ACTION_RESUME_SERVICE:
                if(serviceHandler==null) {
                    serviceHandler = new ProctorServiceHandler(listener);
                }
                serviceHandler.refreshData(true);
                serviceHandler.start();

                if(!ProctorWatchdogJob.isScheduled(this)) {
                    ProctorWatchdogJob.start(this);
                }
                break;

            case ACTION_STOP_SERVICE:
                intentionalDestruction = true;
                if(serviceHandler!=null){
                    serviceHandler.stop();
                }
                stopForegroundService();
                ProctorWatchdogJob.stop(this);
                break;

            case ACTION_REFRESH_DATA:
                if(serviceHandler==null) {
                    serviceHandler = new ProctorServiceHandler(listener);
                }
                serviceHandler.stop();
                serviceHandler.refreshData();
                serviceHandler.start();
                break;
        }

        return START_STICKY;
    }

    private void startForegroundService() {
        Log.d(tag, "startForegroundService");

        NotificationUtil.createChannel(this,NotificationTypes.TestProctor);

        PackageManager packageManager = getPackageManager();
        String packageName = getPackageName();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);

        int notificationId = NotificationTypes.TestProctor.getId();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationTypes.TestProctor.getChannelId())
                .setContentTitle(ViewUtil.getString(R.string.notification_testproctor_header))
                .setContentText(ViewUtil.getString(R.string.notification_testproctor_body))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        Notification notification = builder.build();
        startForeground(notificationId, notification);
    }

    private void stopForegroundService() {
        Log.d(tag, "stopForegroundService");
        stopForeground(true);
        stopSelf();
    }

    ProctorServiceHandler.Listener listener = new ProctorServiceHandler.Listener() {
        @Override
        public void onFinished() {
            Log.d(tag, "onFinished");
            Proctor.stopService(getApplicationContext());
        }

        @Override
        public void onNotify(final NotificationNode node) {
            Log.d(tag, "onNotify(id="+node.id+", type="+node.type+")");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    //noinspection deprecation
                    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                            PowerManager.FULL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP,
                            tag+"_"+SystemClock.uptimeMillis());

                    wakeLock.acquire();
                    NotificationManager.getInstance().notifyUser(node);
                    wakeLock.release();
                }
            });
        }
    };

}
