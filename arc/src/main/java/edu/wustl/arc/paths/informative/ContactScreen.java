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
package edu.wustl.arc.paths.informative;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import edu.wustl.arc.ui.Button;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Intent.ACTION_DIAL;
import static android.content.Intent.ACTION_SENDTO;

import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.font.Fonts;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.misc.TransitionSet;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.utilities.PreferencesManager;
import edu.wustl.arc.utilities.ViewUtil;

import static edu.wustl.arc.study.Study.TAG_CONTACT_EMAIL;
import static edu.wustl.arc.study.Study.TAG_CONTACT_INFO;

public class ContactScreen extends BaseFragment {

    String stringPhoneNumber;
    String stringEmail;

    TextView textViewBack;
    TextView textViewHeader;
    TextView textViewPhoneNumber;

    TextView textViewEmailHeader;
    TextView textViewEmailAddress;

    Button callButton;
    Button emailButton;

    public ContactScreen() {
        stringPhoneNumber = PreferencesManager.getInstance().getString(TAG_CONTACT_INFO ,ViewUtil.getString(R.string.contact_call2));
        stringEmail = PreferencesManager.getInstance().getString(TAG_CONTACT_EMAIL, ViewUtil.getString(R.string.contact_email2));
        allowBackPress(false);
        setTransitionSet(TransitionSet.getSlidingDefault());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        View contactDetailsContainer = view.findViewById(R.id.contact_details_container);
        if (Study.getStateMachine().shouldShowDetailedContactScreen()) {
            contactDetailsContainer.setVisibility(View.VISIBLE);
        } else {
            contactDetailsContainer.setVisibility(View.GONE);
        }

        textViewHeader = view.findViewById(R.id.textViewHeader);
        textViewHeader.setText(ViewUtil.getHtmlString(R.string.resources_contact));

        textViewPhoneNumber = view.findViewById(R.id.textViewSubHeader);
        textViewPhoneNumber.setText(stringPhoneNumber);

        textViewEmailHeader = view.findViewById(R.id.textViewEmailHeader);
        textViewEmailHeader.setText(ViewUtil.getHtmlString(R.string.contact_email1));

        textViewEmailAddress = view.findViewById(R.id.textViewEmailSubHeader);
        textViewEmailAddress.setText(stringEmail);

        textViewBack = view.findViewById(R.id.textViewBack);
        textViewBack.setText(ViewUtil.getHtmlString(R.string.button_back));
        textViewBack.setTypeface(Fonts.robotoMedium);
        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationManager.getInstance().popBackStack();
            }
        });


        callButton = view.findViewById(R.id.button);
        callButton.setText(ViewUtil.getString(R.string.button_call));
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = stringPhoneNumber.replace("-","");
                number = number.replace("+", "");
                Intent intent = new Intent(ACTION_DIAL, Uri.parse("tel:" + number));
                startActivity(intent);
            }
        });

        emailButton = view.findViewById(R.id.emailButton);
        emailButton.setText(ViewUtil.getString(R.string.button_email));
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ACTION_SENDTO, Uri.fromParts(
                        "mailto",stringEmail, null));
                startActivity(Intent.createChooser(intent,""));
            }
        });

        textViewBack.setVisibility(View.VISIBLE);

        return view;
    }

}
