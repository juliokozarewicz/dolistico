package juliokozarewicz.accounts.application.enums;

public enum AccountsUpdateEnum {

    // =========================================== ( domain success codes init )

    // accounts
    ACCOUNTS_UPDATE_PASSWORD("ACCOUNTS_UPDATE_PASSWORD"),
    ACCOUNTS_UPDATE_EMAIL("ACCOUNTS_UPDATE_EMAIL"),
    ACCOUNTS_DELETE("ACCOUNTS_DELETE"),;

    // ============================================ ( domain success codes end )

    private final String reasonCode;

    AccountsUpdateEnum(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonCode() {
        return reasonCode;
    }

}