package ru.kamuzta.rollfactorymgr.ui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

//todo
public class ButtonLogged extends Button {

    public ButtonLogged() {
    }

    public ButtonLogged(String text) {
        super(text);
    }

    public ButtonLogged(String text, Node graphic) {
        super(text, graphic);
    }

    @Override
    public void fire() {
        if (!isDisabled()) {

            String graphicText = "";
            if (getGraphic()!=null && getGraphic() instanceof TextFlow){
                ObservableList<Node> childs = ((TextFlow) getGraphic()).getChildren();
                if (!childs.isEmpty() && childs.get(0) instanceof Text){
                    graphicText = "/"+((Text)childs.get(0)).getText()+"";
                }
            }
            super.fire();
        }
    }
}
