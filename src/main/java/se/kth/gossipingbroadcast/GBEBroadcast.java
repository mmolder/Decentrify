package se.kth.gossipingbroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by mikael on 2017-04-03.
 */
public class GBEBroadcast implements KompicsEvent {

    public Object payload;
    public KAddress source;

    public GBEBroadcast(KAddress source, Object payload) {
        this.source = source;
        this.payload = payload;
    }
}
