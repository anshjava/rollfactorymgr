package ru.kamuzta.rollfactorymgr.ui.table;

import javafx.scene.control.TableCell;
import org.apache.commons.lang3.StringUtils;
import ru.kamuzta.rollfactorymgr.model.RollType;

public class RollTypeCell<T> extends TableCell<T, RollType> {

    @Override
    protected void updateItem(RollType item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(StringUtils.EMPTY);
            return;
        }
        setText(item.getTypeName());
    }
}
