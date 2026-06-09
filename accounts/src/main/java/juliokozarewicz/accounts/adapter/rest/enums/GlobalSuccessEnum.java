package juliokozarewicz.accounts.adapter.rest.enums;

public enum GlobalSuccessEnum {

    // =========================================== ( domain success codes init )

    // accounts
    ACCOUNTS_CREATED_SUCCESSFULLY(200, "ACCOUNTS_CREATED_SUCCESSFULLY"),
    ACCOUNTS_PASSWORD_LINK_SUCCESSFULLY(200, "ACCOUNTS_PASSWORD_LINK_SUCCESSFULLY"),
    ACCOUNTS_UPDATE_PASSWORD_SUCCESSFULLY(200, "ACCOUNTS_UPDATE_PASSWORD_SUCCESSFULLY"),
    ACCOUNTS_LOGIN_REQUEST_SUCCESSFULLY(200, "ACCOUNTS_LOGIN_REQUEST_SUCCESSFULLY"),
    ACCOUNTS_LOGIN_SUCCESSFULLY(200, "ACCOUNTS_LOGIN_SUCCESSFULLY"),
    ACCOUNTS_LOGIN_REFRESH_SUCCESSFULLY(200, "ACCOUNTS_LOGIN_REFRESH_SUCCESSFULLY"),
    ACCOUNTS_RETRIEVED_SUCCESSFULLY(200, "ACCOUNTS_RETRIEVED_SUCCESSFULLY");

    // ============================================ ( domain success codes end )

    private final int statusCode;
    private final String messageCode;

    GlobalSuccessEnum(int statusCode, String messageCode) {
        this.statusCode = statusCode;
        this.messageCode = messageCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public String getMessageCode() {
        return messageCode;
    }

}