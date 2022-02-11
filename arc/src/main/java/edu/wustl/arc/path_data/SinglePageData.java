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
package edu.wustl.arc.path_data;

import edu.wustl.arc.api.tests.data.BaseData;
import edu.wustl.arc.study.PathSegmentData;

/*
TODO: We should consider just getting rid of this class. The only place that it was being used
was for the QuestionInterrupted Fragment, which handled setting its own data, and returned null from
onDataCollection(). So practically speaking, this class didn't do anything.

For Gson serialization purposes, we need onProcess to return a BaseData object, but PathSegmentData's
objects property can't be changed to List<BaseData> without having to propagate a LOT of changes to
method return values and classes (like BaseFragments' onDataCollection()).
 */

public class SinglePageData extends PathSegmentData {


    public SinglePageData(){
        super();

    }

    @Override
    protected BaseData onProcess() {
//        if(objects.size()>0){
//            return objects.get(0);
//        }
        return null;
    }
}
