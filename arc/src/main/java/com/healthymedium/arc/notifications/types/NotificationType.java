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
package com.healthymedium.arc.notifications.types;

import androidx.annotation.RawRes;

import com.healthymedium.arc.notifications.NotificationNode;

public abstract class NotificationType {

    protected int id;
    protected int importance;
    protected String channelId;
    protected String channelName;
    protected String channelDesc;
    protected String extra;
    protected boolean proctored;
    protected boolean showBadge = true;

    @RawRes
    protected int soundResource = -1;

    public NotificationType(){

    }

    public int getId(){
        return id;
    }

    public String getChannelId(){
        return channelId;
    }

    public String getChannelName(){
        return channelName;
    }

    public String getChannelDesc(){
        return channelDesc;
    }

    public boolean hasExtra(){
        return extra!=null;
    }

    public String getExtra(){
        return extra;
    }

    public int getImportance(){
        return importance;
    }

    public boolean isProctored(){
        return proctored;
    }

    public boolean shouldShowBadge(){
        return showBadge;
    }

    public int getSoundResource() {
        return soundResource;
    }

    // abstract methods ----------------------------------------------------------------------------

    public abstract String getContent(NotificationNode node);

    // the return value dictates whether or not the notification is shown to the user
    public abstract boolean onNotifyPending(NotificationNode node);

    // callback for when the notification is shown to the user
    public abstract void onNotify(NotificationNode node);

}
