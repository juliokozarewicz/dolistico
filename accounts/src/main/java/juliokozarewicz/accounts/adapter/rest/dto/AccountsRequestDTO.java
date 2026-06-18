package juliokozarewicz.accounts.adapter.rest.dto;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class AccountsRequestDTO {

    public void validateUserIp(String userIp) {

        if (userIp == null || userIp.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BAD_REQUEST);
        }

        userIp = userIp.trim();

        if (userIp.length() > 45) {
            throw new DomainException(DomainExceptionEnum.BAD_REQUEST);
        }

        try {
            InetAddress.getByName(userIp);
        } catch (Exception e) {
            throw new DomainException(DomainExceptionEnum.BAD_REQUEST);
        }

    }

    public void validateUserAgent(String userAgent) {

        if (userAgent == null || userAgent.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BAD_REQUEST);
        }

        userAgent = userAgent.trim();

        if (userAgent.length() > 512) {
            throw new DomainException(DomainExceptionEnum.BAD_REQUEST);
        }

        if (
            userAgent.contains("\r") ||
            userAgent.contains("\n") ||
            userAgent.indexOf('\0') >= 0
        ) {
            throw new DomainException(DomainExceptionEnum.BAD_REQUEST);
        }

        for (char c : userAgent.toCharArray()) {

            if (Character.isISOControl(c) && c != '\t') {
                throw new DomainException(DomainExceptionEnum.BAD_REQUEST);
            }

        }

    }

}