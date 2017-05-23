package se.kth.graph;

/**
 * Created by mikael on 2017-05-20.
 */
public class Edge {
    public Vertex v1;
    public Vertex v2;

    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(o == this) {
            return true;
        }
        return false;
    }
}
