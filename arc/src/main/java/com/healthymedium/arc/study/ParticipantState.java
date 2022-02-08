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
package com.healthymedium.arc.study;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ParticipantState {

    public String id;

    public DateTime studyStartDate;

    public List<TestCycle> testCycles;
    public int currentTestSession;
    public int currentTestCycle;
    public int currentTestDay;

    public Earnings earnings;

    public CircadianClock circadianClock;
    public boolean hasValidSchedule;
    public boolean isStudyRunning;
    public int hasCommittedToStudy;
    public boolean hasBeenShownNotificationOverview;
    public boolean hasBeenShownBatteryOptimizationOverview;

    // These are variables only used during runtime
    public DateTime lastPauseTime;

    public ParticipantState(){
        circadianClock = new CircadianClock();
        lastPauseTime = new DateTime();
        testCycles = new ArrayList<>();
        earnings = new Earnings();
        hasCommittedToStudy = -1;
    }

}
