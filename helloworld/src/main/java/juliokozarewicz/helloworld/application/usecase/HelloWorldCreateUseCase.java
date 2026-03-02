package juliokozarewicz.helloworld.application.usecase;

import juliokozarewicz.helloworld.domain.model.HelloWorldCreateModel;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldCreateUseCase {

    public String execute(String message) {

        HelloWorldCreateModel finalMessage = new HelloWorldCreateModel(message);
        return finalMessage.getMessage();

    }

}