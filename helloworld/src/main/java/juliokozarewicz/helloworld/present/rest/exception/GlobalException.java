package juliokozarewicz.helloworld.present.rest.exception;

import jakarta.validation.ConstraintViolationException;
import juliokozarewicz.helloworld.present.rest.dto.StandardResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    private static final Logger logger = LoggerFactory.getLogger(
        GlobalException.class);

    // ======================================================= ( business INIT )
    @ExceptionHandler({juliokozarewicz.helloworld.domain.exception.DomainException.class})
    public ResponseEntity<StandardResponseDTO> handleDomainException(
        juliokozarewicz.helloworld.domain.exception.DomainException ex) {

        GlobalExceptionEnum restError = GlobalExceptionEnum.fromDomainError(ex.getError());

        return ResponseEntity
        .status(restError.statusCode)
        .body(
            new StandardResponseDTO.Builder()
            .statusCode(restError.statusCode)
            .messageCode(restError.statusMessage)
            .build()
        );

    }
    // ======================================================== ( business END )

    // ==================================================== ( DTO validation INIT )
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {

        List<Map<String, String>> fieldErrors = new LinkedList<>();

        ex.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            String[] parts = path.split("\\.");
            String field = parts[parts.length - 1];

            Map<String, String> item = new LinkedHashMap<>();
            item.put("field", field);
            item.put("message", violation.getMessage());
            fieldErrors.add(item);
        });

        return ResponseEntity
        .status(GlobalExceptionEnum.UNPROCESSABLE_ENTITY.statusCode)
        .body(
            new StandardResponseDTO.Builder()
                .statusCode(GlobalExceptionEnum.UNPROCESSABLE_ENTITY.statusCode)
                .messageCode(GlobalExceptionEnum.UNPROCESSABLE_ENTITY.statusMessage)
                .meta(Map.of("fieldErrors", fieldErrors))
                .build()
        );
    }
    // ==================================================== ( DTO validation END )

    // ==================================================== ( bad request INIT )
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        NoHandlerFoundException.class,
        HttpRequestMethodNotSupportedException.class,
        NoResourceFoundException.class
    })
    public ResponseEntity<StandardResponseDTO> handleBadRequest(Exception ex) {

        return ResponseEntity
        .status(GlobalExceptionEnum.BAD_REQUEST.statusCode)
        .body(
            new StandardResponseDTO.Builder()
            .statusCode(GlobalExceptionEnum.BAD_REQUEST.statusCode)
            .messageCode(GlobalExceptionEnum.BAD_REQUEST.statusMessage)
            .build()
        );

    }
    // ===================================================== ( bad request END )

    // =================================================== ( fallback 500 INIT )
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponseDTO> handleGeneric(Exception ex) {

        // logs
        logger.error(ex.toString());

        return ResponseEntity
        .status(GlobalExceptionEnum.INTERNAL_SERVER_ERROR.statusCode)
        .body(
            new StandardResponseDTO.Builder()
            .statusCode(GlobalExceptionEnum.INTERNAL_SERVER_ERROR.statusCode)
            .messageCode(GlobalExceptionEnum.INTERNAL_SERVER_ERROR.statusMessage)
            .build()
        );

    }
    // ==================================================== ( fallback 500 END )

}