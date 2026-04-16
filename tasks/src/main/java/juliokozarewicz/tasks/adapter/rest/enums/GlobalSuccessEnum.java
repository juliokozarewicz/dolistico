package juliokozarewicz.tasks.adapter.rest.enums;

public enum GlobalSuccessEnum {

    // =========================================== ( domain success codes init )

    // tasks
    TASK_CREATED_SUCCESSFULLY(201, "TASK_CREATED_SUCCESSFULLY"),
    TASKS_RETRIEVED_SUCCESSFULLY(200, "TASKS_RETRIEVED_SUCCESSFULLY"),
    TASKS_UPDATED_SUCCESSFULLY(200, "TASKS_UPDATED_SUCCESSFULLY"),
    TASKS_DELETED_SUCCESSFULLY(200, "TASKS_DELETED_SUCCESSFULLY"),

    // categories
    CATEGORIES_CREATED_SUCCESSFULLY(201, "CATEGORIES_CREATED_SUCCESSFULLY"),
    CATEGORIES_UPDATED_SUCCESSFULLY(200, "CATEGORIES_UPDATED_SUCCESSFULLY"),
    CATEGORIES_RETRIEVED_SUCCESSFULLY(200, "CATEGORIES_RETRIEVED_SUCCESSFULLY"),
    CATEGORIES_DELETED_SUCCESSFULLY(200, "CATEGORIES_DELETED_SUCCESSFULLY");

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