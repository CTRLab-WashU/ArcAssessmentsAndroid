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
package com.healthymedium.arc.notifications;

import com.healthymedium.arc.utilities.CacheManager;

import org.joda.time.DateTime;

public class ProctorDeviation {

    DateTime lastIncident;
    DateTime lastRequest;

    public ProctorDeviation(){
        lastIncident = null;
        lastRequest = null;
    }

    public void markIncident(){
        lastIncident = DateTime.now();
    }

    public void markRequest(){
        lastRequest = DateTime.now();
    }

    public boolean save(){
        CacheManager cache = CacheManager.getInstance();
        if(cache==null) {
            return false;
        }
        if(!cache.putObject("ProctorDeviation", this)){
            return false;
        }
        return cache.save("ProctorDeviation");
    }

    public static ProctorDeviation load(){
        CacheManager cache = CacheManager.getInstance();
        if(cache==null) {
            return new ProctorDeviation();
        }
        return cache.getObject("ProctorDeviation", ProctorDeviation.class);
    }

    public static boolean shouldRequestBeMade(){
        ProctorDeviation deviation = load();

        if(deviation.lastIncident==null){
            return false;
        }
        if(deviation.lastRequest==null){
            return true;
        }
        if(deviation.lastRequest.isAfter(deviation.lastIncident)){
            return false;
        }
        return deviation.lastRequest.plusHours(24).isBeforeNow();
    }

}
