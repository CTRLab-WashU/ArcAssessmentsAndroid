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
package edu.wustl.arc.navigation;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.misc.TransitionSet;

public class NavigationController {

    protected static String tag = "NavigationController";

    protected FragmentManager fragmentManager;
    protected Listener listener;

    protected int currentFragmentId = -1;
    protected int containerViewId = -1;

    public NavigationController(FragmentManager fragmentManager, @IdRes int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
    }

    public void removeListener() {
        listener = null;
    }

    public void open(ArcBaseFragment fragment) {
        if (fragmentManager != null) {
            TransitionSet transitions = fragment.getTransitionSet();
            String tag = fragment.getSimpleTag();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            transitions.enter,
                            transitions.exit,
                            transitions.popEnter,
                            transitions.popExit)
                    .replace(containerViewId, fragment,tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
            currentFragmentId = fragment.getId();
            if(listener!=null){
                listener.onOpen();
            }
        }
    }

    public void open(ArcBaseFragment fragment, TransitionSet transitions) {
        if (fragmentManager != null) {
            String tag = fragment.getSimpleTag();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            transitions.enter,
                            transitions.exit,
                            transitions.popEnter,
                            transitions.popExit)
                    .replace(containerViewId, fragment,tag)
                    .addToBackStack(tag)
                    .commitAllowingStateLoss();
            currentFragmentId = fragment.getId();
            if(listener!=null){
                listener.onOpen();
            }
        }
    }

    public void popBackStack() {
        if (fragmentManager != null) {
            fragmentManager.popBackStack();
            if(listener!=null){
                listener.onPopBack();
            }
        }
    }

    public int getBackStackEntryCount() {
        return fragmentManager.getBackStackEntryCount();
    }

    public void clearBackStack() {
        try {
            int count = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < count; i++) {
                int id = fragmentManager.getBackStackEntryAt(i).getId();
                fragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } catch (IllegalStateException e) {
        }
    }

    public ArcBaseFragment getCurrentFragment(){
        return (ArcBaseFragment) fragmentManager.findFragmentById(currentFragmentId);
    }

    public interface Listener {
        void onOpen();
        void onPopBack();
    }

}
