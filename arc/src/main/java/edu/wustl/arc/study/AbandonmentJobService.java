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

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class AbandonmentJobService extends JobService {

    static private final String tag = "AbandonmentJobService";
    public static final int jobId = 8800;

    @Override
    public boolean onStartJob(JobParameters jobParameters){
        Log.i(tag,"Running abandonment check");

        // First, check that we actually have a test running, and that we've hit our timeout for abandonment
        // Then, mark it abandoned, and send the data

        Participant participant = Study.getParticipant();
        StateMachine stateMachine = Study.getStateMachine();

        if(participant.isCurrentlyInTestSession() && participant.checkForTestAbandonment()){
            Log.i(tag, participant.getCurrentTestSession().getTestData().toString());
            stateMachine.abandonTest();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters){
        return false;
    }

    public static void scheduleSelf(Context context){
        Log.i(tag,"Schedule abandonment job");

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);

        ComponentName serviceComponent = new ComponentName(context, AbandonmentJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);       // device should be idle
        builder.setRequiresCharging(false);         // we don't care if the device is charging or not
        builder.setPersisted(false);                // set persistent across reboots
        builder.setMinimumLatency(300000);          // minimum of 5 minutes before this service gets called
        builder.setOverrideDeadline(600000);        // maximum of 10 minutes before this service gets called
        builder.setBackoffCriteria(300000, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setRequiresBatteryNotLow(false);
            builder.setRequiresStorageNotLow(false);
        }

        jobScheduler.schedule(builder.build());
    }

    public static void unscheduleSelf(Context context){
        Log.i(tag,"Cancelling abandonment job");
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }
}
