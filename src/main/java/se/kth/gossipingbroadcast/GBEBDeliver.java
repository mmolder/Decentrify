package se.kth.gossipingbroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by mikael on 2017-04-03.
 */
public class GBEBDeliver implements KompicsEvent {

    private KAddress peer;
    private Object message;

    public GBEBDeliver(KAddress peer, Object message) {
        this.peer = peer;
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }

    public KAddress getPeer() {
        return peer;
    }
}
