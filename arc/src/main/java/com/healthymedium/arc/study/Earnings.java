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

import androidx.annotation.Nullable;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.api.models.EarningDetails;
import com.healthymedium.arc.api.models.EarningOverview;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;

public class Earnings {

    public static final String TWENTY_ONE_SESSIONS = "21-sessions";
    public static final String TWO_A_DAY = "2-a-day";
    public static final String FOUR_OUT_OF_FOUR = "4-out-of-4";
    public static final String TEST_SESSION = "test-session";

    private EarningOverview overview;
    private int overviewRefresh;
    private DateTime overviewUpdateTime;
    Listener overviewListener;

    private String prevWeeklyTotal = new String();
    private String prevStudyTotal = new String();

    private EarningDetails details;
    private int detailsRefresh;
    private DateTime detailsUpdateTime;
    Listener detailsListener;

    public Earnings(){
        overviewRefresh = 0;
        detailsRefresh = 0;
    }

    public void linkAgainstRestClient(){
        RestClient client = Study.getRestClient();
        client.addUploadListener(new RestClient.UploadListener() {
            @Override
            public void onStart() {
                invalidate();
            }

            @Override
            public void onStop() {
                if(Study.getRestClient().isUploadQueueEmpty()) {
                    internalRefreshOverview();
                    internalRefreshDetails();
                }
            }
        });
    }

    public void refreshOverview(Listener listener) {
        overviewListener = listener;
        internalRefreshOverview();
    }

    private void internalRefreshOverview() {

        RestClient client = Study.getRestClient();

        overviewRefresh = -1;

        client.getEarningOverview(new RestClient.Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                overviewRefresh = 1;
                overview = response.getOptionalAs("earnings",EarningOverview.class);
                overviewUpdateTime = DateTime.now();
                Collections.sort(overview.goals,new EarningOverview.GoalComparator());
                if(overviewListener!=null) {
                    overviewListener.onSuccess();
                    overviewListener = null;
                }
            }

            @Override
            public void onFailure(RestResponse response) {
                overviewRefresh = 0;
                if(overviewListener!=null) {
                    overviewListener.onFailure();
                    overviewListener = null;
                }
            }
        });
    }

    public boolean isRefreshingOverview(){
        return overviewRefresh==-1;
    }

    public boolean hasCurrentOverview(){
        // Return false to always refresh locally
        return false;
    }

    public EarningOverview getOverview(){
        return overview;
    }

    public void setOverview(EarningOverview overview){
        this.overview = overview;
        overviewRefresh = 1;
        overviewUpdateTime = DateTime.now();
    }

    @Nullable
    public DateTime getOverviewRefreshTime(){
        return overviewUpdateTime;
    }

    public String getPrevWeeklyTotal() {
        return prevWeeklyTotal;
    }

    public String getPrevStudyTotal() {
        return prevStudyTotal;
    }

    public void refreshDetails(Listener listener){
        detailsListener = listener;
        internalRefreshDetails();
    }

    private void internalRefreshDetails(){

        RestClient client = Study.getRestClient();

        detailsRefresh = -1;

        client.getEarningDetails(new RestClient.Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                detailsRefresh = 1;
                details = response.getOptionalAs("earnings",EarningDetails.class);
                detailsUpdateTime = DateTime.now();
                if(details.cycles==null) {
                    details.cycles = new ArrayList<>();
                }
                if(detailsListener!=null){
                    detailsListener.onSuccess();
                    detailsListener = null;
                }
            }

            @Override
            public void onFailure(RestResponse response) {
                detailsRefresh = 0;
                if(detailsListener!=null){
                    detailsListener.onFailure();
                    detailsListener = null;
                }
            }
        });
    }

    public boolean isRefreshingDetails(){
        return detailsRefresh==-1;
    }

    public boolean hasCurrentDetails(){
        // return false to always refresh locally
        return false;
    }

    public EarningDetails getDetails(){
        return details;
    }

    public void setDetails(EarningDetails details){
        this.details = details;
        detailsRefresh = 1;
        detailsUpdateTime = DateTime.now();
    }

    @Nullable
    public DateTime getDetailsRefreshTime(){
        return detailsUpdateTime;
    }

    public void invalidate(){
        if(overview!=null){
            prevStudyTotal = overview.total_earnings;
            prevWeeklyTotal = overview.cycle_earnings;
        }
        overviewRefresh = 0;
        detailsRefresh = 0;
    }

    public interface Listener {
        void onSuccess();
        void onFailure();
    }

}
