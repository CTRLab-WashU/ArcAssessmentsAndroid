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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import edu.wustl.arc.study.TestSession;

@RunWith(JUnit4.class)
public class TestSessionTest {

    @Before
    public void setup(){

    }

    @Test
    public void getConstructorArguments() {

        int id = 0;
        int index = 0;
        int dayIndex = 0;

        TestSession session = new TestSession(dayIndex,index,id);

        Assert.assertEquals(id,session.getId());
        Assert.assertEquals(index,session.getIndex());
        Assert.assertEquals(dayIndex,session.getDayIndex());
    }

    @Test
    public void getDateTimes() {

        DateTime prescribedTime = DateTime.parse("2019-06-26 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss"));

        TestSession session = new TestSession(0,0,0);
        session.setPrescribedTime(prescribedTime);
        Assert.assertEquals(prescribedTime,session.getPrescribedTime());
        Assert.assertEquals(prescribedTime, session.getScheduledTime());
        Assert.assertEquals(prescribedTime.plusHours(2),session.getExpirationTime());
    }

    @Test
    public void getUserModifiedDateTimes() {

        DateTime prescribedTime = DateTime.parse("2019-06-26 09:30:00", DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss"));

        TestSession session = new TestSession(0,0,0);
        session.setPrescribedTime(prescribedTime);
        Assert.assertEquals(prescribedTime,session.getPrescribedTime());

        LocalDate scheduledDate = LocalDate.parse("2019-06-26");
        session.setScheduledDate(scheduledDate);

        DateTime scheduledTime = session.getScheduledTime();
        Assert.assertEquals(scheduledDate,scheduledTime.toLocalDate());
        Assert.assertEquals(prescribedTime.toLocalTime(),scheduledTime.toLocalTime());

        DateTime expirationTime = session.getExpirationTime();
        Assert.assertEquals(scheduledDate,expirationTime.toLocalDate());
        Assert.assertEquals(prescribedTime.plusHours(2).toLocalTime(),expirationTime.toLocalTime());

    }

    @Test
    public void getFlagsInit() {

        TestSession session = new TestSession(0,0,0);
        session.setPrescribedTime(DateTime.now().plusHours(1));

        Assert.assertFalse(session.isAvailableNow());

        Assert.assertFalse(session.wasFinished());
        Assert.assertFalse(session.isFinished());
        Assert.assertFalse(session.isOngoing());
        Assert.assertFalse(session.isOver());
        Assert.assertFalse(session.wasMissed());

    }


    @Test
    public void getFlagsStarted() {

        TestSession session = new TestSession(0,0,0);
        session.setPrescribedTime(DateTime.now().minusHours(1));

        Assert.assertTrue(session.isAvailableNow());

        session.markStarted();

        Assert.assertFalse(session.wasFinished());
        Assert.assertFalse(session.isFinished());

        Assert.assertTrue(session.isOngoing());
        Assert.assertFalse(session.isOver());
        Assert.assertFalse(session.wasMissed());

    }

    @Test
    public void getFlagsAbandoned() {

        TestSession session = new TestSession(0,0,0);
        session.setPrescribedTime(DateTime.now().minusHours(1));

        Assert.assertTrue(session.isAvailableNow());

        session.markStarted();
        session.markAbandoned();

        Assert.assertFalse(session.wasFinished());
        Assert.assertFalse(session.isFinished());

        Assert.assertFalse(session.isOngoing());
        Assert.assertTrue(session.isOver());
        Assert.assertFalse(session.wasMissed());

    }

    @Test
    public void getFlagsCompleted() {

        TestSession session = new TestSession(0,0,0);
        session.setPrescribedTime(DateTime.now().minusHours(1));

        Assert.assertTrue(session.isAvailableNow());

        session.markStarted();
        session.markCompleted();

        Assert.assertTrue(session.wasFinished());
        Assert.assertTrue(session.isFinished());

        Assert.assertFalse(session.isOngoing());
        Assert.assertTrue(session.isOver());
        Assert.assertFalse(session.wasMissed());

    }

    @Test
    public void getFlagsMissed() {

        TestSession session = new TestSession(0,0,0);
        session.setPrescribedTime(DateTime.now().plusHours(3));

        Assert.assertFalse(session.isAvailableNow());

        session.markMissed();

        Assert.assertFalse(session.wasFinished());
        Assert.assertFalse(session.isFinished());

        Assert.assertFalse(session.isOngoing());
        Assert.assertTrue(session.isOver());
        Assert.assertTrue(session.wasMissed());

    }

    @Test
    public void getFlagsInterrupted() {

        TestSession session = new TestSession(0,0,0);
        session.setPrescribedTime(DateTime.now());

        Assert.assertEquals(-99,session.wasInterrupted());
        session.markInterrupted(true);
        Assert.assertEquals(1,session.wasInterrupted());

    }

}