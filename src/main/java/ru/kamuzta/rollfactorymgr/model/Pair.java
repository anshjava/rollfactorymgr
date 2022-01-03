package ru.kamuzta.rollfactorymgr.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@ToString
@EqualsAndHashCode
public class Pair<F, S> {
    private F first;
    private S second;

    public Pair(Map.Entry<F, S> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public Pair(F first, S second) {
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

    public boolean isEmpty() {
        return first == null && second == null;
    }

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    public static <F, S> Pair<F, S> empty() {
        return new Pair<>(null, null);
    }
}
