package org.onos.byon;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.cluster.ClusterService;
import org.onosproject.net.device.DeviceStore;

import java.util.logging.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by niubin on 17-6-9.
 */
@Component(immediate = true)
public class NetworkMonitor {
    private final org.slf4j.Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkStore networkStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkService networkService;

    private final NetworkListener networkListener = new InternalNetworkListener();
    @Activate
    public void activate(){
        networkService.addListeners(networkListener);
        log.info("Started");
    }
    @Deactivate
    public void deactivate(){
        networkService.removeListeners(networkListener);
        log.info("Stoped");
    }
    private void handleNetworkEvent(NetworkEvent networkEvent){
        switch (networkEvent.type()) {
            case NETWORK_ADD:
                log.info("ADD");
            case NETWORK_REMOVE:
                log.info("Remove");
            case NETWORK_UPDATE:
                log.info("Update");
        }

    }

    private  class InternalNetworkListener implements NetworkListener {


        @Override
        public void event(NetworkEvent networkEvent) {
            handleNetworkEvent(networkEvent);
        }
    }
}
