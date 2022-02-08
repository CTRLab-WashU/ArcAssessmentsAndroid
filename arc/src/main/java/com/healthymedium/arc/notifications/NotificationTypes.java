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

import com.healthymedium.arc.notifications.types.NotificationType;
import com.healthymedium.arc.notifications.types.TestProctor;
import com.healthymedium.arc.notifications.types.TestConfirmed;
import com.healthymedium.arc.notifications.types.TestMissed;
import com.healthymedium.arc.notifications.types.TestNext;
import com.healthymedium.arc.notifications.types.TestTake;
import com.healthymedium.arc.notifications.types.VisitNextDay;
import com.healthymedium.arc.notifications.types.VisitNextMonth;
import com.healthymedium.arc.notifications.types.VisitNextWeek;

public class NotificationTypes {

    public static TestProctor TestProctor = new TestProctor();
    public static TestMissed TestMissed = new TestMissed();
    public static TestTake TestTake = new TestTake();

    public static VisitNextDay VisitNextDay = new VisitNextDay();
    public static VisitNextWeek VisitNextWeek = new VisitNextWeek();
    public static VisitNextMonth VisitNextMonth = new VisitNextMonth();

    // deprecated at this point
    public static TestConfirmed TestConfirmed = new TestConfirmed();
    public static TestNext TestNext = new TestNext();

    public static NotificationType fromId(int id) {

        if(id==TestProctor.getId()){
            return TestProctor;
        }
        if(id==TestMissed.getId()){
            return TestMissed;
        }
        if(id==TestTake.getId()){
            return TestTake;
        }
        if(id==VisitNextDay.getId()){
            return VisitNextDay;
        }
        if(id==VisitNextWeek.getId()){
            return VisitNextWeek;
        }
        if(id==VisitNextMonth.getId()){
            return VisitNextMonth;
        }
        if(id==TestConfirmed.getId()){ // dep
            return TestConfirmed;
        }
        if(id==TestNext.getId()){ // dep
            return TestNext;
        }

        return null;
    }

    public static String getName(int id) {
        if(id==TestProctor.getId()){
            return "proctor";
        }
        if(id==TestMissed.getId()){
            return "test missed";
        }
        if(id==TestTake.getId()){
            return "test take";
        }
        if(id==VisitNextDay.getId()){
            return "visit next day";
        }
        if(id==VisitNextWeek.getId()){
            return "visit next week";
        }
        if(id==VisitNextMonth.getId()){
            return "visit next month";
        }
        if(id==TestConfirmed.getId()){ // dep
            return "test confirmed";
        }
        if(id==TestNext.getId()){ // dep
            return "test next";
        }

        return "?";
    }

}
