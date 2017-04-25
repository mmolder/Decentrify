package se.kth.gossipingbroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by mikael on 2017-04-03.
 */
public class GBEBroadcast implements KompicsEvent {

    private Object message;
    private KAddress source;

    public GBEBroadcast(KAddress source, Object message) {
        this.source = source;
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }

    public KAddress getSource() {
        return source;
    }
}
