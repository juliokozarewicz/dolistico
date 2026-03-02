package juliokozarewicz.helloworld.application.usecase;

import juliokozarewicz.helloworld.domain.entity.HelloWorldEntity;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldCreateUseCase {

    public String execute(String message) {

        HelloWorldEntity finalMessage = new HelloWorldEntity(message);
        return finalMessage.getMessage();

    }

}