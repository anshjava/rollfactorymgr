package ru.kamuzta.rollfactorymgr.model;

import lombok.ToString;

import java.util.Map;

/**
 * Pair without cheching equals and hashcode
 */
@ToString
public class IdentityPair<F, S> {
    private F first;
    private S second;

    public IdentityPair(Map.Entry<F, S> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public IdentityPair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }
}