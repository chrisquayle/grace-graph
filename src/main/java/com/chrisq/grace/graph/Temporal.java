package com.chrisq.grace.graph;

import java.time.Instant;

public interface Temporal {
    Instant getAsAtUtc();
    void setAsAtUtc(Instant asAtUtc);
}
