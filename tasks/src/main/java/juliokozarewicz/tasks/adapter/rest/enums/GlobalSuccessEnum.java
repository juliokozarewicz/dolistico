package juliokozarewicz.tasks.adapter.rest.enums;

public enum GlobalSuccessEnum {

    CREATE_TASK_SUCCESS(201, "CREATE_TASK_SUCCESS"),
    GET_TASKS_SUCCESS(200, "GET_TASKS_SUCCESS"),
    UPDATE_TASK_SUCCESS(200, "UPDATE_TASK_SUCCESS");

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