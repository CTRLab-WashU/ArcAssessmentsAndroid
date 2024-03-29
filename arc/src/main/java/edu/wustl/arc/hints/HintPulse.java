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
package edu.wustl.arc.hints;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;

import edu.wustl.arc.assessments.R;
import edu.wustl.arc.utilities.ViewUtil;

public class HintPulse extends View{

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ObjectAnimator animator;
    private Bitmap bitmap;
    private View view;
    private boolean running = false;

    private int strokeWidth;
    private int radius = 0;
    private int height;
    private int width;
    private int x,y;

    public HintPulse(Context context, View view) {
        super(context);
        this.view = view;

        strokeWidth = ViewUtil.dpToPx(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(ViewUtil.getColor(R.color.hintDark));
    }

    void process(){
        int halfWidth = strokeWidth/2;

        width = view.getWidth()+halfWidth;
        height = view.getHeight()+halfWidth;

        int locations[] = new int[2];
        view.getLocationOnScreen(locations);

        x = locations[0]-halfWidth;
        y = locations[1]-halfWidth;

        boolean radiusMatchesWidth = isApproximatelyEqual(radius,width/2,20);
        boolean radiusMatchesHeight = isApproximatelyEqual(radius,height/2,20);
        boolean isCircle = radiusMatchesWidth || radiusMatchesHeight;
        if(isCircle){
            int offset = strokeWidth/2;
            x -= offset;
            y -= offset;
        }

        setPivotX(x+(width/2));
        setPivotY(y+(height/2));

        bitmap = Bitmap.createBitmap(width+strokeWidth,height+strokeWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if(isCircle){
            canvas.drawCircle((width+strokeWidth)/2,(height+strokeWidth)/2, radius, paint);
        } else {
            RectF rect = new RectF(halfWidth,halfWidth, width, height);
            canvas.drawRoundRect(rect, radius, radius, paint);
        }

    }

    private boolean isApproximatelyEqual(int val0, int val1, int variance){
        return (Math.abs(val0-val1) < variance);
    }

    public void setRadius(int dp) {
        radius = ViewUtil.dpToPx(dp);
    }

    public View getView(){
        return view;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bitmap!=null && running){
            if (!bitmap.isRecycled()){
                canvas.drawBitmap(bitmap, x, y, paint);
            }
        }
    }

    public void cleanup(){
        if(bitmap!=null) {
            bitmap.recycle();
        }
    }

    public void start(long delay){

        float scaleX;
        float scaleY;

        if(width>height){
            scaleX = 1.25f;
            scaleY = (height+(0.25f*width))/height;
        } else {
            scaleY = 1.25f;
            scaleX = (width+(0.25f*height))/width;
        }

        animator = ObjectAnimator.ofPropertyValuesHolder(
                this,
                PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_X, scaleX),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleY));
        animator.setDuration(2000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                running = true;
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animation.start();
                    }
                }, 500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setStartDelay(delay);

        if(delay==0){
            animator.start();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(animator!=null){
                        animator.start();
                    }
                }
            },delay);
        }

    }

    public void stop(){
        if(animator!=null){
            animator.cancel();
        }
    }

}
