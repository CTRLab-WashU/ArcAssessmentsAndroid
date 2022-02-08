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
package edu.wustl.arc.paths.questions;

import android.annotation.SuppressLint;

import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.misc.TransitionSet;
import edu.wustl.arc.paths.informative.RebukedCommitmentThankYouScreen;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class QuestionRemoteStudyCommitment extends QuestionPolarAlt {

    public QuestionRemoteStudyCommitment(boolean allowBack, String header, String subheader, String yesAnswer, String noAnswer) {
        super(allowBack,header,subheader, yesAnswer, noAnswer);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSubHeaderTextSize(17);
        setSubHeaderLineSpacing(ViewUtil.dpToPx(9), 1);
    }

    @Override
    protected void onNextRequested() {
        if (answered) {
            if (answerIsYes) {
                // go to next fragment
                Study.getParticipant().markCommittedToStudy();
                Study.getInstance().openNextFragment();
            } else {
                // go to thank you screen
                Study.getParticipant().rebukeCommitmentToStudy();
                Study.getParticipant().save();
                BaseFragment fragment = new RebukedCommitmentThankYouScreen(ViewUtil.getString(R.string.onboarding_nocommit_header), ViewUtil.getString(R.string.onboarding_nocommit_body), false);
                fragment.setTransitionSet(TransitionSet.getSlidingDefault());
                NavigationManager.getInstance().open(fragment);
            }
        }
    }

}
