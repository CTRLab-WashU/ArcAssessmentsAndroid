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
package edu.wustl.arc.paths.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.misc.TransitionSet;
import edu.wustl.arc.paths.informative.EarningsDetailsScreen;
import edu.wustl.arc.ui.BottomNavigationView;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.navigation.SlidingNavigationController;
import edu.wustl.arc.navigation.NavigationManager;

import static edu.wustl.arc.ui.BottomNavigationView.TAG_EARNINGS;
import static edu.wustl.arc.ui.BottomNavigationView.TAG_HOME;
import static edu.wustl.arc.ui.BottomNavigationView.TAG_PROGRESS;
import static edu.wustl.arc.ui.BottomNavigationView.TAG_RESOURCES;

@SuppressLint("ValidFragment")
public class LandingScreen extends BaseFragment {

  SlidingNavigationController navigationController;
  BottomNavigationView bottomNavigationView;
  TransitionSet transitionSet;

  HomeScreen home;
  ProgressScreen progress;
  EarningsScreen earnings;
  ResourcesScreen resources;

  public LandingScreen() {

  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_landing, container, false);

    bottomNavigationView = view.findViewById(R.id.navigation);
    transitionSet = new TransitionSet();

    home = new HomeScreen();
    progress = new ProgressScreen();
    earnings = new EarningsScreen();
    resources = new ResourcesScreen();

    home.setBottomNavigationView(bottomNavigationView);

    FragmentManager fragmentManager = getChildFragmentManager();
    navigationController = new SlidingNavigationController(fragmentManager,R.id.landing_frame);
    navigationController.addFragmentToSet(home);
    navigationController.addFragmentToSet(progress);
    navigationController.addFragmentToSet(earnings);
    navigationController.addFragmentToSet(new EarningsDetailsScreen());
    navigationController.addFragmentToSet(resources);
    navigationController.open(home,transitionSet);

    bottomNavigationView.setHomeSelected();
    bottomNavigationView.setListener(new BottomNavigationView.Listener() {
      @Override
      public void onSelected(int tag) {
        switch (tag) {
          case TAG_HOME:
            navigationController.open(home,transitionSet);
            break;
          case TAG_PROGRESS:
            navigationController.open(progress,transitionSet);
            break;
          case TAG_EARNINGS:
            navigationController.open(earnings,transitionSet);
            break;
          case TAG_RESOURCES:
            navigationController.open(resources,transitionSet);
            break;
        }
      }
    });

    return view;
  }

  @Override
  public void onPause() {
    super.onPause();
    NavigationManager.getInstance().removeController();
  }

  @Override
  public void onResume() {
    super.onResume();
    NavigationManager.getInstance().setController(navigationController);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    view.setPadding(0,0,0,0);
  }

}
