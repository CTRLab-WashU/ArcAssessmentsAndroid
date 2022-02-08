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

import edu.wustl.arc.api.models.WakeSleepData;
import edu.wustl.arc.api.models.WakeSleepSchedule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CircadianClock {

    private DateTime createdOn;

    private List<CircadianRhythm> rhythms = new ArrayList<>();

    public CircadianClock(){
        rhythms.add(new CircadianRhythm("Sunday"));
        rhythms.add(new CircadianRhythm("Monday"));
        rhythms.add(new CircadianRhythm("Tuesday"));
        rhythms.add(new CircadianRhythm("Wednesday"));
        rhythms.add(new CircadianRhythm("Thursday"));
        rhythms.add(new CircadianRhythm("Friday"));
        rhythms.add(new CircadianRhythm("Saturday"));
        createdOn = DateTime.now();
    }

    public List<CircadianRhythm> getRhythms() {
        return Collections.unmodifiableList(rhythms);
    }

    public CircadianRhythm getRhythm(int index) {
        return rhythms.get(index);
    }

    public CircadianRhythm getRhythm(String weekday) {
        for(CircadianRhythm rhythm : rhythms){
            if(rhythm.getWeekday().equals(weekday)){
                return rhythm;
            }
        }
        return new CircadianRhythm("");
    }

    public boolean hasBedRhythmChanged(int index) {
        return getRhythm(index).lastUpdatedBedOn().isAfter(createdOn);
    }

    public boolean hasWakeRhythmChanged(int index) {
        return getRhythm(index).lastUpdatedWakedOn().isAfter(createdOn);
    }

    public boolean hasBedRhythmChanged(String weekday) {
        return getRhythm(weekday).lastUpdatedBedOn().isAfter(createdOn);
    }

    public boolean hasWakeRhythmChanged(String weekday) {
        return getRhythm(weekday).lastUpdatedWakedOn().isAfter(createdOn);
    }

    public boolean haveRhythmsChanged(String weekday) {
        return hasWakeRhythmChanged(weekday) && hasBedRhythmChanged(weekday);
    }

    public int getRhythmIndex(String weekday) {
        int size = rhythms.size();
        for(int i=0;i<size;i++){
            if(rhythms.get(i).getWeekday().equals(weekday)){
                return i;
            }
        }
        return 0;
    }

    public void setRhythms(LocalTime wake, LocalTime bed) {
        rhythms.get(0).setTimes(wake,bed); // Sunday
        rhythms.get(1).setTimes(wake,bed); // Monday
        rhythms.get(2).setTimes(wake,bed); // Tuesday
        rhythms.get(3).setTimes(wake,bed); // Wednesday
        rhythms.get(4).setTimes(wake,bed); // Thursday
        rhythms.get(5).setTimes(wake,bed); // Friday
        rhythms.get(6).setTimes(wake,bed); // Saturday
    }

    public void setRhythms(String wake, String bed) {
        rhythms.get(0).setTimes(wake,bed); // Sunday
        rhythms.get(1).setTimes(wake,bed); // Monday
        rhythms.get(2).setTimes(wake,bed); // Tuesday
        rhythms.get(3).setTimes(wake,bed); // Wednesday
        rhythms.get(4).setTimes(wake,bed); // Thursday
        rhythms.get(5).setTimes(wake,bed); // Friday
        rhythms.get(6).setTimes(wake,bed); // Saturday
    }

    public boolean isValid(){
        List<CircadianInstant> orderedRhythms = getRhythmInstances(LocalDate.now(),7);
        List<DateTime> mockDates = new ArrayList<>();

        int size = orderedRhythms.size();
        if(size == 0){
            return false;
        }

        for(int i=0;i<size;i++){
            mockDates.add(orderedRhythms.get(i).getWakeTime());
            mockDates.add(orderedRhythms.get(i).getBedTime());
        }

        size = mockDates.size();

        for(int i=1;i<size;i++){
            if(mockDates.get(i).isBefore(mockDates.get(i-1))) {
                return false;
            }
        }

        return true;
    }

    public static DateTime getCorrectedTime(LocalTime localTime, LocalDate startDate) {
        DateTimeZone timeZone = DateTimeZone.getDefault();

        LocalDateTime startDateTime = startDate.toLocalDateTime(localTime);

        if(timeZone.isLocalDateTimeGap(startDateTime)) {
            //correct the time by moving it to the end of the gap
            long dayStartMillis = startDateTime.toLocalDate().toDateTimeAtStartOfDay().getMillis();
            long endOfGapMillis = timeZone.nextTransition(dayStartMillis);
            return new DateTime(endOfGapMillis, timeZone);
        }

        return startDate.toDateTime(localTime);
    }


    // this outputs a list of pairs<wake,sleep>
    // the list starts with the date provided and provides a weeks worth of pairs
    public List<CircadianInstant> getRhythmInstances(LocalDate startDate, int numDays){

        List<CircadianInstant> orderedRhythms = new ArrayList<>();
        int rhythmCount = rhythms.size();

        if(rhythmCount==0){
            return orderedRhythms;
        }

        int index = startDate.getDayOfWeek();
        if(index==7){
            index = 0;
        }

        for(int i=0;i<numDays;i++){
            CircadianInstant instant = new CircadianInstant();
            DateTime wake;
            DateTime bed;

            if(rhythms.get(index).isNocturnal()){
                wake = getCorrectedTime(rhythms.get(index).getWakeTime(), startDate);
                bed = getCorrectedTime(rhythms.get(index).getBedTime(), startDate).plusDays(1);
            } else {
                wake = getCorrectedTime(rhythms.get(index).getWakeTime(), startDate);
                bed = getCorrectedTime(rhythms.get(index).getBedTime(), startDate);
            }
            instant.setWakeTime(wake);
            instant.setBedTime(bed);

            orderedRhythms.add(instant);
            startDate = startDate.plusDays(1);

            index++;
            if(index>=7){
                index = 0;
            }
        }

        return orderedRhythms;
    }

    private int getNextIndex(int index){
        index++;
        if(index >= rhythms.size()){
            index = 0;
        }
        return index;
    }

    private int getPreviousIndex(int index){
        index--;
        if(index < 0){
            index = rhythms.size()-1;
        }
        return index;
    }

    public static CircadianClock fromWakeSleepSchedule(WakeSleepSchedule schedule){
        CircadianClock clock = new CircadianClock();
        List<WakeSleepData> dataList = schedule.wakeSleepData;

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("h:mm a")
                .toFormatter()
                .withLocale(Locale.US);

        DateTimeFormatter twentyFourHourFormatter = new DateTimeFormatterBuilder()
                .appendPattern("H:mm")
                .toFormatter()
                .withLocale(Locale.US);

        for(WakeSleepData data : dataList){

            // handle any odd period formats the server may send our way
            data.wake = data.wake
                    .replace("a.m.","AM")
                    .replace("p.m.","PM");
            data.bed = data.bed
                    .replace("a.m.","AM")
                    .replace("p.m.","PM");
            data.wake = data.wake
                    .replace("a. m.","AM")
                    .replace("p. m.","PM");
            data.bed = data.bed
                    .replace("a. m.","AM")
                    .replace("p. m.","PM");
            data.wake = data.wake
                    .replace("午前","AM")
                    .replace("午後","PM");
            data.bed = data.bed
                    .replace("午前","AM")
                    .replace("午後","PM");

            LocalTime wake;
            if (data.wake.toUpperCase().contains("AM") ||
                data.wake.toUpperCase().contains("PM")) {
                wake = LocalTime.parse(data.wake,formatter);
            } else {
                wake = LocalTime.parse(data.wake, twentyFourHourFormatter);
            }

            LocalTime bed;
            if (data.wake.toUpperCase().contains("AM") ||
                    data.wake.toUpperCase().contains("PM")) {
                bed = LocalTime.parse(data.bed,formatter);
            } else {
                bed = LocalTime.parse(data.bed, twentyFourHourFormatter);
            }

            clock.getRhythm(data.weekday).setTimes(wake,bed);
        }

        return clock;
    }
}
