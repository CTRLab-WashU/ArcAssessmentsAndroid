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
package com.healthymedium.arc.navigation;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;

import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;

import java.util.ArrayList;
import java.util.List;

public class SlidingNavigationController extends NavigationController {

    List<BaseFragment> fragments = new ArrayList<>();
    int currentIndex;

    public SlidingNavigationController(FragmentManager fragmentManager, @IdRes int containerViewId) {
        super(fragmentManager,containerViewId);
    }

    public void setFragmentSet(List<BaseFragment> fragments) {
        this.fragments = fragments;
    }

    public void addFragmentToSet(BaseFragment fragment) {
        this.fragments.add(fragment);
    }

    @Override
    public void open(BaseFragment fragment) {
        if(!fragments.contains(fragment)) {
            fragments.add(fragment);
        }
        int index = fragments.indexOf(fragment);
        if(index<currentIndex) {
            openLeft(fragment);
        } else {
            openRight(fragment);
        }
        currentIndex = index;
    }

    private void openLeft(BaseFragment fragment) {
        TransitionSet set = new TransitionSet();
        set.enter = R.anim.slide_in_left;
        set.popEnter =  R.anim.slide_in_right;
        set.exit = R.anim.slide_out_right;
        set.popExit =  R.anim.slide_out_left;
        open(fragment,set);
    }

    private void openRight(BaseFragment fragment) {
        TransitionSet set = new TransitionSet();
        set.enter = R.anim.slide_in_right;
        set.popEnter =  R.anim.slide_in_left;
        set.exit = R.anim.slide_out_left;
        set.popExit =  R.anim.slide_out_right;
        open(fragment,set);
    }



}
