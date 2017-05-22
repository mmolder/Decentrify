package se.kth.graph;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-22.
 */
public class RemoveVertex implements KompicsEvent {
    public Vertex w;

    public RemoveVertex(Vertex w) {
        this.w = w;
    }
}
