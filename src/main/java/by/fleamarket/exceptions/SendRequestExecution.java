package by.fleamarket.exceptions;

public class SendRequestExecution extends RuntimeException {

    public SendRequestExecution(String message) {
        super(message);
    }

    public SendRequestExecution(String message, Throwable cause) {
        super(message, cause);
    }

    public SendRequestExecution(Throwable cause) {
        super(cause);
    }
}
