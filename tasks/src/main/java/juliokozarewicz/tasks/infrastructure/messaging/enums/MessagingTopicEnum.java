package juliokozarewicz.tasks.infrastructure.messaging.enums;

import lombok.Getter;

@Getter
public enum MessagingTopicEnum {

    TASKS_CREATE_UPDATE("tasks-create-update-persist");

    public static final String TASKS_CREATE_UPDATE_PERSIST = "tasks-create-update-persist";

    private final String topicName;

    MessagingTopicEnum(String topicName) {
        this.topicName = topicName;
    }

}