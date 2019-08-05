package com.healthymedium.arc.study;

import org.joda.time.DateTime;

import java.util.List;

public class TestDay {

    int dayIndex;
    List<TestSession> sessions;

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

}