package jpabook.jpashop.exception;

public class NullEnoughStockException extends RuntimeException {

    public NullEnoughStockException() {
        super();
    }

    public NullEnoughStockException(String message) {
        super(message);
    }

    public NullEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullEnoughStockException(Throwable cause) {
        super(cause);
    }
}
