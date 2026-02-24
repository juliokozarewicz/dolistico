package juliokozarewicz.helloworld.interfaces.rest.exception;

import juliokozarewicz.helloworld.domain.exeption.BusinessException;
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

    private static final Logger logger =
        LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ======================================================= ( business INIT )
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardResponse> handleBusiness(BusinessException ex) {

        return ResponseEntity
            .status(ex.getErrorCode().getHttpStatus())
            .body(StandardResponse.of(
                ex.getErrorCode().name(),
                ex.getErrorCode().getHttpStatus()
            ));
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
            .body(StandardResponse.of(
                "BAD_REQUEST",
                400
            ));
    }
    // ===================================================== ( bad request END )

    // =================================================== ( fallback 500 INIT )
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleGeneric(Exception ex) {

        // logs
        logger.error(ex.toString());

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(StandardResponse.of(
                "UNEXPECTED_ERROR",
                500
            ));

    }
    // ==================================================== ( fallback 500 END )

}