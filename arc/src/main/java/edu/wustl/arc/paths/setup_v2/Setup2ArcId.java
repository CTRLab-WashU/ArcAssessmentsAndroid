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
package edu.wustl.arc.paths.setup_v2;

import android.annotation.SuppressLint;

import edu.wustl.arc.core.Config;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.path_data.SetupPathData;
import edu.wustl.arc.study.PathSegment;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class Setup2ArcId extends Setup2Template {

    public Setup2ArcId() {
        super(6, 0, ViewUtil.getString(R.string.login_enter_ARCID));
    }

    @Override
    protected boolean shouldAutoProceed() {
        return true;
    }

    @Override
    protected void onNextRequested() {
        super.onNextRequested();

        if(Config.REST_BLACKHOLE) {
            Study.getInstance().openNextFragment();
            return;
        }

        SetupPathData setupPathData = ((SetupPathData)Study.getCurrentSegmentData());
        setupPathData.id = characterSequence.toString();
        Study.getInstance().openNextFragment();
    }

    private boolean fragmentExists(PathSegment path, Class tClass) {
        int last = path.fragments.size()-1;
        String oldName = path.fragments.get(last).getSimpleTag();
        String newName = tClass.getSimpleName();
        return oldName.equals(newName);
    }
}
