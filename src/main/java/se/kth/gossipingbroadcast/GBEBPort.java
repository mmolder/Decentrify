package se.kth.gossipingbroadcast;

import se.sics.kompics.PortType;

/**
 * Created by mikael on 2017-04-03.
 */
public class GBEBPort extends PortType {
    {
        indication(GBEBDeliver.class);
        request(GBEBroadcast.class);
    }
}
