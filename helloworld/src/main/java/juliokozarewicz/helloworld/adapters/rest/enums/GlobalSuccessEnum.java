package juliokozarewicz.helloworld.adapters.rest.enums;

public enum GlobalSuccessEnum {

    HELLO_WORLD_SUCCESS(200, "HELLO_WORLD_SUCCESS"),
    HELLO_WORLD_CREATE(201, "HELLO_WORLD_CREATE");

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