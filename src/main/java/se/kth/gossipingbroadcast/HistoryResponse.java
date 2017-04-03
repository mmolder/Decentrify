package se.kth.gossipingbroadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;

/**
 * Created by mikael on 2017-04-03.
 */
public class HistoryResponse implements KompicsEvent {

    private HashMap<KAddress, Object> past;

    public HistoryResponse(HashMap<KAddress, Object> past) {
        this.past = new HashMap<>(past);
    }

    public HashMap<KAddress, Object> getPast() {
        return past;
    }
}
