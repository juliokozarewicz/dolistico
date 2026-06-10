package juliokozarewicz.accounts.application.command;

public interface AccountsUpdatePasswordConfirmCommand {

    String token();
    String newPassword();
    char[] userPassword();

}