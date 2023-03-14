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
import edu.wustl.arc.api.tests.data.GridTest;
import edu.wustl.arc.api.tests.data.GridTestImage;
import edu.wustl.arc.api.tests.data.GridTestSection;
import edu.wustl.arc.api.tests.data.GridTestTap;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.study.PathSegmentData;
import edu.wustl.arc.time.TimeUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class GridTestPathData extends PathSegmentData {

    DateTime start;
    List<Section> sections = new ArrayList<>();

    public GridTestPathData(){
        super();
    }

    public void markStarted() {
        start = DateTime.now();
    }

    public boolean hasStarted(){
        return start!=null;
    }

    public void startNewSection(){
        sections.add(new Section());
    }

    public Section getCurrentSection(){
        return sections.get(sections.size()-1);
    }

    public void updateCurrentSection(Section section){
        sections.set(sections.size()-1,section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    protected BaseData onProcess() {
        GridTest test = new GridTest();

        test.sections = new ArrayList<>();

        long startTime = 0;

        if(start != null)
        {
            test.date = TimeUtil.toUtcDouble(start);
            startTime = start.getMillis();
        }

        for(Section section : sections){
            GridTestSection testSection = new GridTestSection();
            testSection.eCount = section.eCount;
            testSection.fCount = section.fCount;

            testSection.images = new ArrayList<>();
            for(Image image : section.images){
                GridTestImage testImage = new GridTestImage();
                testImage.x = image.x;
                testImage.y = image.y;
                testImage.image = image.image;
                testSection.images.add(testImage);
            }

            testSection.choices = new ArrayList<>();
            for(Tap tap : section.choices){
                GridTestTap testTap = new GridTestTap();
                testTap.x = tap.x;
                testTap.y = tap.y;
                if(start != null)
                {
                    testTap.selectionTime = Double.valueOf((tap.selectionTime - startTime) / (double) 1000);
                }
                testSection.choices.add(testTap);
            }

            if(start != null)
            {
                testSection.displayDistraction = Double.valueOf((section.displayTimeDistraction - startTime) / (double)1000);
                testSection.displayTestGrid = Double.valueOf((section.displayTimeTestGrid - startTime) / (double)1000);
                testSection.displaySymbols = Double.valueOf((section.displayTimeSymbols - startTime) / (double)1000);
            }
            test.sections.add(testSection);
        }

        return test;

    }

    public class Section {

        private long displayTimeSymbols;
        private long displayTimeDistraction;
        private long displayTimeTestGrid;
        private int eCount;
        private int fCount;
        private List<Image> images = new ArrayList<>();
        private List<Tap> choices = new ArrayList<>();

        public Section(){

        }

        public void markSymbolsDisplayed() {
            displayTimeSymbols = System.currentTimeMillis();
        }

        public void markDistractionDisplayed() {
            displayTimeDistraction = System.currentTimeMillis();
        }

        public void markTestGridDisplayed() {
            displayTimeTestGrid = System.currentTimeMillis();
        }

        public void setECount(int eCount) {
            this.eCount = eCount;
        }

        public void setFCount(int fCount) {
            this.fCount = fCount;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public List<Tap> getChoices() {
            return choices;
        }

        public void setChoices(List<Tap> choices) {
            this.choices = choices;
        }





    }

    public static class Tap {

        private long selectionTime;
        private int x;
        private int y;

        public Tap(int x, int y, long selectionTime){
            this.selectionTime = selectionTime;
            this.x = x;
            this.y = y;
        }

        public Tap(){

        }

    }

    public static class Image {

        public static transient final String PHONE = "phone";
        public static transient final String PEN = "pen";
        public static transient final String KEY = "key";

        private String image;
        private int x;
        private int y;

        public Image(int row,int col,String name) {
            image = name;
            x = row;
            y = col;
        }

        public String name() {
            return image;
        }

        public int row() {
            return x;
        }

        public int column() {
            return y;
        }

        public int id() {
            if(image.equals(PHONE)) {
                return R.drawable.phone;
            }
            if(image.equals(PEN)) {
                return R.drawable.pen;
            }
            if(image.equals(KEY)) {
                return R.drawable.key;
            }
            return 0;
        }

    }

}