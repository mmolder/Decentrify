package se.kth.graph;

import se.kth.growonlyset.TwoPhaseSet;

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

    public TwoPhaseSet vertices = new TwoPhaseSet();
    public TwoPhaseSet edges = new TwoPhaseSet();

    /** lookup vertix v, returns true if present in verticesAdded and not in verticesRemoved */
    public boolean lookup(Vertex v) {
        //return verticesAdded.contains(v) && !verticesRemoved.contains(v);
        return vertices.contains(v);
    }

    /** lookup edge e, return true of both vertices at the end exists and is a valid edge (not removed) */
    public boolean lookup(Edge e) {
        //return lookup(e.v1) && lookup(e.v2) && edgesAdded.contains(e) && !edgesRemoved.contains(e);
        return lookup(e.v1) && lookup(e.v2) && edges.contains(e);
    }

    /** addVertex adds a new vertex to the graph */
    public boolean addVertex(Vertex w, boolean atSource) {
        // at source
        if(atSource) {
            //verticesAdded.add(w);
            //vertices.add(w);
            return true;
        }
        // downstream
        else {
            //verticesAdded.add(w);
            vertices.add(w);
        }
        return false;
    }

    /** addEdge adds a new edge in the graph, it has to first pass the precondition at the source before it can be
     *  added downstream */
    public boolean addEdge(Edge e, boolean atSource) {
        // at source
        if(atSource) {
            // pre: lookup(u) && lookup(v)
            if(lookup(e.v1) && lookup(e.v2)) {
                //edgesAdded.add(new Edge(u, v));
                //edges.add(new Edge(e.v1, e.v2));
                return true;
                //return new Edge(e.v1, e.v2);
            }
        }
        // downstream
        else {
            //edgesAdded.add(new Edge(u, v));
            edges.add(new Edge(e.v1, e.v2));
        }
        return false;
    }

    /** removeVertex removes a vertex from the graph, it has to pass a number of preconditions at the source before
     *  it can be removed downstream */
    public boolean removeVertex(Vertex w, boolean atSource) {
        // at source
        if(atSource) {
            // pre: lookup(w)
            if(lookup(w)) {
                // pre: ∀(u,v)∈(EA\ER):u != w && v != w
                /*
                for(Edge e : edgesAdded) {
                    if(!edgesRemoved.contains(e)) {
                        if(!e.v1.equals(w) && !e.v2.equals(w)) {
                            return w;
                        }
                    }
                }*/

                for(int i = 0; i < edges.size(); i++) {
                    Edge v = (Edge)edges.elementAt(i);
                    if(lookup(v) && !v.v1.equals(w) && !v.v2.equals(w)) {
                        return true;
                        //return w;
                    }
                }
            }
        }
        //downstream
        else {
            // pre: addVertex(w) delivered
            if(lookup(w)) {
                //verticesRemoved.add(w);
                vertices.remove(w);
            }
        }
        return false;
    }

    /** removeEdge removes an edge from the graph, at the source it only has to exist before it can be removed,
     *  downstram it has to been delivered first */
    public boolean removeEdge(Edge e, boolean atSource) {
        // at source
        if(atSource) {
            // pre: lookup((u,v))
            if(lookup(e)) {
                //edgesRemoved.add(e);
                //edges.remove(e);
                //return e;
                return true;
            }
        }
        // downstream
        else {
            // pre: addEdge(u,v) delivered
            if(lookup(e)) {
                //edgesRemoved.add(e);
                edges.remove(e);
            }
        }
        return false;
        //return null;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertices: " + vertices.print());
        sb.append(" Edges: " + edges.print());
        return sb.toString();
    }

    public void printDetails() {
        System.out.println("Graph contains:");
        for(int i = 0; i < edges.size(); i++) {
            Edge e = (Edge)edges.elementAt(i);
            System.out.println("["+ e + " : {" + e.v1 + ", " + e.v2 + "}]");
        }
    }
}
