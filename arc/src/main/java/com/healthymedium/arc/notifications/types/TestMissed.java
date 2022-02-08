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
package com.healthymedium.arc.notifications.types;

import android.content.Context;

import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.notifications.NotificationManager;
import com.healthymedium.arc.notifications.NotificationNode;
import com.healthymedium.arc.notifications.NotificationTypes;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.ViewUtil;

public class TestMissed extends NotificationType {

    public static final String TAG_TEST_MISSED_COUNT = "TestMissedCount";

    public TestMissed(){
        super();
        id = 2;
        channelId = "TEST_MISSED";
        channelName = "Test Missed";
        channelDesc = "Notifies user when a test was missed";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION;
        proctored = true;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(NotificationNode node) {
        return ViewUtil.getString(R.string.notification_missedtests);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        PreferencesManager preferences = PreferencesManager.getInstance();
        int count = preferences.getInt(TAG_TEST_MISSED_COUNT, 0);
        count++;

        boolean showUser = (count == 4);
        return showUser;
    }

    @Override
    public void onNotify(NotificationNode node) {
        int notifyId = NotificationNode.getNotifyId(node.id, NotificationTypes.TestTake.id);
        NotificationManager.getInstance().removeUserNotification(notifyId);

        PreferencesManager preferences = PreferencesManager.getInstance();
        preferences.putInt(TAG_TEST_MISSED_COUNT, 0);
    }

}