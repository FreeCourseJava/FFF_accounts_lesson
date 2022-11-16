package org.homework.service;

import org.homework.annotation.Service;
import org.homework.entity.Account;
import org.homework.repository.RepositoryAccount;

@Service
public class AccountService {

    private RepositoryAccount accountRepo;
    private CurrencyService currencyServ;

    public AccountService(RepositoryAccount accountRepo, CurrencyService currencyServ) {
        this.accountRepo = accountRepo;
        this.currencyServ = currencyServ;
        System.out.println("Выполнен конструктор AccServ");
    }

     public void cashTranslation(String donor, String acceptor, double sum) {
        Account donorAcc = accountRepo.getEntity(donor);
        Account acceptorAcc = accountRepo.getEntity(acceptor);
        if (donorAcc == null) {
            System.out.println("Аккаунт отправителя не существует");
            return;
        }
        if (acceptorAcc == null) {
            System.out.println("Аккаунт получателя не существует");
            return;
        }

        if (sum <= 0) {
            System.out.println("Некорректная сумма перевода");
            return;
        }

        if (donorAcc.balance < sum) {
            System.out.println("Недостаточно средств на счету отправителя");
            return;
        }

        double sumDonor = currencyServ.convertFromUSD(currencyServ.convertToUSD(sum, "RUB"),
                donorAcc.currencyAbbrev);
        double sumAcceptor = currencyServ.convertFromUSD(currencyServ.convertToUSD(sum, "RUB"),
                acceptorAcc.currencyAbbrev);

        acceptorAcc.balance = acceptorAcc.balance + sumAcceptor;
        donorAcc.balance = donorAcc.balance - sumDonor;
        accountRepo.putEntities(donorAcc, acceptorAcc);
        System.out.println("Перевод осуществлен успешно. Счета обновлены");
    }

}
