package se.kth.graph;

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

    /** lookup edge e, return true of both vertices at the end exists and is a valid edge (not removed) */
    public boolean lookup(Edge e) {
        return lookup(e.v1) && lookup(e.v2) && edgesAdded.contains(e) && !edgesRemoved.contains(e);
    }

    /** addVertex adds a new vertex to the graph */
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

    /** addEdge adds a new edge in the graph, it has to first pass the precondition at the source before it can be
     *  added downstream */
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

    /** removeVertex removes a vertex from the graph, it has to pass a number of preconditions at the source before
     *  it can be removed downstream */
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

    /** removeEdge removes an edge from the graph, at the source it only has to exist before it can be removed,
     *  downstram it has to been delivered first */
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
