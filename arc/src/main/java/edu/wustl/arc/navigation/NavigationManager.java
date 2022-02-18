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

import androidx.fragment.app.FragmentManager;

import edu.wustl.arc.core.ArcBaseFragment;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.misc.TransitionSet;

public class NavigationManager {

    private static NavigationManager instance;
    private NavigationController defaultController;
    private NavigationController registeredController;

    private NavigationManager() {
        // Make empty constructor private
    }

    public static synchronized void initialize(final FragmentManager fragmentManager) {
        instance = new NavigationManager();
        instance.defaultController = new NavigationController(fragmentManager,R.id.content_frame);
    }

    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NavigationManager.class.getSimpleName() + " is not initialized, call initialize(..) method first.");
        }
        return instance;
    }

    public FragmentManager getFragmentManager() {
        return getController().getFragmentManager();
    }

    public NavigationController.Listener getListener() {
        return getController().getListener();
    }

    public void setListener(NavigationController.Listener listener) {
        getController().setListener(listener);
    }

    public void setDefaultListener(NavigationController.Listener listener) {
        defaultController.setListener(listener);
    }

    public void open(ArcBaseFragment fragment) {
        getController().open(fragment);
    }

    public void open(ArcBaseFragment fragment, TransitionSet transition) {
        getController().open(fragment,transition);
    }

    public void popBackStack() {
        getController().popBackStack();
    }

    public int getBackStackEntryCount() {
        return getController().getBackStackEntryCount();
    }

    public void clearBackStack() {
        getController().clearBackStack();
    }

    public ArcBaseFragment getCurrentFragment(){
        return getController().getCurrentFragment();
    }

    public void setController(NavigationController controller) {
        registeredController = controller;
    }

    public NavigationController getController() {
        if(registeredController!=null) {
            return registeredController;
        }
        return defaultController;
    }

    public void removeController() {
        registeredController = null;
    }

}
