package org.jono.medicalmodelsservice.service;

import static org.jono.medicalmodelsservice.utils.ListUtils.listToMapOfIdToItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForestBuilder<N, R extends NodeRelationship, D extends NodeData> {
    @Getter
    private final List<N> rootNodes;
    private final Map<String, N> allNodes;
    private final Set<String> added;
    private final List<D> nodeDataList;

    private final Function<D, N> nodeConstructorFn;
    private final Function<N, List<N>> getChildrenFn;

    public static <N, R extends NodeRelationship, D extends NodeData> List<N> buildForest(final List<D> nodeDataList,
            final List<R> relationshipList,
            final Function<D, N> nodeConstructorFn,
            final Function<N, List<N>> getChildrenFn) {
        return new ForestBuilder<>(nodeDataList, relationshipList, nodeConstructorFn, getChildrenFn).getRootNodes();
    }

    private ForestBuilder(final List<D> nodeDataList,
            final List<R> relationshipList,
            final Function<D, N> nodeConstructorFn,
            final Function<N, List<N>> getChildrenFn) {
        log.info(nodeDataList.toString());
        log.info(relationshipList.toString());

        this.rootNodes = new ArrayList<>();
        this.allNodes = new HashMap<>();
        this.added = new HashSet<>();
        this.nodeDataList = nodeDataList;

        this.nodeConstructorFn = nodeConstructorFn;
        this.getChildrenFn = getChildrenFn;

        final Map<String, D> docMap = listToMap(nodeDataList);
        for (final NodeRelationship relationship : relationshipList) {
            final D parentData = docMap.get(relationship.getParentId());
            final D childData = docMap.get(relationship.getChildId());
            if (Objects.nonNull(parentData) && Objects.nonNull(childData)) {
                added.add(parentData.getId());
                added.add(childData.getId());
                addNode(parentData, childData);
            } else {
                log.warn(
                        "There is a NodeRelationship with a parent or child not in the node data list. It will be "
                                + "ignored. "
                                + "parentId: {}, childId: {}, parentNode: {}, childNode: {}",
                        relationship.getParentId(), relationship.getChildId(), parentData, childData);
            }
        }
        addRemainingNodes();
    }

    private Map<String, D> listToMap(final List<D> nodeDataList) {
        return listToMapOfIdToItem(nodeDataList, NodeData::getId);
    }

    private void addNode(final D parentNodeData, final D childNodeData) {
        if (allNodes.containsKey(parentNodeData.getId())) {
            final N childNode = nodeConstructorFn.apply(childNodeData);
            getChildren(allNodes.get(parentNodeData.getId())).add(childNode);
            allNodes.put(childNodeData.getId(), childNode);
        } else {
            final N parentNode = nodeConstructorFn.apply(parentNodeData);
            final N childNode = nodeConstructorFn.apply(childNodeData);
            getChildren(parentNode).add(childNode);
            allNodes.put(parentNodeData.getId(), parentNode);
            allNodes.put(childNodeData.getId(), childNode);
            rootNodes.add(parentNode);
        }
    }

    private void addRemainingNodes() {
        for (final D nodeData : nodeDataList) {
            if (!added.contains(nodeData.getId())) {
                final N node = nodeConstructorFn.apply(nodeData);
                rootNodes.add(node);
            }
        }
    }

    private List<N> getChildren(final N parentNode) {
        return getChildrenFn.apply(parentNode);
    }
}
