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
package edu.wustl.arc.notifications;

import edu.wustl.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotificationNodes {

    static private final String NOTIFICATION_NODES = "notification_nodes";
    static private final String NOTIFICATION_REQUEST_INDEX = "notification_request_index";

    private List<NotificationNode> nodes;
    private int requestIndex;

    public void load(){
        if(PreferencesManager.getInstance().contains(NOTIFICATION_NODES)) {
            NotificationNode[] nodeArrays = PreferencesManager.getInstance().getObject(NOTIFICATION_NODES, NotificationNode[].class);
            nodes = Collections.synchronizedList(new ArrayList<>(Arrays.asList(nodeArrays)));
        } else {
            nodes = Collections.synchronizedList(new ArrayList<NotificationNode>());
        }
        requestIndex = PreferencesManager.getInstance().getInt(NOTIFICATION_REQUEST_INDEX,0);
    }

    public void save(){
        PreferencesManager.getInstance().putObject(NOTIFICATION_NODES,nodes.toArray());
        PreferencesManager.getInstance().putInt(NOTIFICATION_REQUEST_INDEX,requestIndex);
    }

    public boolean remove(List<NotificationNode> removeNodes){
        boolean result = nodes.remove(removeNodes);
        save();
        return result;
    }

    public boolean remove(NotificationNode node){
        boolean result = nodes.remove(node);
        save();
        return result;
    }

    public NotificationNode add(Integer type, Integer id, DateTime time){
        requestIndex++;
        NotificationNode node = new NotificationNode(id,type,requestIndex,time);
        nodes.add(node);
        save();
        return node;
    }

    public NotificationNode get(int type, int sessionId){
        int size = nodes.size();
        for(int i=0;i<size;i++){
            if(nodes.get(i).id==sessionId && nodes.get(i).type==type){
                return nodes.get(i);
            }
        }
        return null;
    }

    public List<NotificationNode> getAll(){
        return nodes;
    }

}
