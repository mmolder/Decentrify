package se.kth.graph;

import java.util.UUID;

/**
 * Created by mikael on 2017-05-20.
 */
public class Vertex {
    public String id;

    public Vertex() {
        this.id = UUID.randomUUID().toString();
    }
}
