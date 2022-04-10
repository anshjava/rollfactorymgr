package ru.kamuzta.rollfactorymgr.utils;

import org.mockito.Mockito;

/**
 *
 */
public final class MockitoUtils {
    /**
     * Wrapper of {@link Mockito#mock(Class)} for configurig  mocks in Stream-style (см. {@link Mockito#when(Object) when()})
     */
    public static <V> MockBinder<V> mock(Class<V> mockClass) {
        return new MockBinder<>(mockClass);
    }

    public static class MockBinder<V> {
        private V mock;

        public MockBinder(Class<V> mock) {
            this.mock = Mockito.mock(mock);
        }

        public MockBinder<V> andApply(ExceptionalConsumer<V> supplier) {
            try {
                supplier.apply(mock);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public V get() {
            return mock;
        }
    }

    public interface ExceptionalConsumer<T> {
        void apply(T mock) throws Exception;
    }
}
