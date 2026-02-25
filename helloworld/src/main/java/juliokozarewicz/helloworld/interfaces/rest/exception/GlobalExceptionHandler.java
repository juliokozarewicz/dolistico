package juliokozarewicz.helloworld.interfaces.rest.exception;

import juliokozarewicz.helloworld.interfaces.rest.dto.StandardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(
        GlobalExceptionHandler.class);

    // ======================================================= ( business INIT )
    @ExceptionHandler({juliokozarewicz.helloworld.domain.exception.DomainException.class})
    public ResponseEntity<StandardResponse> handleDomainException(
        juliokozarewicz.helloworld.domain.exception.DomainException ex) {

        GlobalErrorEnum restError = GlobalErrorEnum.fromDomainError(ex.getError());

        return ResponseEntity
            .status(restError.statusCode)
            .body(
                new StandardResponse.Builder()
                    .statusCode(restError.statusCode)
                    .messageCode(restError.statusMessage)
                    .build()
            );
    }
    // ======================================================== ( business END )

    // ==================================================== ( bad request INIT )
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        NoHandlerFoundException.class,
        HttpRequestMethodNotSupportedException.class,
        NoResourceFoundException.class
    })
    public ResponseEntity<StandardResponse> handleBadRequest(Exception ex) {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                new StandardResponse.Builder()
                    .statusCode(GlobalErrorEnum.BAD_REQUEST.statusCode)
                    .messageCode(GlobalErrorEnum.BAD_REQUEST.statusMessage)
                    .build()
            );

    }
    // ===================================================== ( bad request END )

    // =================================================== ( fallback 500 INIT )
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleGeneric(Exception ex) {

        // logs
        logger.error(ex.toString());

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                new StandardResponse.Builder()
                    .statusCode(GlobalErrorEnum.INTERNAL_SERVER_ERROR.statusCode)
                    .messageCode(GlobalErrorEnum.INTERNAL_SERVER_ERROR.statusMessage)
                    .build()
            );

    }
    // ==================================================== ( fallback 500 END )

}