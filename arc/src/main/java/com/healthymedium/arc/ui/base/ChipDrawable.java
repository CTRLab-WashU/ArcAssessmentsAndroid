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
package com.healthymedium.arc.ui.base;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class ChipDrawable extends SimpleDrawable {

    public ChipDrawable(){
        super();
    }

    @Override
    protected void updateOffsets() {

        // create a rect that's small enough that the stroke isn't cut off
        int offset = (int) (strokeWidth/2);
        rect.set(offset,offset,width-offset,height-offset);

    }

    protected Path getPath(Rect rect) {

        Path path = new Path();

        int radius = height;

        path.moveTo(rect.left + radius, rect.top);

        // top line
        path.lineTo(rect.right - radius, rect.top);

        // right arc
        RectF rightRect = new RectF(rect.right - radius, rect.top, rect.right, rect.bottom);
        path.arcTo(rightRect, 270F, 180F, false);

        // bottom line
        path.lineTo(rect.left + radius, rect.bottom);

        // left arc
        RectF leftRect = new RectF(rect.left, rect.top, rect.left + radius, rect.bottom);
        path.arcTo(leftRect, 90F, 180F, false);

        path.close();

        return path;
    }


}
