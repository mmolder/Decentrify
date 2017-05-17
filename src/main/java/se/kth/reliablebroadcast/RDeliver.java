package se.kth.reliablebroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;

/**
 * Created by mikael on 2017-04-03.
 */
public class RDeliver implements KompicsEvent {

    private KAddress source;
    private Object payload;
    private HashMap<KAddress, Object> past = new HashMap<>();

    public RDeliver(KAddress source, Object payload, HashMap<KAddress, Object> past) {
        this.source = source;
        this.payload = payload;
        this.past = past;
    }

    public KAddress getSource() {
        return source;
    }

    public Object getPayload() {
        return payload;
    }

    public HashMap<KAddress, Object> getPast() {
        return past;
    }
}
