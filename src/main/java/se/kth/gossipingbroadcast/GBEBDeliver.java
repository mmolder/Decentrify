package se.kth.gossipingbroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by mikael on 2017-04-03.
 */
public class GBEBDeliver implements KompicsEvent {

    public KAddress source;
    public Object payload;

    public GBEBDeliver(KAddress source, Object payload) {
        this.source = source;
        this.payload = payload;
    }
}
