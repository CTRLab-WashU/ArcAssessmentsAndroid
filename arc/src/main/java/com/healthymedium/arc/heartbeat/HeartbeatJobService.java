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
package com.healthymedium.arc.heartbeat;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.healthymedium.arc.core.Application;
import android.util.Log;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.StateMachine;

import static com.healthymedium.arc.study.Study.getRestClient;

public class HeartbeatJobService extends JobService {

    private static final String tag = "HeartbeatJobService";
    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.params = jobParameters;
        Log.i(tag,"onStartJob");

        if(!Config.REST_HEARTBEAT){
            if(!Application.getInstance().isVisible()) {
                checkUploadQueue();
            }
            return true;
        }

        Log.i(tag,"trying heartbeat");
        String participantId = Study.getParticipant().getId();
        HeartbeatManager.getInstance().tryHeartbeat(getRestClient(), participantId, new HeartbeatManager.Listener() {
            @Override
            public void onSuccess(boolean tried) {
                if(!Application.getInstance().isVisible()) {
                    checkUploadQueue();
                }
            }

            @Override
            public void onFailure() {
                jobFinished(params,false);
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(tag,"onStopJob");
        return false;
    }

    private void checkUploadQueue(){
        Log.i(tag,"checkUploadQueue");
        RestClient client = Study.getRestClient();
        if(client.isUploadQueueEmpty()){
            checkState();
            jobFinished(params,false);
            return;
        }

        client.addUploadListener(new RestClient.UploadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {
                Study.getRestClient().removeUploadListener(this);
                checkState();
                jobFinished(params,false);
            }
        });
        client.popQueue();
    }

    private void checkState(){
        Log.i(tag,"checkState");
        StateMachine stateMachine = Study.getStateMachine();
        stateMachine.decidePath();
        stateMachine.save(true);
    }








}