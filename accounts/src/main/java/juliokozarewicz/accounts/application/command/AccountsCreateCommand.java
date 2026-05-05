package juliokozarewicz.accounts.application.command;

public interface AccountsCreateCommand {

    String fullName();
    String email();
    char[] userPassword();

}