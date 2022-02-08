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
package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.api.tests.data.PriceTest;
import com.healthymedium.arc.api.tests.data.PriceTestSection;
import com.healthymedium.arc.study.PathSegmentData;
import com.healthymedium.arc.time.TimeUtil;
import com.healthymedium.arc.utilities.PriceManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class PriceTestPathData extends PathSegmentData {

    List<Section> sections = new ArrayList<>();
    List<PriceManager.Item> priceSet;
    DateTime start;

    public PriceTestPathData(){
        super();
    }

    public void markStarted(){
        start = DateTime.now();
    }

    public boolean hasStarted(){
        return start!=null;
    }

    public List<PriceManager.Item> getPriceSet(){
        if(priceSet==null){
            loadPriceSet();
        }
        return priceSet;
    }

    public List<Section> getSections() {
        if(sections.size()==0) {
            loadPriceSet();
        }
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    private void loadPriceSet(){
        priceSet = PriceManager.getInstance().getPriceSet();
        if(sections.size()==0){
            int size = priceSet.size();
            for(int i=0;i<size;i++){
                sections.add(new Section());
            }
        }
    }

    @Override
    protected BaseData onProcess() {

        PriceTest test = new PriceTest();

        test.sections = new ArrayList<>();

        long startTime = 0;

        if(start != null)
        {
            test.date = TimeUtil.toUtcDouble(start);
            startTime = start.getMillis();
        }

        for(Section section : sections){
            PriceTestSection testSection = new PriceTestSection();
            testSection.selectedIndex = section.selectedIndex;
            testSection.correctIndex = section.correctIndex;
            testSection.goodPrice = section.goodPrice;
            testSection.altPrice = section.altPrice;
            testSection.price = section.price;
            testSection.item = section.item;

            if(start != null)
            {
                testSection.stimulusDisplayTime = Double.valueOf((section.stimulusDisplayTime - startTime) / (double)1000);
                testSection.questionDisplayTime = Double.valueOf((section.questionDisplayTime - startTime) / (double)1000);
                testSection.selectionTime = Double.valueOf((section.selectionTime - startTime) / (double)1000);
            }

            test.sections.add(testSection);
        }

        return test;
    }

    public static class Section {

        private int goodPrice;
        private long stimulusDisplayTime;
        private long questionDisplayTime;
        private String item;
        private String price;
        private String altPrice;
        private int correctIndex;
        private int selectedIndex;
        private long selectionTime;

        public Section(){
            goodPrice = 99;
            item = "";
        }

        public void selectGoodPrice(int goodPrice) {
            this.goodPrice = goodPrice;
        }

        public void markStimulusDisplayed() {
            stimulusDisplayTime = System.currentTimeMillis();
        }

        public void markQuestionDisplayed() {
            questionDisplayTime = System.currentTimeMillis();
        }

        public void setItem(String item) {
            this.item = item;
        }
        public String getItem() {
            return this.item;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setAltPrice(String altPrice) {
            this.altPrice = altPrice;
        }

        public void setCorrectIndex(Integer correctIndex) {
            this.correctIndex = correctIndex;
        }

        public void markSelection(int index, long selectionTime){
            this.selectionTime =  selectionTime;
            selectedIndex = index;
        }

    }
}
