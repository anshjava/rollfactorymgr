package ru.kamuzta.rollfactorymgr.ui.table;

import javafx.scene.control.TableCell;
import org.apache.commons.lang3.StringUtils;
import ru.kamuzta.rollfactorymgr.model.client.ClientState;

public class ClientStateCell<T> extends TableCell<T, ClientState> {

    @Override
    protected void updateItem(ClientState item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(StringUtils.EMPTY);
            return;
        }
        setText(item.toString());
    }
}
