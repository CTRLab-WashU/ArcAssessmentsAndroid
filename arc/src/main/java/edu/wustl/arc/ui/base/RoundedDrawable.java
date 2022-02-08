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
package edu.wustl.arc.ui.base;


import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class RoundedDrawable extends SimpleDrawable {

    private int radiusTopLeft;
    private int radiusTopRight;
    private int radiusBottomLeft;
    private int radiusBottomRight;

    public RoundedDrawable(){
        super();
    }

    @Override
    protected void updateOffsets() {

        // create a rect that's small enough that the stroke isn't cut off
        int offset = (int) (strokeWidth/2);
        rect.set(offset,offset,width-offset,height-offset);

        if(drawStroke) {
            int maxRadius = Math.min(rect.width() / 2, rect.height() / 2);

            if (radiusTopLeft > maxRadius) {
                radiusTopLeft = maxRadius;
            }
            if (radiusTopRight > maxRadius) {
                radiusTopRight = maxRadius;
            }
            if (radiusBottomLeft > maxRadius) {
                radiusBottomLeft = maxRadius;
            }
            if (radiusBottomRight > maxRadius) {
                radiusBottomRight = maxRadius;
            }
        }
    }

    public void setRadius(int radius) {
        radiusTopLeft = radius;
        radiusTopRight = radius;
        radiusBottomLeft = radius;
        radiusBottomRight = radius;
    }

    public void setRadius(int topLeft, int topRight, int bottomLeft, int bottomRight) {
        radiusTopLeft = topLeft;
        radiusTopRight = topRight;
        radiusBottomLeft = bottomLeft;
        radiusBottomRight = bottomRight;
    }


    protected Path getPath(Rect rect) {

        Path path = new Path();

        path.moveTo(rect.left + radiusTopLeft, rect.top);

        // top line
        path.lineTo(rect.right - radiusTopRight, rect.top);

        // top right corner
        RectF topRightRect = new RectF(rect.right - radiusTopRight,rect.top,rect.right,rect.top+radiusTopRight);
        path.arcTo(topRightRect, 270F, 90F, false);

        // right line
        path.lineTo(rect.right, rect.bottom - radiusBottomRight);

        // bottom right corner
        RectF bottomRightRect = new RectF(rect.right - radiusBottomRight,rect.bottom - radiusBottomRight,rect.right,rect.bottom);
        path.arcTo(bottomRightRect, 0F, 90F, false);

        // bottom line
        path.lineTo(rect.left + radiusBottomLeft, rect.bottom);

        // bottom left corner
        RectF bottomLeftRect = new RectF(rect.left,rect.bottom - radiusBottomLeft,rect.left+radiusBottomLeft,rect.bottom);
        path.arcTo(bottomLeftRect, 90F, 90F, false);

        // left side
        path.lineTo(rect.left, rect.top + radiusTopLeft);

        // top left corner
        RectF topLeftRect = new RectF(rect.left,rect.top,rect.left+radiusTopLeft,rect.top+radiusTopLeft);
        path.arcTo(topLeftRect, 180F, 90F, false);

        path.close();

        return path;
    }


}
