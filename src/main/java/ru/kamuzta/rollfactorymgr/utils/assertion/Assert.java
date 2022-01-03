package ru.kamuzta.rollfactorymgr.utils.assertion;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Collection;

public class Assert {

    /**
     * Verifies that the passed object is not null,
     * and otherwise throws an AssertException.
     * <p/>
     * Return testee
     *
     * @param testee checked object
     * @return testee if it is not null
     * @throws AssertException if testee == null
     */
    public static <T> T notNull(@Nullable T testee) throws AssertException {
        return notNull(testee, "assertion failed: testee must not be null");
    }

    /**
     * Verifies that the passed object is not null,
     * and otherwise throws an AssertException.
     * <p/>
     * Return testee
     *
     * @param testee checked object
     * @param claim  message
     * @return testee if it is not null
     * @throws AssertException if testee == null
     */
    public static <T> T notNull(@Nullable T testee, @NotNull String claim) throws AssertException {
        if (testee == null) {
            throw new AssertException(claim);
        }
        return testee;
    }

    /**
     * Makes sure, that String is not <c>null</c> and not empty.
     * <p/>
     *
     * @param testee checked String
     * @return testee, if it is not null or empty
     * @throws AssertException if testee == null or == ""
     */
    public static String notNullAndNotEmpty(@Nullable String testee) throws AssertException {
        isFalse(Strings.isNullOrEmpty(testee), "assertion failed: testee must not neither null neither empty");
        return testee;
    }

    public static void isNotEmpty(@NotNull Collection collection) throws AssertException {
        isFalse(collection.isEmpty(), "Collection must be not empty");
    }

    public static void isEmpty(@NotNull Collection collection) throws AssertException {
        isTrue(collection.isEmpty(), "Collection must be empty");
    }

    /**
     * Throw AssertException.
     *
     * @throws AssertException
     */
    public static void fail() throws AssertException {
        isTrue(false, "assertion failed.");
    }

    /**
     * Makes sure the passed expression is false,
     * and otherwise throws an AssertException.
     *
     * @param test expression to be tested
     * @throws AssertException if the expression is true
     */
    public static void isFalse(boolean test) throws AssertException {
        isFalse(test, "assertion failed: test expression must be false");
    }

    /**
     * Makes sure the passed expression is false,
     * and otherwise throws an AssertException.
     *
     * @param test  expression to be tested
     * @param claim message
     * @throws AssertException if the expression is true
     */
    public static void isFalse(boolean test, @NotNull String claim) throws AssertException {
        isTrue(!test, claim);
    }

    /**
     * Makes sure the passed expression is true,
     * and otherwise throws an AssertException.
     *
     * @param test expression to be tested
     * @throws AssertException if the expression is false
     */
    public static void isTrue(boolean test) throws AssertException {
        isTrue(test, "assertion failed: test expression must be true");
    }

    /**
     * Makes sure the passed expression is true,
     * and otherwise throws an AssertException.
     *
     * @param test  expression to be tested
     * @param claim message
     * @throws AssertException if the expression is false
     */
    public static void isTrue(boolean test, @NotNull String claim) throws AssertException {
        if (!test) {
            throw new AssertException(claim);
        }
    }

    /**
     * Verifies, that value has 2-digit scale
     *
     * @param value number
     * @throws AssertException if scale is not 2
     */
    public static void scaleIs2(@NotNull BigDecimal value) throws AssertException {
        equals(2, value.scale(), "Number " + value + " should have 2 digits after decimal point.");
    }

    /**
     * Verifies that the passed objects are equal (in the sense of the equals() method). Otherwise
     * throws #AssertException
     *
     * @param o1    first object
     * @param o2    second object
     * @throws AssertException if objects are not equal
     */
    public static void equals(@NotNull Object o1, @NotNull Object o2) throws AssertException {
        equals(o1, o2, "Assertion failed: objects must be equal");
    }


    /**
     * Verifies that the passed objects are equal (in the sense of the equals() method). Otherwise
     * throws #AssertException
     *
     * @param o1    first object
     * @param o2    second object
     * @param claim message
     * @throws AssertException if objects are not equal
     */
    public static void equals(@NotNull Object o1, @NotNull Object o2, @NotNull String claim) throws AssertException {
        if (!o1.equals(o2)) {
            throw new AssertException(claim);
        }
    }
}

