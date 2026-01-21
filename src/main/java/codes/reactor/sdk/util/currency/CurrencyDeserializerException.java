package codes.reactor.sdk.util.currency;

import lombok.Getter;

@Getter
public class CurrencyDeserializerException extends RuntimeException {

    private final ErrorCode errorCode;

    public CurrencyDeserializerException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CurrencyDeserializerException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public enum ErrorCode {
        POSITIVE_OVERFLOW,
        NEGATIVE_OVERFLOW,

        FOUND_SIGN_CHARACTER,
        FOUND_INVALID_NUMBER,

        EMPTY_STRING
    }
}