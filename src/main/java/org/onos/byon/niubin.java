package org.onos.byon;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.event.EventDeliveryService;


/* Created by niubin on 17-6-23.
*/
@Component(immediate = true)
public class niubin {
     @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
     protected EventDeliveryService eventDispatcher;

    @Activate
    public void activate(){
        NetworkEvent networkEvent = new NetworkEvent(NetworkEvent.Type.NETWORK_ADD,"event");
        eventDispatcher.post(networkEvent);
    }


}
