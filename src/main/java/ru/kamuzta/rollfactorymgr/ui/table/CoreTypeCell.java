package ru.kamuzta.rollfactorymgr.ui.table;

import javafx.scene.control.TableCell;
import org.apache.commons.lang3.StringUtils;
import ru.kamuzta.rollfactorymgr.model.roll.CoreType;

import java.math.RoundingMode;

public class CoreTypeCell<T> extends TableCell<T, CoreType> {

    @Override
    protected void updateItem(CoreType item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(StringUtils.EMPTY);
            return;
        }
        setText(item.getDiameter().setScale(0, RoundingMode.HALF_UP).toString());
    }
}
