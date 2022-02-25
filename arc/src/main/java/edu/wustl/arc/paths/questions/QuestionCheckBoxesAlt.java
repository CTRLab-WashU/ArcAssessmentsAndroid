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
package edu.wustl.arc.paths.questions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import edu.wustl.arc.paths.templates.AltQuestionTemplate;
import edu.wustl.arc.ui.ArcCheckBox;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class QuestionCheckBoxesAlt extends AltQuestionTemplate {

    List<ArcCheckBox> checkBoxes = new ArrayList<>();
    List<String> options = new ArrayList<>();
    String exclusive;
    int exclusiveIndex = -1;
    boolean exclusiveChecked = false;
    List<String> selections = new ArrayList<>();
    boolean settingUp = false;

    public QuestionCheckBoxesAlt(boolean allowBack, String header, String subheader, List<String> options, String exclusive) {
        super(allowBack,header,subheader);
        type = "checkbox";
        this.options = options;
        this.exclusive = exclusive;
        int size = options.size();
        for(int i=0;i<size;i++){
            if(options.get(i).equals(exclusive)){
                exclusiveIndex = i;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHelpVisible(false);

        settingUp = true;
        checkBoxes.clear();

        int index=0;
        for(final String option : options){
            ArcCheckBox checkBox = new ArcCheckBox(getContext());
            checkBox.setText(option);

            final int checkBoxIndex = index;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    response_time = System.currentTimeMillis();
                    if(checked && (checkBoxIndex==exclusiveIndex)){
                        int size = checkBoxes.size();
                        for(int i=0;i<size;i++){
                            if(i!=exclusiveIndex){
                                checkBoxes.get(i).setChecked(false);
                            }
                        }
                        exclusiveChecked = true;
                    } else if(checked && exclusiveChecked){
                        checkBoxes.get(exclusiveIndex).setChecked(false);
                        exclusiveChecked = false;
                    }


                    if(checked && !settingUp){
                        String option = options.get(checkBoxIndex);
                        if(!selections.contains(option)){
                            selections.add(option);
                        }
                        if(!buttonNext.isEnabled()){
                            buttonNext.setEnabled(true);
                        }
                    } else if(!settingUp) {
                        selections.remove(options.get(checkBoxIndex));
                        if(selections.size()==0 && buttonNext.isEnabled()){
                            buttonNext.setEnabled(false);
                        }
                    }


                }
            });

            checkBoxes.add(checkBox);
            content.addView(checkBox);
            index++;
        }

        return view;
    }

    @Override
    public void onPause() {
        selections.clear();
        int size = checkBoxes.size();
        for(int i=0;i<size;i++){
            if(checkBoxes.get(i).isChecked()){
                selections.add(options.get(i));
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(checkBoxes.size()>0) {
            int optionCount = options.size();
            for(int i=0;i<optionCount;i++){
                checkBoxes.get(i).setChecked(selections.contains(options.get(i)));
            }
        }

        buttonNext.setEnabled(selections.size()>0);
        settingUp = false;
    }

    @Override
    public Object onDataCollection(){
        selections.clear();
        int size = checkBoxes.size();
        for(int i=0;i<size;i++){
            if(checkBoxes.get(i).isChecked()){
                selections.add(options.get(i));
            }
        }
        return super.onDataCollection();
    }

    @Override
    public Object onValueCollection(){
        int size = checkBoxes.size();
        List<Integer> selectedIndexes = new ArrayList<>();
        for (int i=0;i<size;i++) {
            if (checkBoxes.get(i).isChecked()) {
                selectedIndexes.add(i);
            }
        }
        return selectedIndexes;
    }

    @Override
    public String onTextValueCollection(){
        String selectionString = "";
        for (String string : options) {
            if (selections.contains(string)) {
                selectionString += (string + ",");
            }
        }
        if (selectionString.length() > 2) {
            selectionString = selectionString.substring(0, selectionString.length() - 1);
        }
        return selectionString;
    }

}
