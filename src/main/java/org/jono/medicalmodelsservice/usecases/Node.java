package org.jono.medicalmodelsservice.usecases;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Node {
    private String id;
    private List<Node> children;

    public Node(String id) {
        this.id = id;
        this.children = new ArrayList<>();
    }
}
