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
package com.healthymedium.arc.time;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import android.util.Log;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.lang.reflect.Type;
import java.util.Locale;

public class LocalTimeTypeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    DateTimeFormatter formatter;

    public LocalTimeTypeAdapter(){
        formatter = new DateTimeFormatterBuilder()
                .appendPattern("h:mm a")
                .toFormatter()
                .withLocale(Locale.US);
    }

    @Override
    public JsonElement serialize(LocalTime src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString(formatter));
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        String string = json.getAsString();
        LocalTime time = null;

        // handle deserializing from default format
        if(string.contains(".000")){
            time = new LocalTime(string);
            return time;
        }

        try {
            time = LocalTime.parse(string,formatter);
        } catch (IllegalArgumentException e) {
            string = string
                    .replace("a.m.","AM")
                    .replace("p.m.","PM")
                    .replace("a. m.","AM")
                    .replace("p. m.","PM")
                    .replace("午前","AM")
                    .replace("午後","PM");
            time = LocalTime.parse(string,formatter);
        }
        return time;
    }

}
