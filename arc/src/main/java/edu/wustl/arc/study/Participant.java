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

import edu.wustl.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;

public class Participant {

    public static final String TAG_PARTICIPANT_STATE = "ParticipantState";

    protected ParticipantState state;

    public void initialize(){
        state = new ParticipantState();
    }

    public void load() {
        load(false);
    }

    public void load(boolean overwrite){
        if(state!=null && !overwrite){
            return;
        }
        state = PreferencesManager.getInstance().getObject(TAG_PARTICIPANT_STATE,ParticipantState.class);
    }

    public void save(){
        PreferencesManager.getInstance().putObject(TAG_PARTICIPANT_STATE, state);
    }

    public boolean hasId(){
        return state.id!=null;
    }

    public String getId(){
        return state.id;
    }

    public boolean hasSchedule(){
        return state.hasValidSchedule;
    }

    public boolean hasCommittedToStudy(){
        return state.hasCommittedToStudy==1;
    }

    public boolean hasRebukedCommitmentToStudy(){
        return state.hasCommittedToStudy==0;
    }

    public void markCommittedToStudy(){
        state.hasCommittedToStudy = 1;
    }

    public void rebukeCommitmentToStudy(){
        state.hasCommittedToStudy = 0;
    }

    public boolean hasBeenShownNotificationOverview(){
        return state.hasBeenShownNotificationOverview;
    }

    public void markShownNotificationOverview(){
        state.hasBeenShownNotificationOverview = true;
    }

    public boolean hasBeenShownBatteryOptimizationOverview(){
        return state.hasBeenShownBatteryOptimizationOverview;
    }

    public void markShownBatteryOptimizationOverview(){
        state.hasBeenShownBatteryOptimizationOverview = true;
    }

    public void markPaused(){
        state.lastPauseTime = DateTime.now();
    }

    public void markResumed(){

        /*
        We're checking three situations here:
        - Are we currently in a test?
            If so check and see if we should abandon it
        - Should we be in a test, but the state machine is not set to a test path?
            If so let's skip to the next segment
        - Else are we currently in a test path?
            If so, have the state machine decide where to go next
         */
        if(isCurrentlyInTestSession()){
            if(checkForTestAbandonment())
            {
                Study.getInstance().abandonTest();
            }
        } else if(shouldCurrentlyBeInTestSession() && !Study.getStateMachine().isCurrentlyInTestPath()){
            Study.skipToNextSegment();
        }
        else if(Study.getStateMachine().isCurrentlyInTestPath()){
            Study.getStateMachine().decidePath();
            Study.getStateMachine().setupPath();
            Study.getStateMachine().openNext();
        }
    }

    public boolean checkForTestAbandonment(){
        return state.lastPauseTime.plusMinutes(5).isBeforeNow();
    }
    public boolean isCurrentlyInTestSession(){
        if(state.testCycles.size()==0){
            return false;
        }
        TestSession session = getCurrentTestSession();
        if(session==null) {
            return false;
        }
        return session.isOngoing();
    }

    public boolean shouldCurrentlyBeInTestSession(){
        if(state.testCycles.size()==0){
            return false;
        }
        TestSession session = getCurrentTestSession();
        if(session==null) {
            return false;
        }
        return session.getScheduledTime().isBeforeNow();
    }

    public boolean shouldCurrentlyBeInTestCycle() {
        TestCycle cycle = getCurrentTestCycle();
        if(cycle==null) {
            return false;
        }
        return cycle.getActualStartDate().isBeforeNow();
    }

    public TestCycle getCurrentTestCycle(){
        if(state.testCycles.size() > state.currentTestCycle) {
            return state.testCycles.get(state.currentTestCycle);
        }
        return null;
    }

    public TestDay getCurrentTestDay(){
        TestCycle cycle = getCurrentTestCycle();
        if(cycle==null) {
            return null;
        }
        return cycle.getTestDay(state.currentTestDay);
    }

    public void setMostRecentTestSession(TestSession session) {
        state.mostRecentTestSession = session;
        save();
    }

    public TestSession getCurrentTestSession() {
        if (state.mostRecentTestSession != null) {
            return state.mostRecentTestSession;
        }
        TestDay day = getCurrentTestDay();
        if(day==null){
            return null;
        }
        for(TestSession session : day.getTestSessions()) {
            if(session.getIndex()==state.currentTestSession){
                return session;
            }
        }
        return null;
    }

    public void moveOnToNextTestSession(boolean scheduleNotifications){
        Log.i("Participant", "moveOnToNextTestSession");

        state.currentTestSession++;

        if(getCurrentTestDay().isOver()){
            state.currentTestDay++;
            state.currentTestSession = 0;
        }

        if(getCurrentTestCycle().isOver()){
            state.currentTestSession = 0;
            state.currentTestDay = 0;
            state.currentTestCycle++;
            if(state.currentTestCycle >=state.testCycles.size()){
                state.isStudyRunning = false;
            }
        }
        save();
    }

    public void setCircadianClock(CircadianClock clock){
        state.circadianClock = clock;
    }

    public CircadianClock getCircadianClock(){
        return state.circadianClock;
    }

    public boolean isStudyRunning(){
        return state.isStudyRunning;
    }

    public void markStudyStarted(){
        state.isStudyRunning = true;
        save();
    }

    public void markStudyStopped(){
        state.isStudyRunning = false;
        save();
    }

    public ParticipantState getState(){
        return state;
    }

    public void setState(ParticipantState state){
        this.state = state;
        save();
    }

    public void setState(ParticipantState state, boolean shouldSave){
        this.state = state;
        if(shouldSave) {
            save();
        }
    }

    public DateTime getStartDate() {
        return state.studyStartDate;
    }

    public DateTime getFinishDate() {
        int size = state.testCycles.size();
        if(size>0) {
            return state.testCycles.get(size-1).getActualEndDate();
        }
        return null;
    }

    public TestSession getSessionById(int id) {
        for(TestCycle cycle : state.testCycles) {
            for(TestDay day : cycle.getTestDays()) {
                for(TestSession session : day.getTestSessions()) {
                    if(session.getId()==id){
                        return session;
                    }
                }
            }
        }
        return null;
    }

    public TestCycle getCycleBySessionId(int id) {
        for(TestCycle cycle : state.testCycles) {
            for(TestDay day : cycle.getTestDays()) {
                for(TestSession session : day.getTestSessions()) {
                    if(session.getId()==id){
                        return cycle;
                    }
                }
            }
        }
        return null;
    }

    public TestDay getDayBySessionId(int id) {
        for(TestCycle cycle : state.testCycles) {
            for(TestDay day : cycle.getTestDays()) {
                for(TestSession session : day.getTestSessions()) {
                    if(session.getId()==id){
                        return day;
                    }
                }
            }
        }
        return null;
    }

    public boolean isScheduleCorrupted() {
        if(state.testCycles==null) {
            return false;
        }
        for (TestCycle cycle : state.testCycles) {
            if(!cycle.hasStarted()) {
                if(cycle.isScheduleCorrupted()){
                    return true;
                }
            }
        }
        return false;
    }

}
