package dk.siema.siemaexamproject.bll.exceptions;

public class DalException extends RuntimeException {
    public DalException(String message) {
        super(message);
    }
    public DalException(String message, Throwable cause) {
        super(message, cause);
    }
}
