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
package com.healthymedium.arc.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.notifications.types.NotificationType;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {

    static private final String tag = "NotificationManager";

    static public final String NOTIFICATION_ID = "notification_id";
    static public final String NOTIFICATION_TYPE = "notification_type";

    private NotificationNodes nodes = new NotificationNodes();
    private List<NotificationType> types;
    private Context context;

    private static NotificationManager instance;

    private NotificationManager(Context context, List<NotificationType> types) {
        this.context = context;
        this.types = types;

        for(NotificationType type : types){
            NotificationUtil.createChannel(context,type);
        }

        nodes.load();
    }

    public static synchronized void initialize(Context context, List<NotificationType> types) {
        instance = new NotificationManager(context,types);
    }

    public static synchronized NotificationManager getInstance() {
        if(instance==null){
            Application application = Application.getInstance();
            initialize(application.getAppContext(), application.getNotificationTypes());
        }
        return instance;
    }

    // scheduling ----------------------------------------------------------------------------------

    public void scheduleAllNotifications() {
        Log.i(tag,"scheduleAllNotifications()");
        List<NotificationNode> nodeList = nodes.getAll();
        List<NotificationNode> removeList = new ArrayList<>();
        for(NotificationNode node : nodeList){
            if(node.time.isBeforeNow()){
                removeList.add(node);
                continue;
            }
            NotificationType type = getNotificationType(node.type);
            scheduleNotification(node.id,type,node.time);
        }
        nodes.remove(removeList);
    }

    public void scheduleNotification(int sessionId, NotificationType type, DateTime timeStamp) {
        Log.i(tag,"scheduleNotification(type="+type.getChannelName()+" ,id="+sessionId+")");
        Intent notificationIntent = new Intent(context, NotificationAlarmReceiver.class);

        NotificationNode node = nodes.get(type.getId(),sessionId);
        if(node==null){
            node = nodes.add(type.getId(),sessionId, timeStamp);
        } else {
            node.time = timeStamp;
            nodes.save();
        }

        if(type.isProctored()){
            return;
        }

        notificationIntent.putExtra(NOTIFICATION_ID, node.id);
        notificationIntent.putExtra(NOTIFICATION_TYPE, node.type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, node.requestCode, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,timeStamp.getMillis(), pendingIntent);
    }

    public void unscheduleAllNotifications() {
        Log.i(tag,"unscheduleAllNotifications()");
        List<NotificationNode> nodeList = nodes.getAll();
        for(NotificationNode node : nodeList){
            NotificationType type = getNotificationType(node.type);
            unscheduleNotification(node.id,type);
        }
    }

    public boolean unscheduleNotification(int sessionId, NotificationType type) {
        Log.i(tag,"unscheduleNotification(id="+sessionId+", type="+type.getChannelName()+")");
        NotificationNode node = nodes.get(type.getId(), sessionId);
        if(node==null){
            return false;
        }

        nodes.remove(node);

        if(type.isProctored()){
            return true;
        }

        Intent notificationIntent = new Intent(context, NotificationAlarmReceiver.class);
        notificationIntent.putExtra(NOTIFICATION_ID, node.id);
        notificationIntent.putExtra(NOTIFICATION_TYPE, node.type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, node.requestCode, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    public void notifyUser(NotificationNode node){
        Log.i(tag,"notifyUser(id="+node.id+", type="+node.type+")");

        // remove node
        nodes.remove(node);

        // grab the type
        NotificationType type = getNotificationType(node.type);
        if(type==null) {
            Log.w(tag,"notification type not found, aborting");
            return;
        } else {
            Log.i(tag,"type = "+type.getChannelName());
        }

        if(type.onNotifyPending(node)){
            Log.i(tag,"pending notification allowed");
            type.onNotify(node);

            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = NotificationUtil.buildNotification(context,node,type);
            notificationManager.notify(node.getNotifyId(), notification);
        } else {
            Log.i(tag,"pending notification denied");
        }
    }

    public void removeUserNotification(int notifyId) {
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notifyId);
    }

    public NotificationType getNotificationType(int typeId){
        for(NotificationType type : types){
            if(type.getId()==typeId){
                return type;
            }
        }
        return null;
    }

    public NotificationNode getNode(int typeId, int sessionId){
        return nodes.get(typeId,sessionId);
    }

    public boolean removeNode(NotificationNode node){
        if(node==null){
            return true;
        }
        return nodes.remove(node);
    }

    public NotificationNodes getNodes(){
        return nodes;
    }

}