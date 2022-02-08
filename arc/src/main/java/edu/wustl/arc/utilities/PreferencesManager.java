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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import edu.wustl.arc.api.tests.data.BaseData;
import edu.wustl.arc.study.PathSegment;
import edu.wustl.arc.study.PathSegmentTypeAdapter;
import edu.wustl.arc.time.DateTimeTypeAdapter;
import edu.wustl.arc.time.LocalDateTypeAdapter;
import edu.wustl.arc.time.LocalTimeTypeAdapter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {

    private static PreferencesManager instance;
    private final SharedPreferences sharedPreferences;
    private static final String tag = "PreferencesManager";
    Gson objectGson;

    private PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName()+".prefs", Context.MODE_PRIVATE);
        buildObjectGson();
    }

    public static synchronized void initialize(Context context) {
        instance = new PreferencesManager(context);
    }

    public static synchronized PreferencesManager getInstance() {
        return instance;
    }

    private void buildObjectGson(){
        objectGson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriAdapter())
                .registerTypeAdapter(List.class, new ListTypeAdapter())
                .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(PathSegment.class,new PathSegmentTypeAdapter())
                .registerTypeAdapter(BaseData.class, new BaseDataTypeAdapter())
                .create();
    }

    public Gson getGson() {
        return objectGson;
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }


    @SuppressLint("ApplySharedPref")
    public void removeImmediately(String key) {
        sharedPreferences.edit().remove(key).commit();
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public void removeAll() {
        sharedPreferences.edit().clear().commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public double getDouble(String key, double defValue) {
        if(contains(key)){
            return getObject(key, double.class);
        }
        return defValue;
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }
    
    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void putDouble(String key, double value) {
        putObject(key,value);
    }

    public void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    // Immediately commit preferences ONLY in instances where it is time-sensitive.
    @SuppressLint("ApplySharedPref")
    public void putStringImmediately(String key, String value) {
        if(value==null){
            Log.i(tag,"invalid string, failed to save");
            throw new RuntimeException("tried to save a null string");
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    public void putString(String key, String value) {
        if(value==null){
            Log.i(tag,"invalid string, failed to save");
            throw new RuntimeException("tried to save a null string");
        }
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void putObject(String key, Object object) {
        if(object==null){
            Log.i(tag,"invalid object, failed to save");
            throw new RuntimeException("tried to save a null object");
        }
        String json = objectGson.toJson(object);
        sharedPreferences.edit().putString(key, json).apply();
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String json = sharedPreferences.getString(key, "{}");
        return objectGson.fromJson(json, clazz);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        List<T> list = null;
        Type listType = new TypeToken<ArrayList<T>>(){}.getType();
        String json = sharedPreferences.getString(key, "{}");
        if(!json.equals("{}")){
            list  =  objectGson.fromJson(json, listType);
        }
        if(list==null){
            list = new ArrayList<>();
        }
        return list;
    }

    /*
    public <T> void putList(String key, List<T> objects) {
        Type listType = new TypeToken<List<T>>(){}.getType();
        String json = objectGson.toJson(objects, listType);
        sharedPreferences.edit().putString(key, json).apply();
    }
    */

    public <T> T getObject(String key, Type type) {
        String json = sharedPreferences.getString(key, "{}");
        return objectGson.fromJson(json, type);
    }

    private class UriAdapter extends TypeAdapter<Uri> {
        @Override
        public void write(JsonWriter out, Uri uri) throws IOException {
            if (uri != null) {
                out.value(uri.toString());
            }
            else {
                out.nullValue();
            }
        }

        @Override
        public Uri read(JsonReader in) throws IOException {
            return Uri.parse(in.nextString());
        }
    }

    public class ListTypeAdapter implements JsonDeserializer<List> {

        @Override
        public List deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List list = new ArrayList();
            Type type = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];

            if (json.isJsonArray()) {
                for (JsonElement element : json.getAsJsonArray()) {
                    list.add(context.deserialize(element,type));
                }
            }

            return list;
        }

    }

    public class BaseDataTypeAdapter implements JsonDeserializer<BaseData>, JsonSerializer<BaseData>
    {
        @Override
        public BaseData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject obj = json.getAsJsonObject();


            try {
                Class actualClass = null;
                actualClass = Class.forName(obj.get("actual_class").getAsString());
                Object data = objectGson.fromJson(obj.get("data"), actualClass);

                if(BaseData.class.isAssignableFrom(data.getClass()))
                {
                    return (BaseData) data;
                }

            } catch (ClassNotFoundException e) {

                e.printStackTrace();
            }


            return new BaseData();
        }

        @Override
        public JsonElement serialize(BaseData src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject result = new JsonObject();
            result.add("data", objectGson.toJsonTree(src));
            result.addProperty("actual_class",  src.getClass().getName());
            return result;
        }
    }

}
