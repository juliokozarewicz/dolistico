package juliokozarewicz.helloworld.interfaces.rest.dto;

import java.time.LocalDateTime;

public record StandardResponse(

    LocalDateTime timestamp,
    String errorCode,
    int status

) {

    public static StandardResponse of( String errorCode, int status ) {

        return new StandardResponse(
            LocalDateTime.now(),
            errorCode,
            status

        );

    }

}
