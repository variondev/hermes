package dev.varion.hermes.distributed;

public class DistributedLockException extends Exception {

    DistributedLockException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
