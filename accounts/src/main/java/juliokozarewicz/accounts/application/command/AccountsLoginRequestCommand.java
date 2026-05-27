package juliokozarewicz.accounts.application.command;

public interface AccountsLoginRequestCommand {

    String email();
    char[] userPassword();

}