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

import android.content.Context;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.Device;
import com.healthymedium.arc.heartbeat.HeartbeatManager;
import com.healthymedium.arc.utilities.MigrationUtil;
import com.healthymedium.arc.utilities.PreferencesManager;
import com.healthymedium.arc.utilities.VersionUtil;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;

import androidx.annotation.VisibleForTesting;

public class Study{

    public static final String TAG_INITIALIZED = "initialized";
    public static final String TAG_CONTACT_INFO = "ContactInfo";
    public static final String TAG_CONTACT_EMAIL = "ContactInfoEmail";


    protected static Study instance;
    protected static boolean valid;
    protected Context context;

    static Scheduler scheduler;
    static StateMachine stateMachine;
    static Participant participant;
    static RestClient restClient;
    static MigrationUtil migrationUtil;
    static PrivacyPolicy privacyPolicy;

    public static synchronized void initialize(Context context) {
        if(instance==null) {
            instance = new Study();
        }
        instance.context = context;
    }

    /**
     * Only to be used with the validation app, always overwrites the study
     * @param context must be app context
     */
    @VisibleForTesting
    public static synchronized void initializeValidationAppOnly(Context context) {
        instance = new Study();
        instance.context = context;
    }

    public static synchronized Study getInstance() {
        return instance;
    }

    protected Study() {

    }

    public static boolean isValid(){
        if(instance==null){
            return false;
        }
        return valid;
    }

    // class registrations -------------------------------------------------------------------------

    public boolean registerParticipantType(Class tClass) {
        return registerParticipantType(tClass,false);
    }

