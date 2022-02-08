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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.lang.reflect.Type;

public class PathSegmentTypeAdapter implements JsonSerializer<PathSegment>, JsonDeserializer<PathSegment> {

    @Override
    public PathSegment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        PathSegment newSegment = new PathSegment();

        JsonObject obj = json.getAsJsonObject();


        if(obj.has("currentIndex"))
        {
            JsonElement currentIndex = obj.get("currentIndex");
            newSegment.currentIndex = currentIndex.getAsInt();
        }

        if(obj.has("dataObject"))
        {
            try {
                JsonElement dataObject = obj.get("dataObject");

                // Previous versions did not store the dataObject in this way, so we need to check and
                // make sure we're not trying to deserialize an old version (because this won't work!)
                if(dataObject.isJsonObject() && ((JsonObject)dataObject).has("data")) {
                    JsonObject d = dataObject.getAsJsonObject();
                    Class actualClass = null;
                    actualClass = Class.forName(d.get("actual_class").getAsString());
                    Object data = PreferencesManager.getInstance().getGson().fromJson(d.get("data"), actualClass);

                    if (PathSegmentData.class.isAssignableFrom(data.getClass())) {
                        newSegment.dataObject = (PathSegmentData) data;
                    }
                }
                else
                {
                    newSegment.dataObject = context.deserialize(dataObject, PathSegmentData.class);
                }
            }
            catch (ClassNotFoundException e) {

                e.printStackTrace();
            }
        }

        return newSegment;
    }

    @Override
    public JsonElement serialize(PathSegment pathSegment, Type srcType, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("currentIndex", new JsonPrimitive(pathSegment.currentIndex));
        if (pathSegment.dataObject != null) {


            JsonObject jsonDataObject = new JsonObject();
            jsonDataObject.add("data", PreferencesManager.getInstance().getGson().toJsonTree(pathSegment.dataObject));
            jsonDataObject.addProperty("actual_class",  pathSegment.dataObject.getClass().getName());

            result.add("dataObject", jsonDataObject);
        }
        return result;
    }

}
