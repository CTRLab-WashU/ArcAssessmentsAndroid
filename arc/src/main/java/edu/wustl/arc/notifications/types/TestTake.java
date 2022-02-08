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
package edu.wustl.arc.notifications.types;

import edu.wustl.arc.core.Application;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.notifications.NotificationNode;
import edu.wustl.arc.study.Participant;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestCycle;
import edu.wustl.arc.study.TestSession;
import edu.wustl.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TestTake extends NotificationType {

    private DateTime expirationTime;
    private String body;

    public TestTake(){
        super();
        id = 1;
        channelId = "TEST_TAKE";
        channelName = "Test Reminder";
        channelDesc = "Notifies user when it is time to take a test";
        importance = NotificationImportance.HIGH;
        extra = Config.INTENT_EXTRA_OPENED_FROM_NOTIFICATION;
        proctored = true;
        soundResource = R.raw.pluck;
    }

    @Override
    public String getContent(NotificationNode node) {

        Participant participant = Study.getParticipant();
        TestSession session = participant.getSessionById(node.id);
        TestCycle cycle = participant.getCycleBySessionId(node.id);

        if(session==null) {
            return "";
        }

        expirationTime = session.getExpirationTime();

        // if first test of the cycle
        if (session.getIndex() == 0 && session.getDayIndex() == 0) {
            body = ViewUtil.getString(R.string.notification1_firstday);
        }

        // if first of day 4
        else if (session.getIndex() == 0 && session.getDayIndex() == 3) {
            body = ViewUtil.getString(R.string.notification1_halfway);
        }

        // if first session of last day of cycle
        else if (session.getIndex() == 0 && session.getDayIndex() == cycle.getNumberOfTestDays()-1) {
            body = ViewUtil.getString(R.string.notification1_lastday);
        }

        // if first of day
        else if (session.getIndex() == 0) {
            body = ViewUtil.getString(R.string.notification1_default);
        }

        // if second of day
        else if (session.getIndex() == 1) {
            body = ViewUtil.getString(R.string.notifications2_default);
        }

        // if third of day
        else if (session.getIndex() == 2) {
            body = ViewUtil.getString(R.string.notification3_default);
        }

        // if last of cycle
        else if (session.getIndex() == 3 && session.getDayIndex() == cycle.getNumberOfTestDays()-1) {
            body = ViewUtil.getString(R.string.notification4_lastday);
        }

        // if last of day
        else if (session.getIndex() == 3) {
            body = ViewUtil.getString(R.string.notification4_default);
        }

        String pattern = DateTimeFormat.patternForStyle("-S", Application.getInstance().getLocale());
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        String time = fmt.print(expirationTime);

        return body.replace("{TIME}", time);
    }

    @Override
    public boolean onNotifyPending(NotificationNode node) {
        return true;
    }

    @Override
    public void onNotify(NotificationNode node) {

    }

}
