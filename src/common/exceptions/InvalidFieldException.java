package common.exceptions;

public class InvalidFieldException extends InputException {
    public InvalidFieldException(String fieldName) {
        super("Это не "+ fieldName);
    }
}
