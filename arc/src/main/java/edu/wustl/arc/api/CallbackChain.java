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
package edu.wustl.arc.api;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CallbackChain {

    private static final String tag = "CallbackChain";

    AtomicBoolean stopped = new AtomicBoolean(false);

    
    List<Link> links = Collections.synchronizedList(new ArrayList<Link>());
    RestClient.Listener clientListener;

    AtomicReference<Object> cache = new AtomicReference<>();
    Object cachedObject = new Object();

    Gson gson;

    public CallbackChain(Gson gson){
        this.gson = gson;
        cache.set(cachedObject);
    }

    public Object getCachedObject(){
        return cache.get();
    }

    public void setCachedObject(Object object){
        cache.compareAndSet(cachedObject,object);
    }

    public boolean addLink(Call call){
        return addLink(call,null);
    }

    public boolean addLink(Call call, Listener listener){
        if(call==null){
            return false;
        }
        Link link = new Link();
        link.call = call;
        link.listener = listener;
        links.add(link);
        return true;
    }

    public boolean addLink(ShallowListener listener){
        ShallowLink link = new ShallowLink();
        link.listener = listener;
        links.add(link);
        return true;
    }

    public void execute(RestClient.Listener clientListener){
        this.clientListener = clientListener;
        RestResponse response = new RestResponse();
        response.successful = true;
        response.code = 200;
        handleTail(response);
    }

    public void stop() {
        if(links.size()>0){
            stopped.compareAndSet(false,true);
        }
    }

    private Callback callback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> retrofitResponse) {
            Log.i(tag,"parsing response");
            RestResponse response = RestResponse.fromRetrofitResponse(retrofitResponse);
            Log.i(tag,gson.toJson(response));

            if(links.size()==0){
                Log.wtf(tag,"link doesn't exist, investigate this");
                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            Link link = links.remove(0);

            boolean proceed;
            if(link.listener!=null) {
                if(response.successful) {
                    proceed = link.listener.onResponse(CallbackChain.this,response);
                } else {
                    proceed = link.listener.onFailure(CallbackChain.this,response);
                }
            } else {
                proceed = response.successful;
            }

            if(!proceed){
                Log.i(tag,"stop requested");
                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            handleTail(response);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
            Log.i(tag,"parsing failure");
            RestResponse response = RestResponse.fromRetrofitFailure(throwable);
            Log.i(tag,gson.toJson(response));

            if(links.size()==0){
                Log.wtf(tag,"link doesn't exist, investigate this");
                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            Link link = links.remove(0);

            boolean proceed;
            if(link.listener!=null) {
                if(response.successful) {
                    proceed = link.listener.onResponse(CallbackChain.this,response);
                } else {
                    proceed = link.listener.onFailure(CallbackChain.this,response);
                }
            } else {
                proceed = response.successful;
            }

            if(!proceed){
                Log.i(tag,"stop requested");
                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            handleTail(response);
        }
    };

    private void handleTail(RestResponse response) {
        Log.i(tag,"handling tail");

        if(links.size()==0) {
            Log.i(tag,"stopping at the end of the chain");
            if(clientListener!=null) {
                clientListener.onSuccess(response);
            }
            return;
        }

        if(stopped.get()){
            Log.i(tag,"stopping as requested");
            stopped.compareAndSet(true,false);
            return;
        }

        if(links.get(0) instanceof ShallowLink) {
            Log.i(tag,"executing shadow link");
            ((ShallowLink)links.remove(0)).listener.onExecute(CallbackChain.this);
            handleTail(response);
            return;
        }

        if(links.get(0).call==null) {
            Log.e(tag,"call is null, aborting");
            if(clientListener!=null) {
                clientListener.onFailure(response);
            }
            return;
        }

        Log.i(tag,"starting new link");
        links.get(0).call.enqueue(callback);
    }

    private class Link {
        Call call;
        Listener listener;
    }

    public interface Listener {
        boolean onResponse(CallbackChain chain, RestResponse response);
        boolean onFailure(CallbackChain chain, RestResponse response);
    }

    private class ShallowLink extends Link {
        ShallowListener listener;
    }

    public interface ShallowListener {
        void onExecute(CallbackChain chain);
    }
}
