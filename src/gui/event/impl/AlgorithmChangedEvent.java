package gui.event.impl;

import gui.event.Event;
import algo.Algorithm;

public class AlgorithmChangedEvent implements Event {
    private final Algorithm<?,?>  algorithm;

    public AlgorithmChangedEvent(Algorithm<?,?> algorithm) {
        this.algorithm = algorithm;
    }

    public Algorithm<?,?> getAlgorithm() {
        return algorithm;
    }
}
