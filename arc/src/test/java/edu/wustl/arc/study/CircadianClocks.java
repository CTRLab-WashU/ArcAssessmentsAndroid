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

import org.junit.Assert;

import edu.wustl.arc.study.CircadianClock;

public class CircadianClocks {

    public static CircadianClock getDefault(){
        CircadianClock clock = new CircadianClock();
        clock.getRhythm(0).setTimes("08:00:00","17:00:00"); // Sunday
        clock.getRhythm(1).setTimes("08:00:00","17:00:00"); // Monday
        clock.getRhythm(2).setTimes("08:00:00","17:00:00"); // Tuesday
        clock.getRhythm(3).setTimes("08:00:00","17:00:00"); // Wednesday
        clock.getRhythm(4).setTimes("08:00:00","17:00:00"); // Thursday
        clock.getRhythm(5).setTimes("08:00:00","17:00:00"); // Friday
        clock.getRhythm(6).setTimes("08:00:00","17:00:00"); // Saturday
        Assert.assertTrue(clock.isValid());
        return clock;
    }

    public static CircadianClock getDayShift(){
        CircadianClock clock = new CircadianClock();
        clock.getRhythm(0).setTimes("08:30:00","20:30:00"); // Sunday
        clock.getRhythm(1).setTimes("07:00:00","20:15:00"); // Monday
        clock.getRhythm(2).setTimes("07:30:00","21:00:00"); // Tuesday
        clock.getRhythm(3).setTimes("07:00:00","21:00:00"); // Wednesday
        clock.getRhythm(4).setTimes("07:30:00","21:00:00"); // Thursday
        clock.getRhythm(5).setTimes("07:00:00","20:15:00"); // Friday
        clock.getRhythm(6).setTimes("09:30:00","23:15:00"); // Saturday
        Assert.assertTrue("day shift is invalid",clock.isValid());
        return clock;
    }

}