    public boolean registerParticipantType(Class tClass, boolean overwrite){
        if(participant!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!Participant.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            participant = (Participant) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean registerRestApi(Class clientClass) {
        return registerRestApi(clientClass,false);
    }

    public boolean registerRestApi(Class clientClass, boolean overwrite){
        if(restClient!=null && !overwrite){
            return false;
        }
        if(clientClass==null){
            return false;
        }
        if(!RestClient.class.isAssignableFrom(clientClass)){
            return false;
        }
        try {
            restClient = (RestClient) clientClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Set a custom rest client directly without using reflection
     * Use in case of custom constructor vs a no-arg constructor
     * @param client to be used for the study
     * @return true if the client was overwritten, false otherwise
     */
    public static boolean setRestClient(RestClient client, boolean overwrite){
        if(restClient!=null && !overwrite){
            return false;
        }
        if(client == null){
            return false;
        }
        restClient = client;
        return true;
    }

    public boolean registerRestApi(Class clientClass, Class apiClass){
        return registerRestApi(clientClass, apiClass,false);
    }

    public boolean registerRestApi(Class clientClass, Class apiClass, boolean overwrite){
        if(restClient!=null && !overwrite){
            return false;
        }
        if(clientClass==null){
            return false;
        }
        if(!RestClient.class.isAssignableFrom(clientClass)){
            return false;
        }
        try {
            restClient = (RestClient) clientClass.getDeclaredConstructor(Class.class).newInstance(apiClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return true;
    }

    public  boolean registerStateMachine(Class tClass) {
        return registerStateMachine(tClass,false);
    }

    public  boolean registerStateMachine(Class tClass, boolean overwrite){
        if(stateMachine!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!StateMachine.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            stateMachine = (StateMachine) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean registerScheduler(Class tClass){
        return registerScheduler(tClass,false);
    }

    public boolean registerScheduler(Class tClass, boolean overwrite){
        if(scheduler!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!Scheduler.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            scheduler = (Scheduler) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean registerMigrationUtil(Class tClass){
        return registerMigrationUtil(tClass,false);
    }

    public boolean registerMigrationUtil(Class tClass, boolean overwrite){
        if(migrationUtil!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!MigrationUtil.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            migrationUtil = (MigrationUtil) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean registerPrivacyPolicy(Class tClass){
        return registerPrivacyPolicy(tClass,false);
    }

    public boolean registerPrivacyPolicy(Class tClass, boolean overwrite){
        if(privacyPolicy!=null && !overwrite){
            return false;
        }
        if(tClass==null){
            return false;
        }
        if(!PrivacyPolicy.class.isAssignableFrom(tClass)){
            return false;
        }
        try {
            privacyPolicy = (PrivacyPolicy) tClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void checkRegistrations(){
        if(participant==null){
            participant = new Participant();
        }
        if(scheduler==null){
            scheduler = new Scheduler();
        }
        if(stateMachine==null){
            stateMachine = new StateMachine();
        }
        if(restClient==null){
            restClient = new RestClient(null);
        }
        if(migrationUtil==null){
            migrationUtil = new MigrationUtil();
        }
        if(privacyPolicy==null){
            privacyPolicy = new PrivacyPolicy();
        }
    }

    //  ----------------------------------------------------------------------------

    public void load(){

        checkRegistrations();

        boolean initialized = PreferencesManager.getInstance().getBoolean(TAG_INITIALIZED,false);
        if(!initialized){
            participant.initialize();
            participant.save();

            stateMachine.initialize();
            stateMachine.save();

            PreferencesManager.getInstance().putLong(MigrationUtil.TAG_VERSION_LIB,VersionUtil.getCoreVersionCode());
            PreferencesManager.getInstance().putLong(MigrationUtil.TAG_VERSION_APP,VersionUtil.getAppVersionCode());
            PreferencesManager.getInstance().putBoolean(TAG_INITIALIZED,true);
            HeartbeatManager.getInstance().scheduleHeartbeat();

        }  else {
            migrationUtil.checkForUpdate();
            migrationUtil = null; // not needed after this

            participant.load();
            stateMachine.load();

            if(participant.isScheduleCorrupted()) {
                boolean fixed = getScheduler().fixCorruptedSchedule(participant);
                if(fixed) {
                    participant.save();
                    if(!getCurrentTestCycle().hasStarted()) {
                        getScheduler().unscheduleNotifications(getCurrentTestCycle());
                        getScheduler().scheduleNotifications(getCurrentTestCycle(), false);
                    }
                    getRestClient().submitTestSchedule();
                    Log.w("Schedule Corruption","Found schedule corruption, was able to fix it");
                } else {
                    Log.w("Schedule Corruption","Found schedule corruption, was not able to fix it");
                }
            }
        }

        if(Config.ENABLE_EARNINGS){
            participant.getEarnings().linkAgainstRestClient();
        }

        if(Config.REPORT_STUDY_INFO){
        }

        valid = true;
    }

    public void run(){
        stateMachine.decidePath();
        stateMachine.setupPath();
        participant.save();
        stateMachine.save();
    }

    // registered class getters --------------------------------------------------------------------

    public static Participant getParticipant(){
        return participant;
    }

    public static Scheduler getScheduler(){
        return scheduler;
    }

    public static StateMachine getStateMachine(){
        return stateMachine;
    }

    public static RestClient getRestClient(){
        return restClient;
    }

    public static PrivacyPolicy getPrivacyPolicy() { return privacyPolicy; }

    // commonly used accessors ---------------------------------------------------------------------

    public static TestCycle getCurrentTestCycle() {
        return participant.getCurrentTestCycle();
    }

    public static TestSession getCurrentTestSession() {
        return participant.getCurrentTestSession();
    }

    public static TestDay getCurrentTestDay() {
        return participant.getCurrentTestDay();
    }

    public static PathSegmentData getCurrentSegmentData() {
        try {
            return stateMachine.cache.segments.get(0).dataObject;
        } catch (IndexOutOfBoundsException e) {
            Application.getInstance().restart();
            return new PathSegmentData();
        }
    }

    public static void setCurrentSegmentData(Object object) {
        try {
            stateMachine.cache.segments.get(0).dataObject = (PathSegmentData) object;
        } catch (IndexOutOfBoundsException e) {
            Application.getInstance().restart();
        }
    }

    public static PathSegment getCurrentSegment() {
        try {
            return stateMachine.cache.segments.get(0);
        } catch (IndexOutOfBoundsException e) {
            Application.getInstance().restart();
            return new PathSegment();
        }
    }

    // operations ----------------------------------------------------------------------------------

    // try to open next fragment in segment
    // if at the end of segment, start next
    // if no more segments, decide path
    public static boolean openNextFragment(){
        return stateMachine.openNext();
    }

    public static boolean skipToNextSegment(){
        return stateMachine.skipToNextSegment();
    }

    public static boolean openNextFragment(int skips){
        return stateMachine.openNext(skips);
    }

    public static boolean openPreviousFragment() {
        return stateMachine.openPrevious();
    }

    public static boolean openPreviousFragment(int skips){
        return stateMachine.openPrevious(skips);
    }


    // Mark the current test as abandoned, and setup the stateMachine so that it knows what to
    // display next.
    
    public static void abandonTest()
    {
        //TODO: move the dialog to somewhere further up this chain
//        LoadingDialog dialog = new LoadingDialog();
//        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");

//        dialog.dismiss();
        stateMachine.abandonTest();
        stateMachine.decidePath();
        stateMachine.setupPath();
        stateMachine.openNext();
    }

    public static void updateAvailability(int minWakeTime, int maxWakeTime)
    {
        stateMachine.setPathSetupAvailability(minWakeTime, maxWakeTime, true);
        stateMachine.openNext();
    }

    public static void updateAvailabilityOnboarding(int minWakeTime, int maxWakeTime)
    {
        stateMachine.setPathSetupAvailability(minWakeTime, maxWakeTime, false);
        stateMachine.openNext();
    }

    public static void adjustSchedule()
    {
        stateMachine.setPathAdjustSchedule();
        stateMachine.openNext();
    }
}
