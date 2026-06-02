package juliokozarewicz.accounts.adapter.rest.dto;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class AccountsRequestDTO {

    // Validate IP
    public void validateUserIp(String userIp) {

        // Regex
        Pattern IP_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4]" +
            "[0-9]|[01]?[0-9][0-9]?)$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"
        );

        if (
            userIp == null ||
            userIp.isEmpty() ||
            !IP_PATTERN.matcher(userIp).matches()
        ) {

            // call custom error
            throw new DomainException(DomainExceptionEnum.BAD_REQUEST);

        }

    }

    // Validate Agent
    public void validateUserAgent(String userAgent) {

        // Regex
        Pattern USER_AGENT_PATTERN = Pattern.compile(
            "^[\\w\\d\\s\\.\\/\\-\\(\\)\\;\\,\\:]+$"
        );

        if (
            userAgent == null ||
            userAgent.isEmpty() ||
            !USER_AGENT_PATTERN.matcher(userAgent).matches()
        ) {

            // call custom error
            throw new DomainException(DomainExceptionEnum.BAD_REQUEST);

        }
    }

}