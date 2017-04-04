package se.kth.causalbroadcast;

import se.sics.kompics.PortType;

/**
 * Created by mikael on 2017-04-04.
 */
public class CRBPort extends PortType {
    {
        indication(CRBDeliver.class);
        request(CRBroadcast.class);
    }
}
