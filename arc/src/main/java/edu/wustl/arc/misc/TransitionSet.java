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
package edu.wustl.arc.misc;

import androidx.annotation.AnimRes;

import edu.wustl.arc.assessments.R;

public class TransitionSet {

    @AnimRes
    public int enter = 0;
    @AnimRes
    public int exit = 0;
    @AnimRes
    public int popEnter = 0;
    @AnimRes
    public int popExit = 0;



    public static TransitionSet getFadingDefault(){
        return getFadingDefault(true);
    }

    public static TransitionSet getFadingDefault(boolean animateEntry){
        TransitionSet set = new TransitionSet();
        if(animateEntry) {
            set.enter = R.anim.fade_in;
            set.popEnter =  R.anim.fade_in;
        }
        set.exit = R.anim.fade_out;
        set.popExit =  R.anim.fade_out;
        return set;
    }

    public static TransitionSet getSlidingDefault(){
        return getSlidingDefault(true);
    }

    public static TransitionSet getSlidingDefault(boolean animateEntry){
        TransitionSet set = new TransitionSet();
        if(animateEntry) {
            set.enter = R.anim.slide_in_right;
            set.popEnter =  R.anim.slide_in_left;
        }
        set.exit = R.anim.slide_out_left;
        set.popExit =  R.anim.slide_out_right;
        return set;
    }

    public static TransitionSet getSlidingInFadingOut(){
        TransitionSet set = new TransitionSet();
        set.enter = R.anim.slide_in_right;
        set.popEnter =  R.anim.slide_in_left;
        set.exit = R.anim.fade_out;
        set.popExit =  R.anim.fade_out;
        return set;
    }

    public static TransitionSet getFadingInSlidingOut(){
        TransitionSet set = new TransitionSet();
        set.enter = R.anim.fade_in;
        set.popEnter =  R.anim.fade_in;
        set.exit = R.anim.slide_out_left;
        set.popExit =  R.anim.slide_out_right;
        return set;
    }


}
