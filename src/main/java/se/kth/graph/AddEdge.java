package se.kth.graph;

import se.sics.kompics.KompicsEvent;

/**
 * Created by mikael on 2017-05-22.
 */
public class AddEdge implements KompicsEvent {
    public Edge e;

    public AddEdge(Edge e) {
        this.e = e;
    }
}
