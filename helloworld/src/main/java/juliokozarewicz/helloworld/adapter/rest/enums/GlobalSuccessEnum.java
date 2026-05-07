package juliokozarewicz.helloworld.adapter.rest.enums;

public enum GlobalSuccessEnum {

    // =========================================== ( domain success codes init )

    // helloworld
    HELLOWORLD_CREATED_SUCCESSFULLY(200, "HELLOWORLD_CREATED_SUCCESSFULLY"),
    HELLOWORLD_RETRIEVED_SUCCESSFULLY(200, "HELLOWORLD_RETRIEVED_SUCCESSFULLY");

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