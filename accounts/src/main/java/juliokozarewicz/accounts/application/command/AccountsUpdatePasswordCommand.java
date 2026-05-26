package juliokozarewicz.accounts.application.command;

public interface AccountsUpdatePasswordCommand {

    String token();
    String newPassword();
    char[] userPassword();

}