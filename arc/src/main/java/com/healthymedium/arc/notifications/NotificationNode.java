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

import androidx.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.Comparator;

public class NotificationNode {
    public int id;
    public int type;
    public DateTime time;
    public int requestCode;

    NotificationNode(int id, int type, int requestCode, DateTime time){
        this.id = id;
        this.type = type;
        this.time = time;
        this.requestCode = requestCode;
    }

    // We need to make sure that notification id's are unique, but we also need to be able to
    // recreate a notification id for a given session.
    // This should separate notification ids for the different notification types into their own
    // sections, so as long as we're not going over 10,000 sessions, we shouldn't run into any
    // collisions.
    public int getNotifyId(){
        return (type * 10000) + id;
    }

    @NonNull
    @Override
    public String toString() {
        return "id="+id+" type="+type+" time="+time.toString();
    }

    public static class TimeComparator implements Comparator<NotificationNode>{
        public int compare(NotificationNode a, NotificationNode b){
            return a.time.compareTo(b.time);
        }
    }

    public static int getNotifyId(int id, int type){
        return (type * 10000) + id;
    }
}
