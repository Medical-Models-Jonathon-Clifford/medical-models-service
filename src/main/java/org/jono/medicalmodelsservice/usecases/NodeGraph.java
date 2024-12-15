package org.jono.medicalmodelsservice.usecases;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class NodeGraph {
    private List<Node> topLevelNodes;
    private Map<String, Node> allNodes;

    public NodeGraph() {
        this.topLevelNodes = new ArrayList<>();
        this.allNodes = new HashMap<>();
    }

    public void addNode(String parentId, String childId) {
        if (allNodes.containsKey(parentId)) {
            allNodes.get(parentId).getChildren().add(new Node(childId));
        } else {
            Node parentNode = new Node(parentId);
            Node childNode = new Node(childId);
            parentNode.getChildren().add(childNode);
            allNodes.put(parentId, parentNode);
            topLevelNodes.add(parentNode);
        }
    }
}
