package org.onos.byon;

import org.onosproject.event.AbstractEvent;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceEvent;

/**
 * Created by niubin on 17-6-9.
 */
public class NetworkEvent extends AbstractEvent<NetworkEvent.Type, String> {

    public enum Type {
        NETWORK_ADD,
        NETWORK_REMOVE,
        NETWORK_UPDATE

    }
    public NetworkEvent(Type type,String string){
        super(type,string);
    }
}
