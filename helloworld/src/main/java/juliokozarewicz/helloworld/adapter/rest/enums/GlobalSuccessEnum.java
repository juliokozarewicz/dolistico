package juliokozarewicz.helloworld.adapter.rest.enums;

public enum GlobalSuccessEnum {

    HELLO_WORLD_CREATED_SUCCESSFULLY(200, "HELLO_WORLD_CREATED_SUCCESSFULLY"),
    HELLO_WORLD_RETRIEVED_SUCCESSFULLY(200, "HELLO_WORLD_RETRIEVED_SUCCESSFULLY");

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