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
package com.healthymedium.arc.paths.templates;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class QuestionTemplate extends StandardTemplate {

    private String question;
    private double display_time;
    protected double response_time;
    protected String type = "unknown";

    public QuestionTemplate(boolean allowBack, String header, String subheader) {
        super(allowBack,header,subheader);
        question = header.replace("<b>","").replace("</b>","");
    }

    public QuestionTemplate(boolean allowBack, String header, String subheader, String button) {
        super(allowBack,header,subheader,button);
        question = header.replace("<b>","").replace("</b>","");
    }

    @Override
    public void onStart(){
        super.onStart();
        display_time = System.currentTimeMillis();
    }

    @Override
    public Object onDataCollection(){

        Map<String, Object> response = new HashMap<>();
        response.put("display_time", display_time/(double)1000);
        response.put("response_time", response_time/(double)1000);
        response.put("question", question);
        response.put("type", type);

        Object value = onValueCollection();
        if(value!=null){
            response.put("value", value);
        }

        String textValue = onTextValueCollection();
        if(textValue!=null){
            response.put("text_value", textValue);
        }

        return response;
    }

    public Object onValueCollection(){
        return null;
    }

    public String onTextValueCollection(){
        return null;
    }

}
