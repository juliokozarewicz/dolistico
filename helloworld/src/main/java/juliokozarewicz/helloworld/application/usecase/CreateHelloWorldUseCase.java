package juliokozarewicz.helloworld.application.usecase;

import juliokozarewicz.helloworld.domain.entity.HelloWorld;
import org.springframework.stereotype.Service;

@Service
public class CreateHelloWorldUseCase {

    public String execute(String message) {

        HelloWorld finalMessage = new HelloWorld(message);
        return finalMessage.getMessage();

    }

}