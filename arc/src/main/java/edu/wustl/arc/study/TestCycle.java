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

import edu.wustl.arc.time.TimeUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class TestCycle {

    private int id;

    private DateTime scheduledStartDate;          // official start date of the TestCycle,
    private DateTime scheduledEndDate;            // official end date of the TestCycle,
    private DateTime actualStartDate;           // user-modified start date of the TestCycle,
    private DateTime actualEndDate;             // user-modified end date of the TestCycle,

    private List<TestDay> days;

    public TestCycle(int id, DateTime start, DateTime end) {
        this.id = id;
        scheduledStartDate = TimeUtil.setMidnight(start);
        scheduledEndDate = TimeUtil.setMidnight(end);
        days = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public DateTime getScheduledStartDate() {
        return scheduledStartDate;
    }

    public DateTime getScheduledEndDate() {
        return scheduledEndDate;
    }

    public DateTime getActualStartDate() {
        if(actualStartDate == null) {
            return scheduledStartDate;
        }
        return actualStartDate;
    }

    public void setActualStartDate(DateTime actualStartDate) {
        this.actualStartDate = TimeUtil.setMidnight(actualStartDate);
    }

    public DateTime getActualEndDate() {
        if(actualEndDate == null) {
            return scheduledEndDate;
        }
        return actualEndDate;
    }

    public void setActualEndDate(DateTime actualEndDate) {
        this.actualEndDate = TimeUtil.setMidnight(actualEndDate);
    }

    public List<TestDay> getTestDays() {
        return days;
    }

    public TestDay getTestDay(int dayIndex) {
        if(dayIndex>=days.size()) {
            return null;
        }
        return days.get(dayIndex);
    }

    public List<TestSession> getTestSessions() {
        List<TestSession> sessions = new ArrayList<>();
        for(TestDay day : days) {
            sessions.addAll(day.sessions);
        }
        return sessions;
    }

    public TestSession getTestSession(int index) {
        int pointer = 0;
        for(TestDay day : days) {
            for(TestSession session : day.sessions) {
                if(pointer==index) {
                    return session;
                }
                pointer++;
            }
        }
        return null;
    }

    public int getNumberOfTestDays() {
        return days.size();
    }

    public int getNumberOfTests() {
        int count = 0;
        for(TestDay day : days) {
            count += day.getNumberOfTests();
        }
        return count;
    }

    public int getNumberOfTestsLeft() {
        int count = 0;
        for(TestDay day : days) {
            count += day.getNumberOfTestsLeft();
        }
        return count;
    }

    public boolean isOver() {
        if(getActualEndDate().isBeforeNow()) {
            return true;
        }
        if(days.size()==0) {
            return true;
        }
        int last = days.size()-1;
        return days.get(last).isOver();
    }

    public boolean hasStarted() {
        if(days.size()==0) {
            return false;
        }
        return days.get(0).hasStarted();
    }

    public boolean hasThereBeenAFinishedTest() {
        for(TestDay day : days) {
            if(day.getNumberOfTestsFinished()>0) {
                return true;
            }
        }
        return false;
    }

    public int getProgress() {
        float progress = 0;
        int numEntries = days.size();
        for(TestDay day : days){
            progress += ((float)day.getProgress()/numEntries);
        }
        return (int) progress;
    }

    public void shiftSchedule(int numDays) {
        for(TestDay day : days) {
            day.setStartTime(day.getStartTime().plusDays(numDays));
            day.setEndTime(day.getEndTime().plusDays(numDays));

            List<TestSession> sessions = day.getTestSessions();
            for(TestSession session : sessions) {
                LocalDate date = session.getPrescribedTime().plusDays(numDays).toLocalDate();
                session.setScheduledDate(date);
            }
        }
        List<TestSession> sessions = getTestSessions();
        int last = sessions.size()-1;
        setActualStartDate(sessions.get(0).getScheduledTime());
        setActualEndDate(sessions.get(last).getScheduledTime().plusDays(1));
    }

   /**
    * 1 schedule was found in Production to have an invalid schedule a.k.a "corrupted".
    * The "corruption" was that the first test scheduled for each day had a start_date for the day before. 
    * The participant reported an issue with tests not starting despite getting notifications. 
    * Our study admin checked their test schedule and noticed that the test dates didn't line up right.
    *
    * To fix the issue, we first check if a schedule is in this invalid format.
    * Then, we re-create the user's schedule and upload it to the server.
    * 
    * This fix was validated by testing different paths with the "corrupted" schedule. 
    * Reinstall to mock user with bad data on two versions. One with the fix the other without. 
    * One corrected itself immediately, the other corrected itself after updating to the new version.
    */
    public boolean isScheduleCorrupted() {
        for(TestDay day : days) {
            if(day.isScheduleCorrupted()){
                return true;
            }
        }
        return false;
    }

}
