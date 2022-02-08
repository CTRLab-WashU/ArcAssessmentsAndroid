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
package edu.wustl.arc.core;
import edu.wustl.arc.study.TestVariant;

public class Config {

    public static String FLAVOR_DEV = "dev";
    public static String FLAVOR_QA = "qa";
    public static String FLAVOR_PROD = "prod";

    // Core
    public static boolean CHOOSE_LOCALE = false;

    // Rest API
    public static String REST_ENDPOINT = "http://thinkhealthymedium.com/"; // where we send the data
    public static boolean REST_BLACKHOLE = false; // used for debugging, keeps all rest calls from reaching the outside world
    public static boolean REST_HEARTBEAT = true; // heartbeat will fail if blackhole is enabled
    public static boolean CHECK_SESSION_INFO = false; // if true, an api is called after registration to check for existing session info
    public static boolean CHECK_CONTACT_INFO = false; // if true, an api is called after registration to check for contact info
    public static boolean CHECK_PROGRESS_INFO = false; //

    public static boolean LOGIN_USE_AUTH_DETAILS = false; //
    //
    public static boolean ENABLE_SIGNATURES = false; // if true, signatures will be required before and after every test
    public static boolean ENABLE_VIGNETTES = false; // if true, a notification reminding the user of the upcoming visit will appear one month, week, and day from the start date
    public static boolean ENABLE_LEGACY_PRICE_SETS = false; // if true, the PriceManager will continue to use a long-standing, but incorrect, method to determine the price set for a given test session.
    public static boolean ENABLE_EARNINGS = false;
    public static boolean IS_REMOTE = false; // if true, user will be required to state whether or not they commit to the study
    public static boolean EXPECTS_2FA_TEXT = true; // if true, user will receive a 2FA text message, if false use site code
    public static boolean USE_HELP_SCREEN = true; // if true, uses the actual HelpScreen for the help buttons instead of the contact screen
    public static boolean REPORT_STUDY_INFO = false; // send study info to analytics

    //Debug
    public static boolean DEBUG_DIALOGS = false; // click the header on most screens a couple times and a debug dialog will appear

    // Test Variants
    public static TestVariant.Grid TEST_VARIANT_GRID = TestVariant.Grid.V1;
    public static TestVariant.Price TEST_VARIANT_PRICE = TestVariant.Price.Original;
    public static TestVariant.Symbol TEST_VARIANT_SYMBOL = TestVariant.Symbol.V1;

    // Runtime
    public static final String INTENT_EXTRA_OPENED_FROM_NOTIFICATION = "OPENED_FROM_NOTIFICATION";
    public static boolean OPENED_FROM_NOTIFICATION = false;

    public static final String INTENT_EXTRA_OPENED_FROM_VISIT_NOTIFICATION = "OPENED_FROM_VISIT_NOTIFICATION";
    public static boolean OPENED_FROM_VISIT_NOTIFICATION = false;



}
