package se.kth.gossipingbroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;

/**
 * Created by mikael on 2017-04-03.
 */
public class HistoryResponse implements KompicsEvent {

    public HashMap<Object, KAddress> past;

    public HistoryResponse(HashMap<Object, KAddress> past) {
        this.past = new HashMap<>(past);
    }
}
