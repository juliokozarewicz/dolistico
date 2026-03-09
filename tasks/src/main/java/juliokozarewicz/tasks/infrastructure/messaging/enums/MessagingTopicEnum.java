package juliokozarewicz.tasks.infrastructure.messaging.enums;

import lombok.Getter;

@Getter
public enum MessagingTopicEnum {

    TASKS_CREATE_PERSIST("tasks.create.persist.v1");

    private final String topicName;

    MessagingTopicEnum(String topicName) {
        this.topicName = topicName;
    }

}