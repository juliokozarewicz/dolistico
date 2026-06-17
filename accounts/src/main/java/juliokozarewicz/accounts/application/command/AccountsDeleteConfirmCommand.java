package juliokozarewicz.accounts.application.command;

public interface AccountsDeleteConfirmCommand {

    String token();
    char[] userPassword();

}