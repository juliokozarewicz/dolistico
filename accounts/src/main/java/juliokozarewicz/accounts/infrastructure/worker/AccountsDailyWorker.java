package juliokozarewicz.accounts.infrastructure.worker;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AccountsDailyWorker {

    @Scheduled(cron = "0 50 12 * * *", zone = "America/Sao_Paulo")
    public void deleteAccountExpiredJob() {

        System.out.println("#################################################");
        System.out.println("#################################################");
        System.out.println("                    Running                      ");
        System.out.println("#################################################");
        System.out.println("#################################################");

    }

}