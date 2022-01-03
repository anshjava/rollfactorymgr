package ru.kamuzta.rollfactorymgr.exception;

import org.jetbrains.annotations.NotNull;

public class ApplicationInitException extends UserFriendlyException {
    private final String title;
    private final String content;

    public ApplicationInitException(@NotNull String title, @NotNull String content) {
        super(content);
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
