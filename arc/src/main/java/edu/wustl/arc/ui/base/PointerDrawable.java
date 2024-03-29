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

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import edu.wustl.arc.utilities.ViewUtil;

public class PointerDrawable extends SimpleDrawable {

    public static final int POINTER_NONE = -1;
    public static final int POINTER_BELOW = 0;
    public static final int POINTER_ABOVE = 1;

    private int pointerConfig = POINTER_NONE;
    private int pointerSize = ViewUtil.dpToPx(16);
    private int pointerX = 0;
    private int radius = 0;

    private Paint shadowPaint;

    public PointerDrawable(int pointerConfig, int pointerSize){
        super();
        this.pointerConfig = pointerConfig;
        this.pointerSize = pointerSize;
    }

    public PointerDrawable(int pointerConfig){
        super();
        this.pointerConfig = pointerConfig;
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        shadowPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.OUTER));
    }

    public PointerDrawable(){
        super();
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        shadowPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.OUTER));
    }

    public void setPointerConfig(int config){
        pointerConfig = config;
    }

    public int getPointerConfig(){
        return pointerConfig;
    }

    public void setPointerSize(int size){
        pointerSize = size;
    }

    public int getPointerSize(){
        return pointerSize;
    }

    public void setPointerX(int pos){
        pointerX = pos;
    }

    public int getPointerX(){
        return pointerX;
    }

    @Override
    public void draw(Canvas canvas) {
        if(path==null){
            return;
        }
        canvas.drawPath(path,shadowPaint);
        super.draw(canvas);
    }

    @Override
    protected void updateOffsets() {

        // create a rect that's small enough that the stroke isn't cut off
        int offset = (int) (strokeWidth/2);
        rect.set(offset,offset,width-offset,height-offset);

        if(drawStroke) {
            int maxRadius = Math.min(rect.width() / 2, rect.height() / 2);

            if (radius > maxRadius) {
                radius = maxRadius;
            }
        }
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    protected Path getPath(Rect rect) {
        this.rect = rect;

        int top = rect.top;
        int bottom = rect.bottom;

        if(pointerConfig==POINTER_BELOW){
            top = rect.top+pointerSize;
        }
        if(pointerConfig==POINTER_ABOVE){
            bottom = rect.bottom-pointerSize;
        }

        path = new Path();

        path.moveTo(rect.left + radius, top);

        // top pointer
        if(pointerConfig==POINTER_BELOW) {
            path.lineTo(pointerX - pointerSize, top);
            path.lineTo(pointerX, rect.top);
            path.lineTo(pointerX + pointerSize, top);
        }

        // top line
        path.lineTo(rect.right - radius, top);

        // top right corner
        RectF topRightRect = new RectF(rect.right - radius,top,rect.right,top+radius);
        path.arcTo(topRightRect, 270F, 90F, false);

        // right line
        path.lineTo(rect.right, bottom - radius);

        // bottom right corner
        RectF bottomRightRect = new RectF(rect.right - radius,bottom - radius,rect.right,bottom);
        path.arcTo(bottomRightRect, 0F, 90F, false);

        // bottom pointer
        if(pointerConfig==POINTER_ABOVE) {
            path.lineTo(pointerX + pointerSize, bottom);
            path.lineTo(pointerX, rect.bottom);
            path.lineTo(pointerX - pointerSize, bottom);
        }

        // bottom line
        path.lineTo(rect.left + radius, bottom);

        // bottom left corner
        RectF bottomLeftRect = new RectF(rect.left,bottom - radius,rect.left+radius,bottom);
        path.arcTo(bottomLeftRect, 90F, 90F, false);

        // left side
        path.lineTo(rect.left, top + radius);

        // top left corner
        RectF topLeftRect = new RectF(rect.left,top,rect.left+radius,top+radius);
        path.arcTo(topLeftRect, 180F, 90F, false);

        path.close();

        return path;
    }


}
