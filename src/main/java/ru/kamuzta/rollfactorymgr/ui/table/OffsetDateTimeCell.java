package ru.kamuzta.rollfactorymgr.ui.table;

import javafx.scene.control.TableCell;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeCell<T> extends TableCell<T, OffsetDateTime> {

    @Override
    protected void updateItem(OffsetDateTime item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(StringUtils.EMPTY);
            return;
        }
        setText(item.format(DateTimeFormatter.ISO_DATE));
    }
}
