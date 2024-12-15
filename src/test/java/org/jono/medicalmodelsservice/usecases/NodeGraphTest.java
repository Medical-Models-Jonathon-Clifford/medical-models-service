package org.jono.medicalmodelsservice.usecases;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class NodeGraphTest {
    @Test
    public void testNodeGraph() {
        NodeGraph nodeGraph = new NodeGraph();
        nodeGraph.addNode("1", "2");
        assertThat(nodeGraph, is(nodeGraph));
    }
}