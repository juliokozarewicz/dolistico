package juliokozarewicz.helloworld.domain.usecase;

import org.springframework.stereotype.Service;

@Service
public class HelloWorldUseCase {

    public String execute(String message) {

        String finalMessage = message != null ? message : "Hello World!";
        return finalMessage;

    }

}
