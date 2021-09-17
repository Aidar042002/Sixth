package common.exceptions;

public class NotFileButDirectoryException extends UncorrectFieldException {
    public NotFileButDirectoryException(String filePath) {
        super("Это директория : (" + filePath + ")");
    }
}
