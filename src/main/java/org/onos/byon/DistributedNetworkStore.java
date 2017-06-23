/*
 * Copyright 2015 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onos.byon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.HostId;
import org.onosproject.store.AbstractStore;
import org.onosproject.store.Store;

import org.onosproject.store.serializers.KryoNamespaces;
import org.onosproject.store.service.ConsistentMap;
import org.onosproject.store.service.MapEvent;
import org.onosproject.store.service.MapEventListener;
import org.onosproject.store.service.Serializer;
import org.onosproject.store.service.StorageService;
import org.onosproject.store.service.Versioned;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.onos.byon.NetworkEvent.Type.NETWORK_ADD;
import static org.onos.byon.NetworkEvent.Type.NETWORK_REMOVE;
import static org.onos.byon.NetworkEvent.Type.NETWORK_UPDATE;

/**
 * Network Store implementation backed by consistent map.
 */
@Component(immediate = true)
@Service
public class DistributedNetworkStore
        extends AbstractStore<NetworkEvent, NetworkStoreDelegate>
        implements NetworkStore {

    private  Logger log = LoggerFactory.getLogger(DistributedNetworkStore.class);

    /*
     * TODO Lab 5: Get a reference to the storage service
     *
     * All you need to do is uncomment the following two lines.
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected StorageService storageService;

    /*
     * TODO Lab 5: Replace the ConcurrentMap with ConsistentMap
     */

    private ConsistentMap<String, Set<HostId>> nets;
    /*
     * TODO Lab 6: Create a listener instance of InternalListener
     *
     * You will first need to implement the class (at the bottom of the file).
     */
    InternalListener internalListener = new InternalListener();

    @Activate
    public void activate() {
        /**
         * TODO Lab 5: Replace the ConcurrentHashMap with ConsistentMap
         *
         * You should use storageService.consistentMapBuilder(), and the
         * serializer: Serializer.using(KryoNamespaces.API)
         */

        nets=storageService.<String,Set<HostId>>consistentMapBuilder()
                .withSerializer(Serializer.using(KryoNamespaces.API))
                .withName("onos-byon").build();


        /*
         * TODO Lab 6: Add the listener to the networks map
         *
         * Use networks.addListener()
         */
        nets.addListener(internalListener);
        log.info("Started");
    }

    @Deactivate
    public void deactivate() {
        /*
         * TODO Lab 6: Remove the listener from the networks map
         *
         * Use networks.removeListener()
         */
        nets.removeListener(internalListener);
        log.info("Stopped");
    }

    @Override
    public void putNetwork(String network) {
        nets.putIfAbsent(network, Sets.<HostId>newHashSet());
    }

    @Override
    public void removeNetwork(String network) {
        nets.remove(network);
    }

    @Override
    public Set<String> getNetworks() {
        return ImmutableSet.copyOf(nets.keySet());
    }

    @Override
    public boolean addHost(String network, HostId hostId) {
        /*
         * TODO Lab 5: Update the Set to Versioned<Set<HostId>>
         *
         * You will also need to extract the value in the if statement.
         */
        Set<HostId> existingHosts = Versioned.valueOrNull(nets.get(network));
        if (existingHosts.contains(hostId)) {
            return false;
        }

        nets.computeIfPresent(network,
                                  (k, v) -> {
                                      Set<HostId> result = Sets.newHashSet(v);
                                      result.add(hostId);
                                      return result;
                                  });
        return true;
    }

    @Override
    public void removeHost(String network, HostId hostId) {
        /*
         * TODO Lab 5: Update the Set to Versioned<Set<HostId>>
         */
        Versioned<Set<HostId>> hosts =
                nets.computeIfPresent(network,
                                          (k, v) -> {
                                              Set<HostId> result = Sets.newHashSet(v);
                                              result.remove(hostId);
                                              return result;
                                          });
        checkNotNull(hosts, "Network %s does not exist", network);
    }

    @Override
    public Set<HostId> getHosts(String network) {
        /*
         * TODO Lab 5: Update return value
         *
         * ConsistentMap returns a Versioned<V>, so you need to extract the value
         */
        return   Versioned.valueOrNull(nets.get(network));

    }



    /*
     * TODO Lab 6: Implement an InternalListener class for remote map events
     *
     * The class should implement the MapEventListener interface and
     * its event method.
     */

    private class InternalListener implements MapEventListener<String, Set<HostId>> {
        @Override
        public void event(MapEvent<String, Set<HostId>> mapEvent) {
            final NetworkEvent.Type type;
            switch (mapEvent.type()) {
                case INSERT:
                    type = NETWORK_ADD;
                    break;
                case UPDATE:
                    type = NETWORK_UPDATE;
                    break;
                case REMOVE:
                default:
                    type = NETWORK_REMOVE;
                    break;
            }
            log.info("notifyDelegate Event");
            notifyDelegate(new NetworkEvent(type, mapEvent.key()));
        }
    }


}
