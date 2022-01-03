package ru.kamuzta.rollfactorymgr.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Calculation of the first screen controls
 * and return to the first control if the focus goes beyond the screen
 * Tabs must be focusable to work correctly
 */
@Slf4j
public class FocusController {
    private static final int MAX_COUNT = 2;
    private final List<Node> firstFocusElements;

    private Node lastFocusedNodeBeforeHide;
    private final Parent parent;

    public FocusController(Parent parent) {
        this.parent = parent;
        firstFocusElements = findFirstFocusableNodes(parent);
    }

    public void tryReturnFocus(Node focusedNode){
        if (focusedNode == null || parentIs(focusedNode, parent)){
            return;
        }
        ofNullable(getFirstFocusableNode()).ifPresent(Node::requestFocus);
    }


    public void requestFocusOnLastOrFirst(){
        Node node = lastFocusedNodeBeforeHide != null ? lastFocusedNodeBeforeHide : getFirstFocusableNode();
        ofNullable(node).ifPresent(Node::requestFocus);
    }

    public Node getLastFocusedNodeBeforeHide() {
        return lastFocusedNodeBeforeHide;
    }

    public void setLastFocusedNodeBeforeHide(Node lastFocusedNodeBeforeHide) {
        this.lastFocusedNodeBeforeHide = lastFocusedNodeBeforeHide;
    }

    private Node getFirstFocusableNode(){
        for (Node node : firstFocusElements) {
            if (!node.isDisable()) {
                return node;
            }
        }
        return null;
    }

    private List<Node> getChilds(Node node) {
        List<Node> result = new ArrayList<>();
        if (node instanceof TabPane) {
            TabPane tabPane = (TabPane) node;
            tabPane.getTabs().forEach(tab -> {
                result.add(tab.getContent());
            });
        }else if (node instanceof ScrollPane){
            ScrollPane scrollPane = (ScrollPane) node;
            result.add(scrollPane.getContent());
        }else if (node instanceof Parent){
            result.addAll( ((Parent)node).getChildrenUnmodifiable());
        }

        return result;
    }

    private List<Node> findFirstFocusableNodes(Parent view) {
        List<Node> result = new ArrayList<>();
        processNodes(view, result);
        return result;
    }

    private boolean parentIs(Node node, Node parent) {
        boolean in = false;
        while (node != null){
            if (parent == node){
                in = true;
                break;
            }
            node = node.getParent();
        }
        return in;
    }

    private void processNodes(Node node, List<Node> result) {
        if (isSuitable(node)){
            result.add(node);
            if (isFull(result)) {
                return;
            }
        }
        if (node instanceof Parent) {
            List<Node> nodes = getChilds(node);
            for (Node tNode : nodes) {
                processNodes(tNode, result);
                if (isFull(result)) {
                    return;
                }
            }
        }
    }

    private boolean isFull(List<Node> result) {
        return result.size() > MAX_COUNT;
    }

    private boolean isSuitable(Node node) {
        return node instanceof Control && node.isFocusTraversable() &&  !(node instanceof ScrollPane);
    }
}
