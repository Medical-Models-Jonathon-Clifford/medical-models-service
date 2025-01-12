package org.jono.medicalmodelsservice.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static org.jono.medicalmodelsservice.utils.ListUtils.listToMapFn;

@Slf4j
public class GraphBuilder<N, R extends NodeRelationship, D extends NodeData> {
    @Getter
    private final List<N> rootNodes;
    private final Map<String, N> allNodes;
    private final Set<String> added;
    private final List<D> nodeDataList;

    private final Function<D, N> nodeConstructorFn;
    private final Function<N, List<N>> getChildrenFn;

    public static <N, R extends NodeRelationship, D extends NodeData> List<N> buildGraph(List<D> nodeDataList,
                                                                                         List<R> relationshipList,
                                                                                         Function<D, N> nodeConstructorFn,
                                                                                         Function<N, List<N>> getChildrenFn) {
        return new GraphBuilder<>(nodeDataList, relationshipList, nodeConstructorFn, getChildrenFn).getRootNodes();
    }

    private GraphBuilder(List<D> nodeDataList,
                         List<R> relationshipList,
                         Function<D, N> nodeConstructorFn,
                         Function<N, List<N>> getChildrenFn) {
        log.info(nodeDataList.toString());
        log.info(relationshipList.toString());

        this.rootNodes = new ArrayList<>();
        this.allNodes = new HashMap<>();
        this.added = new HashSet<>();
        this.nodeDataList = nodeDataList;

        this.nodeConstructorFn = nodeConstructorFn;
        this.getChildrenFn = getChildrenFn;

        Map<String, D> docMap = listToMap(nodeDataList);
        for (NodeRelationship relationship : relationshipList) {
            D parentData = docMap.get(relationship.getParentId());
            D childData = docMap.get(relationship.getChildId());
            if (Objects.nonNull(parentData) && Objects.nonNull(childData)) {
                added.add(parentData.getId());
                added.add(childData.getId());
                addNode(parentData, childData);
            } else {
                log.warn("There is a NodeRelationship with a parent or child not in the node data list. It will be " +
                                "ignored. parentId: {}, childId: {}, parentNode: {}, childNode: {}",
                        relationship.getParentId(), relationship.getChildId(), parentData, childData);
            }
        }
        addRemainingNodes();
    }

    private Map<String, D> listToMap(List<D> nodeDataList) {
        return listToMapFn(nodeDataList, NodeData::getId);
    }

    private void addNode(D parentNodeData, D childNodeData) {
        if (allNodes.containsKey(parentNodeData.getId())) {
            N childNode = nodeConstructorFn.apply(childNodeData);
            getChildren(allNodes.get(parentNodeData.getId())).add(childNode);
            allNodes.put(childNodeData.getId(), childNode);
        } else {
            N parentNode = nodeConstructorFn.apply(parentNodeData);
            N childNode = nodeConstructorFn.apply(childNodeData);
            getChildren(parentNode).add(childNode);
            allNodes.put(parentNodeData.getId(), parentNode);
            allNodes.put(childNodeData.getId(), childNode);
            rootNodes.add(parentNode);
        }
    }

    private void addRemainingNodes() {
        for (D nodeData : nodeDataList) {
            if (!added.contains(nodeData.getId())) {
                N node = nodeConstructorFn.apply(nodeData);
                rootNodes.add(node);
            }
        }
    }

    private List<N> getChildren(N parentNode) {
        return getChildrenFn.apply(parentNode);
    }
}
