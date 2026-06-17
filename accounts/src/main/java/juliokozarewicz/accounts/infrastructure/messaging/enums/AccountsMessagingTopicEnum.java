package juliokozarewicz.accounts.infrastructure.messaging.enums;

public class AccountsMessagingTopicEnum {

    public static final String SEND_SIMPLE_EMAIL = "send.simple.email.v1";
    public static final String ACCOUNTS_NOT_ACTIVATED_DELETE = "accounts.not.activated.delete.v1";
    public static final String ACCOUNTS_CREATE_LOG = "accounts.create.log.v1";
    public static final String ACCOUNTS_CREATE_LOGIN_DEVICE = "accounts.create.login.device.v1";

    private AccountsMessagingTopicEnum() {}

}
