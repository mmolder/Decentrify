package se.kth.reliablebroadcast;

import se.sics.kompics.PortType;

/**
 * Created by mikael on 2017-04-03.
 */
public class RBPort extends PortType {
    {
        indication(RDeliver.class);
        request(RBroadcast.class);
    }
}
