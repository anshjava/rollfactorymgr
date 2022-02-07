package ru.kamuzta.rollfactorymgr.ui.table;

import javafx.scene.control.TableCell;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalCell<T> extends TableCell<T, BigDecimal> {
    private int scale;

    public BigDecimalCell(int scale) {
        super();
        this.scale = scale;
    }

    @Override
    protected void updateItem(BigDecimal item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText("0.0");
            return;
        }
        setText(item.setScale(scale, RoundingMode.HALF_UP).toString());
    }
}
