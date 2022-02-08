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
package edu.wustl.arc.study;

import android.util.Log;

import edu.wustl.arc.api.tests.data.BaseData;
import edu.wustl.arc.core.BaseFragment;
import edu.wustl.arc.navigation.NavigationManager;

import java.util.ArrayList;
import java.util.List;

public class PathSegment {

    public List<BaseFragment> fragments = new ArrayList<>();
    public int currentIndex;
    public PathSegmentData dataObject;

    public PathSegment(){

    }

    public PathSegment(List<BaseFragment> fragments, Class dataClass){
        this.fragments = fragments;
        this.currentIndex = -1;
        if(PathSegmentData.class.isAssignableFrom(dataClass)){
            try {
                dataObject = (PathSegmentData) dataClass.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("PathSegment", "created using invalid class ("+dataClass.getName()+")");
        }

    }

    public PathSegment(List<BaseFragment> fragments){
        this.fragments = fragments;
        this.currentIndex = -1;
        dataObject = new PathSegmentData();
    }

    public boolean openNext(){
        currentIndex++;
        if(fragments.size() > currentIndex) {
            fragments.get(currentIndex);
            NavigationManager.getInstance().open(fragments.get(currentIndex));
            return true;
        } else {
            NavigationManager.getInstance().open(new BaseFragment());
        }
        return false;
    }

    public boolean openNext(int skips){
        if(skips==0){
            return openNext();
        }
        if(fragments.size() > currentIndex+skips) {
            for(int i=0;i<skips+1;i++){
                currentIndex++;
                NavigationManager.getInstance().open(fragments.get(currentIndex));
            }
            return true;
        } else {
            BaseFragment fragment = new BaseFragment();
            NavigationManager.getInstance().open(fragment);
        }
        return false;
    }

    public boolean openPrevious(int skips){
        if(currentIndex-skips < 0){
            return false;
        }

        // There is a rare situation where this method is getting called, despite there not actually
        // being any fragments in this PathSegment. Since we obviously can't pop nonexistent fragments,
        // we just have to bail.
        if(fragments.size() - skips <= 0)
        {
            return false;
        }

        for(int i=0;i<skips+1;i++){
            if(!fragments.get(currentIndex).isBackAllowed()){
                return false;
            }
            currentIndex--;
            NavigationManager.getInstance().popBackStack();
        }

        return true;
    }

    public BaseData collectData(){
        int size = fragments.size();
        for(int i=0;i<size;i++){
            Object obj = fragments.get(i).onDataCollection();
            if(obj!=null){
                dataObject.add(obj);
            }
        }
        return dataObject.process();
    }

}