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
import org.joda.time.LocalTime;

// the distinction between cycles and rhythms is that a ry
public class CircadianRhythm {

    private LocalTime bed;
    private LocalTime wake;
    private String weekday;
    private long lastBedUpdate;
    private long lastWakeUpdate;

    CircadianRhythm(String weekday){
        this.weekday = weekday;
        bed = LocalTime.MIDNIGHT;
        wake = LocalTime.MIDNIGHT;

        long now = DateTime.now().getMillis();
        lastBedUpdate = now;
        lastWakeUpdate = now;
    }

    public void setTimes(String wake, String bed) {
        this.wake = LocalTime.parse(wake);
        lastWakeUpdate = DateTime.now().getMillis();

        this.bed = LocalTime.parse(bed);
        lastBedUpdate = DateTime.now().getMillis();
    }

    public void setTimes(LocalTime wake, LocalTime bed) {
        this.wake = wake;
        lastWakeUpdate = DateTime.now().getMillis();

        this.bed = bed;
        lastBedUpdate = DateTime.now().getMillis();
    }

    public LocalTime getBedTime() {
        return bed;
    }

    public void setBedTime(LocalTime bed) {
        this.bed = bed;
        lastBedUpdate = DateTime.now().getMillis();
    }

    public LocalTime getWakeTime() {
        return wake;
    }

    public void setWakeTime(LocalTime wake) {
        this.wake = wake;
        lastWakeUpdate = DateTime.now().getMillis();
    }

    public String getWeekday() {
        return weekday;
    }

    public boolean isNocturnal(){
        return (wake.isAfter(bed));
    }

    public DateTime lastUpdatedWakedOn(){
        return new DateTime(lastWakeUpdate);
    }

    public DateTime lastUpdatedBedOn(){
        return new DateTime(lastBedUpdate);
    }

}
