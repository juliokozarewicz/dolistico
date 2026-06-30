package juliokozarewicz.accounts.domain.entity;

import juliokozarewicz.accounts.domain.exception.DomainException;
import juliokozarewicz.accounts.domain.exception.DomainExceptionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor
public class AccountsConfigEntity {

    // ===================================================== ( validation init )

    private static final Pattern CONFIG_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-]+$");
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?://).{1,512}$");

    // ====================================================== ( validation end )

    // ==================================================== ( constructor init )

    private UUID id;
    private String configName;
    private String configValue;

    public AccountsConfigEntity(

        UUID id,
        String configName,
        String configValue

    ) {

        this.id = id;
        this.configName = configName;
        this.configValue = configValue;

        validateBusinessRules();

    }

    // ===================================================== ( constructor end )

    private void validateBusinessRules() {

        if (id == null) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        validateConfigName(configName);
        validateConfigValue(configValue);
    }

    private void validateConfigName(String value) {

        if (value == null || value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 3 || value.length() > 256) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!CONFIG_NAME_PATTERN.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }
    }

    private void validateConfigValue(String value) {

        if (value == null || value.isBlank()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (value.length() < 3 || value.length() > 512) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }

        if (!URL_PATTERN.matcher(value).matches()) {
            throw new DomainException(DomainExceptionEnum.BUSINESS_RULES_VIOLATION);
        }
    }

    public void updateValue(String newValue) {

        if (newValue == null) {
            return;
        }

        validateConfigValue(newValue);
        this.configValue = newValue;
    }

}