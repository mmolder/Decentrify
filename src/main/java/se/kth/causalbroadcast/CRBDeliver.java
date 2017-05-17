package se.kth.causalbroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

/**
 * Created by mikael on 2017-04-04.
 */
public class CRBDeliver implements KompicsEvent {
    public KAddress source;
    public Object payload;

    public CRBDeliver(KAddress source, Object payload) {
        this.source = source;
        this.payload = payload;
    }
}
