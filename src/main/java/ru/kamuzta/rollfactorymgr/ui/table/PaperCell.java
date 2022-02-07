package ru.kamuzta.rollfactorymgr.ui.table;

import javafx.scene.control.TableCell;
import org.apache.commons.lang3.StringUtils;
import ru.kamuzta.rollfactorymgr.model.Paper;

import java.math.RoundingMode;

public class PaperCell<T> extends TableCell<T, Paper> {

    @Override
    protected void updateItem(Paper item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(StringUtils.EMPTY);
            return;
        }
        setText(item.getWeight().setScale(0, RoundingMode.HALF_UP).toString());
    }
}
