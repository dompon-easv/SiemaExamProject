package dk.siema.siemaexamproject.bll.exceptions;

public class DataAccessException extends ServiceException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}