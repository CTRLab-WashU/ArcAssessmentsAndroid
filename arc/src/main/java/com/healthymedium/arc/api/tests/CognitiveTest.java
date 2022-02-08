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
package com.healthymedium.arc.api.tests;

import com.healthymedium.arc.api.tests.data.ChronotypeSurvey;
import com.healthymedium.arc.api.tests.data.ContextSurvey;
import com.healthymedium.arc.api.tests.data.GridTest;
import com.healthymedium.arc.api.tests.data.PriceTest;
import com.healthymedium.arc.api.tests.data.SymbolTest;
import com.healthymedium.arc.api.tests.data.WakeSurvey;

public class CognitiveTest extends BaseTest {

    public ChronotypeSurvey chronotype_survey;
    public ContextSurvey context_survey;
    public WakeSurvey wake_survey;
    public SymbolTest symbol_test;
    public PriceTest price_test;
    public GridTest grid_test;

    public CognitiveTest(){
        type = "cognitive";
    }

    @Override
    public int getProgress(boolean testCompleted){
        if(testCompleted){
            return 100;
        }

        int progress = 0;
        int divisor = 4;

        if(wake_survey!=null){
            progress += wake_survey.getProgress();
            divisor++;
        }
        if(chronotype_survey!=null){
            progress += chronotype_survey.getProgress();
            divisor++;
        }
        if(context_survey!=null){
            progress += context_survey.getProgress();
        }
        if(grid_test!=null){
            progress += grid_test.getProgress();
        }
        if(price_test!=null){
            progress += price_test.getProgress();
        }
        if(symbol_test!=null){
            progress += symbol_test.getProgress();
        }
        progress /= divisor;

        return progress;
    }

}
