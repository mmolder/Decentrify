package se.kth.causalbroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by mikael on 2017-04-04.
 */
public class CRBDeliver implements KompicsEvent {
    private KAddress source;
    private Object message;

    public CRBDeliver(KAddress source, Object message) {
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
