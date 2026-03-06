package juliokozarewicz.tasks.infrastructure.messaging.enums;

import lombok.Getter;

@Getter
public enum MessagingTopicEnum {

    PERSIST_TASK_DATABASE("tasks.create.database.v1");

    private final String topicName;

    MessagingTopicEnum(String topicName) {
        this.topicName = topicName;
    }

}