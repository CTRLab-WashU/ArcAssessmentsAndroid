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
package com.healthymedium.arc.api.models;

import com.healthymedium.arc.utilities.CacheManager;

import java.io.File;
import java.lang.reflect.Field;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CachedObject {

    public String filename;
    public String mediaType;

    public RequestBody getRequestBody(){

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        File object = CacheManager.getInstance().getFile(filename,false);
        if(object==null){
        } else {
            RequestBody uploadFile = RequestBody.create(MediaType.parse(mediaType), object);
            builder.addFormDataPart("file", filename, uploadFile);
        }

        Field[] fields = getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            String key = fields[i].getName();
            String value = fieldToString(fields[i]);
            if(value.length()>0) {
                builder.addFormDataPart(key, value);
            }
        }

        return builder.build();
    }


    public String fieldToString(Field field) {
        String name = field.getName();
        if(name.equals("serialVersionUID")){
            return "";
        }
        if(name.equals("filename")){
            return "";
        }
        if(name.equals("mediaType")){
            return "";
        }

        Object value = null;
        try {
            field.setAccessible(true);
            value = field.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(value==null){
            return "";
        }

        return String.valueOf(value);
    }
}
