package com.chrisq.grace.graph;

import java.io.Serializable;
import java.time.Instant;

public interface GraphProvider extends Serializable {
    Graph getGraph(String graphId, Instant asAtUtc);
}
