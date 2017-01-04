package org.itracker.model;

/**
 * An interface to be implemented by java.lang.Enum classes that need
 * to associate a unique and constant integer code to their enum constants.
 * <p/>
 * <p>The main use case is to allow to persist an enum constant to
 * an integer rather than a string value, which is supported directly
 * by the java.lang.Enum class through the enum.name() and enum.valueOf(String)
 * methods. <br>
 * The enum.ordinal() position could be used, but isn't the best approach for
 * this use case because we don't have any control on it :
 * it is zero-based and changes if the position of the enum constant changes.
 * <br>
 * Using the enum name isn't satisfactory either because we would have
 * to update the database if we ever need to rename an enum constant. </p>
 * <p/>
 * <p>This class allows to migrate to Java 5 enums retaining full backwards
 * compatibility with iTracker 2, in which all enumerations were simply defined
 * as <code>static final int</code> fields. </p>
 * <p/>
 * <p>This interface allows to handle all such enums consistently
 * and to use a single Hibernate custom type to persist them all. </p>
 *
 * @author johnny
 */
public interface IntCodeEnum<E extends Enum<E>> {

    int DEFAULT_CODE = 1;

    /**
     * Returns the integer value representing this enum constant.
     *
     * @return unique constant as defined in iTracker 2
     */
    Integer getCode();

    /**
     * Returns a java.lang.Enum constant matching the given integer value.
     * <p/>
     * <p>This method should actually be static, so that we don't need
     * an enum constant instance to lookup another instance by code.
     * <br>
     * However Java interfaces don't allow static methods and Java 5 enums
     * must inherit java.lang.Enum directly. So there's no way to create
     * a common base class with a static fromCode(int) method
     * for all enums in our application for EnumCodeUserType to use
     * in a type-safe way! </p>
     *
     * <p>You should instead implement a static valueOf(int) wrapped by this method:
     * <pre>
     * static final E valueOf(Integer code) {
     *     for (E val: values()) {
     *         if (val.code == code) {
     *             return val;
     *         }
     *     }
     *     throw new IllegalArgumentException("Unknown code : " + code);
     * }
     *
     * </pre>
     * </p>
     *
     * @param code unique enum constant as defined in iTracker 2
     * @return java.lang.Enum constant instance for the given code
     * @throws IllegalArgumentException no matching enum constant for
     *                                  the given <code>code</code>
     */
     E fromCode(Integer code);

}
