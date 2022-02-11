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
package edu.wustl.arc.time;


import android.content.ContentResolver;
import android.provider.Settings;
import androidx.annotation.StringRes;

import edu.wustl.arc.core.Application;
import edu.wustl.arc.utilities.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class TimeUtil {

    public static String getWeekday(){
        return getWeekday(DateTime.now());
    }

    public static String getWeekday(DateTime dateTime){

        switch(dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                return "Sunday";
            case DateTimeConstants.MONDAY:
                return "Monday";
            case DateTimeConstants.TUESDAY:
                return "Tuesday";
            case DateTimeConstants.WEDNESDAY:
                return "Wednesday";
            case DateTimeConstants.THURSDAY:
                return "Thursday";
            case DateTimeConstants.FRIDAY:
                return "Friday";
            case DateTimeConstants.SATURDAY:
                return "Saturday";
            default:
                return "";
        }
    }

    public static double toUtcDouble(DateTime dateTime){
        double utc = dateTime.getMillis();
        return utc/1000;
    }

    public static DateTime fromUtcDouble(double dateTime){
        long longTime = (long)(dateTime*1000L);
        return new DateTime(longTime);
    }

    public static String format(DateTime dateTime, @StringRes int format){
        return dateTime.toString(ViewUtil.getString(format), Application.getInstance().getLocale());
    }

    public static String format(DateTime dateTime, @StringRes int format, Locale locale){
        return dateTime.toString(ViewUtil.getString(format),locale);
    }

    public static DateTime setTime(DateTime date, String time){
        String[] split = time.split("[: ]");
        if(split.length != 3){
            return date;
        }
        split[2]  = split[2].toLowerCase();
        int hours = Integer.valueOf(split[0]);
        int minutes = Integer.valueOf(split[1]);
        if(split[2].toLowerCase().equals("pm") && hours != 12){
            hours +=12;
        } else if(split[2].toLowerCase().equals("am") && hours == 12){
            hours = 0;
        } else {
            //hours -= 1;
        }
        DateTime newDate = date
                .withHourOfDay(hours)
                .withMinuteOfHour(minutes)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        return newDate;
    }

    public static DateTime setMidnight(DateTime date){
        DateTime newDate = date
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        return newDate;
    }

    public static boolean isAutoTimeEnabled(){
        ContentResolver resolver = Application.getInstance().getContentResolver();
        boolean autoTimeEnabled = Settings.Global.getInt(resolver, Settings.Global.AUTO_TIME, 0)==1;
        boolean autoTimeZoneEnabled = Settings.Global.getInt(resolver, Settings.Global.AUTO_TIME_ZONE, 0)==1;
        return autoTimeEnabled && autoTimeZoneEnabled;
    }

    public static String getTimezoneName(){
        TimeZone tz = TimeZone.getDefault();
        return tz.getDisplayName(Locale.US);
    }

    public static String getTimezoneOffset(){
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(timeZone);
        int millis = timeZone.getOffset(calendar.getTimeInMillis());

        String offset = String.format("%02d:%02d", Math.abs(millis / 3600000), Math.abs((millis / 60000) % 60));
        offset = "UTC"+(millis >= 0 ? "+" : "-") + offset;
        return offset;
    }

}
