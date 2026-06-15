package juliokozarewicz.accounts.application.command;

public interface AccountsUpdateEmailConfirmCommand {

    String token();
    String pin();

}