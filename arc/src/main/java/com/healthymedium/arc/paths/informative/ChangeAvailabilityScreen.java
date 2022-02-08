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
package com.healthymedium.arc.paths.informative;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.healthymedium.arc.ui.Button;
import com.healthymedium.arc.font.Fonts;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.misc.TransitionSet;
import com.healthymedium.arc.study.Participant;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.study.TestCycle;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.utilities.ViewUtil;

import org.joda.time.DateTime;

public class ChangeAvailabilityScreen extends BaseFragment {

    String stringHeader;
    String changeDateHeader;

    boolean showChangeDate;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewChangeDate;

    Button button;
    Button changeDateButton;

    FrameLayout lineFrameLayout;

    public ChangeAvailabilityScreen() {
        stringHeader = Application.getInstance().getResources().getString(R.string.availability_changetime);

        Participant participant = Study.getParticipant();
        TestCycle cycle = participant.getCurrentTestCycle();
        DateTime startDate = cycle.getScheduledStartDate();
        DateTime endDate = cycle.getScheduledEndDate();
        DateTime now = DateTime.now();

        if (now.isBefore(startDate) || now.isAfter(endDate)) {
            // We're not in a visit, so show the 1 week adjustment option
            showChangeDate = true;
            changeDateHeader = Application.getInstance().getResources().getString(R.string.availability_changedates);
        } else {
            // We are in a visit
            showChangeDate = false;
        }

        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_availability, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(stringHeader));

        textViewChangeDate = view.findViewById(R.id.textViewChangeDate);
        changeDateButton = view.findViewById(R.id.changeDateButton);

        lineFrameLayout = view.findViewById(R.id.lineFrameLayout);

        if (showChangeDate) {
            textViewChangeDate.setText(Html.fromHtml(changeDateHeader));
            changeDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Study.adjustSchedule();
                }
            });
        } else {
            textViewChangeDate.setVisibility(View.GONE);
            changeDateButton.setVisibility(View.GONE);
            lineFrameLayout.setVisibility(View.GONE);
        }

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setText(ViewUtil.getHtmlString(R.string.button_back));
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });


        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.updateAvailability(8, 18);
            }
        });

        textViewBack.setVisibility(View.VISIBLE);

        return view;
    }

}
