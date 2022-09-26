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
package edu.wustl.arc.utilities;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;

import edu.wustl.arc.core.ArcApplication;
import edu.wustl.arc.assessments.R;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

public class Phrase {

    Context context;
    String phrase;

    public Phrase(Context context, @StringRes int id){
        this.context = context;
        phrase = getString(id);
    }

    public Phrase(@StringRes int id){
        // TODO: mdephillips 3/18/21 storing a strong ref to app context within a model object like the Phrase class is dangerous design.  We have to re-factor the context out of the Phrase class.
        this.context = ArcApplication.getInstance().getAppContext();
        phrase = getString(id);
    }

    public Phrase(Context context, String string){
        this.context = context;
        phrase = string;
    }

    public Phrase(String string){
        // TODO: mdephillips 3/18/21 storing a strong ref to app context within a model object like the Phrase class is dangerous design.  We have to re-factor the context out of the Phrase class.
        this.context = ArcApplication.getInstance().getAppContext();
        phrase = string;
    }
    
    public static Phrase getTimeReplacedPhrase(int stringResId, String time1, String time2) {
        Phrase timeReplacePhrase = new Phrase(stringResId);
        timeReplacePhrase.replaceTimes(time1, time2);
        return timeReplacePhrase;
    }

    private String getString(@StringRes int id){
        return context.getString(id);
    }

    // ---------------------------------------------------------------------------------------------

    public void replace(@StringRes int tokenId, @StringRes int stringId){
        String token = getString(tokenId);
        String string = getString(stringId);
        replace(token, string);
    }

    public void replace(@StringRes int tokenId, String string){
        String token = getString(tokenId);
        replace(token, string);
    }

    public void replace(String token, String string){
        phrase = phrase.replace(token, string);
    }

    // ---------------------------------------------------------------------------------------------

    public void replaceDate(String date){
        replace(R.string.token_date, date);
    }

    public void replaceDates(String date1, String date2){
        replace(R.string.token_date1, date1);
        replace(R.string.token_date2, date2);
    }

    public void replaceDate(@StringRes int format, DateTime dateTime){
        String string = dateTime.toString(getString(format), ArcApplication.getInstance().getLocale());
        replaceDate(string);
    }

    public void replaceDate(@StringRes int tokenId, @StringRes int format, DateTime dateTime){
        String string = dateTime.toString(getString(format), ArcApplication.getInstance().getLocale());
        replace(tokenId,string);
    }

    public void replaceDates(@StringRes int format, DateTime date1, DateTime date2){
        replaceDate(R.string.token_date1, format, date1);
        replaceDate(R.string.token_date2, format, date2);
    }

    // ---------------------------------------------------------------------------------------------
    // time

    public void replaceTime(@StringRes int format, DateTime time){
        String string = time.toString(getString(format), ArcApplication.getInstance().getLocale());
        replaceTime(string);
    }

    public void replaceTime(@StringRes int format, LocalTime time){
        String string = time.toString(getString(format), ArcApplication.getInstance().getLocale());
        replaceTime(string);
    }

    public void replaceTime(@StringRes int tokenId, @StringRes int format, LocalTime time){
        String string = time.toString(getString(format), ArcApplication.getInstance().getLocale());
        replace(tokenId, string);
    }

    public void replaceTime(String time){
        replace(R.string.token_time, time);
    }

    public void replaceTime(@StringRes int tokenId, @StringRes int format, DateTime time){
        String string = time.toString(getString(format), ArcApplication.getInstance().getLocale());
        replace(tokenId, string);
    }

    //Should be private, but didn't want to refactor.
    public void replaceTimes(@StringRes int format, DateTime time1, DateTime time2){
        replaceTime(R.string.token_time1, format, time1);
        replaceTime(R.string.token_time2, format, time2);
    }

    public void replaceTimes(@StringRes int format, LocalTime time1, LocalTime time2){
        replaceTime(R.string.token_time1, format, time1);
        replaceTime(R.string.token_time2, format, time2);
    }

    public void replaceTimes(String time1, String time2){
        replace(R.string.token_time1, time1);
        replace(R.string.token_time2, time2);
    }

    // ---------------------------------------------------------------------------------------------

    public void replaceValues(String value1, String value2){
        replace(R.string.token_value1, value1);
        replace(R.string.token_value1, value2);
    }

    public void replaceAmount(String amount) {
        replace(R.string.token_amount, amount);
    }

    public void replaceUnit(String unit){
        replace(R.string.token_unit,unit);
    }

    public void replaceNumber(int number){
        String string = String.valueOf(number);
        replace(R.string.token_number,string);
    }

    public void replaceNumber(float number){
        String string = String.valueOf(number);
        replace(R.string.token_number,string);
    }

    public void replaceNumber(double number){
        String string = String.valueOf(number);
        replace(R.string.token_number,string);
    }

    public static String formatPhoneNumber(final String unformatted) {

        //Convert the string into a list of characters
        List<Character> charList = new ArrayList<>(17);
        for(int i = 0; i < unformatted.length(); ++i) {
            charList.add(i, unformatted.charAt(i));
        }

        //Create a container to contain character removal indices
        //Init all valuess to -1
        int index = 0;
        int [] removeIndices = new int[charList.size()];
        for(int i = 0; i < removeIndices.length; ++i)
            removeIndices[i] = -1;

        //Get indices of all non-digit characters
        for(int i = 0; i < charList.size(); ++i) {
            if(!Character.isDigit(charList.get(i))) {
                //Save the index
                removeIndices[index++] = i;
            }
        }

        //Clean non-digit characters out of charList
        for(int i = charList.size() - 1; i >= 0; --i) {
            if(removeIndices[i] > -1) {
                charList.remove(removeIndices[i]);
            }
        }

        //Build formatted phone number string
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < charList.size(); ++i) {
            //Add non-digit characters in appropriate places.
            if(i == 0)
                builder.append('+');

            if(i == 1 || i == 4 || i == 7)
                builder.append(Character.toChars(8212)); //em dash

            builder.append(charList.get(i));
        }

        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    @NonNull
    @Override
    public String toString() {
        return phrase;
    }

    public Spanned toHtmlString() {
        return Html.fromHtml(phrase);
    }

}
