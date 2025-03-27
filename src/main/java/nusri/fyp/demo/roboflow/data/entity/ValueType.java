package nusri.fyp.demo.roboflow.data.entity;

import lombok.Getter;

/**
 * Enum representing the different value types that can be used in the system.
 * <br> This enum defines various data types that can be used in queries or operations within the system.
 * <br> Each value type represents a specific kind of data, such as integers, floats, booleans, and more.
 */
@Getter
public enum ValueType {

    /**
     * Represents any type of value.
     * <br> This is a generic type that can accept any kind of value.
     */
    ANY("any"),

    /**
     * Represents an integer type value.
     * <br> Used for whole numbers without decimals.
     */
    INTEGER("integer"),

    /**
     * Represents a floating-point type value.
     * <br> Used for decimal numbers.
     */
    FLOAT("float"),

    /**
     * Represents a boolean type value.
     * <br> Used for true or false values.
     */
    BOOLEAN("boolean"),

    /**
     * Represents a dictionary type value.
     * <br> Used for key-value pairs (mapping).
     */
    DICT("dict"),

    /**
     * Represents a list type value.
     * <br> Used for ordered collections of elements.
     */
    LIST("list"),

    /**
     * Represents a string type value.
     * <br> Used for text-based data.
     */
    STRING("string");

    private final String value;

    /**
     * Constructor for the enum value type.
     *
     * @param value the string representation of the value type
     */
    ValueType(String value) {
        this.value = value;
    }
}
