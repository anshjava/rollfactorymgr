package ru.kamuzta.rollfactorymgr.ui;

public enum ExitStatus {
    SUCCESS(0),

    INIT_ERROR(1);

    private final int code;

    ExitStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
