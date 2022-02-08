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
package edu.wustl.arc.api;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestAPI {

    @POST("device-heartbeat")
    Call<ResponseBody> sendHeartbeat(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("device-registration")
    Call<ResponseBody> registerDevice(@Body JsonObject body);

    @POST("request-auth-details")
    Call<ResponseBody> requestAuthDetails(@Body JsonObject body);

    @POST("request-confirmation-code")
    Call<ResponseBody> requestVerificationCode(@Body JsonObject body);

    @GET("get-contact-info")
    Call<ResponseBody> getContactInfo(@Query("device_id") String deviceId);

    @GET("get-session-info")
    Call<ResponseBody> getSessionInfo(@Query("device_id") String deviceId);

    @GET("get-test-schedule")
    Call<ResponseBody> getTestSchedule(@Query("device_id") String deviceId);

    @GET("get-wake-sleep-schedule")
    Call<ResponseBody> getWakeSleepSchedule(@Query("device_id") String deviceId);

    @POST("signature-data")
    Call<ResponseBody> submitSignature(@Body RequestBody singatureData, @Query("device_id") String deviceId);

    @POST("submit-test")
    Call<ResponseBody> submitTest(@Query("device_id") String deviceId, @Body JsonObject test);

    @POST("submit-test-schedule")
    Call<ResponseBody> submitTestSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

    @POST("submit-wake-sleep-schedule")
    Call<ResponseBody> submitWakeSleepSchedule(@Query("device_id") String deviceId, @Body JsonObject body);

    // progress ------------------------------------------------------------------------------------

    @GET("study-summary")
    Call<ResponseBody> getStudySummary(@Query("device_id") String deviceId);

    @GET("study-progress")
    Call<ResponseBody> getStudyProgress(@Query("device_id") String deviceId);

    @GET("cycle-progress")
    Call<ResponseBody> getCycleProgress(@Query("device_id") String deviceId, @Query("cycle") Integer cycle);

    @GET("day-progress")
    Call<ResponseBody> getDayProgress(@Query("device_id") String deviceId, @Query("cycle") Integer cycle, @Query("day") Integer day);

    // earnings ------------------------------------------------------------------------------------

    @GET("earning-overview")
    Call<ResponseBody> getEarningOverview(@Query("device_id") String deviceId, @Query("cycle") Integer cycle, @Query("day") Integer day);

    @GET("earning-details")
    Call<ResponseBody> getEarningDetails(@Query("device_id") String deviceId);

}
