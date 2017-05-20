package se.kth.app.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mikael on 2017-05-20.
 */
public class TwoPhaseGraph {
    public Set<Vertex> verticesAdded = new HashSet<>();
    public Set<Vertex> verticesRemoved = new HashSet<>();
    public Set<Edge> edgesAdded = new HashSet<>();
    public Set<Edge> edgesRemoved = new HashSet<>();

    /** lookup vertix v, returns true if present in verticesAdded and not in verticesRemoved */
    public boolean lookup(Vertex v) {
        return verticesAdded.contains(v) && !verticesRemoved.contains(v);
    }

    public boolean lookup(Edge e) {
        return lookup(e.v1) && lookup(e.v2) && edgesAdded.contains(e) && !edgesRemoved.contains(e);
    }

    public void addVertex(Vertex w, boolean atSource) {
        // at source
        if(atSource) {
            verticesAdded.add(w);
        }
        // downstream
        else {
            verticesAdded.add(w);
        }
    }

    public Edge addEdge(Vertex u, Vertex v, boolean atSource) {
        // at source
        if(atSource) {
            // pre: lookup(u) && lookup(v)
            if(lookup(u) && lookup(v)) {
                edgesAdded.add(new Edge(u, v));
                return new Edge(u, v);
            }
        }
        // downstream
        else {
            edgesAdded.add(new Edge(u, v));
        }
        return null;
    }

    public Vertex removeVertex(Vertex w, boolean atSource) {
        // at source
        if(atSource) {
            // pre: lookup(w)
            if(lookup(w)) {
                // pre: ∀(u,v)∈(EA\ER):u != w && v != w
                for(Edge e : edgesAdded) {
                    if(!edgesRemoved.contains(e)) {
                        if(!e.v1.equals(w) && !e.v2.equals(w)) {
                            return w;
                        }
                    }
                }
            }
        }
        //downstream
        else {
            // pre: addVertex(w) delivered
            if(lookup(w)) {
                verticesRemoved.add(w);
            }
        }
        return null;
    }

    public Edge removeEdge(Edge e, boolean atSource) {
        // at source
        if(atSource) {
            // pre: lookup((u,v))
            if(lookup(e)) {
                edgesRemoved.add(e);
                return e;
            }
        }
        // downstream
        else {
            // pre: addEdge(u,v) delivered
            if(lookup(e)) {
                edgesRemoved.add(e);
            }
        }
        return null;
    }
}
