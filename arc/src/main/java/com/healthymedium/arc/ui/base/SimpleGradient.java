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

import android.graphics.LinearGradient;
import android.graphics.Shader;

public class SimpleGradient {

    public static final int LINEAR_HORIZONTAL = 0;
    public static final int LINEAR_VERTICAL = 1;

    int id;
    int color0;
    int color1;
    Shader.TileMode tileMode;

    public SimpleGradient(int enumeratedValue){
        id = enumeratedValue;
    }

    public int getId() {
        return id;
    }

    Shader getShader(int width, int height){
        switch (id){
            case LINEAR_VERTICAL:
                return new LinearGradient(0, 0, 0, height, color0, color1, tileMode);
            case LINEAR_HORIZONTAL:
                return new LinearGradient(0,0,width,0,color0,color1,tileMode);
        }
        return null;
    }

    void setColor0(int color){
        this.color0 = color;
    }

    void setColor1(int color){
        this.color1 = color;
    }

    void setTileMode(Shader.TileMode tileMode){
        this.tileMode = tileMode;
    }

    public static SimpleGradient getGradient(int gradientId, int colorFirst, int colorSecond) {
        switch (gradientId){
            case SimpleGradient.LINEAR_HORIZONTAL:
                return getHorizontalGradient(colorFirst,colorSecond);
            case SimpleGradient.LINEAR_VERTICAL:
                return getVerticalGradient(colorFirst,colorSecond);
            default:
                return null;
        }
    }

    public static SimpleGradient getHorizontalGradient(int colorLeft, int colorRight) {
        SimpleGradient gradient = new SimpleGradient(SimpleGradient.LINEAR_HORIZONTAL);
        gradient.setColor0(colorLeft);
        gradient.setColor1(colorRight);
        gradient.setTileMode(Shader.TileMode.CLAMP);
        return gradient;
    }

    public static SimpleGradient getVerticalGradient(int colorTop, int colorBottom) {
        SimpleGradient gradient = new SimpleGradient(SimpleGradient.LINEAR_VERTICAL);
        gradient.setColor0(colorTop);
        gradient.setColor1(colorBottom);
        gradient.setTileMode(Shader.TileMode.CLAMP);
        return gradient;
    }

}
