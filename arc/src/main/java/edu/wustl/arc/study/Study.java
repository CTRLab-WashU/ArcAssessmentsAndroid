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

import android.content.Context;

import edu.wustl.arc.core.Application;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.utilities.PreferencesManager;
import edu.wustl.arc.utilities.VersionUtil;

import androidx.annotation.VisibleForTesting;

public class Study{

    public static final String TAG_INITIALIZED = "initialized";
    public static final String TAG_CONTACT_INFO = "ContactInfo";
    public static final String TAG_CONTACT_EMAIL = "ContactInfoEmail";


    protected static Study instance;
    protected static boolean valid;
    protected Context context;

    static StateMachine stateMachine;
    static Participant participant;

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

    public void checkRegistrations(){
        if(participant==null){
            participant = new Participant();
        }
        if(stateMachine==null){
            stateMachine = new StateMachine();
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

            PreferencesManager.getInstance().putBoolean(TAG_INITIALIZED,true);

        }  else {
            participant.load();
            stateMachine.load();
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

    public static StateMachine getStateMachine(){
        return stateMachine;
    }

    // commonly used accessors ---------------------------------------------------------------------

    public static TestCycle getCurrentTestCycle() {
        return participant.getCurrentTestCycle();
    }

    public static TestSession getCurrentTestSession() {
        return participant.getCurrentTestSession();
    }

    /**
     * @param session will be set as the current session for all intents and purposes
     */
    public static void setMostRecentTestSession(TestSession session) {
        participant.setMostRecentTestSession(session);
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
}
