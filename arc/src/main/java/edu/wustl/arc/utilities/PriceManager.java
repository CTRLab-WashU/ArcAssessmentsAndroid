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
package edu.wustl.arc.utilities;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import edu.wustl.arc.core.Application;
import edu.wustl.arc.core.Config;
import edu.wustl.arc.assessments.R;
import edu.wustl.arc.study.ParticipantState;
import edu.wustl.arc.study.Study;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PriceManager {

    private static PriceManager instance;
    private List<List<PriceManager.Item>> priceSets = new ArrayList<>();

    private PriceManager() {
    }

    public static synchronized void initialize() {
        instance = new PriceManager();
    }

    public static synchronized PriceManager getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }

    public synchronized void clearCache() {
        priceSets.clear();
    }

    public List<PriceManager.Item> getPriceSet(){

        if(priceSets.size()==0) {
            priceSets = loadJson(Application.getInstance().getResources().openRawResource(R.raw.price_sets));
        }

        if(Config.ENABLE_LEGACY_PRICE_SETS)
        {
            return getLegacyPriceSet();
        }

        int index = Study.getCurrentTestSession().getId();
        int size = priceSets.size();

        if(size == 0)
        {
            return new ArrayList<>();
        }

        if(index>=size){
            index -= size;
        }
        return priceSets.get(index);
    }

    // Many apps have used this older version of the getPriceSet() method, which returns an incorrect
    // priceSet for later visits. This method is being maintained to provide consistent tests for
    // the participants using these applications.

    private List<PriceManager.Item> getLegacyPriceSet(){

        ParticipantState state = Study.getParticipant().getState();
        int size = priceSets.size();
        int index = 10*state.currentTestCycle +state.currentTestSession;
        if(index>=size){
            index -= size;
        }
        return priceSets.get(index);
    }


    public static List<List<PriceManager.Item>> loadJson(InputStream stream){
        long before = System.currentTimeMillis();
        List<List<PriceManager.Item>> priceSets = new ArrayList<>();
        try {

            JsonReader reader = new JsonReader(new InputStreamReader(stream));
            Gson gson = new GsonBuilder().create();

            // Read file in stream mode
            reader.beginArray();
            while (reader.hasNext()) {
                // Read data into object model
                Item[] items = gson.fromJson(reader, Item[].class);
                priceSets.add(Arrays.asList(items));
            }
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long after = System.currentTimeMillis();
        //Log.i("PriceManager", "loadJson took "+(after-before)+"ms");

        return priceSets;
    }

    public class Item {
        public String item;
        public String price;
        public String alt;

        Item(String item, String price, String alt){
            this.item = item;
            this.price = price;
            this.alt = alt;
        }
    }


}
