package ru.kamuzta.rollfactorymgr.ui.table;

import javafx.scene.control.TableCell;
import org.apache.commons.lang3.StringUtils;
import ru.kamuzta.rollfactorymgr.model.WidthType;

import java.math.RoundingMode;

public class WidthTypeCell<T> extends TableCell<T, WidthType> {

    @Override
    protected void updateItem(WidthType item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(StringUtils.EMPTY);
            return;
        }
        setText(item.getWidth().setScale(0, RoundingMode.HALF_UP).toString());
    }
}
