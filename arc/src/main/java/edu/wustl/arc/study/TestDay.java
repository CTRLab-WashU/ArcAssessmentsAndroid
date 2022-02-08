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
package edu.wustl.arc.study;

import android.util.Log;
import org.joda.time.DateTime;
import java.util.List;

public class TestDay {

    int dayIndex;
    List<TestSession> sessions;
    DateTime start;
    DateTime end;

    public TestDay(int index, List<TestSession> sessions) {
        this.dayIndex = index;
        this.sessions = sessions;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public List<TestSession> getTestSessions() {
        return sessions;
    }

    public TestSession getTestSession(int index) {
        return sessions.get(index);
    }

    public int getNumberOfTestsAvailableNow() {
        int count = 0;
        for(TestSession session : sessions) {
            if(!session.isOver() && session.isAvailableNow()) {
                count++;
            }
        }
        return count;
    }

    public int getNumberOfTests() {
        return sessions.size();
    }

    public int getNumberOfTestsLeft() {
        int count = 0;
        for(TestSession session : sessions) {
            if(!session.isOver()) {
                count++;
            }
        }
        return count;
    }

    public int getNumberOfTestsFinished() {
        int count = 0;
        for(TestSession session : sessions) {
            if(session.isFinished()) {
                count++;
            }
        }
        return count;
    }

    public boolean hasThereBeenAFinishedTest() {
        return getNumberOfTestsFinished()>0;
    }

    public boolean isOver() {
        if(sessions.size()==0) {
            return true;
        }
        int last = sessions.size()-1;
        return sessions.get(last).isOver();
    }

    public boolean hasStarted() {
        if(sessions.size()==0) {
            return false;
        }
        DateTime scheduledTime = sessions.get(0).getScheduledTime();
        if(scheduledTime==null) {
           return false;
        }
        return scheduledTime.isBeforeNow();
    }

    public int getProgress() {
        float progress = 0;
        int numEntries = sessions.size();
        for(TestSession session : sessions){
            progress += ((float)session.getProgress()/numEntries);
        }
        return (int) progress;
    }

    public void setStartTime(DateTime start) {
        this.start = start;
    }

    public DateTime getStartTime() {
        return start;
    }

    public void setEndTime(DateTime end) {
        this.end = end;
    }

    public DateTime getEndTime() {
        return end;
    }

    public boolean isScheduleCorrupted() {
        DateTime start = getStartTime();
        DateTime end = getEndTime();

        DateTime before = null;
        for (TestSession session : sessions) {
            DateTime date = session.getScheduledTime();
            if(before!=null) {
                if(date.isBefore(before)) {
                    Log.e("TestDay","corruption found: sessions are out of order");
                    return true;
                }
            }
            before = date;

            if (date.isBefore(start) || date.isAfter(end)) {
                Log.e("TestDay","corruption found: sessions are out of testing window");
                return true;
            }
        }

        return false;
    }

}
