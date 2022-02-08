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
package com.healthymedium.arc.api;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.library.R;

import java.io.IOException;

import okhttp3.ResponseBody;

public class RestResponse {

    public int code;
    public boolean successful;
    public JsonObject optional = new JsonObject();
    public JsonObject errors = new JsonObject();

    public <T> T getOptionalAs(String key, Class<T> tClass){
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setLenient()
                .create();
        return gson.fromJson(optional.get(key), tClass);
    }

    public <T> T getOptionalAs(Class<T> tClass){
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setLenient()
                .create();
        return gson.fromJson(optional, tClass);
    }

    public static RestResponse fromRetrofitResponse(retrofit2.Response<ResponseBody> retrofitResponse){
        RestResponse response = new RestResponse();

        if (response != null) {
            response.code = retrofitResponse.code();
            String responseData = "{}";
            try {
                if(retrofitResponse.isSuccessful()){
                    responseData = retrofitResponse.body().string();
                } else {
                    responseData = retrofitResponse.errorBody().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
                response.errors.addProperty("show", Application.getInstance().getResources().getString(R.string.login_error3));
                response.errors.addProperty("format","Invalid response format received");
                response.successful = retrofitResponse.isSuccessful();
                return response;
            }

            JsonObject json;
            try {
                json = new JsonParser().parse(responseData).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                response.errors.addProperty("show", Application.getInstance().getResources().getString(R.string.login_error3));
                response.errors.addProperty("unknown","Server Error "+response.code);
                return response;
            }

            JsonObject jsonResponse = json.getAsJsonObject("response");
            response.successful = jsonResponse.get("success").getAsBoolean();

            jsonResponse.remove("success");
            response.optional = jsonResponse;

            JsonObject jsonErrors = json.getAsJsonObject("errors");
            for(String key : jsonErrors.keySet()) {
                response.errors.add(key,jsonErrors.get(key));
            }
        }

        return response;
    }

    public static RestResponse fromRetrofitFailure(@Nullable Throwable throwable) {
        RestResponse response = new RestResponse();
        response.successful = false;
        response.code = -1;
        if(throwable!=null) {
            response.errors.addProperty("show", Application.getInstance().getResources().getString(R.string.login_error3));
            response.errors.addProperty(throwable.getClass().getSimpleName(),throwable.getMessage());
        }
        return response;
    }
}
