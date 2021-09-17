package common.exceptions;

/**
 * при слишком маленьком значении поля
 */
public class FieldValueTooSmallException extends InputException {
    public FieldValueTooSmallException(String minValue) {
        super("Значение должно быть больше  " + minValue + ".");
    }
}