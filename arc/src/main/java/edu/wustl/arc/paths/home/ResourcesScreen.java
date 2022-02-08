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

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.paths.informative.AboutScreen;
import edu.wustl.arc.paths.informative.ChangeAvailabilityScreen;
import edu.wustl.arc.paths.informative.ContactScreen;
import edu.wustl.arc.paths.informative.FAQScreen;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.utilities.ViewUtil;

public class ResourcesScreen extends BaseFragment {

    protected TextView textViewHeader;

    protected RelativeLayout frameLayoutAvailability;
    protected TextView textViewAvailability;

    protected FrameLayout frameLayoutContact;
    protected TextView textViewContact;

    protected FrameLayout frameLayoutAbout;
    protected TextView textViewAbout;

    protected FrameLayout frameLayoutFAQ;
    protected TextView textViewFAQ;

    protected FrameLayout frameLayoutPrivacyPolicy;
    protected TextView textViewPrivacyPolicy;

    public ResourcesScreen() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_header)));
        textViewHeader.setTypeface(Fonts.robotoMedium);

        textViewAvailability = view.findViewById(R.id.textViewAvailability);
        textViewAvailability.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_availability)));
        ViewUtil.underlineTextView(textViewAvailability);

        frameLayoutAvailability = view.findViewById(R.id.frameLayoutAvailability);

        boolean isStudyOver = (Study.getCurrentTestCycle()==null);
        boolean isTestReady = false;

        if(!isStudyOver) {
            isTestReady = Study.getCurrentTestSession().getScheduledTime().minusMinutes(5).isBeforeNow();
        }

        if (isStudyOver || isTestReady) {
            textViewAvailability.setAlpha(0.5f);
            if(isTestReady) {
                TextView textViewChangeDenied = view.findViewById(R.id.textViewChangeDenied);
                textViewChangeDenied.setVisibility(View.VISIBLE);
            }
        } else {
            frameLayoutAvailability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeAvailabilityScreen changeAvailabilityScreen = new ChangeAvailabilityScreen();
                    NavigationManager.getInstance().open(changeAvailabilityScreen);
                }
            });
        }

        textViewContact = view.findViewById(R.id.textViewContact);
        textViewContact.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_contact)));
        ViewUtil.underlineTextView(textViewContact);

        frameLayoutContact = view.findViewById(R.id.frameLayoutContact);
        frameLayoutContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactScreen contactScreen = new ContactScreen();
                NavigationManager.getInstance().open(contactScreen);
            }
        });


        textViewAbout = view.findViewById(R.id.textViewAbout);
        textViewAbout.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_about_link)));
        ViewUtil.underlineTextView(textViewAbout);

        frameLayoutAbout = view.findViewById(R.id.frameLayoutAbout);
        frameLayoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutScreen aboutScreen = new AboutScreen();
                NavigationManager.getInstance().open(aboutScreen);
            }
        });


        textViewFAQ = view.findViewById(R.id.textViewFAQ);
        textViewFAQ.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_faq_link)));
        ViewUtil.underlineTextView(textViewFAQ);

        frameLayoutFAQ = view.findViewById(R.id.frameLayoutFAQ);
        frameLayoutFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQScreen faqScreen = new FAQScreen();
                NavigationManager.getInstance().open(faqScreen);
            }
        });


        textViewPrivacyPolicy = view.findViewById(R.id.textViewPrivacyPolicy);
        textViewPrivacyPolicy.setText(Html.fromHtml(ViewUtil.getString(R.string.resources_privacy_link)));
        ViewUtil.underlineTextView(textViewPrivacyPolicy);

        frameLayoutPrivacyPolicy = view.findViewById(R.id.frameLayoutPrivacyPolicy);
        frameLayoutPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Study.getPrivacyPolicy().show(getContext());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int top = view.getPaddingTop();
        view.setPadding(0,top,0,0);
    }
}
